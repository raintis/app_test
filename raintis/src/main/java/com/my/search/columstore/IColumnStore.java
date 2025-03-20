package com.my.search.columstore;

import org.roaringbitmap.RoaringBitmap;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * description: 列存储接口
 * Created by yu_wei on 2017/07/05.
 * @param <T>
 */
public interface IColumnStore<T> {
    //添加数据
    void addValue(T value);

    //稀疏维使用
    default void addValue(int index,T value){

    }

    String getName();

    Class<T> getDataType();
    //获取数据
    List<T> getData();

    //获取去重后的数据
    Set<T> getDistinctData();

    //获取值对应所有行索引
    Set<Integer> search(T value);

    //获取值对应所有行索引,返回bitset
    BitSet searchOf(T value);

    default  RoaringBitmap searchBigDataOf(T value){
        return new RoaringBitmap();
    }

    //获取指定索引的值
    T getValue(int index);

    //判断是否存在
    boolean contains(T value);
}
