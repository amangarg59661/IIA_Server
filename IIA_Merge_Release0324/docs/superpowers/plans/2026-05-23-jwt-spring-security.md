# JWT + Spring Security Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add JWT authentication with Spring Security to the backend; update both frontend apps to send Bearer tokens.

**Architecture:** Spring Security filter chain with custom JwtAuthFilter extracts JWT from Authorization header, validates via JJWT library, sets SecurityContext + UserContextHolder. Login endpoints generate JWT on successful credential check.

**Tech Stack:** Spring Boot 2.7.3, Spring Security 5.7, JJWT 0.11.5, React/Redux (frontend)

---

### Task 1: Add Dependencies

**Files:**
- Modify: `Backend-prod/pom.xml`
- Modify: `Backend-prod/src/main/resources/application.properties`

- [ ] **Step 1: Add JWT and Spring Security dependencies to pom.xml**

Add these dependencies inside the `<dependencies>` block, after the existing `spring-security-crypto` dependency:

```xml
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
```

Note: The existing `spring-security-crypto` dependency can be removed since `spring-boot-starter-security` includes it. But keeping it won't cause issues (version managed by starter).

- [ ] **Step 2: Add JWT secret to application.properties**

Append to `Backend-prod/src/main/resources/application.properties`:

```properties
# JWT Configuration
jwt.secret=astro-p2p-jwt-secret-key-2026-change-in-production
jwt.expiration=86400000
```

(86400000 ms = 24 hours)

- [ ] **Step 3: Verify dependencies resolve**

Run: `cd Backend-prod && mvn dependency:resolve -q`
Expected: BUILD SUCCESS (dependencies download)

- [ ] **Step 4: Commit**

```bash
git add Backend-prod/pom.xml Backend-prod/src/main/resources/application.properties
git commit -m "feat: add Spring Security + JJWT dependencies and JWT config"
```

---

### Task 2: Create JwtUtil

**Files:**
- Create: `Backend-prod/src/main/java/com/astro/config/JwtUtil.java`

- [ ] **Step 1: Create JwtUtil.java**

```java
package com.astro.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret:astro-p2p-jwt-secret-key-2026-change-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        // Ensure key is at least 256 bits for HS256
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            // Pad to 32 bytes if too short
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userId, List<String> roles, String userType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .claim("userType", userType)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = getClaims(token).get("roles");
        if (roles instanceof List) {
            return (List<String>) roles;
        }
        return List.of();
    }

    public String extractUserType(String token) {
        return (String) getClaims(token).get("userType");
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/config/JwtUtil.java
git commit -m "feat: add JwtUtil for JWT token generation and validation"
```

---

### Task 3: Create JwtAuthFilter

**Files:**
- Create: `Backend-prod/src/main/java/com/astro/config/JwtAuthFilter.java`

- [ ] **Step 1: Create JwtAuthFilter.java**

```java
package com.astro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String userId = jwtUtil.extractUserId(token);
                    List<String> roles = jwtUtil.extractRoles(token);

                    // Set UserContextHolder for JPA Auditing
                    UserContextHolder.set(userId);

                    // Set Spring Security context
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/config/JwtAuthFilter.java
git commit -m "feat: add JwtAuthFilter to extract JWT and set security context"
```

---

### Task 4: Create SecurityConfig + Remove Old Config + Remove UserContextFilter

**Files:**
- Create: `Backend-prod/src/main/java/com/astro/config/SecurityConfig.java`
- Delete: `Backend-prod/src/main/java/com/astro/config/WebSecurityConfig.java`
- Delete: `Backend-prod/src/main/java/com/astro/config/UserContextFilter.java`

- [ ] **Step 1: Create SecurityConfig.java**

