package com.my.search.columstore;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述: 游程编码列存储
 * @author yu_wei  date:2025/2/26 <p>
 * @version v1.0
 */
public class RLEColumnStore<T> extends AbstractColumnStore<T>{

    private final List<T> rleValues = new ArrayList<>(16);// 存储不重复的值序列
    private final List<Integer> rleCounts = new ArrayList<>(16);// 存储连续出现次数
    private final AtomicInteger counter = new AtomicInteger();//记录行数

    public RLEColumnStore(String name, Class<T> dataType) {
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
    public Set<Integer> search(T value) {
        Set<Integer> returnSet = Sets.newTreeSet();
        for (int i = 0; i < rleValues.size(); i++) {
            if(rleValues.get(i).equals(value)){
                int startIndex = i == 0 ? 0 : rleCounts.subList(0,i).stream().mapToInt(Integer::intValue).sum();
                int endIndex = startIndex + rleCounts.get(i);
                for (int j = startIndex; j < endIndex; j++) {
                    returnSet.add(j);
                }
            }
        }
        return returnSet;
    }

    @Override
    public BitSet searchOf(T value) {
        BitSet bitSet = new BitSet();
        for (int i = 0; i < rleValues.size(); i++) {
            if(rleValues.get(i).equals(value)){
                int startIndex = i == 0 ? 0 : rleCounts.subList(0,i).stream().mapToInt(Integer::intValue).sum();
                int endIndex = startIndex + rleCounts.get(i);
                for (int j = startIndex; j < endIndex; j++) {
                    bitSet.set(j);
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
}
