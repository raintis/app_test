package com.my.search.storage;

/**
 * 描述: 列值对
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public class ColValPair <V>{
    public final String columnName;
    public final V value;

    private ColValPair(String columnName, V value) {
        this.columnName = columnName;
        this.value = value;
    }

    public static <V> ColValPair<V> of(String columnName, V value) {
        return new ColValPair<>(columnName, value);
    }

    public String toString() {
        return "ColValPair{" +
                "columnName='" + columnName + '\'' +
                ", value=" + value +
                '}';
    }
}
