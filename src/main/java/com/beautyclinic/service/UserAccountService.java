package com.beautyclinic.service;

import com.beautyclinic.core.exception.EmailAlreadyExistsException;
import com.beautyclinic.core.exception.PasswordMismatchException;
import com.beautyclinic.dto.CustomerOptionDto;
import com.beautyclinic.dto.UserRegistrationDto;
import com.beautyclinic.mapper.UserAccountMapper;
import com.beautyclinic.model.Role;
import com.beautyclinic.model.UserAccount;
import com.beautyclinic.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public UserAccount registerCustomer(UserRegistrationDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException(
                    "Οι κωδικοί δεν ταιριάζουν"
            );
        }
        if (userAccountRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException(
                        "Αυτό το email υπάρχει ήδη"
                );
            }

        UserAccount userAccount = userAccountMapper.toEntity(dto);
        userAccount.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userAccount.setRole(Role.CUSTOMER);
        userAccount.setActive(true);


        return userAccountRepository.save(userAccount);
    }

    public List<CustomerOptionDto> getActiveCustomers() {
        return userAccountRepository
                .findByRoleAndActiveTrueOrderByFullNameAsc(Role.CUSTOMER)
                .stream()
                .map(this::toCustomerOptionDto)
                .toList();
    }

    private CustomerOptionDto toCustomerOptionDto(UserAccount customer) {
        return new CustomerOptionDto(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail()
        );
    }

}
