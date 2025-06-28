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
public class FormTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Convert(converter = com.example.workflow.model.JsonToMapConverter.class)
    private Map<String, Object> schemaJson; // JSON schema as object
} 