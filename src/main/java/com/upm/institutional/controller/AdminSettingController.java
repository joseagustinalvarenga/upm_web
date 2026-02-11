package com.upm.institutional.controller;

import com.upm.institutional.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingController {

    private final SiteSettingService siteSettingService;

    @GetMapping
    public String settingsForm(Model model) {
        String currentHeroImage = siteSettingService.getSetting("home_hero_image_url",
                "/images/university_campus_hero.png");
        model.addAttribute("heroImageUrl", currentHeroImage);

        String carouselImages = siteSettingService.getSetting("home_carousel_images", "");
        model.addAttribute("carouselImages", carouselImages);

        return "admin/settings/form";
    }

    @PostMapping
    public String updateSettings(@RequestParam("heroImageUrl") String heroImageUrl,
            @RequestParam(value = "carouselImages", required = false) String carouselImages,
            RedirectAttributes redirectAttributes) {
        siteSettingService.updateSetting("home_hero_image_url", heroImageUrl);

        if (carouselImages != null) {
            siteSettingService.updateSetting("home_carousel_images", carouselImages);
        }

        redirectAttributes.addFlashAttribute("success", "Configuración actualizada correctamente.");
        return "redirect:/admin/settings";
    }
}
