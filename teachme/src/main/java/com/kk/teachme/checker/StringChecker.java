package com.kk.teachme.checker;

import sun.misc.Regexp;

public class StringChecker implements Checker {
    public enum CaseOption {
        EqualsIgnoreCase, EqualsWithCase
    }

    StringChecker(CaseOption option) {
        this.option = option;
    }

    @Override
    public SolveStatus check(String userAnswer, String correctAnswer) {
        String regexp = "[\\s+\n]";
        String reducedUserAnswer = userAnswer.replace(regexp, " ").trim();
        String reducedCorrectAnswer = correctAnswer.replace(regexp, " ").trim();

        if (option.equals(CaseOption.EqualsIgnoreCase)) {
            if (reducedUserAnswer.equalsIgnoreCase(reducedCorrectAnswer)) {
                return SolveStatus.CORRECT;
            }
        }

        if (option.equals(CaseOption.EqualsWithCase)) {
            if (reducedUserAnswer.equals(reducedCorrectAnswer)) {
                return SolveStatus.CORRECT;
            }
        }

        return SolveStatus.INCORRECT;
    }

    private CaseOption option;
}
