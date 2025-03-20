package com.my.search.storage;

import org.roaringbitmap.RoaringBitmap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述: 游程编码列存储,适用于列成员值数量比较少,且频繁出现的场景，例如财年、期间、组织、币别等维度
 * @author yu_wei  date:2025/2/26 <p>
 * @version v1.0
 */
public class RLEColumnStorage<T> extends AbstractColumnStorage<T>{
    private final List<T> rleValues = new ArrayList<>(16);// 存储不重复的值序列
    private final List<Integer> rleCounts = new ArrayList<>(16);// 存储连续出现次数
    private final AtomicInteger counter = new AtomicInteger();//记录行数

    public RLEColumnStorage(String name, Class<T> dataType) {
        super(name, dataType);
    }

    @Override
    public void addValue(T value) {
        if (rleValues.isEmpty()) {
            rleValues.add(value);
            rleCounts.add(1);
        } else {
            T lastValue = rleValues.get(rleValues.size() - 1);
            if (lastValue.equals(value)) {
                rleCounts.set(rleCounts.size() - 1, rleCounts.get(rleCounts.size() - 1) + 1);
            } else {
                rleValues.add(value);
                rleCounts.add(1);
            }
        }
        counter.incrementAndGet();
    }

    @Override
    public List<T> getData() {
        List<T> decoded = new ArrayList<>();
        for (int i = 0; i < rleValues.size(); i++) {
            T value = rleValues.get(i);
            int count = rleCounts.get(i);
            decoded.addAll(Collections.nCopies(count, value));
        }
        return decoded;
    }

    @Override
    public Set<T> getDistinctData() {
        return new HashSet<>(rleValues);
    }

    @Override
    public RoaringBitmap searchOf(T value) {
        RoaringBitmap bitSet = new RoaringBitmap();
        for (int i = 0; i < rleValues.size(); i++) {
            if(rleValues.get(i).equals(value)){
                int startIndex = i == 0 ? 0 : rleCounts.subList(0,i).stream().mapToInt(Integer::intValue).sum();
                int endIndex = startIndex + rleCounts.get(i);
                for (int j = startIndex; j < endIndex; j++) {
                    bitSet.add(j+1);//bitset的索引从1开始
                }
            }
        }
        return bitSet;
    }

    @Override
    public T getValue(int index) {
        if(index <0 || index >= this.counter.get()){
            throw new IndexOutOfBoundsException();
        }
        int i = 0;
        while (i < rleValues.size()) {
            int count = rleCounts.get(i);
            if (index < count) {
                return rleValues.get(i);
            }
            index -= count;
            i++;
        }
        return null;
    }

    @Override
    public boolean contains(T value) {
        return this.rleValues.contains(value);
    }

    @Override
    public Comparator<Integer> getComparator(boolean isAsc) {
        return null;
    }
}
