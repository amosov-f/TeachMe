package com.kk.teachme.checker;

public class StringChecker implements Checker {
    public enum CaseOption {
        EqualsIgnoreCase, EqualsWithCase
    }

    StringChecker(CaseOption option) {
        this.option = option;
    }

    @Override
    public boolean check(String userAnswer, String correctAnswer) {
        String reducedUserAnswer = userAnswer.replace(" +\n", " ").trim();
        String reducedCorrectAnswer = correctAnswer.replace(" +\n", " ").trim();

        if (option.equals(CaseOption.EqualsIgnoreCase)) {
            if (reducedUserAnswer.equalsIgnoreCase(reducedCorrectAnswer)) {
                return true;
            }
        }

        if (option.equals(CaseOption.EqualsWithCase)) {
            if (reducedUserAnswer.equals(reducedCorrectAnswer)) {
                return true;
            }
        }

        return false;
    }

    private CaseOption option;
}
