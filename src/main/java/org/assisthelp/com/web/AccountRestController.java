package org.assisthelp.com.web;

import org.assisthelp.com.exception.AccountException;
import org.assisthelp.com.service.AccountService;
import org.assisthelp.com.entity.AppRole;
import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.form.RoleUserForm;
import org.assisthelp.com.web.constants.APIConstants;
import org.assisthelp.com.web.model.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/account")
public class AccountRestController {

    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<APIResponse> users() {
        return ResponseEntity.ok(new APIResponse("users", this.accountService.findAllUsers()));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<APIResponse> roles() {

        return ResponseEntity.ok(new APIResponse("roles", this.accountService.findAllRoles()));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<APIResponse> user(String username) {
        if (username != null) {
            return ResponseEntity.ok(new APIResponse("user", this.accountService.findByUsername(username)));
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.USERNAME_REQUIRED));
        }
    }

    @GetMapping("/current-user")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<APIResponse> currentUser() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return ResponseEntity.ok(new APIResponse("user", this.accountService.findByUsername(jwt.getSubject())));
    }

    @GetMapping("/role")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<APIResponse> role(String roleName) {
        if (roleName != null) {
            return ResponseEntity.ok(new APIResponse("role", this.accountService.findRoleByName(roleName)));
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_NAME_REQUIRED));
        }
    }

    @PostMapping("/user")
    public ResponseEntity<APIResponse> addUser(@RequestBody AppUser appUser) {
        if (appUser != null) {
            if (appUser.getUsername() != null && appUser.getPassword() != null && appUser.getEmail() != null) {
                AppUser user = this.accountService.findByUsername(appUser.getUsername());
                if (user == null) {
                    try {
                        return ResponseEntity.ok(new APIResponse("user", this.accountService.addNewUser(appUser)));
                    } catch (AccountException e) {
                        return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                    }
                } else {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "Username already exists"));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "Username, email and password are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.USER_INFO_REQUIRED));
        }
    }

    @PostMapping("/role")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> addRole(@RequestBody AppRole appRole) {
        if (appRole != null) {
            if (appRole.getRoleName() != null) {
                return ResponseEntity.ok(new APIResponse("role", this.accountService.addNewRole(appRole)));
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_NAME_REQUIRED));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_INFO_REQUIRED));
        }
    }

    @PostMapping("/addRoleToUser")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> addRoleToUser(@RequestBody RoleUserForm roleUserForm) {
        if (roleUserForm != null) {
            if (roleUserForm.getUsername() != null && roleUserForm.getRoleName() != null) {
                try {
                    this.accountService.addRoleToUser(roleUserForm.getUsername(), roleUserForm.getRoleName());
                    return ResponseEntity.ok(new APIResponse(APIConstants.MESSAGE, "Role added to user successfully"));
                } catch (AccountException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "Username and role name are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_INFO_REQUIRED));
        }
    }

    //Update role
    @PutMapping("/role")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> updateRole(@RequestBody AppRole appRole) {
        if (appRole != null) {
            if (appRole.getRoleName() != null) {
                return ResponseEntity.ok(new APIResponse("role", this.accountService.updateRole(appRole)));
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_NAME_REQUIRED));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_INFO_REQUIRED));
        }
    }


    //Update current user
    @PutMapping("/current-user")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<APIResponse> updateCurrentUser(@RequestBody AppUser appUser) {

        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        if (appUser != null) {
            if (!appUser.getUsername().equals(jwt.getSubject())) {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "You can only update your own account"));
            }
            AppUser user = this.accountService.findByUsername(appUser.getUsername());
            if (user != null) {
                try {
                    return ResponseEntity.ok(new APIResponse("user", this.accountService.updateUser(appUser)));
                } catch (AccountException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "User dont exists"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.USER_INFO_REQUIRED));
        }
    }

    @PutMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> updateUser(@RequestBody AppUser appUser) {

        if (appUser != null) {
            if (appUser.getUsername() != null && appUser.getPassword() != null && appUser.getEmail() != null) {
                AppUser user = this.accountService.findByUsername(appUser.getUsername());
                if (user != null) {
                    try {
                        return ResponseEntity.ok(new APIResponse("user", this.accountService.updateUser(appUser)));
                    } catch (AccountException e) {
                        return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                    }
                } else {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "User dont exists"));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "Username, email and password are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.USER_INFO_REQUIRED));
        }
    }

    @DeleteMapping("/deleteRoleToUser")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> deleteRoleToUser(@RequestBody RoleUserForm roleUserForm) {
        if (roleUserForm != null) {
            if (roleUserForm.getUsername() != null && roleUserForm.getRoleName() != null) {
                try {
                    this.accountService.deleteRoleToUser(roleUserForm.getUsername(), roleUserForm.getRoleName());
                    return ResponseEntity.ok(new APIResponse(APIConstants.MESSAGE, "Role deleted to user successfully"));
                } catch (AccountException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, "Username and role name are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_INFO_REQUIRED));
        }
    }


    @DeleteMapping("/role")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> deleteRole(@RequestBody AppRole appRole) {
        if (appRole != null) {
            if (appRole.getRoleName() != null) {
                try {
                    this.accountService.deleteRole(appRole.getRoleName());
                    return ResponseEntity.ok(new APIResponse(APIConstants.MESSAGE, "Role deleted successfully"));
                } catch (AccountException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_NAME_REQUIRED));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.ROLE_INFO_REQUIRED));
        }
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> deleteUser(@RequestBody AppUser appUser) {
        if (appUser != null) {
            if (appUser.getUsername() != null) {
                try {
                    this.accountService.deleteUser(appUser.getUsername());
                    return  ResponseEntity.ok(new APIResponse(APIConstants.MESSAGE, "User deleted successfully"));
                } catch (AccountException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.USERNAME_REQUIRED));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST, APIConstants.USER_INFO_REQUIRED));
        }
    }
}
