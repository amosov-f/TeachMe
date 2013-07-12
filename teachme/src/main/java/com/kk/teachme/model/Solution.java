package com.kk.teachme.model;


public class Solution {
    private int id;
    private String solution_text;
    private int checker_id;

    public Solution(int id, String solution_id, int checker_id){
        this.id = id;
        this.solution_text = solution_id;
        this.checker_id = checker_id;
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
