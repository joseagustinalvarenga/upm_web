package com.upm.institutional.controller;

import com.upm.institutional.model.ChristmasVideo;
import com.upm.institutional.repository.ChristmasVideoRepository;
import com.upm.institutional.service.ChristmasService;
import com.upm.institutional.util.ImageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Base64;

@Controller
@RequestMapping("/admin/navidad")
@RequiredArgsConstructor
public class AdminChristmasController {
    private final ChristmasService christmas;
    private final ChristmasVideoRepository videos;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("enabled", christmas.enabled());
        model.addAttribute("hasCode", christmas.hasCode());
        model.addAttribute("videos", christmas.all());
        return "admin/christmas/list";
    }

    @PostMapping("/configuracion")
    public String configure(@RequestParam(defaultValue = "false") boolean enabled,
                            @RequestParam(defaultValue = "") String code, RedirectAttributes flash) {
        try {
            christmas.configure(enabled, code);
            flash.addFlashAttribute("success", enabled ? "Especial activado. Los alumnos pueden ingresar con el código." : "Especial desactivado. Todos los accesos quedaron bloqueados.");
        } catch (IllegalArgumentException e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/navidad";
    }

    @GetMapping("/videos/nuevo")
    public String create(Model model) {
        model.addAttribute("video", new ChristmasVideo());
        return "admin/christmas/form";
    }

    private ChristmasVideo require(Long id) {
        return videos.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/videos/{id}/editar")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("video", require(id));
        return "admin/christmas/form";
    }

    @PostMapping("/videos")
    public String save(@Valid @ModelAttribute("video") ChristmasVideo video, BindingResult errors,
                       @RequestParam(required = false) MultipartFile imageFile,
                       @RequestParam(defaultValue = "false") boolean removeCover,
                       @RequestParam(defaultValue = "false") boolean visible, RedirectAttributes flash) {
        video.setVisible(visible);
        ChristmasVideo previous = video.getId() == null ? null : require(video.getId());
        // Cover data always comes from a validated upload or the existing record.
        video.setCover(previous == null || removeCover ? null : previous.getCover());
        if (ChristmasService.platform(video.getVideoUrl()).isBlank())
            errors.rejectValue("videoUrl", "invalid", "Usá un enlace HTTPS de YouTube, Instagram, TikTok o Facebook.");
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                if (imageFile.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException();
                byte[] image = ImageUtils.compressAndResizeImage(imageFile.getBytes(), 960, .82f);
                if (image == null) throw new IllegalArgumentException();
                video.setCover("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(image));
            } catch (Exception e) { errors.reject("cover", "La portada debe ser una imagen JPG o PNG válida de hasta 5 MB."); }
        }
        if (errors.hasErrors()) return "admin/christmas/form";
        videos.save(video);
        flash.addFlashAttribute("success", "Video guardado correctamente.");
        return "redirect:/admin/navidad";
    }

    @PostMapping("/videos/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        videos.delete(require(id));
        flash.addFlashAttribute("success", "Video eliminado.");
        return "redirect:/admin/navidad";
    }
}
