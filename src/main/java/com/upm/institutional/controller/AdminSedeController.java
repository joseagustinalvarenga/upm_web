package com.upm.institutional.controller;

import com.upm.institutional.model.Sede;
import com.upm.institutional.service.SedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/sedes")
@RequiredArgsConstructor
public class AdminSedeController {

    private final SedeService sedeService;

    @GetMapping
    public String listSedes(Model model) {
        model.addAttribute("sedes", sedeService.getAllSedes());
        return "admin/sedes/list";
    }

    @GetMapping("/new")
    public String newSedeForm(Model model) {
        model.addAttribute("sede", new Sede());
        return "admin/sedes/form";
    }

    @PostMapping
    public String saveSede(@ModelAttribute Sede sede, RedirectAttributes redirectAttributes) {
        sedeService.saveSede(sede);
        redirectAttributes.addFlashAttribute("success", "Sede guardada correctamente.");
        return "redirect:/admin/sedes";
    }

    @GetMapping("/edit/{id}")
    public String editSedeForm(@PathVariable Long id, Model model) {
        Sede sede = sedeService.getSedeById(id);
        model.addAttribute("sede", sede);
        return "admin/sedes/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteSede(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        sedeService.deleteSede(id);
        redirectAttributes.addFlashAttribute("success", "Sede eliminada correctamente.");
        return "redirect:/admin/sedes";
    }
}
