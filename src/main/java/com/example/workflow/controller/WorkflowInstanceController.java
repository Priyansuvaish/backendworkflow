package com.example.workflow.controller;


import com.example.workflow.DTO.TaskDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@PreAuthorize("hasAnyAuthority('Employee','Manager','HR')")
@RequestMapping("/api/workflow-instances")
public class WorkflowInstanceController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @PostMapping("/transition")
    public ResponseEntity<?> transition(@RequestBody Map<String, String> body, Authentication authentication) {
        try {
            // Step 1: Fetch user roles from authorities
            String username = authentication.getName();
            List<String> roles = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .toList();
            System.out.println(username);
            // Step 2: Fetch tasks available to this user (candidate or assigned)
            List<Task> tasks = taskService.createTaskQuery()
                    .or()
                    .taskCandidateGroupIn(roles)
                    .taskAssignee(username)
                    .endOr()
                    .list();


            // ✅ Extra check here before accessing tasks.get(0)
            if (tasks.isEmpty()) {
                return ResponseEntity.status(404).body("No task available for user to transition.");
            }
            // Step 2: claim the task
            Task task = tasks.get(0);
            taskService.claim(task.getId(), username);


            // Step 2: Add any process variables from body (e.g., decision = approve/reject)
            Map<String, Object> variables = new HashMap<>(body);

            // Step 3: Complete the task to move the workflow
            taskService.complete(task.getId(), variables);

            return ResponseEntity.ok("Task completed and transitioned to the next step.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during transition: " + e.getMessage());
        }
    }

    @GetMapping("/tasks")
    public List<TaskDto> getUserTasks(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroupIn(roles)
                .list();

        return tasks.stream()
                .map(task -> {
                    Map<String, Object> vars = runtimeService.getVariables(task.getProcessInstanceId());
                    return new TaskDto(
                            task.getId(),
                            task.getName(),
                            task.getAssignee(),
                            task.getTaskDefinitionKey(),
                            task.getProcessInstanceId(),
                            task.getProcessDefinitionId(),
                            task.getCreateTime(),
                            vars
                    );
                })
                .toList();
    }

    @GetMapping("/assignedtasks")
    public List<TaskDto> getUserAssignTasks(Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(username)
                .list();

        return tasks.stream()
                .map(task -> {
                    Map<String, Object> vars = runtimeService.getVariables(task.getProcessInstanceId());
                    return new TaskDto(
                            task.getId(),
                            task.getName(),
                            task.getAssignee(),
                            task.getTaskDefinitionKey(),
                            task.getProcessInstanceId(),
                            task.getProcessDefinitionId(),
                            task.getCreateTime(),
                            vars
                    );
                })
                .toList();
    }

    @PostMapping("/assign/{id}")
    public ResponseEntity<?> assigntask(@PathVariable String id, Authentication authentication) {
        try {
            String username = authentication.getName();
            // claim the task
            taskService.claim(id, username);

            return ResponseEntity.ok("Task assigned");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during assign: " + e.getMessage());
        }
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approvetask(@PathVariable String id,@RequestBody Map<String, String> body, Authentication authentication) {
        try {
            String username = authentication.getName();

            Map<String, Object> variables = new HashMap<>(body);
            // approve the task
            taskService.complete(id, variables);

            return ResponseEntity.ok("Task approved");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during assign: " + e.getMessage());
        }
    }


} 