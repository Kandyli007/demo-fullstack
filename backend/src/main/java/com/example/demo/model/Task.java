package com.example.demo.model;

import java.time.LocalDateTime;

public record Task(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt
) {
}
