package com.example.workflow.controller;

import com.example.workflow.DTO.BPMN_config;
import com.example.workflow.model.FormTemplate;
import com.example.workflow.service.FormTemplateService;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/form-templates")
public class FormTemplateController {
    private final FormTemplateService service;

    public FormTemplateController(FormTemplateService service) {
        this.service = service;
    }

    @Autowired
    private RepositoryService repositoryService;

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

    @PreAuthorize("hasAnyAuthority('Head')")
    @PostMapping("/deploy")
    public ResponseEntity<String> generateAndDeploy(@RequestBody BPMN_config req) {
        try {
            String bpmnXml = generateBpmnXml(req);
            InputStream bpmnStream = new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8));

            repositoryService.createDeployment()
                    .addInputStream(req.processId + ".bpmn20.xml", bpmnStream)
                    .name("Dynamic JSON Deployment")
                    .deploy();

            return ResponseEntity.ok("Deployed: " + req.processId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    private String generateBpmnXml(BPMN_config req) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("xmlns:flowable=\"http://flowable.org/bpmn\" ")
                .append("targetNamespace=\"http://flowable.org/test\">\n");

        sb.append("<bpmn:process id=\"").append(req.processId)
                .append("\" name=\"").append(req.name)
                .append("\" isExecutable=\"true\">\n");

        // Start Event
        sb.append("<bpmn:startEvent id=\"startEvent\" />\n");

        // Tasks
        for (int i = 0; i < req.tasks.size(); i++) {
            String taskId = req.tasks.get(i);
            sb.append("<bpmn:userTask id=\"").append(taskId).append("\" name=\"").append(taskId).append("\" />\n");
        }

        // End Event
        sb.append("<bpmn:endEvent id=\"endEvent\" />\n");

        // Sequence Flows
        sb.append("<bpmn:sequenceFlow id=\"flow_start_to_").append(req.tasks.get(0))
                .append("\" sourceRef=\"startEvent\" targetRef=\"").append(req.tasks.get(0)).append("\" />\n");

        for (int i = 0; i < req.tasks.size() - 1; i++) {
            sb.append("<bpmn:sequenceFlow id=\"flow_").append(req.tasks.get(i)).append("_to_")
                    .append(req.tasks.get(i + 1)).append("\" sourceRef=\"").append(req.tasks.get(i))
                    .append("\" targetRef=\"").append(req.tasks.get(i + 1)).append("\" />\n");
        }

        sb.append("<bpmn:sequenceFlow id=\"flow_last_to_end\" sourceRef=\"")
                .append(req.tasks.get(req.tasks.size() - 1)).append("\" targetRef=\"endEvent\" />\n");

        sb.append("</bpmn:process>\n");
        sb.append("</bpmn:definitions>");

        return sb.toString();
    }

} 