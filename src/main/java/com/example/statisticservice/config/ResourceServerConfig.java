package com.example.statisticservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain springFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**","/v3/docs","/swagger-ui/**").permitAll()
                        // permit all here and secure the endpoints later in controllers
                        .anyRequest().permitAll())
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Get roles from JWT
            List<String> roles = jwt.getClaimAsStringList("roles");

            // Get scopes from JWT and prefix with "SCOPE_"
            List<String> scopes = jwt.getClaimAsStringList("scope").stream().map(scope -> "SCOPE_" + scope).toList();

            // Combine roles and scopes
            roles.addAll(scopes);


            // Convert roles to SimpleGrantedAuthority for later use of hasAuthority and hasRole or any relevant method
            return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        });

        return converter;
    }
}
