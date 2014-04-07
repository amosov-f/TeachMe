package com.kk.teachme.checker;

public class RadioChecker implements Checker {

    @Override
    public SolveStatus check(String userAnswer, String correctAnswer) {
        if (userAnswer.isEmpty()) {
            return SolveStatus.INVALID;
        }
        if (correctAnswer.equals(userAnswer)) {
            return SolveStatus.CORRECT;
        }
        return SolveStatus.INCORRECT;
    }

    @Override
    public String toString() {
        return "Выбор";
    }
}
