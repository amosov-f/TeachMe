package com.kk.teachme.model;


public class UserProblem {
    private Problem problem;
    private int userId;
    private Status status;

    public UserProblem(Problem problem, int userId, Status status) {
        this.problem = problem;
        this.userId = userId;
        this.status = status;
    }

    public Problem getProblem() {
        return problem;
    }

    public int getUser_id() {
        return userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Problem problem) {
        this.problem = problem;
    }

    public void setUser_id(int userId) {
        this.userId = userId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
