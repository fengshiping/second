package com.wyh;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Problem {
    private final String expression;
    private final String answer;

    public String getExpression() { return expression; }
    public String getAnswer() { return answer; }

    @Override
    public String toString() {
        return expression + " = ";
    }
}
