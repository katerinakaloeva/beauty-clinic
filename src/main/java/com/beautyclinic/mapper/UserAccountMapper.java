package com.beautyclinic.mapper;

import com.beautyclinic.dto.UserRegistrationDto;
import com.beautyclinic.model.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

    public UserAccount toEntity(UserRegistrationDto dto) {
        UserAccount userAccount = new UserAccount();

        userAccount.setFullName(dto.getFullName());
        userAccount.setEmail(dto.getEmail());

        return userAccount;
    }
}