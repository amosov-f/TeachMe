package com.kk.teachme.model;


public class UserProblem {
    private Problem problem;
    private Integer attempts;

    public UserProblem(Problem problem) {
        this(problem, null);
    }

    public UserProblem(Problem problem, Integer attempts) {
        this.problem = problem;
        this.attempts = attempts;
    }

    public Problem getProblem() {
        return problem;
    }

    public Status getStatus() {
        if (attempts == null) {
            return Status.NEW;
        } else if (attempts <= 0) {
            return Status.READ;
        } else {
            return Status.SOLVED;
        }
    }

    public int getAttempts() {
        if (attempts == null) {
            return 0;
        } else {
            return Math.abs(attempts);
        }
    }

    public Integer getRawAttempts() {
        return attempts;
    }

    public void attempt(boolean solved) {
        if (attempts <= 0) {
            attempts--;
            if (solved == true) {
                attempts = Math.abs(attempts);
            }
        }
    }

    @Override
    public String toString() {
        return "UserProblem{" +
                "problem=" + problem.getId() +
                ", status=" + getStatus() +
                ", attempts=" + getAttempts() +
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
}