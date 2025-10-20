package com.wyh;

public class Problem {
    private final String expression;
    private final String answer;

    public Problem(String expression, String answer) {
        this.expression = expression;
        this.answer = answer;
    }

    public String getExpression() {
        return expression;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return expression + " = ";
    }
}