```java
package com.astro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Public endpoints - no JWT needed
                .antMatchers("/login").permitAll()
                .antMatchers("/userDetails").permitAll()
                .antMatchers("/api/vendor-quotation/vendor-login").permitAll()
                .antMatchers("/api/vendor-quotation/VendorStatus/**").permitAll()
                .antMatchers("/api/vendor-quotation/change-password").permitAll()
                .antMatchers("/api/vendor-master-util/**").permitAll()
                // Swagger
                .antMatchers("/swagger-ui/**", "/swagger-ui.html", "/v2/api-docs/**",
                             "/swagger-resources/**", "/webjars/**").permitAll()
                // File endpoints
                .antMatchers("/file/**").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

- [ ] **Step 2: Delete WebSecurityConfig.java**

Delete `Backend-prod/src/main/java/com/astro/config/WebSecurityConfig.java` — CORS config moves into SecurityConfig.

- [ ] **Step 3: Delete UserContextFilter.java**

Delete `Backend-prod/src/main/java/com/astro/config/UserContextFilter.java` — JwtAuthFilter now handles setting UserContextHolder.

- [ ] **Step 4: Compile check**

Run: `cd Backend-prod && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/config/SecurityConfig.java
git rm Backend-prod/src/main/java/com/astro/config/WebSecurityConfig.java
git rm Backend-prod/src/main/java/com/astro/config/UserContextFilter.java
git commit -m "feat: add SecurityConfig with JWT filter chain, remove old WebSecurityConfig and UserContextFilter"
```

---

### Task 5: Modify Login Endpoint to Return JWT

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/UserRoleDto.java`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/UserServiceImpl.java`
- Modify: `Backend-prod/src/main/java/com/astro/controller/UserController.java`

- [ ] **Step 1: Add token field to UserRoleDto**

In `UserRoleDto.java`, add after the `isFirstLogin` field:

```java
    private String token;
```

(Lombok @Data generates getter/setter automatically)

- [ ] **Step 2: Modify UserController to inject JwtUtil and generate token**

Replace the entire `UserController.java` with:

```java
package com.astro.controller;

import com.astro.config.JwtUtil;
import com.astro.dto.workflow.LoginRoleDto;
import com.astro.dto.workflow.UserDto;
import com.astro.dto.workflow.UserRoleDto;
import com.astro.service.UserService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDto userDto) {
        UserRoleDto userRoleDto = userService.login(userDto);

        // Generate JWT token
        List<String> roleNames = List.of();
        if (userRoleDto.getRoles() != null) {
            roleNames = userRoleDto.getRoles().stream()
                    .map(LoginRoleDto::getRoleName)
                    .collect(Collectors.toList());
        }
        String token = jwtUtil.generateToken(
                String.valueOf(userRoleDto.getUserId()),
                roleNames,
                "USER"
        );
        userRoleDto.setToken(token);

        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(userRoleDto), HttpStatus.OK);
    }
}
```

- [ ] **Step 3: Compile check**

Run: `cd Backend-prod && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/dto/workflow/UserRoleDto.java
git add Backend-prod/src/main/java/com/astro/controller/UserController.java
git commit -m "feat: main app login now returns JWT token in response"
```

---

### Task 6: Create Vendor Login Endpoint

**Files:**
- Create: `Backend-prod/src/main/java/com/astro/dto/workflow/VendorLoginRequestDto.java`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/VendorStatusDto.java`
- Modify: `Backend-prod/src/main/java/com/astro/controller/VendorQuotationController.java`
- Modify: `Backend-prod/src/main/java/com/astro/service/VendorQuotationAgainstTenderService.java`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/VendorQuotationAgainstTenderServiceImpl.java`

- [ ] **Step 1: Create VendorLoginRequestDto**

```java
package com.astro.dto.workflow;

import lombok.Data;

@Data
public class VendorLoginRequestDto {
    private String vendorId;
    private String password;
}
```

- [ ] **Step 2: Modify VendorStatusDto — remove password, add token**

Replace the `password` field with `token` in `VendorStatusDto.java`:

Full file:

```java
package com.astro.dto.workflow;

import lombok.Data;

@Data
public class VendorStatusDto {

    private String vendorId;
    private String status;
    private String comments;
    private Boolean emailStatus;
    private String token;
    private Boolean isFirstLogin;
    private Boolean isTempPassword;
    private Boolean emailSent;
    private String createdDate;
}
```

- [ ] **Step 3: Add vendorLogin method to service interface**

In `VendorQuotationAgainstTenderService.java`, add:

```java
    public VendorStatusDto vendorLogin(VendorLoginRequestDto request);
```

- [ ] **Step 4: Implement vendorLogin in service impl**

In `VendorQuotationAgainstTenderServiceImpl.java`, add the import and autowire:

```java
import com.astro.config.JwtUtil;
```

Add field:

```java
    @Autowired
    private JwtUtil jwtUtil;
