package org.reactivestax.canada_active_life.web.security;

import jakarta.servlet.Filter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        System.out.println("authenticationManager = " + authenticationManager);
    }
}
