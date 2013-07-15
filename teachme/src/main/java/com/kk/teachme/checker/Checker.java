package com.kk.teachme.checker;

/**
 * User: Митя
 */

public interface Checker {

    public enum SolveStatus {
        INCORRECT, CORRECT, INVALID
    }

    public SolveStatus check(String userAnswer, String correctAnswer);

    public String getName();

}
