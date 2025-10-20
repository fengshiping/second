package com.wyh;

import java.util.List;
import java.util.ArrayList;

public class PerformanceTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 长时间运行性能分析 ===");

        // 给Profiler时间启动
        Thread.sleep(3000);
        System.out.println("Profiler启动完成，开始测试...");

        List<Long> timings = new ArrayList<>();

        // 运行10轮测试，获得更多采样数据
        for (int round = 1; round <= 10; round++) {
            System.out.println("\n--- 第 " + round + " 轮测试 ---");

            long startTime = System.nanoTime();

            // 生成更多题目以获得更准确的数据
            ProblemGenerator generator = new ProblemGenerator(10);
            List<Problem> problems = generator.generateProblems(10000);

            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;
            timings.add(durationMs);

            System.out.printf("生成 %d 道题目耗时: %dms\n", problems.size(), durationMs);
            System.out.printf("平均每道题目: %.3fms\n", durationMs / (double)problems.size());

            // 等待一下，让Profiler采集更多数据
            if (round < 10) {
                Thread.sleep(1000);
            }
        }

        // 输出统计信息
        printStatistics(timings);
    }

    private static void printStatistics(List<Long> timings) {
        long total = timings.stream().mapToLong(Long::longValue).sum();
        double average = total / (double)timings.size();
        long max = timings.stream().mapToLong(Long::longValue).max().orElse(0);
        long min = timings.stream().mapToLong(Long::longValue).min().orElse(0);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("性能测试统计报告");
        System.out.println("=".repeat(50));
        System.out.printf("测试轮数: %d\n", timings.size());
        System.out.printf("总耗时: %dms\n", total);
        System.out.printf("平均每轮: %.2fms\n", average);
        System.out.printf("最快轮次: %dms\n", min);
        System.out.printf("最慢轮次: %dms\n", max);
        System.out.println("=".repeat(50));
    }
}