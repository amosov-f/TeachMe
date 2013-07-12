package com.kk.teachme.checker;


public class IntChecker implements Checker{
    private int realAnswer;
    public IntChecker(String realAnswer){
        this.realAnswer = Integer.parseInt(realAnswer);
    }

    public int getRealAnswer(){
       return this.realAnswer;
    }

    @Override
    public boolean check(String userAnswer) {
        //TODO check if user_answer is not int
        if (this.getRealAnswer() == Integer.parseInt(userAnswer)) return true;
        return false;
    }
}
