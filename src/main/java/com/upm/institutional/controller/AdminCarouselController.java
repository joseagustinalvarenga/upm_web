package com.upm.institutional.controller;

import com.upm.institutional.service.CarouselImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/carousel")
@RequiredArgsConstructor
public class AdminCarouselController {

    private final CarouselImageService carouselImageService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("images", carouselImageService.findAll());
        return "admin/carousel/list";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        try {
            carouselImageService.uploadImage(imageFile);
            redirectAttributes.addFlashAttribute("success", "Imagen subida correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al subir la imagen: " + e.getMessage());
        }
        return "redirect:/admin/carousel";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carouselImageService.deleteImage(id);
            redirectAttributes.addFlashAttribute("success", "Imagen eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la imagen: " + e.getMessage());
        }
        return "redirect:/admin/carousel";
    }
}
