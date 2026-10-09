package com.upm.institutional.service;

import com.upm.institutional.dto.ProfessionalApplicationForm;
import com.upm.institutional.model.ApplicationStatus;
import com.upm.institutional.model.Professional;
import com.upm.institutional.model.ProfessionalApplication;
import com.upm.institutional.repository.ProfessionalApplicationRepository;
import com.upm.institutional.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfessionalApplicationService {

    private final ProfessionalApplicationRepository applicationRepository;
    private final ProfessionalRepository professionalRepository;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username:no-reply@upm.edu.ar}")
    private String fromEmail;

    @Value("${app.public-base-url:https://upmisiones.com.ar}")
    private String publicBaseUrl;

    @Transactional
    public ProfessionalApplication submitApplication(ProfessionalApplicationForm form) {
        ProfessionalApplication app = new ProfessionalApplication();
        app.setFullName(form.getFullName());
        app.setDni(form.getDni());
        app.setProfession(form.getProfession());
        app.setPhone(form.getPhone());
        app.setEmail(form.getEmail());
        app.setLocality(form.getLocality());
        app.setCourseCompleted(form.getCourseCompleted());
        app.setNotes(form.getNotes());
        app.setStatus(ApplicationStatus.PENDING);

        ProfessionalApplication savedApp = applicationRepository.save(app);

        // Send email notification to UPM
        sendEmailNotification(savedApp);

        return savedApp;
    }

    private void sendEmailNotification(ProfessionalApplication app) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                mailMessage.setFrom(fromEmail);
            }
            mailMessage.setTo("posadasuniversidadpopular@gmail.com");
            mailMessage.setSubject("Nueva Solicitud de Profesional - UPM: " + app.getFullName());

            StringBuilder body = new StringBuilder();
            body.append("Se ha recibido una nueva postulación para la Bolsa de Trabajo / Directorio de Profesionales de la UPM:\n\n");
            body.append("• Nombre y Apellido: ").append(app.getFullName()).append("\n");
            body.append("• DNI: ").append(app.getDni() != null ? app.getDni() : "No especificado").append("\n");
            body.append("• Profesión/Oficio: ").append(app.getProfession()).append("\n");
            body.append("• Teléfono/WhatsApp: ").append(app.getPhone()).append("\n");
            body.append("• Email: ").append(app.getEmail()).append("\n");
            body.append("• Localidad: ").append(app.getLocality()).append("\n");
            body.append("• Curso / Egreso UPM: ").append(app.getCourseCompleted() != null ? app.getCourseCompleted() : "No especificado").append("\n");
            body.append("• Observaciones / Experiencia: ").append(app.getNotes() != null ? app.getNotes() : "Sin observaciones").append("\n\n");
            body.append("Por favor ingresa al Panel de Administración de la UPM para confirmar o rechazar esta solicitud.\n");
            body.append("Revisar solicitudes: ")
                    .append(publicBaseUrl.replaceAll("/+$", ""))
                    .append("/admin/professionals/requests\n");
            body.append("Si no has iniciado sesión, ingresa con tu cuenta de administrador para continuar.\n");

            mailMessage.setText(body.toString());
            emailSender.send(mailMessage);
            log.info("Email de notificación de profesional enviado a posadasuniversidadpopular@gmail.com para {}", app.getEmail());
        } catch (Exception e) {
            log.error("Error al enviar email de notificación de profesional", e);
            // Non-blocking: record remains saved in database for admin review
        }
    }

    public Page<ProfessionalApplication> findAll(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    public Page<ProfessionalApplication> findByStatus(ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByStatus(status, pageable);
    }

    public ProfessionalApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud de profesional no encontrada"));
    }

    public long countPending() {
        return applicationRepository.countByStatus(ApplicationStatus.PENDING);
    }

    @Transactional
    public void approveApplication(Long id) {
        ProfessionalApplication app = findById(id);
        if (app.getStatus() == ApplicationStatus.APPROVED) {
            return; // Already approved
        }
        app.setStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(app);

        // Add to active professionals table
        Professional professional = new Professional();
        professional.setName(app.getFullName());
        professional.setProfession(app.getProfession());
        professional.setPhone(app.getPhone());
        professional.setLocality(app.getLocality());
        professionalRepository.save(professional);

        log.info("Solicitud de profesional #{} aprobada y registrada en el directorio activo", id);
    }

    @Transactional
    public void rejectApplication(Long id) {
        ProfessionalApplication app = findById(id);
        app.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(app);
        log.info("Solicitud de profesional #{} rechazada", id);
    }

    @Transactional
    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
