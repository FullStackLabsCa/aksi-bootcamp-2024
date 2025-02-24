package org.reactivestax.canada_active_life.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestax.canada_active_life.web.security.JwtAuthenticationFilter;
import org.reactivestax.canada_active_life.web.security.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .csrf(csrfConfigurer -> csrfConfigurer.disable())  // Disabling CSRF protection
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()  // Allow Swagger UI access
                    .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll() // Allow API Docs access
                    .requestMatchers(HttpMethod.POST, "/CanadaActiveLife/v1/signup").permitAll()
                    .requestMatchers(HttpMethod.GET, "/CanadaActiveLife/v1/activate-account").permitAll()
                    .requestMatchers(HttpMethod.POST, "/CanadaActiveLife/v1/browse_offered_courses").permitAll())
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())// Require authentication for other endpoints
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JwtAuthorizationFilter(authenticationManager))
//            .formLogin(Customizer.withDefaults()) // Enable Form Login
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
