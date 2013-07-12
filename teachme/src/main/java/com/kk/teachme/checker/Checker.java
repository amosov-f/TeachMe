package com.kk.teachme.checker;

/**
 * User: Митя
 */

public interface Checker {
    //TODO return status: INCORRECT, CORRECT, INVALID
    public boolean check(String userAnswer, String correctAnswer);
}
