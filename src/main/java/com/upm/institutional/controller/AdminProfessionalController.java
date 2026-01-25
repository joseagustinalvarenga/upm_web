package com.upm.institutional.controller;

import com.upm.institutional.model.Professional;
import com.upm.institutional.service.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/professionals")
@RequiredArgsConstructor
public class AdminProfessionalController {

    private final ProfessionalService professionalService;

    @GetMapping
    public String list(Model model, Pageable pageable) {
        Page<Professional> professionals = professionalService.findAll(pageable);
        model.addAttribute("professionals", professionals);
        return "admin/professionals/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("professional", new Professional());
        return "admin/professionals/form";
    }

    @PostMapping
    public String save(@ModelAttribute Professional professional, RedirectAttributes redirectAttributes) {
        professionalService.save(professional);
        redirectAttributes.addFlashAttribute("success", "Profesional guardado correctamente.");
        return "redirect:/admin/professionals";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Professional professional = professionalService.findById(id);
        model.addAttribute("professional", professional);
        return "admin/professionals/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Professional professional,
            RedirectAttributes redirectAttributes) {
        professional.setId(id);
        professionalService.save(professional);
        redirectAttributes.addFlashAttribute("success", "Profesional actualizado correctamente.");
        return "redirect:/admin/professionals";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        professionalService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Profesional eliminado correctamente.");
        return "redirect:/admin/professionals";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "admin/professionals/upload";
    }

    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo.");
            return "redirect:/admin/professionals/upload";
        }

        try {
            professionalService.importFromExcel(file);
            redirectAttributes.addFlashAttribute("success", "Profesionales importados correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al importar el archivo: " + e.getMessage());
        }

        return "redirect:/admin/professionals";
    }
}
