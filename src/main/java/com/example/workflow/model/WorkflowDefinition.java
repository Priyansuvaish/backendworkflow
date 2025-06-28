package com.example.workflow.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Convert(converter = com.example.workflow.model.JsonToMapConverter.class)
    private Map<String, Object> definitionJson;
}           