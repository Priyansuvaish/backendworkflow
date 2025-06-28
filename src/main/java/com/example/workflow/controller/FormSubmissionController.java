package com.example.workflow.controller;

import com.example.workflow.model.FormSubmission;
import com.example.workflow.model.FormTemplate;
import com.example.workflow.model.WorkflowDefinition;
import com.example.workflow.service.FormSubmissionService;
import com.example.workflow.service.FormTemplateService;
import com.example.workflow.service.WorkflowDefinitionService;
import com.example.workflow.util.validator;
import jakarta.persistence.Convert;
import jakarta.persistence.Lob;
import org.everit.json.schema.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@PreAuthorize("hasAnyAuthority('User')")
@RequestMapping("/api/form-submissions")
public class FormSubmissionController {
    private final FormSubmissionService submissionService;
    private final FormTemplateService templateService;
    private final WorkflowDefinitionService workflowDefinitionService;
    @Autowired
    private RuntimeService runtimeService;

    public FormSubmissionController(FormSubmissionService submissionService,
                                    FormTemplateService templateService,
                                    WorkflowDefinitionService workflowDefinitionService) {
        this.submissionService = submissionService;
        this.templateService = templateService;
        this.workflowDefinitionService = workflowDefinitionService;
    }

    @GetMapping
    public List<FormSubmission> getMySubmissions(Authentication authentication) {
        return submissionService.findByUsername(authentication.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormSubmission> getById(@PathVariable Long id, Authentication authentication) {
        return submissionService.findById(id)
                .filter(sub -> sub.getUsername().equals(authentication.getName()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> submitForm(@RequestParam Long templateId, @RequestBody Map<String, Object> submissionJson, Authentication authentication) {
        FormTemplate template = templateService.findById(templateId).orElse(null);
        if (template == null) return ResponseEntity.badRequest().build();

        //validating

        try {
            com.example.workflow.util.validator.validateJson(template.getSchemaJson(), submissionJson);
        } catch (ValidationException e) {
            e.printStackTrace(); // or log it
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        FormSubmission submission = FormSubmission.builder()
                .template(template)
                .username(authentication.getName())
                .submissionJson(submissionJson)
                .build();
        submission = submissionService.save(submission);
        // 👇 Start workflow and get instance details
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                "myProcess",
                Map.of(
                        "formSubmissionId", submission.getId(),
                        "username", authentication.getName()
                )
        );

        // ✅ Confirm with ID and state
        Map<String, Object> response = Map.of(
                "submission", submission,
                "processInstanceId", instance.getId(),
                "processDefinitionId", instance.getProcessDefinitionId(),
                "status", instance.isEnded() ? "COMPLETED" : "Applied"
        );
        return ResponseEntity.ok(response);
    }
} 