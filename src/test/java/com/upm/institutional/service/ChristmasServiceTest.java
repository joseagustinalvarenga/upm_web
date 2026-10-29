package com.upm.institutional.service;

import com.upm.institutional.repository.ChristmasVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChristmasServiceTest {
    private ChristmasService service;
    private Map<String, String> data;
    @BeforeEach void setup() {
        data = new HashMap<>();
        SiteSettingService settings = mock(SiteSettingService.class);
        when(settings.getSetting(anyString(), anyString())).thenAnswer(i -> data.getOrDefault(i.getArgument(0), i.getArgument(1)));
        doAnswer(i -> { data.put(i.getArgument(0), i.getArgument(1)); return null; }).when(settings).updateSetting(anyString(), anyString());
        service = new ChristmasService(settings, new BCryptPasswordEncoder(4), mock(ChristmasVideoRepository.class));
    }
    @Test void startsDisabledAndRequiresCodeToActivate() {
        assertFalse(service.enabled());
        assertThrows(IllegalArgumentException.class, () -> service.configure(true, ""));
        assertThrows(IllegalArgumentException.class, () -> service.configure(true, "123"));
    }
    @Test void codeIsHashedAndSwitchingOffAndOnRevokesAccess() {
        service.configure(true, "Navidad2026");
        assertNotEquals("Navidad2026", data.get("christmas_code_hash"));
        var session = new MockHttpSession();
        assertTrue(service.unlock("127.0.0.1", "Navidad2026", session));
        assertTrue(service.authorized(session));
        service.configure(false, "");
        assertFalse(service.authorized(session));
        service.configure(true, "");
        assertFalse(service.authorized(session));
    }
    @Test void rotatingCodeAndLeavingRevokeAccess() {
        service.configure(true, "Navidad2026");
        var session = new MockHttpSession();
        service.unlock("ip", "Navidad2026", session);
        service.configure(true, "OtroCodigo");
        assertFalse(service.authorized(session));
        assertFalse(service.unlock("ip", "Navidad2026", session));
        assertTrue(service.unlock("ip", "OtroCodigo", session));
        service.leave(session);
        assertFalse(service.authorized(session));
    }
    @Test void fiveFailuresBlockEvenWithNewSession() {
        service.configure(true, "Navidad2026");
        for (int i = 0; i < 5; i++) assertFalse(service.unlock("ip", "incorrecto", new MockHttpSession()));
        assertFalse(service.unlock("ip", "Navidad2026", new MockHttpSession()));
        assertTrue(service.unlock("another-ip", "Navidad2026", new MockHttpSession()));
    }
    @Test void linksRejectExecutableSchemesAndImpersonatedDomains() {
        assertEquals("YouTube", ChristmasService.platform("https://youtu.be/abc"));
        assertEquals("Instagram", ChristmasService.platform("https://www.instagram.com/reel/abc"));
        assertEquals("TikTok", ChristmasService.platform("https://vm.tiktok.com/abc"));
        for (String url : new String[]{"javascript:alert(1)", "https://youtube.com.attacker.org/video", "https://youtube.com@attacker.org/video", "http://youtube.com/video", "https://youtube.com:8080/video"})
            assertEquals("", ChristmasService.platform(url));
    }
}
