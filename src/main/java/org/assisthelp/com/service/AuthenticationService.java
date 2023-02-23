package org.assisthelp.com.service;

import org.assisthelp.com.constant.AuthenticationConstant;
import org.assisthelp.com.exception.AuthenticationTokenException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private JwtEncoder jwtEncoder;

    private JwtDecoder jwtDecoder;

    private UserDetailsService userDetailsService;

    private AuthenticationManager authenticationManager;

    public AuthenticationService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserDetailsService userDetailsService,
                          AuthenticationManager authenticationManager) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    public Map<String, String> authenticate(String grantType, String username, String password,
                                            boolean withRefreshToken, String refreshToken)
            throws AuthenticationTokenException {

        String subject;
        //String ensemble role utilisateurs separe par espace
        String scope;

        if (grantType.equals(AuthenticationConstant.GRANT_TYPE_PASSWORD)) {
            Authentication authentication;
            try {
                authentication = this.authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
            } catch (AuthenticationException e) {
                throw new AuthenticationTokenException("Invalid credentials : " + e.getMessage());
            }
            subject = authentication.getName();
            scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

        } else if(grantType.equals(AuthenticationConstant.GRANT_TYPE_REFRESH_TOKEN)) {
            Jwt decodeJwt;
            try {
                decodeJwt = this.jwtDecoder.decode(refreshToken);
            } catch (JwtException e) {
                throw new AuthenticationTokenException("Invalid refresh token : " + e.getMessage());
            }
            subject = decodeJwt.getSubject();
            UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(subject);
            } catch (UsernameNotFoundException e) {
                throw new AuthenticationTokenException("Invalid user : " + e.getMessage());
            }
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            scope = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        } else {
            throw new AuthenticationTokenException("Grant type not supported : " + grantType);
        }

        Map<String, String> token = new HashMap<>();

        //Date systeme
        Instant instant = Instant.now();
        Instant expAccessToken = instant.plus(withRefreshToken ? 15 : 60, ChronoUnit.MINUTES);

        try {
            String jwtAccessToken = this.generateAccessToken(subject, scope, instant, expAccessToken);
            token.put("access_token", jwtAccessToken);

            if (withRefreshToken) {
                Instant expRefreshToken = instant.plus(1, ChronoUnit.DAYS);
                String jwtRefreshToken = this.generateRefreshToken(subject, instant, expRefreshToken);
                token.put("refresh_token", jwtRefreshToken);
            }
            return token;
        } catch (JwtException e) {
            throw new AuthenticationTokenException("Error while parsing jwt token : " + e.getMessage());
        }
    }

    private String generateAccessToken(String subject, String scope, Instant createdDate, Instant expirationDate) {
        //Par défaut les roles sont dans la clé scope
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .issuer("security-service")
                .claim("scope", scope)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    private String generateRefreshToken(String subject, Instant createdDate, Instant expirationDate) {
        JwtClaimsSet jwtClaimsSetRefresh = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .issuer("security-service")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetRefresh)).getTokenValue();
    }
}
