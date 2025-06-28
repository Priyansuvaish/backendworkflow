package com.example.workflow.service;

import com.example.workflow.model.FormTemplate;
import com.example.workflow.repository.FormTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormTemplateService {
    private final FormTemplateRepository repository;

    public FormTemplateService(FormTemplateRepository repository) {
        this.repository = repository;
    }

    public List<FormTemplate> findAll() {
        return repository.findAll();
    }

    public Optional<FormTemplate> findById(Long id) {
        return repository.findById(id);
    }

    public FormTemplate save(FormTemplate template) {
        return repository.save(template);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
} 