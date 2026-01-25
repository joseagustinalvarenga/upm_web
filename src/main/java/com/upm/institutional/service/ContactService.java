package com.upm.institutional.service;

import com.upm.institutional.dto.ContactForm;
import com.upm.institutional.model.ContactMessage;
import com.upm.institutional.model.MessageStatus;
import com.upm.institutional.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    @Transactional
    public void saveMessage(ContactForm form) {
        ContactMessage message = new ContactMessage();
        message.setFullName(form.getFullName());
        message.setEmail(form.getEmail());
        message.setSubject(form.getSubject());
        message.setMessage(form.getMessage());
        message.setStatus(MessageStatus.NEW);
        contactMessageRepository.save(message);
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
