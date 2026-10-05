package com.upm.institutional.controller;

import com.upm.institutional.model.ApplicationStatus;
import com.upm.institutional.model.Professional;
import com.upm.institutional.model.ProfessionalApplication;
import com.upm.institutional.service.ProfessionalApplicationService;
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
    private final ProfessionalApplicationService applicationService;

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

    @GetMapping("/requests")
    public String listRequests(
            @RequestParam(required = false) ApplicationStatus status,
            Model model,
            Pageable pageable) {
        Page<ProfessionalApplication> applications;
        if (status != null) {
            applications = applicationService.findByStatus(status, pageable);
        } else {
            applications = applicationService.findAll(pageable);
        }
        model.addAttribute("applications", applications);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("pendingCount", applicationService.countPending());
        return "admin/professionals/requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        applicationService.approveApplication(id);
        redirectAttributes.addFlashAttribute("success", "Solicitud aprobada correctamente e incorporada al directorio de profesionales.");
        return "redirect:/admin/professionals/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        applicationService.rejectApplication(id);
        redirectAttributes.addFlashAttribute("success", "Solicitud marcada como rechazada.");
        return "redirect:/admin/professionals/requests";
    }

    @PostMapping("/requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        applicationService.deleteApplication(id);
        redirectAttributes.addFlashAttribute("success", "Solicitud eliminada correctamente.");
        return "redirect:/admin/professionals/requests";
    }
}
