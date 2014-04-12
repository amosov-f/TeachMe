package com.kk.teachme.checker;

public class StringChecker extends Checker {

    private final CaseOption option;

    public StringChecker(CaseOption option) {
        this.option = option;
    }

    @Override
    public SolveStatus check(String correctAnswer, String userAnswer) {

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
    public String getName() {
        return "Строка";
    }

    public static enum CaseOption {
        EqualsIgnoreCase, EqualsWithCase
    }

}
