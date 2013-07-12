package com.kk.teachme.checker;


public class IntChecker implements Checker{
    @Override
    public boolean check(String userAnswer, String realAnswer) {
        //TODO check if user_answer is not int
        if (Integer.parseInt(realAnswer) == Integer.parseInt(userAnswer)) return true;
        return false;
    }
}