```

Add method:

```java
    @Override
    public VendorStatusDto vendorLogin(VendorLoginRequestDto request) {
        VendorStatusDto dto = new VendorStatusDto();
        dto.setVendorId(request.getVendorId());

        // Find vendor login details
        Optional<VendorLoginDetails> vendorLoginOpt = vendorLoginDetailsRepository.findByVendorId(request.getVendorId());

        if (vendorLoginOpt.isEmpty()) {
            dto.setStatus("NOT_FOUND");
            return dto;
        }

        VendorLoginDetails vl = vendorLoginOpt.get();

        // Server-side password validation
        if (!vl.getPassword().equals(request.getPassword())) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.USER_NOT_FOUND,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid credentials."
                )
            );
        }

        dto.setEmailStatus(vl.getEmailSent());
        dto.setEmailSent(vl.getEmailSent());
        dto.setIsFirstLogin(vl.getIsFirstLogin());
        dto.setIsTempPassword(vl.getIsTempPassword());
        dto.setCreatedDate(vl.getCreatedDate() != null ? vl.getCreatedDate().toString() : null);

        // Check vendor approval status
        Optional<VendorMaster> approved = vendorMasterRepository.findByVendorId(request.getVendorId());
        if (approved.isPresent()) {
            VendorMaster vm = approved.get();
            if ("APPROVED".equalsIgnoreCase(vm.getStatus())) {
                dto.setStatus("APPROVED");
            } else if ("REJECTED".equalsIgnoreCase(vm.getStatus())) {
                dto.setStatus("REJECTED");
                dto.setComments(vm.getComments());
            } else if ("CHANGE_REQUEST".equalsIgnoreCase(vm.getStatus())) {
                dto.setStatus("CHANGE_REQUEST");
                dto.setComments(vm.getComments());
            } else {
                dto.setStatus("AWAITING_APPROVAL");
            }
        } else {
            // Check pending vendor
            Optional<VendorMasterUtil> pending = vendorMasterUtilRepository.findByVendorId(request.getVendorId());
            if (pending.isPresent()) {
                dto.setStatus("AWAITING_APPROVAL");
            } else {
                dto.setStatus("NOT_FOUND");
            }
        }

        // Generate JWT token for all valid logins (not just APPROVED)
        String token = jwtUtil.generateToken(
                request.getVendorId(),
                List.of("VENDOR"),
                "VENDOR"
        );
        dto.setToken(token);

        return dto;
    }
```

- [ ] **Step 5: Remove password from getVendorStatus response**

In `VendorQuotationAgainstTenderServiceImpl.java`, in the `getVendorStatus` method, find:

```java
        dto.setPassword(vl.getPassword());
```

Remove this line entirely. The VendorStatusDto no longer has a password field.

- [ ] **Step 6: Add vendor-login endpoint to controller**

In `VendorQuotationController.java`, add import:

```java
import com.astro.dto.workflow.VendorLoginRequestDto;
```

Add endpoint (after the existing `getVendorStatus` method):

```java
    @PostMapping("/vendor-login")
    public ResponseEntity<Object> vendorLogin(@RequestBody VendorLoginRequestDto request) {
        VendorStatusDto responseDTO = vqService.vendorLogin(request);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }
```

- [ ] **Step 7: Compile check**

Run: `cd Backend-prod && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/dto/workflow/VendorLoginRequestDto.java
git add Backend-prod/src/main/java/com/astro/dto/workflow/VendorStatusDto.java
git add Backend-prod/src/main/java/com/astro/controller/VendorQuotationController.java
git add Backend-prod/src/main/java/com/astro/service/VendorQuotationAgainstTenderService.java
git add Backend-prod/src/main/java/com/astro/service/impl/VendorQuotationAgainstTenderServiceImpl.java
git commit -m "feat: add vendor-login endpoint with server-side password validation and JWT"
```

---

### Task 7: Frontend-test — Store Token + Bearer Header

**Files:**
- Modify: `Frontend-test/src/store/slice/authSlice.jsx`
- Modify: `Frontend-test/src/App.js`

- [ ] **Step 1: Update authSlice to store token and persist to localStorage**

Replace the full `Frontend-test/src/store/slice/authSlice.jsx` with:

```jsx
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';

const initialState = {
  userId: null,
  userName: null,
  email: null,
  mobileNumber: null,
  employeeDepartment: null,
  roles: [],
  role: "",
  roleId: null,
  isFirstLogin: false,
  token: localStorage.getItem('token') || null,
  loading: false,
  error: null
};


