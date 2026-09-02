package com.beautyclinic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String showHome(Authentication authentication, Model model) {
        boolean isStaff = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_AESTHETICIAN")
                                || authority.getAuthority().equals("ROLE_ADMIN")
                );

        model.addAttribute("isStaff", isStaff);

        return "home";
    }
}
