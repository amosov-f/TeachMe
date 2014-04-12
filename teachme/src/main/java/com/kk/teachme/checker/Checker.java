package com.kk.teachme.checker;

public abstract class Checker {

    public int id;

    public abstract SolveStatus check(String correctAnswer, String userAnswer);

    public abstract String getName();

    public static enum SolveStatus {
        INCORRECT, CORRECT, INVALID
    }

    public void setId(int id) {
        this.id = id;
    }

}
