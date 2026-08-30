package com.beautyclinic.controller;

import com.beautyclinic.core.exception.EmailAlreadyExistsException;
import com.beautyclinic.core.exception.PasswordMismatchException;
import com.beautyclinic.dto.UserRegistrationDto;
import com.beautyclinic.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserRegistrationController {
    private final UserAccountService userAccountService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

        @PostMapping("/register")
        public String registerCustomer(@Valid @ModelAttribute("user") UserRegistrationDto dto, BindingResult result){
            if(result.hasErrors()){
                return "register";
            }

            try{
                userAccountService.registerCustomer(dto);
                return "redirect:/login?registered";


            }catch (PasswordMismatchException | EmailAlreadyExistsException ex) {
                result.reject("registration", ex.getMessage());
                return "register";
            }

        }
    }
