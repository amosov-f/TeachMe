package com.kk.teachme.checker;


public class IntChecker implements Checker {

    @Override
    public SolveStatus check(String userAnswer, String realAnswer) {
        try {
            int answer = Integer.parseInt(realAnswer);
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
