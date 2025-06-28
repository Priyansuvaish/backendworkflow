package com.example.workflow.controller;

import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @PreAuthorize("hasAnyAuthority('User')")
    @GetMapping("/user/tasks")
    public ResponseEntity<?> getUserTasks(Authentication authentication) {
        String username = authentication.getName(); // From Keycloak

        // ✅ 1. Active tasks where process variable "username" matches
        List<Task> activeTasks = taskService.createTaskQuery()
                .includeProcessVariables()
                .processVariableValueEquals("username", username)
                .list();

        // ✅ 2. Completed tasks (Historic)
        List<HistoricTaskInstance> completedTasks = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .processVariableValueEquals("username", username)
                .finished()
                .list();

        // ✅ 3. Map to common response format
        List<Map<String, Object>> allTasks = new ArrayList<>();

        // Active tasks
        for (Task task : activeTasks) {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", task.getId());
            taskData.put("name", task.getName());
            taskData.put("assignee", task.getAssignee());
            taskData.put("taskDefinitionKey", task.getTaskDefinitionKey());
            taskData.put("processInstanceId", task.getProcessInstanceId());
            taskData.put("processDefinitionId", task.getProcessDefinitionId());
            taskData.put("createTime", task.getCreateTime());
            taskData.put("status", "ACTIVE");
            taskData.put("variables", taskService.getVariables(task.getId())); // Task-level variables
            allTasks.add(taskData);
        }

        // Completed tasks
        for (HistoricTaskInstance task : completedTasks) {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", task.getId());
            taskData.put("name", task.getName());
            taskData.put("assignee", task.getAssignee());
            taskData.put("taskDefinitionKey", task.getTaskDefinitionKey());
            taskData.put("processInstanceId", task.getProcessInstanceId());
            taskData.put("processDefinitionId", task.getProcessDefinitionId());
            taskData.put("createTime", task.getCreateTime());
            taskData.put("endTime", task.getEndTime());
            taskData.put("status", "COMPLETED");

            // Get all historic variables for this task
            List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .list();

            Map<String, Object> variables = vars.stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
            taskData.put("variables", variables);

            allTasks.add(taskData);
        }

        return ResponseEntity.ok(allTasks);
    }
}
