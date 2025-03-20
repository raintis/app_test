package com.my.search.columstore;

import java.util.*;

/**
 * 描述: 稀疏列存储，主要是度量值的存储
 * @author yu_wei  date:2025/2/27 <p>
 * @version v1.0
 */
public class SparseColumnStore<T> extends AbstractColumnStore<T>{
    //稀疏列使用map<行号，值>存储数据
    private final Map<Integer, T> data = new LinkedHashMap<>();

    public SparseColumnStore(String name, Class<T> dataType) {
        super(name, dataType);
    }

    @Override
    public void addValue(T value) {
        throw new UnsupportedOperationException();
    }

    public void addValue(int index,T value){
        data.put(index,value);
    }

    @Override
    public List<T> getData() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Set<Integer> search(T value) {
        Set<Integer> treeSet = new TreeSet<>();
        for (Map.Entry<Integer, T> entry : data.entrySet()) {
            if(entry.getValue().equals(value)){
                treeSet.add(entry.getKey());
            }
        }
        return treeSet;
    }

    @Override
    public BitSet searchOf(T value) {
        BitSet bitSet = new BitSet();
        for (Map.Entry<Integer, T> entry : data.entrySet()) {
            if(entry.getValue().equals(value)){
                bitSet.set(entry.getKey());
            }
        }
        return bitSet;
    }

    @Override
    public T getValue(int index) {
        return data.get(index);
    }

    @Override
    public boolean contains(T value) {
        throw new UnsupportedOperationException();
    }
}
