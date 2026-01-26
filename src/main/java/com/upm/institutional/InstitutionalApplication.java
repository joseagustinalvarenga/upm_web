package com.upm.institutional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstitutionalApplication {

	public static void main(String[] args) {
		String dbUrl = System.getenv("DATABASE_URL");
		if (dbUrl != null && !dbUrl.isEmpty()) {
			// Railway fix: Ensure JDBC prefix
			if (!dbUrl.startsWith("jdbc:")) {
				if (dbUrl.startsWith("postgres://")) {
					dbUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
				} else if (dbUrl.startsWith("postgresql://")) {
					dbUrl = dbUrl.replace("postgresql://", "jdbc:postgresql://");
				}
				System.setProperty("SPRING_DATASOURCE_URL", dbUrl);
			}
		}
		SpringApplication.run(InstitutionalApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner initAdmin(
			com.upm.institutional.repository.UserRepository userRepository,
			org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
		return args -> {
			com.upm.institutional.model.User admin = userRepository.findByUsername("admin").orElse(null);
			if (admin != null) {
				admin.setPasswordHash(passwordEncoder.encode("admin1234"));
				userRepository.save(admin);
				System.out.println("ADMIN PASSWORD RESET TO: admin1234");
			} else {
				System.out.println("ADMIN USER NOT FOUND IN DB");
			}
		};
	}

}
