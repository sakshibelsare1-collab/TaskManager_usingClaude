package com.example.taskmanager.controller;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Write tests")
                .description("Cover controller layer")
                .completed(false)
                .build();
    }

    // ── GET /tasks ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /tasks returns 200 with list of tasks")
    void getAllTasks_returns200() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Write tests"));
    }

    @Test
    @DisplayName("GET /tasks returns 200 with empty list when no tasks")
    void getAllTasks_emptyList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /tasks/{id} ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /tasks/{id} returns 200 with task when found")
    void getTaskById_found() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("GET /tasks/{id} returns 404 when task not found")
    void getTaskById_notFound() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    // ── POST /tasks ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /tasks returns 201 with created task")
    void createTask_returns201() throws Exception {
        Task input = Task.builder().title("New Task").description("desc").build();
        when(taskService.createTask(any(Task.class))).thenReturn(sampleTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /tasks returns 400 when title is blank")
    void createTask_blankTitle_returns400() throws Exception {
        Task invalid = Task.builder().title("").description("desc").build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ── PUT /tasks/{id} ───────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /tasks/{id} returns 200 with updated task")
    void updateTask_success() throws Exception {
        Task updated = Task.builder()
                .id(1L).title("Updated").description("Updated desc").completed(true).build();

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updated);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("PUT /tasks/{id} returns 404 when task not found")
    void updateTask_notFound() throws Exception {
        when(taskService.updateTask(eq(99L), any(Task.class)))
                .thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(put("/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTask)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /tasks/{id} ────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /tasks/{id} returns 204 on success")
    void deleteTask_success() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    @DisplayName("DELETE /tasks/{id} returns 404 when task not found")
    void deleteTask_notFound() throws Exception {
        doThrow(new TaskNotFoundException(99L)).when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());
    }
}
