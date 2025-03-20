package com.my.search;

import org.apache.lucene.util.RamUsageEstimator;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class HighVolumePivot {
    /**
     * 优化版行转列（支持并行处理）
     * @param data 原始数据集合（建议使用内存友好结构）
     * @param rowKeyExtractor 行键提取函数
     * @param columnKeyExtractor 列键提取函数
     * @param valueExtractor 值提取函数
     * @return 行转列后的紧凑结构
     */
    public static <R, C, V> Map<R, Map<C, V>> pivot(
            List<Map<String, Object>> data,
            Function<Map<String, Object>, R> rowKeyExtractor,
            Function<Map<String, Object>, C> columnKeyExtractor,
            Function<Map<String, Object>, V> valueExtractor) {

        // 使用并发结构保证线程安全（可并行处理）
        ConcurrentHashMap<R, Map<C, V>> resultMap = new ConcurrentHashMap<>();
        Set<C> columnKeys = ConcurrentHashMap.newKeySet();

        // 单次遍历完成所有操作（时间复杂度 O(n)）
        data.parallelStream().forEach(row -> { // 启用并行流
            R rowKey = rowKeyExtractor.apply(row);
            C columnKey = columnKeyExtractor.apply(row);
            V value = valueExtractor.apply(row);

            // 记录列键（线程安全）
            columnKeys.add(columnKey);

            // 合并行数据（compute原子操作）
            resultMap.compute(rowKey, (k, existingMap) -> {
                Map<C, V> rowMap = (existingMap != null) ? existingMap : new HashMap<>();
                rowMap.put(columnKey, value);
                return rowMap;
            });
        });

        // 填充缺失列（按需启用）
        /*final Set<C> finalColumns = new TreeSet<>(columnKeys); // 排序可选
        resultMap.replaceAll((k, v) -> {
            finalColumns.forEach(col -> v.putIfAbsent(col, null));
            return v;
        });*/

        return new HashMap<>(resultMap);
    }

    // 使用示例
    @Test
    public  void mainPivoted() {
        // 建议使用更紧凑的数据结构（如自定义对象代替Map）
        List<Map<String, Object>> bigData = generateTestData(1_000_000);

        Map<String, Map<String, Integer>> pivoted = pivot(
                bigData,
                this::genKey,
                row -> (String) row.get("subject"),
                row -> (Integer) row.get("score")
        );
        System.out.println("row-size:" + pivoted.size()*4);
        //System.out.println(pivoted);
        System.out.println(RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOfObject(bigData)));
        System.out.println(RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOfObject(pivoted)));
        System.out.println(ClassLayout.parseInstance(bigData.get(0)).toPrintable());
        System.out.println(pivoted.entrySet().stream().findFirst());
    }

    private String genKey(Map<String,Object> row){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<maxSize;i++){
            sb.append(row.get("student"+i));
        }
        return sb.toString();
    }

    private final static int maxSize = 10;
    // 生成测试数据（模拟百万行）
    private static List<Map<String, Object>> generateTestData(int size) {
        List<Map<String, Object>> data = new ArrayList<>(size);
        String[] students = {"张三", "李四", "王五"};
        String[] subjects = {"数学", "语文", "英语", "物理"};

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            Map<String, Object> row = new HashMap<>();
            for(int k =0;k<maxSize;k++) {
                row.put("student"+k, students[k % 3] + random.nextInt(10));
            }
            row.put("subject", subjects[i % 4] + random.nextInt(20));
            row.put("score", random.nextInt(100));
            data.add(row);
        }
        return data;
    }
}
