package com.kk.teachme.model;


import com.kk.teachme.checker.Checker;

public class Solution {
    private String solutionText;
    private Checker checker;

    public Solution(String solutionText, Checker checker){
        this.solutionText = solutionText;
        this.checker = checker;
    }

    public Checker.SolveStatus check(String answer) {
        return checker.check(answer, solutionText);
    }
}
