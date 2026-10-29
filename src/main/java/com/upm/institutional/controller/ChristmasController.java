package com.upm.institutional.controller;

import com.upm.institutional.service.ChristmasService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/navidad")
@RequiredArgsConstructor
public class ChristmasController {
    private final ChristmasService christmas;

    @ModelAttribute
    public void noCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, private");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("X-Robots-Tag", "noindex, nofollow");
    }

    @GetMapping
    public String entry(HttpServletRequest request, HttpServletResponse response) {
        if (!christmas.enabled()) { response.setStatus(404); return "christmas/unavailable"; }
        return christmas.authorized(request.getSession(false)) ? "redirect:/navidad/galeria" : "christmas/access";
    }

    @PostMapping("/ingresar")
    public String unlock(@RequestParam(defaultValue = "") String code, HttpServletRequest request,
                         RedirectAttributes flash) {
        if (!christmas.enabled()) return "redirect:/navidad";
        if (christmas.unlock(request.getRemoteAddr(), code, request.getSession())) {
            request.changeSessionId();
            return "redirect:/navidad/galeria";
        }
        flash.addFlashAttribute("error", "No pudimos validar el código. Revisalo; tras 5 intentos fallidos esperá 15 minutos.");
        return "redirect:/navidad";
    }

    @GetMapping("/galeria")
    public String gallery(HttpServletRequest request, Model model) {
        if (!christmas.authorized(request.getSession(false))) return "redirect:/navidad";
        var videos = christmas.published();
        model.addAttribute("videos", videos);
        model.addAttribute("categories", videos.stream().map(v -> v.getCategory()).distinct().toList());
        return "christmas/gallery";
    }

    @PostMapping("/salir")
    public String leave(HttpServletRequest request) {
        christmas.leave(request.getSession(false));
        return "redirect:/navidad";
    }
}
