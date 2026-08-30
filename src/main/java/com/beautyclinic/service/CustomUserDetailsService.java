package com.beautyclinic.service;

import com.beautyclinic.model.UserAccount;
import com.beautyclinic.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Δεν βρέθηκε χρήστης με αυτό το email"
                        )
                );
        return User.builder()
                .username(userAccount.getEmail())
                .password(userAccount.getPasswordHash())
                .roles(userAccount.getRole().name())
                .disabled(!userAccount.isActive())
                .build();
    }
}
