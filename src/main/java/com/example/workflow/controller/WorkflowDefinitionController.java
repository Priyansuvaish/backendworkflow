package com.example.workflow.controller;

import com.example.workflow.model.WorkflowDefinition;
import com.example.workflow.service.WorkflowDefinitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-definitions")
public class WorkflowDefinitionController {
    private final WorkflowDefinitionService service;

    public WorkflowDefinitionController(WorkflowDefinitionService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkflowDefinition> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public WorkflowDefinition create(@RequestBody WorkflowDefinition definition) {
        return service.save(definition);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> update(@PathVariable Long id, @RequestBody WorkflowDefinition definition) {
        return service.findById(id)
                .map(existing -> {
                    definition.setId(id);
                    return ResponseEntity.ok(service.save(definition));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 