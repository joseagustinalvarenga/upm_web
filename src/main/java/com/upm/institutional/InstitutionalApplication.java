package com.upm.institutional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstitutionalApplication {

	public static void main(String[] args) {
		// Fix Railway/Heroku JDBC URL format (postgres:// -> jdbc:postgresql://)
		String dbUrl = System.getenv("DATABASE_URL");
		System.out.println("🔍 DEBUG: DATABASE_URL ENV = " + (dbUrl != null ? "FOUND" : "NOT FOUND"));

		if (dbUrl != null && !dbUrl.isEmpty()) {
			try {
				// URI no acepta jdbc: al inicio para parsear componentes
				String cleanDbUrl = dbUrl.replace("jdbc:", "");
				java.net.URI uri = new java.net.URI(cleanDbUrl);

				String host = uri.getHost();
				int port = uri.getPort();
				String path = uri.getPath();
				String query = uri.getQuery();

				String jdbcUrl = "jdbc:postgresql://" + host + (port != -1 ? ":" + port : "") + path;
				if (query != null && !query.isEmpty()) {
					jdbcUrl += "?" + query;
				}

				System.setProperty("SPRING_DATASOURCE_URL", jdbcUrl);

				if (uri.getUserInfo() != null) {
					String[] userInfo = uri.getUserInfo().split(":");
					System.setProperty("SPRING_DATASOURCE_USERNAME", userInfo[0]);
					if (userInfo.length > 1) {
						System.setProperty("SPRING_DATASOURCE_PASSWORD", userInfo[1]);
					}
				}

				System.out.println("✅ JDBC URL DECOMPOSED: " + jdbcUrl);
				System.out.println("✅ USERNAME EXTRACTED: " + (uri.getUserInfo() != null ? "YES" : "NO"));

			} catch (Exception e) {
				System.err.println("❌ Error parsing DATABASE_URL: " + e.getMessage());
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
