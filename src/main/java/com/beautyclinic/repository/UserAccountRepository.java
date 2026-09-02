package com.beautyclinic.repository;

import com.beautyclinic.model.Role;
import com.beautyclinic.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findFirstByRoleAndActiveTrue(Role role);
}