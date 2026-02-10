package com.upm.institutional.controller;

import com.upm.institutional.service.AcademicOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/academic-offer")
@RequiredArgsConstructor
public class AdminAcademicOfferController {

    private final AcademicOfferService academicOfferService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo para cargar.");
            return "redirect:/academic-offer";
        }

        try {
            academicOfferService.saveFromExcel(file);
            redirectAttributes.addFlashAttribute("success", "Oferta académica actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el archivo: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/academic-offer";
    }
}
