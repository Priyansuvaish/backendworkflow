package com.example.workflow.repository;

import com.example.workflow.model.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
} 