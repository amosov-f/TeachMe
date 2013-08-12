package com.kk.teachme.model;


public class UserProblem {
    private Problem problem;
    private Status status;

    public UserProblem(Problem problem) {
        this.problem = problem;
        status = Status.NEW;
    }

    public UserProblem(Problem problem, Status status) {
        this.problem = problem;
        this.status = status;
    }

    public Problem getProblem() {
        return problem;
    }

    public Status getStatus() {
        return status;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
