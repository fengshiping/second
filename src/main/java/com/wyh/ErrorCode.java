package com.wyh;

//错误码
public enum ErrorCode {
    // 参数错误
    INVALID_PARAMETER("INVALID_PARAMETER", "参数错误"),
    MISSING_REQUIRED_PARAMETER("MISSING_REQUIRED_PARAMETER", "缺少必要参数"),
    INVALID_RANGE_PARAMETER("INVALID_RANGE_PARAMETER", "范围参数必须为正整数"),
    INVALID_COUNT_PARAMETER("INVALID_COUNT_PARAMETER", "题目数量必须为正整数"),

    // 文件操作错误
    FILE_READ_ERROR("FILE_READ_ERROR", "文件读取失败"),
    FILE_WRITE_ERROR("FILE_WRITE_ERROR", "文件写入失败"),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "文件不存在"),

    // 题目生成错误
    GENERATION_FAILED("GENERATION_FAILED", "题目生成失败"),
    DUPLICATE_PROBLEM("DUPLICATE_PROBLEM", "生成重复题目"),
    INSUFFICIENT_UNIQUE_PROBLEMS("INSUFFICIENT_UNIQUE_PROBLEMS", "无法生成足够的不重复题目"),

    // 表达式错误
    INVALID_EXPRESSION("INVALID_EXPRESSION", "无效的表达式"),
    DIVISION_BY_ZERO("DIVISION_BY_ZERO", "除零错误"),
    NEGATIVE_RESULT("NEGATIVE_RESULT", "计算结果为负数"),
    INVALID_FRACTION("INVALID_FRACTION", "无效的分数"),

    // 验证错误
    EXERCISE_ANSWER_MISMATCH("EXERCISE_ANSWER_MISMATCH", "题目和答案数量不匹配"),
    GRADING_FAILED("GRADING_FAILED", "判分失败"),

    // 系统错误
    INTERNAL_ERROR("INTERNAL_ERROR", "系统内部错误");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
