package com.example.workflow.service;

import com.example.workflow.model.FormSubmission;
import com.example.workflow.repository.FormSubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormSubmissionService {
    private final FormSubmissionRepository repository;

    public FormSubmissionService(FormSubmissionRepository repository) {
        this.repository = repository;
    }

    public List<FormSubmission> findAll() {
        return repository.findAll();
    }

    public Optional<FormSubmission> findById(Long id) {
        return repository.findById(id);
    }

    public List<FormSubmission> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public FormSubmission save(FormSubmission submission) {
        return repository.save(submission);
    }
} 