package com.upm.institutional.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import static org.junit.jupiter.api.Assertions.*;

class ChristmasMigrationTest {
    @Test void migrationCreatesGalleryAndDefaultsToDisabled() throws Exception {
        var db = new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseType.H2).build();
        try (var connection = db.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/migration/V4__create_site_settings.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/migration/V20__christmas_special.sql"));
            try (var result = connection.createStatement().executeQuery("SELECT setting_value FROM site_settings WHERE setting_key='christmas_enabled'")) {
                assertTrue(result.next()); assertEquals("false", result.getString(1));
            }
            connection.createStatement().execute("INSERT INTO christmas_videos(title, category, video_url) VALUES ('Prueba', 'Cocina', 'https://youtu.be/example')");
            try (var result = connection.createStatement().executeQuery("SELECT visible, display_order FROM christmas_videos")) {
                assertTrue(result.next()); assertTrue(result.getBoolean(1)); assertEquals(0, result.getInt(2));
            }
        } finally { db.shutdown(); }
    }
}
