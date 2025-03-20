package com.my.search.storage;

import org.roaringbitmap.RoaringBitmap;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
* description: 列存储接口
* Created by yu_wei on 2025/03/04.
* @param <T>
 */
public interface IColumnStorage<T>  {
    //添加数据
    void addValue(T value);

    //稀疏维使用
    default void addValue(int index,T value){

    }

    //返回列名
    String getName();

    //返回数据类型
    Class<T> getDataType();

    //获取数据
    List<T> getData();

    //获取去重后的数据
    Set<T> getDistinctData();

    //通过值检索匹配的行号清单
    RoaringBitmap searchOf(T value);

    //获取指定索引的值
    T getValue(int index);

    //判断是否存在该值
    boolean contains(T value);

    Comparator<Integer> getComparator(boolean isAsc);
}
