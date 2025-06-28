package com.example.workflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private FormTemplate template;

    private String username;

    @Lob
    @Convert(converter = com.example.workflow.model.JsonToMapConverter.class)
    private Map<String, Object> submissionJson; // User's filled form as JSON
} 