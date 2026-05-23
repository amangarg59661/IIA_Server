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
