package com.beautyclinic.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/register",
                                "/login",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/treatments",
                                "/api/treatments/**"
                        ).permitAll()
                        .requestMatchers("/booking.html").hasRole("CUSTOMER")
                        .requestMatchers("/my-bookings.html").hasRole("CUSTOMER")
                        .requestMatchers("/api/bookings/**").hasRole("CUSTOMER")
                        .requestMatchers("/bookings/**").hasRole("CUSTOMER")
                        .requestMatchers("/my-bookings/**").hasRole("CUSTOMER")
                        .requestMatchers("/staff-appointments.html")
                        .hasAnyRole("AESTHETICIAN", "ADMIN")
                        .requestMatchers("/api/staff/**")
                        .hasAnyRole("AESTHETICIAN", "ADMIN")
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

    private static final class SpaCsrfTokenRequestHandler
            implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestHandler plainTokenHandler =
                new CsrfTokenRequestAttributeHandler();

        private final CsrfTokenRequestHandler xorTokenHandler =
                new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Supplier<CsrfToken> csrfToken
        ) {
            xorTokenHandler.handle(request, response, csrfToken);
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(
                HttpServletRequest request,
                CsrfToken csrfToken
        ) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());

            CsrfTokenRequestHandler tokenHandler =
                    StringUtils.hasText(headerValue)
                            ? plainTokenHandler
                            : xorTokenHandler;

            return tokenHandler.resolveCsrfTokenValue(request, csrfToken);
        }
    }
}
