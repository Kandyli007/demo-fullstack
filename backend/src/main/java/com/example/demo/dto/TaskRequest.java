package com.example.demo.dto;

import com.example.demo.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题不能超过 100 个字符")
        String title,

        @Size(max = 500, message = "描述不能超过 500 个字符")
        String description,

        TaskStatus status
) {
}
