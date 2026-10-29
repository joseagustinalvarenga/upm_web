package com.upm.institutional.controller;

import com.upm.institutional.repository.ChristmasVideoRepository;
import com.upm.institutional.service.ChristmasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false"})
@AutoConfigureMockMvc
@Transactional
class ChristmasFlowTest {
    @Autowired MockMvc mvc;
    @Autowired ChristmasVideoRepository videos;
    @Autowired ChristmasService christmas;

    @Test void completeLifecycleWithUploadedCoverAndRevocation() throws Exception {
        assertFalse(christmas.enabled());
        ByteArrayOutputStream image = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB), "png", image);
        mvc.perform(multipart("/admin/navidad/videos")
                .file(new MockMultipartFile("imageFile", "cover.png", "image/png", image.toByteArray()))
                .with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", "Receta navideña").param("category", "Cocina")
                .param("videoUrl", "https://youtu.be/example").param("visible", "true").param("displayOrder", "2"))
                .andExpect(redirectedUrl("/admin/navidad"));
        var video = videos.findAll().get(0);
        assertTrue(video.getCover().startsWith("data:image/jpeg;base64,"));
        assertEquals(1, christmas.published().size());
        mvc.perform(post("/admin/navidad/configuracion").with(user("admin").roles("ADMIN")).with(csrf())
                .param("enabled", "true").param("code", "NavidadPrueba"))
                .andExpect(redirectedUrl("/admin/navidad"));
        mvc.perform(get("/navidad/galeria")).andExpect(redirectedUrl("/navidad"));
        MockHttpSession student = new MockHttpSession();
        mvc.perform(post("/navidad/ingresar").session(student).with(csrf()).param("code", "NavidadPrueba"))
                .andExpect(redirectedUrl("/navidad/galeria"));
        mvc.perform(get("/navidad/galeria").session(student)).andExpect(status().isOk())
                .andExpect(content().string(containsString("Receta navideña")));
        // Editing without a replacement preserves the uploaded cover and can hide the video.
        mvc.perform(post("/admin/navidad/videos").with(user("admin").roles("ADMIN")).with(csrf())
                .param("id", video.getId().toString()).param("title", "Receta editada")
                .param("category", "Cocina").param("videoUrl", "https://youtu.be/example").param("displayOrder", "0"))
                .andExpect(redirectedUrl("/admin/navidad"));
        assertTrue(christmas.published().isEmpty());
        assertNotNull(videos.findById(video.getId()).orElseThrow().getCover());
        mvc.perform(post("/admin/navidad/configuracion").with(user("admin").roles("ADMIN")).with(csrf())
                .param("enabled", "false")).andExpect(redirectedUrl("/admin/navidad"));
        mvc.perform(get("/navidad/galeria").session(student)).andExpect(redirectedUrl("/navidad"));
        mvc.perform(get("/navidad")).andExpect(status().isNotFound());
        mvc.perform(post("/admin/navidad/videos/" + video.getId() + "/eliminar")
                .with(user("admin").roles("ADMIN")).with(csrf())).andExpect(redirectedUrl("/admin/navidad"));
        assertEquals(0, videos.count());
    }
}
