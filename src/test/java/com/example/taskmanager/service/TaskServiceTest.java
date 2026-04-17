package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Write tests")
                .description("Cover service layer")
                .completed(false)
                .build();
    }

    // ── getAllTasks ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllTasks returns list of tasks from repository")
    void getAllTasks_returnsList() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Write tests");
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("getAllTasks returns empty list when no tasks exist")
    void getAllTasks_emptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<Task> result = taskService.getAllTasks();

        assertThat(result).isEmpty();
    }

    // ── getTaskById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getTaskById returns task when found")
    void getTaskById_found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Task result = taskService.getTaskById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Write tests");
    }

    @Test
    @DisplayName("getTaskById throws TaskNotFoundException when not found")
    void getTaskById_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── createTask ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("createTask saves task and returns it with generated ID")
    void createTask_success() {
        Task input = Task.builder().title("New Task").description("desc").build();
        Task saved = Task.builder().id(1L).title("New Task").description("desc").build();

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.createTask(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Task");
        verify(taskRepository).save(input);
    }

    @Test
    @DisplayName("createTask clears any client-supplied ID before saving")
    void createTask_clearsClientId() {
        Task input = Task.builder().id(99L).title("Task").build();
        when(taskRepository.save(any(Task.class))).thenReturn(input);

        taskService.createTask(input);

        assertThat(input.getId()).isNull(); // ID must be nulled before save
    }

    // ── updateTask ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateTask updates existing task fields")
    void updateTask_success() {
        Task updated = Task.builder()
                .title("Updated Title")
                .description("Updated Desc")
                .completed(true)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.updateTask(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Desc");
        assertThat(result.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("updateTask throws TaskNotFoundException when task does not exist")
    void updateTask_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(99L, sampleTask))
                .isInstanceOf(TaskNotFoundException.class);
    }

    // ── deleteTask ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteTask deletes task when it exists")
    void deleteTask_success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTask throws TaskNotFoundException when task does not exist")
    void deleteTask_notFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).deleteById(any());
    }
}
