package com.upm.institutional.service;

import com.upm.institutional.dto.ProfessionalApplicationForm;
import com.upm.institutional.model.ProfessionalApplication;
import com.upm.institutional.repository.ProfessionalApplicationRepository;
import com.upm.institutional.repository.ProfessionalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfessionalApplicationServiceTest {
    @Test
    void notificationIncludesAdminReviewLinkAndOnlyPosadasRecipient() {
        ProfessionalApplicationRepository applications = mock(ProfessionalApplicationRepository.class);
        JavaMailSender sender = mock(JavaMailSender.class);
        ProfessionalApplicationService service = new ProfessionalApplicationService(
                applications, mock(ProfessionalRepository.class), sender);
        ReflectionTestUtils.setField(service, "publicBaseUrl", "https://upmisiones.com.ar/");
        when(applications.save(any(ProfessionalApplication.class))).thenAnswer(call -> call.getArgument(0));

        ProfessionalApplicationForm form = new ProfessionalApplicationForm();
        form.setFullName("Profesional de prueba");
        service.submitApplication(form);

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertArrayEquals(new String[]{"posadasuniversidadpopular@gmail.com"}, message.getValue().getTo());
        assertTrue(message.getValue().getText().contains(
                "https://upmisiones.com.ar/admin/professionals/requests"));
        assertFalse(message.getValue().getText().contains(".ar//"));
    }
}