export const login = createAsyncThunk(
  'auth/login',
  async (formData, thunkAPI) => {
    try {
      const response = await axios.post(
        '/login',
        formData
      );
      const data = response.data;
      
      if (data.responseStatus?.statusCode !== 0) {
        return thunkAPI.rejectWithValue(
          data.responseStatus?.message || 'Login failed'
        );
      }
      
      return data.responseData;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error.message
      );
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      state.userRoleId = null;
      state.roleId = null;
      state.role = null;
      state.userId = null;
      state.readPermission = false;
      state.writePermission = false;
      state.loading = false;
      state.error = null;
      state.userName = null;
      state.mobileNumber = null;
      state.email = null;
      state.locationId = null;
      state.employeeDepartment = null;
      state.token = null;
      localStorage.removeItem('token');
    },
     changeRole(state, action) {
      state.role = action.payload;
      state.roleId = state.roles.find(r => r.roleName === action.payload)?.roleId || null;
    },
    clearFirstLogin(state) {
      state.isFirstLogin = false;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
     .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        const {
          userId,
          userName,
          email,
          mobileNumber,
          employeeDepartment,
          roles,
          isFirstLogin,
          token
        } = action.payload;

        state.userId = userId;
        state.userName = userName;
        state.email = email;
        state.mobileNumber = mobileNumber;
        state.employeeDepartment = employeeDepartment;
        state.roles = roles || [];
        state.role = roles?.[0]?.roleName || "";
        state.roleId = roles?.[0]?.roleId || null;
        state.isFirstLogin = isFirstLogin || false;
        state.token = token || null;

        // Persist token
        if (token) {
          localStorage.setItem('token', token);
        }
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || action.error.message;
      });
  }
});

export const { logout, changeRole, clearFirstLogin } = authSlice.actions;
export default authSlice.reducer;
```

- [ ] **Step 2: Update App.js to send Bearer token**

Replace `Frontend-test/src/App.js` with:

```jsx
import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';
import store from './store';
import { logout } from './store/slice/authSlice';

export const baseURL = "http://localhost:8081/astro-service";
axios.defaults.baseURL = baseURL;

// Send Authorization header with every request
axios.interceptors.request.use((config) => {
  const token = store.getState().auth?.token || localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 responses — token expired or invalid
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch(logout());
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchMasters());
  }, [dispatch])

  return (
    <Routes />
  );
}

export default App;
```

- [ ] **Step 3: Commit**

```bash
git add Frontend-test/src/store/slice/authSlice.jsx Frontend-test/src/App.js
git commit -m "feat: frontend-test stores JWT token and sends as Bearer header"
```

---

### Task 8: Vendor Portal — Store Token + Bearer Header + New Login Flow

**Files:**
- Modify: `Astro-vendor-portal-main/src/store/slice/authSlice.jsx`
- Modify: `Astro-vendor-portal-main/src/App.js`
- Modify: `Astro-vendor-portal-main/src/pages/auth/Login.jsx`
- Modify: `Astro-vendor-portal-main/src/src/store/slice/authSlice.jsx` (duplicate)
- Modify: `Astro-vendor-portal-main/src/src/pages/auth/Login.jsx` (duplicate)
- Modify: `Astro-vendor-portal-main/src/src/App.js` (duplicate)

- [ ] **Step 1: Update vendor portal authSlice**

Replace `Astro-vendor-portal-main/src/store/slice/authSlice.jsx` with:

```jsx
import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  vendorId: null,
  emailSent: false,
  createdDate: null,
  status: null,
  isFirstLogin: null,
  isTempPassword: null,
  token: localStorage.getItem('vendorToken') || null,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      localStorage.removeItem('vendorToken');
      return { ...initialState, token: null };
    },

    setVendor: (state, { payload }) => {
      state.vendorId = payload.vendorId;
      state.emailSent = payload.emailSent;
      state.createdDate = payload.createdDate;
      state.status = payload.status;
      state.isFirstLogin = payload.isFirstLogin;
      state.isTempPassword = payload.isTempPassword;
      state.token = payload.token || null;

      // Persist token
      if (payload.token) {
        localStorage.setItem('vendorToken', payload.token);
      }
    },

    setPasswordChanged: (state) => {
      state.isFirstLogin = false;
      state.isTempPassword = false;
    },
  },
});

