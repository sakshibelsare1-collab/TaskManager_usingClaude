package com.example.taskmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple status endpoint for the Task Manager API.
 * The root page is now served from static/index.html.
 */
@RestController
public class HomeController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of(
                "application", "Task Manager API",
                "status", "running",
                "tasksEndpoint", "/tasks"
        ));
    }
}
