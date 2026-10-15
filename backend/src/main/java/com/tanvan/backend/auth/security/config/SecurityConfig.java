package com.tanvan.backend.auth.security.config;

import com.tanvan.backend.auth.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // ✅ NEW: đọc từ application.yml / env
    @Value("${cognito.issuer-uri}")
    private String cognitoIssuerUri;

    @Value("${cognito.jwk-set-uri}")
    private String cognitoJwkSetUri;

    @Bean
    @Primary  // ← QUAN TRỌNG: Ưu tiên bean này
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Dùng allowedOriginPatterns thay vì allowedOrigins
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",   // Vite
                "http://localhost:3000",   // React
                "http://localhost:3001",   // React alt
                "http://localhost:4200",   // Angular
                "http://127.0.0.1:5173",
                "https://project-management-frontend-sp2q.onrender.com" // Render
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Refresh-Token"
        ));
        
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);  // Cache preflight 1 giờ

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/public/**",
                                "/actuator/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/files/**",
                                "/gallery.html")
                        .permitAll()
                        // ✅ Khớp với AuthController mới
                        .requestMatchers("/api/auth/validate", "/api/auth/me", "/api/auth/logout").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())                                    // ✅ NEW: chỉ định decoder
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }


    /**
     * ✅ NEW: JwtDecoder trỏ tới Cognito (hoặc Floci)
     * - issuer-uri: verify claim "iss"
     * - jwk-set-uri: lấy public key để verify signature
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(cognitoJwkSetUri)
                .build();

        // ✅ Verify thêm claim "iss" (issuer)
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(cognitoIssuerUri);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer));

        return decoder;
    }

    /**
     * ✅ FIX: thêm custom converter để getName() trả về "cognito:username"
     *     thay vì "sub" (mặc định).
     *     Controller/Service mới dùng `jwt.getClaimAsString("cognito:username")` 
     *     nên KHÔNG bắt buộc, nhưng hữu ích khi log / debug.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // 1. Map authorities từ "cognito:groups" → ROLE_*
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("cognito:groups");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        // 2. Converter tổng
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        // 3. ✅ Map principal name = "cognito:username"
        converter.setPrincipalClaimName("cognito:username");

        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}