package com.beautyclinic.controller.api;

import com.beautyclinic.dto.CustomerOptionDto;
import com.beautyclinic.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff/customers")
@RequiredArgsConstructor
public class StaffCustomerApiController {

    private final UserAccountService userAccountService;

    @GetMapping
    public List<CustomerOptionDto> getCustomers() {
        return userAccountService.getActiveCustomers();
    }
}
