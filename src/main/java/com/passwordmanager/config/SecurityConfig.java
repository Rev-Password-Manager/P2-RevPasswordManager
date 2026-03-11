package com.passwordmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Logger to track method calls and events
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean 
    public BCryptPasswordEncoder passwordEncoder() { 
        // Log when this bean is created
        logger.info("BCryptPasswordEncoder bean is being created");
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Log when entering the method
        logger.info("Entered filterChain method to configure HTTP security");

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                // Log before configuring request matchers
                logger.info("Configuring request matchers for authentication");
                auth.requestMatchers("/api/passwords/**").authenticated();
                auth.requestMatchers("/dashboard", "/dashboard/**").authenticated();
                auth.anyRequest().permitAll();
            })
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    // Log unauthorized access attempts
                    logger.warn("Unauthorized access attempt: {}", authException.getMessage());
                    response.sendRedirect("/no-access");
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        // Log when exiting the method
        logger.info("Exiting filterChain method after configuring HTTP security");

        return http.build();
    }
}