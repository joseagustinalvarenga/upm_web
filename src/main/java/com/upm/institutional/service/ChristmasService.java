package com.upm.institutional.service;

import com.upm.institutional.model.ChristmasVideo;
import com.upm.institutional.repository.ChristmasVideoRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChristmasService {
    private final SiteSettingService settings;
    private final PasswordEncoder encoder;
    private final ChristmasVideoRepository videos;
    private static final String GRANT = "christmas.grant";
    private final Map<String, Attempts> attempts = new LinkedHashMap<>();
    private record Attempts(int count, Instant until) {}
    private record Grant(String version, Instant until) implements java.io.Serializable {}

    public boolean enabled() { return "true".equals(settings.getSetting("christmas_enabled", "false")); }
    public boolean hasCode() { return !settings.getSetting("christmas_code_hash", "").isBlank(); }

    @Transactional
    public void configure(boolean enabled, String code) {
        if (!code.isBlank() && (code.length() < 6 || code.length() > 64))
            throw new IllegalArgumentException("El código debe tener entre 6 y 64 caracteres.");
        if (enabled && code.isBlank() && !hasCode())
            throw new IllegalArgumentException("Definí un código antes de activar el especial.");
        if (!code.isBlank()) settings.updateSetting("christmas_code_hash", encoder.encode(code));
        settings.updateSetting("christmas_enabled", Boolean.toString(enabled));
        // Every configuration change revokes existing student sessions, including off/on cycles.
        settings.updateSetting("christmas_version", UUID.randomUUID().toString());
    }

    public boolean authorized(HttpSession session) {
        if (!enabled() || session == null) return false;
        Object value = session.getAttribute(GRANT);
        return value instanceof Grant grant && grant.until().isAfter(Instant.now())
                && grant.version().equals(settings.getSetting("christmas_version", ""));
    }

    public synchronized boolean unlock(String address, String code, HttpSession session) {
        Instant now = Instant.now();
        attempts.entrySet().removeIf(e -> !e.getValue().until().isAfter(now));
        Attempts prior = attempts.get(address);
        if (prior != null && prior.count() >= 5) return false;
        if (!enabled()) return false;
        String hash = settings.getSetting("christmas_code_hash", "");
        if (code.length() <= 64 && !hash.isBlank() && encoder.matches(code, hash)) {
            session.setAttribute(GRANT, new Grant(settings.getSetting("christmas_version", ""), now.plusSeconds(7200)));
            attempts.remove(address);
            return true;
        }
        if (attempts.size() >= 10000 && prior == null) return false;
        attempts.put(address, new Attempts(prior == null ? 1 : prior.count() + 1,
                prior == null ? now.plusSeconds(900) : prior.until()));
        return false;
    }

    public void leave(HttpSession session) { if (session != null) session.removeAttribute(GRANT); }
    public List<ChristmasVideo> published() { return videos.findByVisibleTrueOrderByDisplayOrderAscIdAsc(); }
    public List<ChristmasVideo> all() { return videos.findAllByOrderByDisplayOrderAscIdAsc(); }

    public static String platform(String url) {
        try {
            URI uri = URI.create(url);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getUserInfo() != null || uri.getPort() != -1) return "";
            String host = Objects.toString(uri.getHost(), "").toLowerCase(Locale.ROOT);
            if (host.equals("youtu.be") || host.equals("youtube.com") || host.endsWith(".youtube.com")) return "YouTube";
            if (host.equals("instagram.com") || host.endsWith(".instagram.com")) return "Instagram";
            if (host.equals("tiktok.com") || host.endsWith(".tiktok.com")) return "TikTok";
            if (host.equals("facebook.com") || host.endsWith(".facebook.com") || host.equals("fb.watch")) return "Facebook";
        } catch (IllegalArgumentException ignored) { }
        return "";
    }
}
