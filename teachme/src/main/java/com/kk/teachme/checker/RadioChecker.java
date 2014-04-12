package com.kk.teachme.checker;

public class RadioChecker extends Checker {

    @Override
    public SolveStatus check(String correctAnswer, String userAnswer) {
        if (userAnswer.isEmpty()) {
            return SolveStatus.INVALID;
        }
        if (correctAnswer.equals(userAnswer)) {
            return SolveStatus.CORRECT;
        }
        return SolveStatus.INCORRECT;
    }

    @Override
    public String getName() {
        return "Выбор";
    }
}
