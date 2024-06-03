package com.example.todolist;


public class TaskModel {
    private int id;
    private String taskName;
    private String taskTime;
    private String taskDate;
    private boolean taskStatus;

    // Constructor
    public TaskModel(int id, String taskName, String taskTime, String taskDate, boolean taskStatus) {
        this.id = id;
        this.taskName = taskName;
        this.taskTime = taskTime;
        this.taskDate = taskDate;
        this.taskStatus = taskStatus;
    }

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public boolean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }
}

