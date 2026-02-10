package com.upm.institutional.controller;

import com.upm.institutional.dto.ContactForm;
import com.upm.institutional.model.Course;
import com.upm.institutional.model.News;
import com.upm.institutional.service.ContactService;
import com.upm.institutional.service.CourseService;
import com.upm.institutional.service.NewsService;
import com.upm.institutional.service.AcademicOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final CourseService courseService;
    private final NewsService newsService;
    private final ContactService contactService;
    private final com.upm.institutional.service.SiteSettingService siteSettingService;
    private final com.upm.institutional.service.SedeService sedeService;
    private final com.upm.institutional.service.ProfessionalService professionalService;
    private final com.upm.institutional.service.FeatureService featureService;
    private final AcademicOfferService academicOfferService;

    @GetMapping("/")
    public String home(Model model) {
        // Show recent published courses and news (limit to 3 for example)
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Course> recentCourses = courseService.findPublishedCourses(pageable);
        model.addAttribute("courses", recentCourses.getContent());

        Pageable newsPageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "eventDate"));
        Page<News> recentNews = newsService.findPublishedNews(newsPageable);
        model.addAttribute("news", recentNews.getContent());

        String heroImageUrl = siteSettingService.getSetting("home_hero_image_url",
                "/images/university_campus_hero.png");
        model.addAttribute("heroImageUrl", heroImageUrl);

        model.addAttribute("features", featureService.findAllFeatures());

        return "home";
    }

    @GetMapping("/sedes")
    public String sedes(Model model) {
        model.addAttribute("sedes", sedeService.getAllSedes());
        return "sedes";
    }

    @GetMapping("/courses")
    public String courses(Model model, Pageable pageable) {
        Page<Course> courses = courseService.findPublishedCourses(pageable);
        model.addAttribute("courses", courses);
        return "courses/list";
    }

    @GetMapping("/academic-offer")
    public String academicOffer(Model model,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String query) {
        model.addAttribute("offers", academicOfferService.search(area, query));
        model.addAttribute("areas", academicOfferService.findDistinctAreas());
        model.addAttribute("currentArea", area);
        model.addAttribute("currentQuery", query);
        return "academic-offer";
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        Course course = courseService.findPublishedById(id);
        model.addAttribute("course", course);
        return "courses/detail";
    }

    @GetMapping("/news")
    public String news(Model model, Pageable pageable) {
        Page<News> news = newsService.findPublishedNews(pageable);
        model.addAttribute("news", news);
        return "news/list";
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        News news = newsService.findPublishedById(id);
        model.addAttribute("news", news);
        return "news/detail";
    }

    @GetMapping("/contact")
    public String contactForm(@RequestParam(required = false) String subject, Model model) {
        ContactForm form = new ContactForm();
        if (subject != null && !subject.isEmpty()) {
            form.setSubject(subject);
            form.setMessage("Hola, deseo recibir más información sobre el curso: "
                    + subject.replace("Información sobre curso: ", "") + ". Muchas gracias.");
        }
        model.addAttribute("contactForm", form);
        return "contact/form";
    }

    @PostMapping("/contact")
    public String sendContact(@Valid @ModelAttribute ContactForm contactForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "contact/form";
        }
        contactService.saveMessage(contactForm);
        redirectAttributes.addFlashAttribute("success", "Mensaje enviado correctamente.");
        return "redirect:/contact";
    }

    @GetMapping("/professionals")
    public String professionals(Model model,
            @RequestParam(required = false) String profession,
            @RequestParam(required = false) String locality,
            Pageable pageable) {
        Page<com.upm.institutional.model.Professional> professionals = professionalService.search(profession, locality,
                pageable);
        model.addAttribute("professionals", professionals);
        model.addAttribute("profession", profession);
        model.addAttribute("locality", locality);
        return "professionals";
    }
}
