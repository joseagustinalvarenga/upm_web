package com.upm.institutional.controller;

import com.upm.institutional.dto.NewsDto;
import com.upm.institutional.model.News;
import com.upm.institutional.model.Status;
import com.upm.institutional.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping
    public String list(Model model, Pageable pageable) {
        Page<News> news = newsService.findAllNews(pageable);
        model.addAttribute("news", news);
        return "admin/news/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("newsDto", new NewsDto());
        return "admin/news/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute NewsDto newsDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/news/form";
        }
        newsService.saveNews(newsDto);
        redirectAttributes.addFlashAttribute("success", "Noticia guardada correctamente.");
        return "redirect:/admin/news";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        News news = newsService.findById(id);
        model.addAttribute("newsDto", newsService.toDto(news));
        return "admin/news/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute NewsDto newsDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/news/form";
        }
        newsDto.setId(id);
        newsService.saveNews(newsDto);
        redirectAttributes.addFlashAttribute("success", "Noticia actualizada correctamente.");
        return "redirect:/admin/news";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.deleteNews(id);
        redirectAttributes.addFlashAttribute("success", "Noticia eliminada correctamente.");
        return "redirect:/admin/news";
    }

    @PostMapping("/{id}/publish")
    public String publish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.changeStatus(id, Status.PUBLISHED);
        redirectAttributes.addFlashAttribute("success", "Noticia publicada.");
        return "redirect:/admin/news";
    }

    @PostMapping("/{id}/unpublish")
    public String unpublish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.changeStatus(id, Status.DRAFT);
        redirectAttributes.addFlashAttribute("success", "Noticia despublicada.");
        return "redirect:/admin/news";
    }
}
