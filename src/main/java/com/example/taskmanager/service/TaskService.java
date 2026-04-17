package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer containing all business logic for Task operations.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Returns all tasks.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Returns a single task by ID.
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Creates a new task and persists it.
     */
    public Task createTask(Task task) {
        // Ensure we don't accidentally use a client-supplied ID
        task.setId(null);
        return taskRepository.save(task);
    }

    /**
     * Updates an existing task by ID.
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public Task updateTask(Long id, Task updatedTask) {
        Task existing = getTaskById(id); // throws if not found

        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existing);
    }

    /**
     * Deletes a task by ID.
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
