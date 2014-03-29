package com.kk.teachme.checker;

public interface Checker {

    public SolveStatus check(String userAnswer, String correctAnswer);

    public String getName();

}
