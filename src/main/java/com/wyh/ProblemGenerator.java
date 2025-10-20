package com.wyh;

import java.util.*;

public class ProblemGenerator {
    private final int range;
    private final Random random;

    // 添加缓存机制
    private final Map<String, CachedExpression> expressionCache = new HashMap<>();
    private final Set<String> invalidExpressions = new HashSet<>();

    // 缓存内部类
    private static class CachedExpression {
        final String infixString;
        final String answer;
        final String canonicalKey;
        final int operatorCount;

        CachedExpression(ExpressionNode expr) {
            this.infixString = expr.toInfixString();
            this.answer = expr.evaluate().toString();
            this.canonicalKey = expr.getCanonicalKey();
            this.operatorCount = expr.getOperatorCount();
        }
    }

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
        int maxAttempts = count * 100; // 减少尝试次数

        while (problems.size() < count && attempts < maxAttempts) {
            attempts++;

            CachedExpression cached = generateValidCachedExpression();
            if (cached == null || !seenKeys.add(cached.canonicalKey)) {
                continue;
            }

            problems.add(new Problem(cached.infixString, cached.answer));
        }

        if (problems.size() < count) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_UNIQUE_PROBLEMS.getCode(),
                    String.format("无法在合理尝试次数内生成足够的不重复题目（已生成 %d/%d）。请增大范围参数 -r 或减少题目数量 -n",
                            problems.size(), count));
        }

        return problems;
    }

    private CachedExpression generateValidCachedExpression() {
        // 快速尝试3次
        for (int quickAttempt = 0; quickAttempt < 3; quickAttempt++) {
            ExpressionNode expression = generateOptimizedExpression(1 + random.nextInt(3));
            String key = expression.getCanonicalKey();

            // 检查已知无效表达式
            if (invalidExpressions.contains(key)) {
                continue;
            }

            // 检查缓存
            CachedExpression cached = expressionCache.get(key);
            if (cached != null) {
                if (isValidCachedExpression(cached)) {
                    return cached;
                } else {
                    invalidExpressions.add(key);
                    continue;
                }
            }

            // 新表达式，验证并缓存
            try {
                // 快速验证运算符数量
                if (expression.getOperatorCount() > 3) {
                    invalidExpressions.add(key);
                    continue;
                }

                // 验证表达式有效性
                Fraction result = expression.evaluate();
                if (isValidFraction(result)) {
                    CachedExpression newCached = new CachedExpression(expression);
                    expressionCache.put(key, newCached);
                    return newCached;
                } else {
                    invalidExpressions.add(key);
                }
            } catch (Exception e) {
                invalidExpressions.add(key);
            }
        }
        return null;
    }

    private ExpressionNode generateOptimizedExpression(int operatorCount) {
        return generateExpression(operatorCount);
    }

    // 优化原有的 generateExpression 方法
    private ExpressionNode generateExpression(int operatorCount) {
        if (operatorCount == 0) {
            return new ExpressionNode(generateRandomFraction());
        }

        ExpressionNode.Operator op = randomOperator();
        int leftOps = random.nextInt(operatorCount);
        int rightOps = operatorCount - 1 - leftOps;

        ExpressionNode left = generateExpression(leftOps);
        ExpressionNode right = generateExpression(rightOps);

        return applyOptimizedConstraints(op, left, right);
    }

    // 优化约束应用逻辑
    private ExpressionNode applyOptimizedConstraints(ExpressionNode.Operator op,
                                                     ExpressionNode left, ExpressionNode right) {
        // 对于减法和除法，进行快速检查
        if (op == ExpressionNode.Operator.SUBTRACT || op == ExpressionNode.Operator.DIVIDE) {
            try {
                Fraction leftVal = left.evaluate();
                Fraction rightVal = right.evaluate();

                switch (op) {
                    case SUBTRACT:
                        if (leftVal.compareTo(rightVal) < 0) {
                            return new ExpressionNode(op, right, left);
                        }
                        break;

                    case DIVIDE:
                        if (rightVal.isZero()) {
                            // 重新生成右节点，但限制次数
                            right = generateSimpleExpression();
                        }
                        // 检查除法结果是否为真分数
                        Fraction divisionResult = leftVal.divide(rightVal);
                        if (!divisionResult.isProper() && leftVal.compareTo(rightVal) >= 0) {
                            return new ExpressionNode(op, right, left);
                        }
                        break;
                }
            } catch (Exception e) {
                // 如果计算出错，交换节点重试
                return new ExpressionNode(op, right, left);
            }
        }

        return new ExpressionNode(op, left, right);
    }

    // 生成简单表达式（避免深度递归）
    private ExpressionNode generateSimpleExpression() {
        if (random.nextDouble() < 0.5) {
            return new ExpressionNode(generateRandomFraction());
        } else {
            ExpressionNode.Operator op = randomOperator();
            // 避免除法和减法以减少复杂度
            while (op == ExpressionNode.Operator.DIVIDE || op == ExpressionNode.Operator.SUBTRACT) {
                op = randomOperator();
            }
            return new ExpressionNode(op,
                    new ExpressionNode(generateRandomFraction()),
                    new ExpressionNode(generateRandomFraction()));
        }
    }

    private boolean isValidCachedExpression(CachedExpression cached) {
        return cached.operatorCount <= 3;
    }

    private boolean isValidFraction(Fraction fraction) {
        try {
            return fraction.compareTo(new Fraction(0)) >= 0; // 非负数
        } catch (Exception e) {
            return false;
        }
    }

    // 保留原有验证方法（用于兼容性）
    private boolean validateExpression(ExpressionNode expr) {
        try {
            Fraction result = expr.evaluate();
            return expr.getOperatorCount() <= 3;
        } catch (Exception e) {
            return false;
        }
    }

    // 优化分数生成
    private Fraction generateRandomFraction() {
        // 增加整数比例，减少分数运算
        if (random.nextDouble() < 0.8) { // 从0.7提高到0.8
            return new Fraction(random.nextInt(range - 1) + 1);
        } else {
            int denominator = random.nextInt(range - 2) + 2;
            int numerator = random.nextInt(denominator - 1) + 1;
            return new Fraction(numerator, denominator);
        }
    }

    private ExpressionNode.Operator randomOperator() {
        ExpressionNode.Operator[] operators = ExpressionNode.Operator.values();
        // 调整运算符概率，减少除法和减法
        if (random.nextDouble() < 0.3) {
            // 30% 概率选择加法或乘法
            return random.nextBoolean() ? ExpressionNode.Operator.ADD : ExpressionNode.Operator.MULTIPLY;
        }
        return operators[random.nextInt(operators.length)];
    }

    // 添加清理方法（可选）
    public void clearCache() {
        expressionCache.clear();
        invalidExpressions.clear();
    }
}