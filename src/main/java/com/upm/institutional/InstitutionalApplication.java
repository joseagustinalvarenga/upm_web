package com.upm.institutional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstitutionalApplication {

	public static void main(String[] args) {
		System.out.println("====== RAILWAY CONFIGURATION DEBUG ======");
		String dbUrl = System.getenv("DATABASE_URL");

		if (dbUrl != null && !dbUrl.isEmpty()) {
			System.out.println("DATABASE_URL found. Length: " + dbUrl.length());
			// Masking for security: show protocol and end of string
			String maskedUrl = dbUrl.length() > 20
					? dbUrl.substring(0, 15) + "..." + dbUrl.substring(dbUrl.length() - 5)
					: "TOO_SHORT";
			System.out.println("DATABASE_URL (masked): " + maskedUrl);

			// Railway fix: Ensure JDBC prefix
			if (!dbUrl.startsWith("jdbc:")) {
				System.out.println("Fixing URL: missing jdbc: prefix");
				if (dbUrl.startsWith("postgres://")) {
					dbUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
				} else if (dbUrl.startsWith("postgresql://")) {
					dbUrl = dbUrl.replace("postgresql://", "jdbc:postgresql://");
				}
				System.setProperty("SPRING_DATASOURCE_URL", dbUrl);
				System.out.println("Set SPRING_DATASOURCE_URL to corrected value.");
			} else {
				System.out.println("URL already has jdbc: prefix.");
				System.setProperty("SPRING_DATASOURCE_URL", dbUrl);
			}
		} else {
			System.out.println("CRITICAL: DATABASE_URL environment variable is NULL or EMPTY!");
		}
		System.out.println("====== END RAILWAY CONFIGURATION DEBUG ======");

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
