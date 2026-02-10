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
			if (admin == null) {
				admin = new com.upm.institutional.model.User();
				admin.setUsername("admin");
				// Email field not present in User entity, so we skip it
				admin.setRole(com.upm.institutional.model.Role.ADMIN);
				admin.setPasswordHash(passwordEncoder.encode("admin1234"));
				userRepository.save(admin);
				System.out.println("✅ ADMIN USER CREATED: admin / admin1234");
			} else {
				// Do not reset password if user already exists in production
				System.out.println("✅ ADMIN USER FOUND (Password unchanged)");
			}
		};
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner initFeatures(
			com.upm.institutional.repository.FeatureRepository featureRepository) {
		return args -> {
			try {
				if (featureRepository.count() == 0) {
					com.upm.institutional.model.Feature f1 = new com.upm.institutional.model.Feature();
					f1.setTitle("Excelencia Académica");
					f1.setIcon("bi-award");
					f1.setDescription("Programas acreditados internacionalmente y cuerpo docente de primer nivel.");
					f1.setDisplayOrder(1);
					featureRepository.save(f1);

					com.upm.institutional.model.Feature f2 = new com.upm.institutional.model.Feature();
					f2.setTitle("Innovación Tecnológica");
					f2.setIcon("bi-laptop");
					f2.setDescription("Campus inteligente y laboratorios equipados con última tecnología.");
					f2.setDisplayOrder(2);
					featureRepository.save(f2);

					com.upm.institutional.model.Feature f3 = new com.upm.institutional.model.Feature();
					f3.setTitle("Visión Global");
					f3.setIcon("bi-globe-americas");
					f3.setDescription("Convenios de intercambio con universidades líderes en todo el mundo.");
					f3.setDisplayOrder(3);
					featureRepository.save(f3);

					System.out.println("✅ INITIAL FEATURES CREATED");
				}
			} catch (Exception e) {
				System.err.println(
						"⚠️ WARNING: Could not initialize features. Table might be missing or DB locked. Error: "
								+ e.getMessage());
			}
		};
	}

}