export const { logout, setVendor, setPasswordChanged } = authSlice.actions;
export default authSlice.reducer;
```

- [ ] **Step 2: Update vendor portal App.js**

Replace `Astro-vendor-portal-main/src/App.js` with:

```jsx
import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';
import store from './store';
import { logout } from './store/slice/authSlice';

export const baseURL = "http://localhost:8081/astro-service";
axios.defaults.baseURL = baseURL;

// Send Authorization header with every request
axios.interceptors.request.use((config) => {
  const token = store.getState().auth?.token || localStorage.getItem('vendorToken');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 responses — token expired or invalid
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch(logout());
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchMasters());
  }, [dispatch])

  return (
    <Routes />
  );
}

export default App;
```

- [ ] **Step 3: Update vendor portal Login.jsx to use POST /vendor-login**

Replace `Astro-vendor-portal-main/src/pages/auth/Login.jsx` with:

```jsx
import React, { useState, useEffect } from 'react';
import Btn from '../../components/DKG_Btn';
import MyLogo from "../../assets/iia-logo.png";
import FormBody from '../../components/DKG_FormBody';
import FormInputItem from '../../components/DKG_FormInputItem';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import FormContainer from '../../components/DKG_FormContainer';
import { fetchMasters } from '../../store/slice/masterSlice';
import { setVendor } from '../../store/slice/authSlice';
import axios from 'axios';
import { message } from 'antd';

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const [formData, setFormData] = useState({
    userId: '',
    password: ''
  });

  const [messageText, setMessageText] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    if (location.state?.message) {
      setSuccessMessage(location.state.message);
      if (location.state?.vendorId) {
        setFormData(prev => ({
          ...prev,
          userId: location.state.vendorId
        }));
      }
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  const handleFormValueChange = (fieldName, value) => {
    setFormData((prev) => ({
      ...prev,
      [fieldName]: value
    }));
    setMessageText('');
    setSuccessMessage('');
  };

  const handleFormSubmit = async () => {
    const { userId, password } = formData;

    if (!userId || !password) {
      setMessageText('Please enter both Vendor ID and Password');
      return;
    }

    try {
      // Server-side login — password validated on backend
      const response = await axios.post('/api/vendor-quotation/vendor-login', {
        vendorId: userId,
        password: password
      });
      const data = response.data;
      const responseData = data.responseData;
      const status = responseData.status;

      if (status === "APPROVED") {
        if (responseData.isFirstLogin === true) {
          message.info('Please change your temporary password to continue.');
          navigate('/change-password', { 
            state: { 
              vendorId: userId,
              isFirstLogin: true 
            } 
          });
          return;
        }

        dispatch(setVendor(responseData));
        navigate(`/vendor/${userId}`);
      } else if (status === "REJECTED") {
        const comments = responseData.comments || "Your request was rejected with no comments provided.";
        setMessageText(`Sorry, your request has been rejected. Reason: ${comments}`);
      } else if (status === "AWAITING_APPROVAL") {
        if (responseData.isFirstLogin === true) {
          message.info('Please change your temporary password. Your registration is still under review.');
          navigate('/change-password', { 
            state: { 
              vendorId: userId,
              isFirstLogin: true,
              pendingApproval: true
            } 
          });
          return;
        }
        setMessageText("Your registration is in review stage. Please wait for sometime...");
      } else if (status === "CHANGE_REQUEST") {
        if (responseData.isFirstLogin === true) {
          message.info('Please change your temporary password first.');
          navigate('/change-password', { 
            state: { 
              vendorId: userId,
              isFirstLogin: true 
            } 
          });
          return;
        }
        const comments = responseData.comments || "Admin has requested changes to your registration.";
        setMessageText(`Change requested: ${comments}`);
      } else if (status === "NOT_FOUND") {
        setMessageText("Vendor ID not found");
      } else {
        setMessageText("Unknown status");
      }
    } catch (error) {
      console.error('Login error:', error);
      if (error.response?.status === 404) {
        setMessageText("Vendor ID not found");
      } else if (error.response?.data?.responseStatus?.message) {
        setMessageText(error.response.data.responseStatus.message);
      } else {
        setMessageText("Error during login. Please try again.");
      }
    }
  };

  const handleRegisterRedirect = () => {
    navigate('/app');
  };

  return (
    <>
      <header className='bg-darkBlue text-offWhite p-4 fixed top-0 w-full z-30'>
        <h1>Log In</h1>
      </header>
      <FormContainer className='mt-20 main-content border-none !shadow-none'>
        <main className='w-full p-4 flex flex-col h-fit justify-center items-center gap-8 bg-white relative z-20 rounded-md'>
          <img src={MyLogo} width={200} height={150} alt="Logo" />
          
          {successMessage && (
            <div style={{
              backgroundColor: '#d4edda',
              color: '#155724',
              padding: '12px 20px',
              borderRadius: '8px',
              width: '100%',
              maxWidth: '400px',
              textAlign: 'center',
              border: '1px solid #c3e6cb'
            }}>
              {successMessage}
            </div>
          )}

          <FormBody onFinish={handleFormSubmit} initialValues={formData}>
            <FormInputItem 
              label="Vendor ID" 
              placeholder="COMP001" 
              name='userId' 
              onChange={handleFormValueChange}
              value={formData.userId}
              required 
            />
            <FormInputItem 
              type='password' 
              label="Password" 
              placeholder="*****" 
              name='password' 
              onChange={handleFormValueChange} 
              required 
            />
            {messageText && (
              <p className="text-red-500" style={{ 
                backgroundColor: '#fee2e2', 
                padding: '10px', 
                borderRadius: '6px',
                border: '1px solid #fecaca'
              }}>
                {messageText}
              </p>
            )}
            <div className='custom-btn'>
              <Btn htmlType="submit" text="Sign In"/>
            </div>
          </FormBody>
          <h2 className='text-gray-500 text-center'>
            Account credentials unavailable?<br />
            Request Admin for your credentials.
          </h2>
          <p className='text-gray-500 text-center'>
            New to us? <span className="text-sm text-blue-600 cursor-pointer hover:underline" onClick={handleRegisterRedirect}>Register here</span>
          </p>
        </main>
      </FormContainer>
    </>
  );
};

