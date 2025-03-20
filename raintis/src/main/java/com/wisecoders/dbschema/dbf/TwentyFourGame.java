package com.wisecoders.dbschema.dbf;

import java.util.*;
import java.util.stream.Collectors;

public class TwentyFourGame {

    private static final double EPSILON = 1e-6;
    private static final Map<String, Boolean> cache = new HashMap<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入4个数字，用空格隔开:");
        List<Double> numbers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            numbers.add(scanner.nextDouble());
        }
        scanner.close();

        if (canGetTwentyFour(numbers)) {
            System.out.println("可以得到24!");
        } else {
            System.out.println("无法得到24.");
        }
    }

    public static boolean canGetTwentyFour(List<Double> numbers) {
        if (numbers.size() == 1) {
            return Math.abs(numbers.get(0) - 24) < EPSILON;
        }

        // 生成缓存键
        String cacheKey = numbers.stream()
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        // 检查缓存
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i != j) {
                    // 使用 Stream 生成 remainingNumbers
                    int finalI = i;
                    int finalJ = j;
                    List<Double> remainingNumbers = numbers.stream()
                            .filter(num -> !num.equals(numbers.get(finalI)) && !num.equals(numbers.get(finalJ)))
                            .collect(Collectors.toList());

                    // 尝试所有操作并提前返回
                    if (tryOperations(remainingNumbers, numbers.get(i), numbers.get(j))) {
                        cache.put(cacheKey, true); // 缓存结果
                        return true;
                    }
                }
            }
        }

        cache.put(cacheKey, false); // 缓存结果
        return false;
    }

    private static boolean tryOperations(List<Double> remainingNumbers, double a, double b) {
        return tryOperation(remainingNumbers, a, b, '+') ||
                tryOperation(remainingNumbers, a, b, '-') ||
                tryOperation(remainingNumbers, a, b, '*') ||
                tryOperation(remainingNumbers, a, b, '/');
    }

    private static boolean tryOperation(List<Double> remainingNumbers, double a, double b, char op) {
        double res = calculate(a, b, op);
        if (Double.isNaN(res) || Double.isInfinite(res)) {
            return false; // 剪枝：无效结果
        }
        List<Double> newNumbers = new ArrayList<>(remainingNumbers);
        newNumbers.add(res);
        return canGetTwentyFour(newNumbers);
    }

    private static double calculate(double a, double b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (Math.abs(b) < EPSILON) {
                    return Double.NaN; // 表示无效操作
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operation");
        }
    }

}