package org.assisthelp.com.web;

import org.assisthelp.com.constant.AuthenticationConstant;
import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.exception.AccountException;
import org.assisthelp.com.service.AccountService;
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

    private AccountService accountService;

    public AuthRestController(AuthenticationService authenticationService, AccountService accountService) {
        this.authenticationService = authenticationService;
        this.accountService = accountService;
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


    @PostMapping("/signup")
    public ResponseEntity<Map> signUp(String username, String emailAddress, String password) {
        System.out.println("CALLED : " + username + " " + emailAddress + " " + password);
        if (username == null || emailAddress == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, emailAddress and password are required"));
        }

        //Check emailAddress is valid or not
        if (!emailAddress.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email address is not valid"));
        }

        //Check password is valid or not
        /*if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is not valid"));
        }*/

        //Check if username or emailAddress already exists
        if (this.accountService.existsAppUserByEmailOrUsername(emailAddress, username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username or email address already exists"));
        }

        try {
            AppUser user = new AppUser();
            user.setEmail(emailAddress);
            user.setUsername(username);
            user.setPassword(password);
            this.accountService.addNewUser(user);

            String grantType = AuthenticationConstant.GRANT_TYPE_PASSWORD;
            boolean withRefreshToken = true;
            String refreshToken = null;

            Map<String, String> token = this.authenticationService.authenticate(grantType, username, password, withRefreshToken, refreshToken);

            return new ResponseEntity<>(token, HttpStatus.OK);

        } catch (AccountException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
