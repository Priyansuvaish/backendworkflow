package com.example.workflow.DTO;

import java.util.Date;
import java.util.Map;

public record TaskDto(
        String id,
        String name,
        String assignee,
        String taskDefinitionKey,
        String processInstanceId,
        String processDefinitionId,
        Date createTime,
        Map<String, Object> variables
) {}
