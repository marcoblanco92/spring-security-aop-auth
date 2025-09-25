package com.marbl.spring_security_aop_auth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marbl.spring_security_aop_auth.component.oauth.Oauth2Service;
import com.marbl.spring_security_aop_auth.component.filter.JwtAuthenticationFilter;
import com.marbl.spring_security_aop_auth.component.filter.JwtRefreshFilter;
import com.marbl.spring_security_aop_auth.entity.provider.AuthProvider;
import com.marbl.spring_security_aop_auth.entity.user.User;
import com.marbl.spring_security_aop_auth.model.auth.LoginResponse;
import com.marbl.spring_security_aop_auth.service.blacklist.TokenBlacklistService;
import com.marbl.spring_security_aop_auth.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationProvider authenticationProvider;
    private final Oauth2Service oauth2Service;

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/login/google")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/v1/**")
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/auth/login", "/api/v1/users", "/api/v1/auth/reset-password", "/api/v1/auth/reset-password/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtRefreshFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, tokenBlacklistService);
    }

    @Bean
    public JwtRefreshFilter jwtRefreshFilter() {
        return new JwtRefreshFilter(jwtTokenProvider);
    }

    @Bean
    @Order(3)
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/google/login").authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(
                                (request, response, authentication) -> {
                                    User user = oauth2Service.processOauthPostLogin((OAuth2User) authentication.getPrincipal(), AuthProvider.GOOGLE);
                                    String jwt = jwtTokenProvider.generateToken(user.getUsername(), user.getRoles().stream().map(roles -> roles.getRoleName().name()).toList(), 3600000);

                                    LoginResponse loginResponse = new LoginResponse(jwt, "Bearer", jwtTokenProvider.getExpiresAt(jwt));

                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");

                                    ObjectMapper mapper = new ObjectMapper();
                                    String json = mapper.writeValueAsString(loginResponse);

                                    response.getWriter().write(json);
                                    response.getWriter().flush();
                                }
                        )
                );

        return http.build();
    }


}
