package com.wyh;

import java.util.*;

public class ProblemGenerator {
    private final int range;
    private final Random random;

    public ProblemGenerator(int range) {
        if (range <= 0) {
            throw new BusinessException(ErrorCode.INVALID_RANGE_PARAMETER.getCode(),
                    "数值范围必须为正整数");
        }
        this.range = range;
        this.random = new Random();
    }

    public List<Problem> generateProblems(int count) {
        if (count <= 0) {
            throw new BusinessException(ErrorCode.INVALID_COUNT_PARAMETER.getCode(),
                    "题目数量必须为正整数");
        }

        Set<String> seenKeys = new HashSet<>();
        List<Problem> problems = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = count * 200; // 增加尝试次数

        while (problems.size() < count && attempts < maxAttempts) {
            attempts++;

            try {
                ExpressionNode expression = generateExpression(1 + random.nextInt(3));
                if (!validateExpression(expression)) {
                    continue;
                }

                String key = expression.getCanonicalKey();
                if (seenKeys.contains(key)) {
                    continue;
                }

                seenKeys.add(key);
                Fraction answer = expression.evaluate();
                problems.add(new Problem(expression.toInfixString(), answer.toString()));

            } catch (ArithmeticException e) {
                // 忽略数学计算错误，继续生成
                continue;
            } catch (Exception e) {
                // 其他异常记录并继续
                System.err.println("生成表达式时发生错误: " + e.getMessage());
                continue;
            }
        }

        if (problems.size() < count) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_UNIQUE_PROBLEMS.getCode(),
                    String.format("无法在合理尝试次数内生成足够的不重复题目（已生成 %d/%d）。请增大范围参数 -r 或减少题目数量 -n",
                            problems.size(), count));
        }

        return problems;
    }

    private ExpressionNode generateExpression(int operatorCount) {
        if (operatorCount == 0) {
            return new ExpressionNode(generateRandomFraction());
        }

        ExpressionNode.Operator op = randomOperator();
        int leftOps = random.nextInt(operatorCount);
        int rightOps = operatorCount - 1 - leftOps;

        ExpressionNode left = generateExpression(leftOps);
        ExpressionNode right = generateExpression(rightOps);

        // 应用约束条件
        return applyConstraints(op, left, right);
    }

    private ExpressionNode applyConstraints(ExpressionNode.Operator op,
                                            ExpressionNode left, ExpressionNode right) {
        Fraction leftVal = left.evaluate();
        Fraction rightVal = right.evaluate();

        switch (op) {
            case SUBTRACT:
                // 确保左值 >= 右值
                if (leftVal.compareTo(rightVal) < 0) {
                    return new ExpressionNode(op, right, left);
                }
                break;

            case DIVIDE:
                // 确保右值非零且结果为真分数
                if (rightVal.isZero()) {
                    right = generateExpression(right.getOperatorCount());
                    rightVal = right.evaluate();
                }
                Fraction result = leftVal.divide(rightVal);
                if (!result.isProper()) {
                    // 调整使得结果为真分数
                    if (leftVal.compareTo(rightVal) >= 0) {
                        ExpressionNode temp = left;
                        left = right;
                        right = temp;
                    }
                }
                break;
        }

        return new ExpressionNode(op, left, right);
    }

    private boolean validateExpression(ExpressionNode expr) {
        try {
            Fraction result = expr.evaluate();
            // 检查运算符数量
            if (expr.getOperatorCount() > 3) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Fraction generateRandomFraction() {
        // 生成分数：整数或真分数
        if (random.nextDouble() < 0.7) {
            // 生成整数
            return new Fraction(random.nextInt(range - 1) + 1);
        } else {
            // 生成真分数
            int denominator = random.nextInt(range - 2) + 2;
            int numerator = random.nextInt(denominator - 1) + 1;
            return new Fraction(numerator, denominator);
        }
    }

    private ExpressionNode.Operator randomOperator() {
        ExpressionNode.Operator[] operators = ExpressionNode.Operator.values();
        return operators[random.nextInt(operators.length)];
    }
}
