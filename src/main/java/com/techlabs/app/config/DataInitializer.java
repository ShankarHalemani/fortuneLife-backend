package com.techlabs.app.config;

import com.techlabs.app.entity.Role;
import com.techlabs.app.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List<String> requiredRoles = List.of("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER", "ROLE_AGENT");

        for (String roleName : requiredRoles) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                Role role = new Role();
                role.setRoleName(roleName);
                roleRepository.save(role);
                logger.info("Created role: {}", roleName);
            }
        }
    }
}