export default Login;
```

- [ ] **Step 4: Update duplicate src/src/ files**

Copy the same changes to the duplicate directory:
- `Astro-vendor-portal-main/src/src/store/slice/authSlice.jsx` ← same as Step 1
- `Astro-vendor-portal-main/src/src/App.js` ← same as Step 2  
- `Astro-vendor-portal-main/src/src/pages/auth/Login.jsx` ← same as Step 3

- [ ] **Step 5: Commit**

```bash
git add Astro-vendor-portal-main/src/store/slice/authSlice.jsx
git add Astro-vendor-portal-main/src/App.js
git add Astro-vendor-portal-main/src/pages/auth/Login.jsx
git add Astro-vendor-portal-main/src/src/store/slice/authSlice.jsx
git add Astro-vendor-portal-main/src/src/App.js
git add Astro-vendor-portal-main/src/src/pages/auth/Login.jsx
git commit -m "feat: vendor portal uses POST /vendor-login with JWT Bearer token"
```

---

### Task 9: Build and Test

**Files:**
- No new files. Verification task.

- [ ] **Step 1: Backend full build**

Run: `cd Backend-prod && mvn clean compile -q`
Expected: BUILD SUCCESS with 0 errors

- [ ] **Step 2: Check for any remaining references to UserContextFilter**

Grep for `UserContextFilter` across the codebase. If any imports or references exist (besides in JwtAuthFilter), fix them.

- [ ] **Step 3: Check for any remaining X-User-Id header references in frontend**

Grep for `X-User-Id` across Frontend-test and Astro-vendor-portal. All should be removed (replaced by Authorization header).

- [ ] **Step 4: Check VendorStatusDto password references**

Grep for `setPassword` or `getPassword` calls on VendorStatusDto in service code. The `getVendorStatus` method should no longer call `dto.setPassword()`.

- [ ] **Step 5: Verify SecurityConfig public endpoints cover all login flows**

Verify these paths are in permitAll:
- `/login` ✓
- `/api/vendor-quotation/vendor-login` ✓
- `/api/vendor-quotation/VendorStatus/**` ✓
- `/api/vendor-quotation/change-password` ✓
- `/api/vendor-master-util/**` ✓
- `/file/**` ✓
- Swagger paths ✓

- [ ] **Step 6: Commit final verification**

If any fixes were needed, commit them:

```bash
git add -A
git commit -m "fix: resolve remaining JWT integration issues found during verification"
```
