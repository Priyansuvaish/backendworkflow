package com.example.workflow.service;

import com.example.workflow.model.WorkflowDefinition;
import com.example.workflow.repository.WorkflowDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowDefinitionService {
    private final WorkflowDefinitionRepository repository;

    public WorkflowDefinitionService(WorkflowDefinitionRepository repository) {
        this.repository = repository;
    }

    public List<WorkflowDefinition> findAll() {
        return repository.findAll();
    }

    public Optional<WorkflowDefinition> findById(Long id) {
        return repository.findById(id);
    }

    public WorkflowDefinition save(WorkflowDefinition definition) {
        return repository.save(definition);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
} 