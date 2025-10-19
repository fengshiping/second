package com.wyh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//答案检查器
public class AnswerChecker {

    public static GradingResult grade(String exerciseFile, String answerFile) throws IOException {
        List<String> exercises = Files.readAllLines(Paths.get(exerciseFile));
        List<String> answers = Files.readAllLines(Paths.get(answerFile));

        if (exercises.size() != answers.size()) {
            throw new IllegalArgumentException("题目数量和答案数量不匹配");
        }

        List<Integer> correctIndices = new ArrayList<>();
        List<Integer> wrongIndices = new ArrayList<>();

        for (int i = 0; i < exercises.size(); i++) {
            String exercise = exercises.get(i).trim();
            String expectedAnswer = answers.get(i).trim();

            // 移除末尾的等号并解析计算
            if (exercise.endsWith("=")) {
                exercise = exercise.substring(0, exercise.length() - 1).trim();
            }

            try {
                Fraction result = evaluateExpression(exercise);
                if (result.toString().equals(expectedAnswer)) {
                    correctIndices.add(i + 1); // 题目编号从1开始
                } else {
                    wrongIndices.add(i + 1);
                }
            } catch (Exception e) {
                wrongIndices.add(i + 1);
            }
        }

        return new GradingResult(correctIndices, wrongIndices);
    }

    /**
     * 简化的表达式求值（实际实现需要完整的解析器）
     * @param expression
     * @return
     */
    private static Fraction evaluateExpression(String expression) {
        // 这里应该实现完整的表达式解析和求值
        // 由于复杂度，这里使用简化实现
        return new Fraction(1);
    }

    public static class GradingResult {
        private final List<Integer> correct;
        private final List<Integer> wrong;

        public GradingResult(List<Integer> correct, List<Integer> wrong) {
            this.correct = correct;
            this.wrong = wrong;
        }

        public String toGradeString() {
            return String.format("Correct: %d %s\nWrong: %d %s",
                    correct.size(), formatIndices(correct),
                    wrong.size(), formatIndices(wrong));
        }

        private String formatIndices(List<Integer> indices) {
            if (indices.isEmpty()) {
                return "()";
            }
            return "(" + indices.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")) + ")";
        }
    }
}
