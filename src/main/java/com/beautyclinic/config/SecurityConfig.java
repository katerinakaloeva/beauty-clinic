package com.beautyclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
//                λεει για κάθε HTTP request, έλεγξε τους παρακάτω κανόνες
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register", "/login", "/css/**").permitAll()
                        .requestMatchers("/bookings/**").hasRole("CUSTOMER")
                        .requestMatchers("/my-bookings").hasRole("CUSTOMER")

                        .requestMatchers("/appointments/**", "/treatments/**")
                        .hasAnyRole("AESTHETICIAN", "ADMIN")
                        .requestMatchers("/home")
                        .hasAnyRole("CUSTOMER", "AESTHETICIAN", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .build();
    }
}