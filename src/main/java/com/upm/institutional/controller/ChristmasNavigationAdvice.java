package com.upm.institutional.controller;

import com.upm.institutional.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(assignableTypes = {PublicController.class, ChristmasController.class})
@RequiredArgsConstructor
public class ChristmasNavigationAdvice {
    private final SiteSettingService settings;
    @ModelAttribute("christmasEnabled")
    public boolean enabled() { return "true".equals(settings.getSetting("christmas_enabled", "false")); }
}
