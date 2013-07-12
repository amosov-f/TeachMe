package com.kk.teachme.model;


public class Solution {
    private int id;
    private String solution_text;
    private int checker_id;

    public Solution(int id, String solutionId, int checkerId){
        this.id = id;
        this.solution_text = solutionId;
        this.checker_id = checkerId;
    }
    public int getId(){
        return id;
    }
    public String getSolution_text(){
        return solution_text;
    }
    public int getChecker_id(){
        return checker_id;
    }
}
