package com.wyh;
import java.util.Objects;

public class Fraction implements Comparable<Fraction> {
    private final int numerator;
    private final int denominator;

    public Fraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("分母不能为零");
        }

        // 规范化符号
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        int gcd = gcd(Math.abs(numerator), denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    public Fraction(int wholeNumber) {
        this.numerator = wholeNumber;
        this.denominator = 1;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // 四则运算
    public Fraction add(Fraction other) {
        int newNum = this.numerator * other.denominator + other.numerator * this.denominator;
        int newDen = this.denominator * other.denominator;
        return new Fraction(newNum, newDen);
    }

    public Fraction subtract(Fraction other) {
        int newNum = this.numerator * other.denominator - other.numerator * this.denominator;
        int newDen = this.denominator * other.denominator;
        return new Fraction(newNum, newDen);
    }

    public Fraction multiply(Fraction other) {
        int newNum = this.numerator * other.numerator;
        int newDen = this.denominator * other.denominator;
        return new Fraction(newNum, newDen);
    }

    public Fraction divide(Fraction other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("除零错误");
        }
        int newNum = this.numerator * other.denominator;
        int newDen = this.denominator * other.numerator;
        return new Fraction(newNum, newDen);
    }

    // 比较方法
    public boolean isZero() {
        return numerator == 0;
    }

    public boolean isProper() {
        return Math.abs(numerator) < denominator;
    }

    @Override
    public int compareTo(Fraction other) {
        long left = (long) this.numerator * other.denominator;
        long right = (long) other.numerator * this.denominator;
        return Long.compare(left, right);
    }

    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }

        if (Math.abs(numerator) < denominator) {
            return numerator + "/" + denominator;
        }

        int whole = numerator / denominator;
        int remainder = Math.abs(numerator % denominator);
        if (remainder == 0) {
            return String.valueOf(whole);
        }
        return whole + "'" + remainder + "/" + denominator;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Fraction other = (Fraction) obj;
        return numerator == other.numerator && denominator == other.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    public int getNumerator() { return numerator; }
    public int getDenominator() { return denominator; }
}
