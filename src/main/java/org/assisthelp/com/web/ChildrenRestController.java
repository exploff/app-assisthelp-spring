package org.assisthelp.com.web;

import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.entity.Children;
import org.assisthelp.com.exception.ChildrenException;
import org.assisthelp.com.service.AccountService;
import org.assisthelp.com.service.ChildrenService;
import org.assisthelp.com.web.constants.APIConstants;
import org.assisthelp.com.web.model.APIResponse;
import org.assisthelp.com.form.ChildrenDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController()
@RequestMapping("/children")
public class ChildrenRestController {

    private ChildrenService childrenService;

    private AccountService accountService;

    public ChildrenRestController(ChildrenService childrenService, AccountService accountService) {
        this.childrenService = childrenService;
        this.accountService = accountService;
    }

    @GetMapping("/childrens")
    @PreAuthorize("hasAnyAuthority('SCOPE_ASSMAT', 'SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> childrens(String username) {
        if (username != null) {
            AppUser user = this.accountService.findByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(new APIResponse("childrens", this.childrenService.findAllByUser(user)));
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "User with username " + username + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "username is required"));
        }
    }

    @GetMapping("/current-childrens")
    @PreAuthorize("hasAnyAuthority('SCOPE_ASSMAT', 'SCOPE_ADMIN')")
    public ResponseEntity<APIResponse> currentChildrens() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        String username = jwt.getSubject();
        if (username != null) {
            AppUser user = this.accountService.findByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(new APIResponse("childrens", this.childrenService.findAllByUser(user)));
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "User with username " + username + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "username is required"));
        }
    }

    @GetMapping("/children")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> children(Long childrenId) {
        if (childrenId != null) {
            Optional<Children> optChildren = this.childrenService.findById(childrenId);
            return optChildren.map(children -> ResponseEntity.ok(new APIResponse("children", children))).orElseGet(() -> ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "Children with id " + childrenId + " not found")));
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "childrenId is required"));
        }
    }

    @PostMapping("/children")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> addChildren(@RequestBody ChildrenDTO childrenDTO) {
        if (childrenDTO != null) {
            if (childrenDTO.getFirstName() != null && childrenDTO.getLastName() != null && childrenDTO.getUserId() != null) {

                Optional<AppUser> optUser = this.accountService.findByUserId(childrenDTO.getUserId());
                if (optUser.isPresent()) {
                    try {
                        Children children = this.childrenService.addChildren(childrenDTO, optUser.get());
                        return ResponseEntity.ok(new APIResponse("children added", children));

                    } catch (ChildrenException e) {

                        return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                                e.getMessage()));
                    }
                } else {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            "User with id " + childrenDTO.getUserId() + " not found"));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "Firstname, lastname and userId are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    APIConstants.CHILDREN_INFO_REQUIRED));
        }
    }

    @PutMapping("/children")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> updateChildren(@RequestBody ChildrenDTO childrenDTO) {
        if (childrenDTO != null) {
            if (childrenDTO.getFirstName() != null && childrenDTO.getLastName() != null && childrenDTO.getUserId() != null) {
                try {
                    Children children = this.childrenService.updateChildren(childrenDTO);
                    return ResponseEntity.ok(new APIResponse("children updated", children));

                } catch (ChildrenException e) {

                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "Firstname, lastname and userId are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    APIConstants.CHILDREN_INFO_REQUIRED));
        }

    }

    @DeleteMapping("/children")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> deleteChildren(Long childrenId) {
        if (childrenId != null) {
            try {
                this.childrenService.deleteChildrenById(childrenId);
                return ResponseEntity.ok(new APIResponse("Children deleted", ""));
            } catch (ChildrenException e) {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        e.getMessage()));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "childrenId is required"));
        }
    }

    @DeleteMapping("/childrens")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> deleteChildrens(Long userId) {
        if (userId != null) {
            try {
                Optional<AppUser> user = this.accountService.findByUserId(userId);
                if (user.isPresent()) {
                    this.childrenService.deleteChildrensByUser(user.get());
                    return ResponseEntity.ok(new APIResponse("Childrens deleted", ""));
                } else {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            "User not found with id " + userId));
                }
            } catch (ChildrenException e) {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        e.getMessage()));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "userId is required"));
        }    }
}