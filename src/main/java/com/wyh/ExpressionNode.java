package com.wyh;
import java.util.ArrayList;
import java.util.List;

public class ExpressionNode {
    public enum NodeType { NUMBER, OPERATOR }
    public enum Operator { ADD, SUBTRACT, MULTIPLY, DIVIDE }

    private NodeType type;
    private Fraction value;
    private Operator operator;
    private ExpressionNode left;
    private ExpressionNode right;

    // 叶节点构造函数
    public ExpressionNode(Fraction value) {
        this.type = NodeType.NUMBER;
        this.value = value;
    }

    // 运算符节点构造函数
    public ExpressionNode(Operator operator, ExpressionNode left, ExpressionNode right) {
        this.type = NodeType.OPERATOR;
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Fraction evaluate() {
        if (type == NodeType.NUMBER) {
            return value;
        }

        Fraction leftVal = left.evaluate();
        Fraction rightVal = right.evaluate();

        switch (operator) {
            case ADD: return leftVal.add(rightVal);
            case SUBTRACT: return leftVal.subtract(rightVal);
            case MULTIPLY: return leftVal.multiply(rightVal);
            case DIVIDE: return leftVal.divide(rightVal);
            default: throw new IllegalStateException("未知运算符");
        }
    }

    public String toInfixString() {
        if (type == NodeType.NUMBER) {
            return value.toString();
        }

        String leftStr = left.toInfixString();
        String rightStr = right.toInfixString();

        // 根据优先级决定是否加括号
        if (needsParentheses(left, false)) {
            leftStr = "(" + leftStr + ")";
        }
        if (needsParentheses(right, true)) {
            rightStr = "(" + rightStr + ")";
        }

        String opStr = getOperatorSymbol();
        return leftStr + " " + opStr + " " + rightStr;
    }

    private boolean needsParentheses(ExpressionNode child, boolean isRight) {
        if (child.type != NodeType.OPERATOR) {
            return false;
        }

        int parentPrecedence = getPrecedence(this.operator);
        int childPrecedence = getPrecedence(child.operator);

        if (childPrecedence < parentPrecedence) {
            return true;
        }

        // 处理结合性
        if (childPrecedence == parentPrecedence) {
            if (isRight && (operator == Operator.SUBTRACT || operator == Operator.DIVIDE)) {
                return true;
            }
        }

        return false;
    }

    private int getPrecedence(Operator op) {
        switch (op) {
            case ADD: case SUBTRACT: return 1;
            case MULTIPLY: case DIVIDE: return 2;
            default: return 0;
        }
    }

    private String getOperatorSymbol() {
        switch (operator) {
            case ADD: return "+";
            case SUBTRACT: return "-";
            case MULTIPLY: return "×";
            case DIVIDE: return "÷";
            default: return "?";
        }
    }

    // 生成规范化键用于去重
    public String getCanonicalKey() {
        if (type == NodeType.NUMBER) {
            return value.toString();
        }

        String leftKey = left.getCanonicalKey();
        String rightKey = right.getCanonicalKey();

        // 对可交换运算符进行排序
        if (operator == Operator.ADD || operator == Operator.MULTIPLY) {
            if (leftKey.compareTo(rightKey) > 0) {
                return operator.name() + "(" + rightKey + "," + leftKey + ")";
            }
        }

        return operator.name() + "(" + leftKey + "," + rightKey + ")";
    }

    public int getOperatorCount() {
        if (type == NodeType.NUMBER) {
            return 0;
        }
        return 1 + left.getOperatorCount() + right.getOperatorCount();
    }

    // Getters
    public NodeType getType() { return type; }
    public Fraction getValue() { return value; }
    public Operator getOperator() { return operator; }
    public ExpressionNode getLeft() { return left; }
    public ExpressionNode getRight() { return right; }
}
