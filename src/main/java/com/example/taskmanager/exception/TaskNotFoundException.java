package com.example.taskmanager.exception;

/**
 * Thrown when a task with a given ID cannot be found.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }
}
