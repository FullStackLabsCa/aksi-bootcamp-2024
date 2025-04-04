package org.reactivestax.canada_active_life.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.reactivestax.canada_active_life.web.security.JwtAuthenticationFilter;
import org.reactivestax.canada_active_life.web.security.JwtAuthorizationFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final ObjectMapper objectMapper;
    private final FamilyManagementService familyManagementService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .csrf(csrfConfigurer -> csrfConfigurer.disable())  // Disabling CSRF protection
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()  // Allow Swagger UI access
                    .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll() // Allow API Docs access
                    .requestMatchers(HttpMethod.POST, "/CanadaActiveLife/v1/signup").permitAll()
                    .requestMatchers(HttpMethod.GET, "/CanadaActiveLife/v1/activate-account").permitAll()
                    .requestMatchers(  "/CanadaActiveLife/v1/browse_offered_courses").permitAll())
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())// Require authentication for other endpoints
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, objectMapper, familyManagementService), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JwtAuthorizationFilter(authenticationManager))
//            .formLogin(Customizer.withDefaults()) // Enable Form Login
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173"); // Allow frontend URL
        corsConfiguration.addAllowedMethod("*"); // Allow all HTTP methods
        corsConfiguration.addAllowedHeader("*"); // Allow all headers
        corsConfiguration.setAllowCredentials(true); // Allow credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
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
