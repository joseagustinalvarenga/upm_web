package com.upm.institutional.controller;

import com.upm.institutional.model.ContactMessage;
import com.upm.institutional.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/messages")
@RequiredArgsConstructor
public class AdminMessageController {

    private final ContactService contactService;

    @GetMapping
    public String list(Model model, Pageable pageable) {
        Page<ContactMessage> messages = contactService.findAllMessages(pageable);
        model.addAttribute("messages", messages);
        return "admin/messages/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ContactMessage message = contactService.findById(id);
        model.addAttribute("message", message);
        return "admin/messages/detail";
    }

    @PostMapping("/{id}/mark-read")
    public String markRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.markAsRead(id);
        redirectAttributes.addFlashAttribute("success", "Mensaje marcado como leído.");
        return "redirect:/admin/messages";
    }

    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.archiveMessage(id);
        redirectAttributes.addFlashAttribute("success", "Mensaje archivado.");
        return "redirect:/admin/messages";
    }
}
