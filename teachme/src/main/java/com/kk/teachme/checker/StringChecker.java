package com.kk.teachme.checker;

public class StringChecker implements Checker {
    private CaseOption option;

    StringChecker(CaseOption option) {
        this.option = option;
    }

    @Override
    public SolveStatus check(String userAnswer, String correctAnswer) {

        String regexp = "[\\s+\n]";
        String reducedUserAnswer = userAnswer.replace(regexp, " ").trim();
        String reducedCorrectAnswer = correctAnswer.replace(regexp, " ").trim();

        if (userAnswer.isEmpty()) {
            return SolveStatus.INVALID;
        }

        if (option == CaseOption.EqualsIgnoreCase) {
            if (reducedUserAnswer.equalsIgnoreCase(reducedCorrectAnswer)) {
                return SolveStatus.CORRECT;
            }
        }

        if (option == CaseOption.EqualsWithCase) {
            if (reducedUserAnswer.equals(reducedCorrectAnswer)) {
                return SolveStatus.CORRECT;
            }
        }

        return SolveStatus.INCORRECT;
    }

    @Override
    public String toString() {
        return "Строка";
    }

    public static enum CaseOption {
        EqualsIgnoreCase, EqualsWithCase
    }
}
