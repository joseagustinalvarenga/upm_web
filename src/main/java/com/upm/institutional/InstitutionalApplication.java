package com.upm.institutional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstitutionalApplication {

	public static void main(String[] args) {
		System.out.println("====== RAILWAY CONFIGURATION DEBUG ======");
		String dbUrl = System.getenv("DATABASE_URL");

		if (dbUrl == null || dbUrl.trim().isEmpty()) {
			System.out.println("CRITICAL: DATABASE_URL is NULL or EMPTY");
			throw new RuntimeException(
					"ANTIGRAVITY DEBUG: DATABASE_URL environment variable is MISSING. Please check Railway Service Variables.");
		}

		System.out.println("DATABASE_URL found. Length: " + dbUrl.length());
		String maskedUrl = dbUrl.length() > 20
				? dbUrl.substring(0, 15) + "..." + dbUrl.substring(dbUrl.length() - 5)
				: "TOO_SHORT";
		System.out.println("DATABASE_URL (masked): " + maskedUrl);

		// Railway fix: Ensure JDBC prefix and correct protocol
		String finalUrl = dbUrl;
		if (!finalUrl.startsWith("jdbc:")) {
			if (finalUrl.startsWith("postgres://")) {
				finalUrl = finalUrl.replace("postgres://", "jdbc:postgresql://");
			} else if (finalUrl.startsWith("postgresql://")) {
				finalUrl = finalUrl.replace("postgresql://", "jdbc:postgresql://");
			}
		}

		System.out.println("Final SPRING_DATASOURCE_URL: "
				+ (finalUrl.length() > 20 ? finalUrl.substring(0, 15) + "..." : finalUrl));

		if (finalUrl.equals("jdbc:postgresql://:/")) {
			throw new RuntimeException(
					"ANTIGRAVITY DEBUG: Generated URL is invalid (jdbc:postgresql://:/). Original was: " + maskedUrl);
		}

		System.setProperty("SPRING_DATASOURCE_URL", finalUrl);
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
