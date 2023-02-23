package org.assisthelp.com.web;

import org.assisthelp.com.constant.AuthenticationConstant;
import org.assisthelp.com.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthRestController {

    private AuthenticationService authenticationService;

    public AuthRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map> jwtToken(String grantType, String username, String password,
                                        boolean withRefreshToken, String refreshToken) {

        if (grantType == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Grant type is required"));
        }

        if (grantType.equals(AuthenticationConstant.GRANT_TYPE_PASSWORD)) {
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }
        } else if(grantType.equals(AuthenticationConstant.GRANT_TYPE_REFRESH_TOKEN)) {
            if (refreshToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Refresh_token is required"));
            }
        } else {
            return new ResponseEntity<>(Map.of("error", "Grant type is not supported : " + grantType),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, String> token = this.authenticationService.authenticate(grantType, username, password, withRefreshToken, refreshToken);

            return new ResponseEntity<>(token, HttpStatus.OK);

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
