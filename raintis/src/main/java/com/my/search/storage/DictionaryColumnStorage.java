package com.my.search.storage;

import org.roaringbitmap.RoaringBitmap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 描述: 字典映射方式的列式存储，会冗余数据但查询性能更优，以空间换取时间
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public class DictionaryColumnStorage<T> extends AbstractColumnStorage<T>{
    private final Map<T,Integer> valueDictionary = new HashMap<>();
    private final List<Integer> encodedData = new ArrayList<>();
    private final List<T> reverseDictionary = new ArrayList<>();
    private final RoaringBitmap EMPTY_BITSET = new RoaringBitmap();

    private final Map<T, RoaringBitmap> valueDictionaryBitSet = new HashMap<>();
    private final AtomicInteger rowCount = new AtomicInteger(1);

    public DictionaryColumnStorage(String name, Class<T> dataType) {
        super(name, dataType);
    }

    @Override
    public void addValue(T value) {
        int code = valueDictionary.computeIfAbsent(value, k -> {
            reverseDictionary.add(k);
            return reverseDictionary.size() - 1;
        });
        encodedData.add(code);
        //值对应一个bitset，为了快速检索使用
        valueDictionaryBitSet.computeIfAbsent(value, k -> new RoaringBitmap()).add(rowCount.getAndIncrement());
    }

    @Override
    public List<T> getData() {
        return encodedData.stream()
                .map(reverseDictionary::get)
                .collect(Collectors.toList());
    }

    @Override
    public Set<T> getDistinctData() {
        return new HashSet<>(reverseDictionary);
    }

    @Override
    public RoaringBitmap searchOf(T value) {
        return this.valueDictionaryBitSet.getOrDefault(value,EMPTY_BITSET);
    }

    @Override
    public T getValue(int index) {
        if(index < 0 || index >= encodedData.size()){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + encodedData.size());
        }
        return reverseDictionary.get(encodedData.get(index));
    }

    @Override
    public boolean contains(T value) {
        return valueDictionaryBitSet.containsKey(value);
    }

    @Override
    public Comparator<Integer> getComparator(boolean isAsc) {
        return (o1, o2) ->{
            T v1 = reverseDictionary.get(this.encodedData.get(o1));
            T v2 = reverseDictionary.get(this.encodedData.get(o2));
            return isAsc ? v1.toString().compareTo(v2.toString()) : v2.toString().compareTo(v1.toString());
        };
    }
}
