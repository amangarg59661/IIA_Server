# JWT + Spring Security Authentication — Design Spec

**Date:** 2026-05-23
**Status:** Approved
**Scope:** Add JWT authentication with Spring Security to backend; update both frontends

---

## Context

Current system has no authentication framework:
- Main app login: `POST /login` → BCrypt password check → returns `UserRoleDto` (userId, roles)
- Vendor portal login: `GET /VendorStatus/{vendorId}` → returns plain-text password to browser → client-side comparison
- Identity: `UserContextFilter` reads raw `X-User-Id` header → `UserContextHolder` (spoofable)
- Dependencies: `spring-security-crypto` only (for BCrypt). No `spring-boot-starter-security`.
- CORS: `WebSecurityConfig` implements `WebMvcConfigurer` (not actual Spring Security)

### Security Issues This Fixes
1. **Password exposure**: Vendor login API returns password in response body
2. **Identity spoofing**: Any client can set `X-User-Id` header to impersonate users
3. **No endpoint protection**: All API endpoints accessible without authentication

---

## Architecture

```
Authentication Flow:
  Login Request (credentials) → Controller → validate password → JwtUtil.generateToken(userId, roles) → return JWT in response

Request Flow:
  Client Request + Authorization: Bearer <token>
    → JwtAuthFilter (OncePerRequestFilter, before Spring Security auth)
      → JwtUtil.validateToken(token)
      → Extract userId from claims
      → Set UserContextHolder (for JPA Auditing)
      → Set SecurityContextHolder (for Spring Security)
    → Controller (authenticated)
```

---

## Backend Changes

### 1. Dependencies (pom.xml)

Add:
- `spring-boot-starter-security` (brings full Spring Security filter chain)
- `io.jsonwebtoken:jjwt-api:0.11.5`
- `io.jsonwebtoken:jjwt-impl:0.11.5` (runtime)
- `io.jsonwebtoken:jjwt-jackson:0.11.5` (runtime)

### 2. JwtUtil.java (new)

Location: `com.astro.config.JwtUtil`

Responsibilities:
- `generateToken(String userId, List<String> roles)` → JWT string
  - Subject: userId (String — numeric for main app, "ABC0001" format for vendor)
  - Claim "roles": list of role names
  - Claim "userType": "USER" or "VENDOR"
  - Expiry: 24 hours
  - Algorithm: HMAC-SHA256
  - Secret: read from `jwt.secret` property (default fallback for dev)
- `validateToken(String token)` → boolean
- `extractUserId(String token)` → String
- `extractRoles(String token)` → List<String>

### 3. JwtAuthFilter.java (new)

Location: `com.astro.config.JwtAuthFilter`

Extends `OncePerRequestFilter`. Runs before Spring Security's `UsernamePasswordAuthenticationFilter`.

Logic:
1. Extract `Authorization` header
2. If starts with "Bearer ", extract token
3. Validate via JwtUtil
4. If valid:
   - Set `UserContextHolder.set(userId)` (for JPA Auditing — same ThreadLocal as before)
   - Create `UsernamePasswordAuthenticationToken` with userId as principal, roles as authorities
   - Set `SecurityContextHolder.getContext().setAuthentication(authToken)`
5. If invalid/missing: do nothing (Spring Security will reject if endpoint requires auth)
6. Always call `filterChain.doFilter()`
7. Clear `UserContextHolder` in finally block

### 4. SecurityConfig.java (new)

Location: `com.astro.config.SecurityConfig`

Replaces current `WebSecurityConfig.java`. Uses `SecurityFilterChain` bean (Spring Boot 2.7 style).

Configuration:
- **CSRF**: Disabled (REST API, stateless)
- **Session**: STATELESS (no HttpSession)
- **CORS**: Configured to allow all origins/methods (same as current WebMvcConfigurer)
- **Public endpoints** (permitAll):
  - `POST /login`
  - `POST /api/vendor-quotation/vendor-login` (new)
  - `GET /api/vendor-quotation/VendorStatus/**` (keep accessible but password removed from response)
  - `POST /api/vendor-quotation/change-password`
  - `/api/vendor-master-util/**` (vendor registration — unauthenticated vendors register here)
  - `/swagger-ui/**`, `/v2/api-docs/**`, `/swagger-resources/**`, `/webjars/**`
  - `/file/**` (file uploads/downloads)
  - `/userDetails` (WorkflowController — used during login flow)
