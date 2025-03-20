package com.my.search.columstore;

import org.roaringbitmap.RoaringBitmap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * description: 字典列存储
 * attention: 非线程安全，避免多线并发使用
 * @author yu_wei date: 2025/2/23
 * @param <T>
 */
public class DicColumnStore4BigData<T> extends AbstractColumnStore<T> {

    private final Map<T,Integer> valueDictionary = new HashMap<>();
    private final List<Integer> encodedData = new ArrayList<>();
    private final List<T> reverseDictionary = new ArrayList<>();
    private final RoaringBitmap EMPTY_BITSET = new RoaringBitmap();

    private final Map<T, RoaringBitmap> valueDictionaryBitSet = new HashMap<>();
    private final AtomicInteger rowCount = new AtomicInteger();

    public DicColumnStore4BigData(String name, Class<T> dataType) {
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
    public Set<Integer> search(T value) {
        return this.valueDictionaryBitSet.getOrDefault(value,EMPTY_BITSET).stream().boxed().collect(Collectors.toSet());
    }

    @Override
    public BitSet searchOf(T value) {
        return new BitSetAdapter(this.valueDictionaryBitSet.getOrDefault(value,EMPTY_BITSET));
    }

    @Override
    public T getValue(int index) {
        if(index < 0 || index >= encodedData.size()){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + encodedData.size());
        }
        return reverseDictionary.get(encodedData.get(index));
    }

    public RoaringBitmap searchBigDataOf(T value){
        return this.valueDictionaryBitSet.getOrDefault(value,EMPTY_BITSET);
    }

    @Override
    public boolean contains(T value) {
        return valueDictionaryBitSet.containsKey(value);
    }

/*    static class SimpleVal<T>{
        private  int index;
        private  T value;

        static <T> SimpleVal<T> of(int index,T value){
            SimpleVal<T> v = new SimpleVal<>();
            v.index = index;
            v.value = value;
            return v;
        }
    }*/

    static class BitSetAdapter extends BitSet {
        private final RoaringBitmap target;

        BitSetAdapter(RoaringBitmap target) {
            this.target = target;
        }

        @Override
        public void flip(int bitIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(int bitIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void and(BitSet set) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void or(BitSet set) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void xor(BitSet set) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void andNot(BitSet set) {
            throw new UnsupportedOperationException();
        }

        // 重写所有修改方法...

        @Override
        public int cardinality() {
            return target.getCardinality();
        }

        @Override
        public boolean get(int bitIndex) {
            return target.contains(bitIndex);
        }
    }
}
