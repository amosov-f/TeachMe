package com.kk.teachme.checker;

/**
 * User: Митя
 */

public interface Checker {

    public SolveStatus check(String userAnswer, String correctAnswer);

    public String getName();

}