- **All other endpoints**: authenticated
- **Filter order**: JwtAuthFilter added before `UsernamePasswordAuthenticationFilter`

### 5. UserContextFilter.java (remove)

Deleted. Its responsibility (reading identity from request → UserContextHolder) is now handled by JwtAuthFilter.

### 6. UserController.java (modify)

Login endpoint changes:
- After successful authentication, call `JwtUtil.generateToken(userId, roleNames)`
- Set token on `UserRoleDto` response
- Return as before (wrapped in ResponseBuilder)

### 7. UserRoleDto.java (modify)

Add field: `private String token;`

### 8. VendorQuotationController.java (modify)

New endpoint: `POST /api/vendor-quotation/vendor-login`
- Request body: `{ vendorId, password }`
- Server-side password validation (compare plain text for now)
- Check vendor status (APPROVED, REJECTED, etc.)
- If valid + APPROVED: generate JWT with vendorId as subject, userType=VENDOR
- Return: `{ vendorId, status, token, isFirstLogin, isTempPassword, emailSent, createdDate }`
- **Does NOT** return password

### 9. VendorStatusDto.java (modify)

- Remove `password` field (no longer exposed in any API response)
- Add `token` field

### 10. VendorLoginRequestDto.java (new)

Location: `com.astro.dto.workflow.VendorLoginRequestDto`

Fields:
- `private String vendorId;`
- `private String password;`

### 11. application.properties (modify)

Add: `jwt.secret=astro-p2p-jwt-secret-key-2026-change-in-production`

### 12. WebSecurityConfig.java (remove)

CORS config moves into SecurityConfig. This file deleted.

---

## Frontend Changes

### Frontend-test (Main App)

**authSlice.jsx:**
- On login fulfilled: store `token` from `action.payload.token`
- Persist token to `localStorage.setItem('token', token)`
- On logout: `localStorage.removeItem('token')`
- On app load: restore token from localStorage

**App.js:**
- Axios interceptor: read token from Redux store or localStorage
- Send `Authorization: Bearer <token>` instead of `X-User-Id`
- Add response interceptor: if 401, dispatch logout + redirect to login

### Astro-vendor-portal (Vendor Portal)

**authSlice.jsx:**
- Add `token` to state
- `setVendor` reducer: store token
- On logout: clear token from localStorage

**App.js:**
- Same Bearer header interceptor as main app
- Response interceptor for 401

**Login.jsx:**
- Change from `GET /VendorStatus/{vendorId}` + client-side password check
- To `POST /vendor-login` with `{ vendorId, password }`
- Response includes `token` + `status` + all existing fields (minus password)
- On success: dispatch `setVendor` with response data including token

---

## What This Does NOT Include

1. **Vendor password BCrypt migration** — vendor passwords remain plain text. Separate scope.
2. **Refresh tokens** — 24h expiry, user re-logs in. Simple.
3. **Role-based endpoint authorization** — all authenticated users can access all endpoints. Fine-grained RBAC is separate scope.
4. **Password reset flow changes** — existing change-password endpoints remain public (vendor needs to change temp password before getting JWT).

---

## Risk Assessment

| Risk | Severity | Mitigation |
|------|----------|------------|
| Spring Security auto-config breaks existing endpoints | High | Explicit permitAll for all current public paths. Test every endpoint category. |
| CORS breaks with Spring Security | Medium | Configure CorsConfigurationSource in SecurityConfig matching current behavior |
| Frontend 401 loops | Low | Response interceptor clears token + redirects to login |
| Vendor portal dual login flow | Low | Old GET /VendorStatus still works (minus password). New POST /vendor-login is additive. |

---

## Execution Order

1. Backend: Add dependencies (pom.xml)
2. Backend: Create JwtUtil
3. Backend: Create JwtAuthFilter
4. Backend: Create SecurityConfig + remove WebSecurityConfig + remove UserContextFilter
5. Backend: Modify UserController + UserRoleDto (main app login returns JWT)
6. Backend: Create vendor-login endpoint + VendorLoginRequestDto + modify VendorStatusDto
7. Backend: Add jwt.secret to application.properties
8. Frontend-test: Update authSlice + App.js (Bearer header)
9. Vendor-portal: Update authSlice + App.js + Login.jsx
10. Build + test
