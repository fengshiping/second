package com.wyh;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.stream.Collectors;

//全局异常处理器
public class GlobalExceptionHandler {

    public static ApiResponse<?> handleException(Exception e) {
        if (e instanceof BusinessException) {
            return handleBusinessException((BusinessException) e);
        } else if (e instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException) e);
        } else if (e instanceof IOException) {
            return handleIOException((IOException) e);
        } else if (e instanceof ArithmeticException) {
            return handleArithmeticException((ArithmeticException) e);
        } else {
            return handleUnexpectedException(e);
        }
    }

    private static ApiResponse<?> handleBusinessException(BusinessException e) {
        String errorCode = e.getErrorCode();
        String message = e.getMessage();

        return ApiResponse.error(errorCode, message);
    }

    private static ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        String message = e.getMessage();

        // 根据错误消息内容判断具体的错误类型
        if (message != null) {
            if (message.contains("分母不能为零")) {
                return ApiResponse.error(ErrorCode.DIVISION_BY_ZERO.getCode(),
                        ErrorCode.DIVISION_BY_ZERO.getMessage());
            } else if (message.contains("范围") || message.contains("range")) {
                return ApiResponse.error(ErrorCode.INVALID_RANGE_PARAMETER.getCode(),
                        ErrorCode.INVALID_RANGE_PARAMETER.getMessage() + ": " + message);
            } else if (message.contains("数量") || message.contains("count")) {
                return ApiResponse.error(ErrorCode.INVALID_COUNT_PARAMETER.getCode(),
                        ErrorCode.INVALID_COUNT_PARAMETER.getMessage() + ": " + message);
            }
        }

        return ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(),
                ErrorCode.INVALID_PARAMETER.getMessage() + ": " + message);
    }

    private static ApiResponse<?> handleIOException(IOException e) {
        if (e instanceof NoSuchFileException) {
            return ApiResponse.error(ErrorCode.FILE_NOT_FOUND.getCode(),
                    ErrorCode.FILE_NOT_FOUND.getMessage() + ": " + e.getMessage());
        }

        return ApiResponse.error(ErrorCode.FILE_READ_ERROR.getCode(),
                ErrorCode.FILE_READ_ERROR.getMessage() + ": " + e.getMessage());
    }

    private static ApiResponse<?> handleArithmeticException(ArithmeticException e) {
        String message = e.getMessage();
        if (message != null && message.contains("除零")) {
            return ApiResponse.error(ErrorCode.DIVISION_BY_ZERO.getCode(),
                    ErrorCode.DIVISION_BY_ZERO.getMessage());
        }

        return ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(),
                "数学计算错误: " + message);
    }

    private static ApiResponse<?> handleUnexpectedException(Exception e) {
        // 记录详细的错误日志（在实际项目中应该使用日志框架）
        System.err.println("未预期的错误: " + e.getClass().getName());
        System.err.println("错误信息: " + e.getMessage());
        e.printStackTrace();

        return ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(),
                "系统内部错误，请稍后重试");
    }

    // 命令行特定的异常处理方法
    public static void handleCommandLineException(Exception e) {
        ApiResponse<?> response = handleException(e);

        System.err.println("错误: " + response.getMessage());
        if (response.getErrorCode() != null) {
            System.err.println("错误代码: " + response.getErrorCode());
        }

        // 对于参数错误，显示使用说明
        if (ErrorCode.INVALID_PARAMETER.getCode().equals(response.getErrorCode()) ||
                ErrorCode.MISSING_REQUIRED_PARAMETER.getCode().equals(response.getErrorCode())) {
            System.err.println();
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("用法:");
        System.out.println("  生成题目: java MathProblemGenerator -r <范围> [-n <数量>]");
        System.out.println("  判定对错: java MathProblemGenerator -e <题目文件> -a <答案文件>");
        System.out.println("说明:");
        System.out.println("  -r 必须在生成模式下提供，表示数值范围");
        System.out.println("  生成的表达式满足：不产生负数；除法结果为真分数；运算符≤3个");
    }
}
