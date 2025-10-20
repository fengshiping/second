package com.wyh;

import lombok.Data;

/**
 * 命令行参数封装类
 * 用于解析和存储命令行输入的参数
 */
@Data
public class CommandLineArgs {
    private int count = 10;          // 题目数量，默认10
    private int range = -1;          // 数值范围，-1表示未设置
    private String exerciseFile;     // 题目文件路径
    private String answerFile;       // 答案文件路径

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getExerciseFile() {
        return exerciseFile;
    }

    public void setExerciseFile(String exerciseFile) {
        this.exerciseFile = exerciseFile;
    }

    public String getAnswerFile() {
        return answerFile;
    }

    public void setAnswerFile(String answerFile) {
        this.answerFile = answerFile;
    }

    /**
     * 判断是否为判分模式
     * @return 如果设置了题目文件或答案文件，则为判分模式
     */
    public boolean isGradingMode() {
        return exerciseFile != null || answerFile != null;
    }

    /**
     * 验证判分模式参数是否完整
     * @throws BusinessException 如果参数不完整
     */
    public void validateGradingMode() {
        if (isGradingMode() && (exerciseFile == null || answerFile == null)) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                    "判分模式必须同时提供 -e 和 -a 参数");
        }
    }

    /**
     * 验证生成模式参数是否完整
     * @throws BusinessException 如果参数不完整
     */
    public void validateGenerationMode() {
        if (!isGradingMode() && range <= 0) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode(),
                    "生成模式必须提供 -r 参数且为正整数");
        }
    }

    /**
     * 获取参数摘要，用于日志和调试
     */
    public String getSummary() {
        if (isGradingMode()) {
            return String.format("判分模式: 题目文件=%s, 答案文件=%s", exerciseFile, answerFile);
        } else {
            return String.format("生成模式: 数量=%d, 范围=%d", count, range);
        }
    }

    @Override
    public String toString() {
        return "CommandLineArgs{" +
                "count=" + count +
                ", range=" + range +
                ", exerciseFile='" + exerciseFile + '\'' +
                ", answerFile='" + answerFile + '\'' +
                ", isGradingMode=" + isGradingMode() +
                '}';
    }
}
