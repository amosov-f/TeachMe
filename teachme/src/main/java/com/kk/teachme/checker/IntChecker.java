package com.kk.teachme.checker;


public class IntChecker extends Checker {

    @Override
    public SolveStatus check(String correctAnswer, String userAnswer) {
        try {
            int answer = Integer.parseInt(correctAnswer);
            if (answer == Integer.parseInt(userAnswer)) {
                return SolveStatus.CORRECT;
            }
            return SolveStatus.INCORRECT;
        } catch (NumberFormatException e) {
            return SolveStatus.INVALID;
        }
    }

    @Override
    public String getName() {
        return "Число";
    }

}
