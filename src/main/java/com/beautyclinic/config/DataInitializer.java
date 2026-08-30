package com.beautyclinic.config;

import com.beautyclinic.model.Role;
import com.beautyclinic.model.Treatment;
import com.beautyclinic.model.UserAccount;
import com.beautyclinic.repository.TreatmentRepository;
import com.beautyclinic.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final TreatmentRepository treatmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String aestheticianPassword;

    public DataInitializer(
            TreatmentRepository treatmentRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${DEV_ADMIN_PASSWORD}") String adminPassword,
            @Value("${DEV_AESTHETICIAN_PASSWORD}") String aestheticianPassword
    ) {
        this.treatmentRepository = treatmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.aestheticianPassword = aestheticianPassword;
    }

    @Override
    public void run(String... args) {

        createTreatmentsIfMissing();

        createUserIfMissing(
                "Development Administrator",
                "admin@beautyclinic.local",
                adminPassword,
                Role.ADMIN
        );

        createUserIfMissing(
                "Development Aesthetician",
                "aesthetician@beautyclinic.local",
                aestheticianPassword,
                Role.AESTHETICIAN
        );
    }

    private void createTreatmentsIfMissing() {

        if (treatmentRepository.count() != 0) {
            return;
        }

        Treatment laser = new Treatment();
        laser.setName("Laser Hair Removal");
        laser.setDurationMinutes(60);
        laser.setPrice(50.0);
        laser.setActive(true);

        Treatment facial = new Treatment();
        facial.setName("Facial Cleaning");
        facial.setDurationMinutes(45);
        facial.setPrice(40.0);
        facial.setActive(true);

        Treatment hydrafacial = new Treatment();
        hydrafacial.setName("Hydrafacial");
        hydrafacial.setDurationMinutes(60);
        hydrafacial.setPrice(70.0);
        hydrafacial.setActive(true);

        Treatment microneedling = new Treatment();
        microneedling.setName("Microneedling");
        microneedling.setDurationMinutes(60);
        microneedling.setPrice(80.0);
        microneedling.setActive(true);

        treatmentRepository.save(laser);
        treatmentRepository.save(facial);
        treatmentRepository.save(hydrafacial);
        treatmentRepository.save(microneedling);
    }

    private void createUserIfMissing(
            String fullName,
            String email,
            String rawPassword,
            Role role
    ) {
        if (userAccountRepository.findByEmail(email).isPresent()) {
            return;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setFullName(fullName);
        userAccount.setEmail(email);
        userAccount.setPasswordHash(passwordEncoder.encode(rawPassword));
        userAccount.setRole(role);
        userAccount.setActive(true);

        userAccountRepository.save(userAccount);
    }
}