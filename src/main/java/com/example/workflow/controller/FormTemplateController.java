package com.example.workflow.controller;

import com.example.workflow.model.FormTemplate;
import com.example.workflow.service.FormTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/form-templates")
public class FormTemplateController {
    private final FormTemplateService service;

    public FormTemplateController(FormTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public List<FormTemplate> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormTemplate> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('Head')")
    @PostMapping
    public FormTemplate create(@RequestBody FormTemplate template) {
        return service.save(template);
    }

    @PreAuthorize("hasAnyAuthority('Head')")
    @PutMapping("/{id}")
    public ResponseEntity<FormTemplate> update(@PathVariable Long id, @RequestBody FormTemplate template) {
        return service.findById(id)
                .map(existing -> {
                    template.setId(id);
                    return ResponseEntity.ok(service.save(template));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('Head')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 