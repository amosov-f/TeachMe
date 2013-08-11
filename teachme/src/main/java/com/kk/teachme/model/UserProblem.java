package com.kk.teachme.model;


public class UserProblem {
    private User user;
    private Problem problem;
    private Status status;

    public UserProblem(User user, Problem problem) {
        this.user = user;
        this.problem = problem;
        status = null;
    }

    public UserProblem(User user, Problem problem, Status status) {
        this.user = user;
        this.problem = problem;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public Problem getProblem() {
        return problem;
    }

    public Status getStatus() {
        return status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
