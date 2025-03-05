package org.reactivestax.ems.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // stateless session management, spring security wouldn't set jsession cookie
        return http.build();
    }

    // option 2, here we are using hasAuthority("SCOPE_ems.call")) to specify the authority to make the api request

//    @Bean
    public SecurityFilterChain enableOption1FilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(HttpMethod.GET, "/swagger-ui/**","/v3/api-docs/**","/v3/api-docs/swagger-config").permitAll())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasAuthority("SCOPE_ems.call"))
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults()))
                .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // stateless session management, spring security wouldn't set jsession cookie

        return http.build();
    }

    //option 3 here we are using access(hasScope("ems.call") to specify the scope required for any request
//    @Bean
    public SecurityFilterChain enableOption2FilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(HttpMethod.GET, "/swagger-ui/**","/v3/api-docs/**","/v3/api-docs/swagger-config").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").access(hasScope("ems.call")).anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // stateless session management, spring security wouldn't set jsession cookie

        return http.build();
    }
}
