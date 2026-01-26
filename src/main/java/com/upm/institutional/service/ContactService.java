package com.upm.institutional.service;

import com.upm.institutional.dto.ContactForm;
import com.upm.institutional.model.ContactMessage;
import com.upm.institutional.model.MessageStatus;
import com.upm.institutional.repository.ContactMessageRepository;
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
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public void saveMessage(ContactForm form) {
        ContactMessage message = new ContactMessage();
        message.setFullName(form.getFullName());
        message.setEmail(form.getEmail());
        message.setSubject(form.getSubject());
        message.setMessage(form.getMessage());
        message.setStatus(MessageStatus.NEW);
        contactMessageRepository.save(message);

        // Send email notification
        sendEmailNotification(form);
    }

    private void sendEmailNotification(ContactForm form) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo("posadasuniversidadpopular@gmail.com");
            mailMessage.setSubject("Nuevo Mensaje Web: " + form.getSubject());
            mailMessage.setText("Nombre: " + form.getFullName() + "\n" +
                    "Email: " + form.getEmail() + "\n\n" +
                    "Mensaje:\n" + form.getMessage());

            emailSender.send(mailMessage);
            log.info("Email notification sent successfully for contact form: {}", form.getEmail());
        } catch (Exception e) {
            log.error("Error sending email notification", e);
            // Don't rollback transaction just because email failed
        }
    }

    public Page<ContactMessage> findAllMessages(Pageable pageable) {
        return contactMessageRepository.findAll(pageable);
    }

    public ContactMessage findById(Long id) {
        return contactMessageRepository.findById(id).orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
    }

    @Transactional
    public void markAsRead(Long id) {
        ContactMessage message = findById(id);
        if (message.getStatus() == MessageStatus.NEW) {
            message.setStatus(MessageStatus.READ);
            contactMessageRepository.save(message);
        }
    }

    @Transactional
    public void archiveMessage(Long id) {
        ContactMessage message = findById(id);
        message.setStatus(MessageStatus.ARCHIVED);
        contactMessageRepository.save(message);
    }
}
