package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((requests) -> requests
                                                // Allow public access to pages and static resources
                                                .requestMatchers("/", "/first", "/first/**", "/login", "/register",
                                                                "/faq", "/branch", "/api/**", "/css/**", "/js/**",
                                                                "/images/**", "/icons/**")
                                                .permitAll()
                                                // Allow our Custom Login Controller to handle the request
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .anyRequest().permitAll() // TEMPORARY: Allow all for testing
                                )
                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                // telling Spring Security: "Don't process login yourself, I have a
                                                // controller for it"
                                                // By NOT setting .loginProcessingUrl(), or setting it to something
                                                // unused
                                                .loginProcessingUrl("/security-check")
                                                .permitAll())
                                .logout((logout) -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/first")
                                                .permitAll())
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }
}
