package com.wyh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try {
            execute(args);
        } catch (Exception e) {
            GlobalExceptionHandler.handleCommandLineException(e);
            System.exit(1);
        }
    }

    private static void execute(String[] args) {
        if (args.length == 0) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                    "缺少必要参数，请使用 -r 参数指定数值范围或使用 -e 和 -a 参数进行判分");
        }

        CommandLineArgs cli = parseCommandLine(args);

        if (cli.isGradingMode()) {
            performGrading(cli);
        } else {
            generateProblems(cli);
        }
    }

    private static void performGrading(CommandLineArgs cli) {
        validateGradingParameters(cli);

        try {
            AnswerChecker.GradingResult result =
                    AnswerChecker.grade(cli.getExerciseFile(), cli.getAnswerFile());
            Files.write(Paths.get("Grade.txt"), result.toGradeString().getBytes());
            System.out.println("判分完成，结果已写入 Grade.txt");

            // 输出统计信息
            System.out.println(result.toGradeString());

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.GRADING_FAILED.getCode(),
                    "判分过程发生错误: " + e.getMessage(), e);
        }
    }

    private static void generateProblems(CommandLineArgs cli) {
        validateGenerationParameters(cli);

        try {
            ProblemGenerator generator = new ProblemGenerator(cli.getRange());
            List<Problem> problems = generator.generateProblems(cli.getCount());

            writeProblemsToFile(problems);
            writeAnswersToFile(problems);

            System.out.printf("已成功生成 %d 道题目到 Exercises.txt，答案写入 Answers.txt\n", problems.size());
            System.out.println("所有题目均满足：不产生负数、除法结果为真分数、运算符不超过3个且无重复题目");

        } catch (BusinessException e) {
            throw e; // 重新抛出业务异常
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.GENERATION_FAILED.getCode(),
                    "题目生成过程中发生错误: " + e.getMessage(), e);
        }
    }

    private static void validateGradingParameters(CommandLineArgs cli) {
        if (cli.getExerciseFile() == null || cli.getAnswerFile() == null) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                    "判分模式必须同时提供 -e 和 -a 参数");
        }

        if (!Files.exists(Paths.get(cli.getExerciseFile()))) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND.getCode(),
                    "题目文件不存在: " + cli.getExerciseFile());
        }

        if (!Files.exists(Paths.get(cli.getAnswerFile()))) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND.getCode(),
                    "答案文件不存在: " + cli.getAnswerFile());
        }
    }

    private static void validateGenerationParameters(CommandLineArgs cli) {
        if (cli.getRange() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_RANGE_PARAMETER.getCode(),
                    "范围参数 -r 必须为正整数，当前值: " + cli.getRange());
        }

        if (cli.getCount() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_COUNT_PARAMETER.getCode(),
                    "题目数量 -n 必须为正整数，当前值: " + cli.getCount());
        }

        if (cli.getRange() < 3) {
            System.out.println("警告: 范围参数较小，可能影响题目生成多样性");
        }
    }

    private static void writeProblemsToFile(List<Problem> problems) throws IOException {
        List<String> exercises = problems.stream()
                .map(Problem::toString)
                .collect(Collectors.toList());
        Files.write(Paths.get("Exercises.txt"), exercises);
    }

    private static void writeAnswersToFile(List<Problem> problems) throws IOException {
        List<String> answers = problems.stream()
                .map(Problem::getAnswer)
                .collect(Collectors.toList());
        Files.write(Paths.get("Answers.txt"), answers);
    }

    private static CommandLineArgs parseCommandLine(String[] args) {
        CommandLineArgs cli = new CommandLineArgs();

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-n":
                        if (i + 1 < args.length) {
                            cli.setCount(Integer.parseInt(args[++i]));
                        } else {
                            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                                    "-n 参数后必须跟题目数量");
                        }
                        break;
                    case "-r":
                        if (i + 1 < args.length) {
                            cli.setRange(Integer.parseInt(args[++i]));
                        } else {
                            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                                    "-r 参数后必须跟数值范围");
                        }
                        break;
                    case "-e":
                        if (i + 1 < args.length) {
                            cli.setExerciseFile(args[++i]);
                        } else {
                            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                                    "-e 参数后必须跟题目文件路径");
                        }
                        break;
                    case "-a":
                        if (i + 1 < args.length) {
                            cli.setAnswerFile(args[++i]);
                        } else {
                            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                                    "-a 参数后必须跟答案文件路径");
                        }
                        break;
                    default:
                        throw new BusinessException(ErrorCode.INVALID_PARAMETER.getCode(),
                                "未知参数: " + args[i]);
                }
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER.getCode(),
                    "参数格式错误，数值参数必须为整数");
        }

        return cli;
    }
}