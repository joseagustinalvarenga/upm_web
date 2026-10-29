package com.upm.institutional.controller;

import com.upm.institutional.config.SecurityConfig;
import com.upm.institutional.model.ChristmasVideo;
import com.upm.institutional.repository.*;
import com.upm.institutional.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest({ChristmasController.class, AdminChristmasController.class})
@Import({SecurityConfig.class, CustomUserDetailsService.class})
class ChristmasControllerTest {
    @Autowired MockMvc mvc;
    @MockBean ChristmasService christmas;
    @MockBean ChristmasVideoRepository videos;
    @MockBean SiteSettingService settings;
    @MockBean UserRepository users;
    @MockBean FeatureRepository features;

    @Test void disabledAndDirectGalleryAreBlocked() throws Exception {
        mvc.perform(get("/navidad")).andExpect(status().isNotFound())
                .andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(get("/navidad/galeria")).andExpect(redirectedUrl("/navidad"));
        verify(christmas, never()).published();
    }
    @Test void enabledEntryRendersCodeFormWithCsrf() throws Exception {
        when(christmas.enabled()).thenReturn(true);
        mvc.perform(get("/navidad")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Código de acceso")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        mvc.perform(post("/navidad/ingresar").param("code", "test")).andExpect(status().isForbidden());
    }
    @Test void correctCodeRedirectsAndWrongCodeShowsError() throws Exception {
        when(christmas.enabled()).thenReturn(true);
        mvc.perform(post("/navidad/ingresar").with(csrf()).param("code", "wrong"))
                .andExpect(redirectedUrl("/navidad")).andExpect(flash().attributeExists("error"));
        when(christmas.unlock(anyString(), eq("correct"), any())).thenReturn(true);
        mvc.perform(post("/navidad/ingresar").with(csrf()).param("code", "correct"))
                .andExpect(redirectedUrl("/navidad/galeria"));
    }
    @Test void galleryRendersVideosAndEmptyState() throws Exception {
        when(christmas.authorized(any())).thenReturn(true);
        when(christmas.published()).thenReturn(List.of());
        mvc.perform(get("/navidad/galeria")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Estamos preparando")));
        ChristmasVideo video = new ChristmasVideo();
        video.setTitle("Pan dulce"); video.setCategory("Cocina");
        video.setVideoUrl("https://youtu.be/example");
        when(christmas.published()).thenReturn(List.of(video));
        mvc.perform(get("/navidad/galeria")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Pan dulce")))
                .andExpect(content().string(containsString("noopener noreferrer")));
    }
    @Test void adminRequiresAuthentication() throws Exception {
        mvc.perform(get("/admin/navidad")).andExpect(status().is3xxRedirection());
    }
    @Test @WithMockUser(roles = "ADMIN") void adminScreensRenderAndRequireCsrf() throws Exception {
        when(christmas.all()).thenReturn(List.of());
        mvc.perform(get("/admin/navidad")).andExpect(status().isOk());
        mvc.perform(get("/admin/navidad/videos/nuevo")).andExpect(status().isOk());
        mvc.perform(post("/admin/navidad/configuracion")).andExpect(status().isForbidden());
        mvc.perform(post("/admin/navidad/configuracion").with(csrf()).param("enabled", "false"))
                .andExpect(redirectedUrl("/admin/navidad"));
        verify(christmas).configure(false, "");
    }
    @Test @WithMockUser(roles = "ADMIN") void unsafeVideoLinkIsRejected() throws Exception {
        mvc.perform(post("/admin/navidad/videos").with(csrf()).param("title", "Video")
                        .param("category", "Cocina").param("videoUrl", "javascript:alert(1)")
                        .param("displayOrder", "0").param("visible", "true"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("video", "videoUrl"));
        verify(videos, never()).save(any());
    }
}
