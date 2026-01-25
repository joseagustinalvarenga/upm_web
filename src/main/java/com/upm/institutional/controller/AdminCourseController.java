package com.upm.institutional.controller;

import com.upm.institutional.dto.CourseDto;
import com.upm.institutional.model.Course;
import com.upm.institutional.model.Status;
import com.upm.institutional.service.CourseService;
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
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping
    public String list(Model model, Pageable pageable) {
        Page<Course> courses = courseService.findAllCourses(pageable);
        model.addAttribute("courses", courses);
        return "admin/courses/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("courseDto", new CourseDto());
        return "admin/courses/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute CourseDto courseDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/courses/form";
        }
        courseService.saveCourse(courseDto);
        redirectAttributes.addFlashAttribute("success", "Curso guardado correctamente.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("courseDto", courseService.toDto(course));
        return "admin/courses/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute CourseDto courseDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/courses/form";
        }
        courseDto.setId(id); // Ensure ID matches
        courseService.saveCourse(courseDto);
        redirectAttributes.addFlashAttribute("success", "Curso actualizado correctamente.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.deleteCourse(id);
        redirectAttributes.addFlashAttribute("success", "Curso eliminado correctamente.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/{id}/publish")
    public String publish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.changeStatus(id, Status.PUBLISHED);
        redirectAttributes.addFlashAttribute("success", "Curso publicado.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/{id}/unpublish")
    public String unpublish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.changeStatus(id, Status.DRAFT);
        redirectAttributes.addFlashAttribute("success", "Curso despublicado.");
        return "redirect:/admin/courses";
    }
}
