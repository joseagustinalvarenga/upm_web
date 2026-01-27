package com.upm.institutional.controller;

import com.upm.institutional.model.Feature;
import com.upm.institutional.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/features")
@RequiredArgsConstructor
public class AdminFeatureController {

    private final FeatureService featureService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("features", featureService.findAllFeatures());
        return "admin/features/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("feature", new Feature());
        return "admin/features/form";
    }

    @PostMapping
    public String save(@ModelAttribute Feature feature, RedirectAttributes redirectAttributes) {
        featureService.saveFeature(feature);
        redirectAttributes.addFlashAttribute("success", "Cuadro guardado correctamente.");
        return "redirect:/admin/features";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("feature", featureService.findById(id));
        return "admin/features/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Feature feature,
            RedirectAttributes redirectAttributes) {
        feature.setId(id);
        featureService.saveFeature(feature);
        redirectAttributes.addFlashAttribute("success", "Cuadro actualizado correctamente.");
        return "redirect:/admin/features";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        featureService.deleteFeature(id);
        redirectAttributes.addFlashAttribute("success", "Cuadro eliminado correctamente.");
        return "redirect:/admin/features";
    }
}
