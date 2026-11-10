package com.example.demo.service;

import com.example.demo.dto.TaskRequest;
import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public TaskService() {
        create(new TaskRequest("搭建 Spring Boot 后端", "创建 REST API 并配置跨域访问", TaskStatus.DONE));
        create(new TaskRequest("实现 Vue 3 前端", "集成 Element Plus 完成任务管理页面", TaskStatus.IN_PROGRESS));
        create(new TaskRequest("前后端联调", "通过 Vite 代理调用 /api 接口", TaskStatus.TODO));
    }

    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(Task::id).reversed())
                .toList();
    }

    public Task findById(long id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    public Task create(TaskRequest request) {
        Task task = new Task(
                sequence.incrementAndGet(),
                request.title().trim(),
                normalize(request.description()),
                request.status() == null ? TaskStatus.TODO : request.status(),
                LocalDateTime.now()
        );
        tasks.put(task.id(), task);
        return task;
    }

    public Task update(long id, TaskRequest request) {
        Task existing = findById(id);
        Task updated = new Task(
                id,
                request.title().trim(),
                normalize(request.description()),
                request.status() == null ? existing.status() : request.status(),
                existing.createdAt()
        );
        tasks.put(id, updated);
        return updated;
    }

    public void delete(long id) {
        if (tasks.remove(id) == null) {
            throw new TaskNotFoundException(id);
        }
    }

    private String normalize(String description) {
        return description == null ? "" : description.trim();
    }
}
