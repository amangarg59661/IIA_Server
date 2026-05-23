package com.astro.dto.workflow;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserRoleDto {

  //  private Integer userRoleId;
   // private Integer roleId;
  //  private String role;
    private Integer userId;
    private String userName;
    private String mobileNumber;
    private String email;
    private String employeeDepartment;
  //  private boolean readPermission;
   // private boolean writePermission;
    private String createdBy;
    private String createdDate;
    private List<LoginRoleDto> roles;

    // TC_14 FIX: Add first login flag to prompt password change
    private Boolean isFirstLogin;
    private String token;
}
