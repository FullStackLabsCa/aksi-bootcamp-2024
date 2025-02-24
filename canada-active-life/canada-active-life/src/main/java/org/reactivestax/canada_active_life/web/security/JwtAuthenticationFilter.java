package org.reactivestax.canada_active_life.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.enums.SecurityConstants;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private static final Function<GrantedAuthority,String> authToRoleFn = authority -> authority.getAuthority().replace("ROLE_","").toLowerCase();
    private final ObjectMapper mapper;
    private final FamilyManagementService familyManagementService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper, FamilyManagementService familyManagementService) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
        this.familyManagementService = familyManagementService;
        setFilterProcessesUrl(SecurityConstants.LOGIN_URL);
        log.info("Calling Authentication Filter");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            //unmarshall Json payload
            LoginUser loginUser;
            if(req.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
                loginUser = mapper.readValue(req.getInputStream(), LoginUser.class);
                log.debug("Attempting to authenticate user: {}", loginUser);
            } else {
                throw new AccessDeniedException("Unable to find user credentials");
            }

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException io) {
            throw new AccessDeniedException("Unable to parse user credentials", io);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {

        User principal = (User) auth.getPrincipal(); // logged in user
        List<String> claims = principal.getAuthorities().stream().map(authToRoleFn).collect(Collectors.toList());

        familyManagementService.sendOTPViaEms(familyManagementService.checkFamilyMemberValidity(principal.getUsername()));

        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withClaim("scopes", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

        Map<String,String> payload = new HashMap<>();
        payload.put("token",token);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(mapper.writeValueAsString(payload));
        res.getWriter().flush();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    static class LoginUser {
        private String username;
        private String password;
    }
}