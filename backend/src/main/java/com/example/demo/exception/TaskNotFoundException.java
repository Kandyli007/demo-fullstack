package com.example.demo.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(long id) {
        super("找不到 ID 为 " + id + " 的任务");
    }
}
