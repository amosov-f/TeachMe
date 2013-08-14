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

    @Override
    public String toString() {
        return "UserProblem{" +
                "problem=" + problem.getId() +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProblem that = (UserProblem) o;

        if (!problem.equals(that.problem)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return problem.hashCode();
    }
}
