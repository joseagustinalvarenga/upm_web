package com.upm.institutional.controller;

import com.upm.institutional.service.AcademicOfferService;
import com.upm.institutional.service.ContactService;
import com.upm.institutional.service.CourseService;
import com.upm.institutional.service.FeatureService;
import com.upm.institutional.service.NewsService;
import com.upm.institutional.service.ProfessionalService;
import com.upm.institutional.service.SedeService;
import com.upm.institutional.service.SiteSettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicController.class)
public class AcademicOfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private NewsService newsService;

    @MockBean
    private ContactService contactService;

    @MockBean
    private SiteSettingService siteSettingService;

    @MockBean
    private SedeService sedeService;

    @MockBean
    private ProfessionalService professionalService;

    @MockBean
    private FeatureService featureService;

    @MockBean
    private AcademicOfferService academicOfferService;

    @MockBean
    private com.upm.institutional.repository.UserRepository userRepository;

    @MockBean
    private com.upm.institutional.repository.FeatureRepository featureRepository;

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    public void testAcademicOfferPage() throws Exception {
        mockMvc.perform(get("/academic-offer"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-offer"))
                .andExpect(model().attributeExists("offers"))
                .andExpect(model().attributeExists("areas"));
    }
}
