package com.my.search.columstore;

import java.util.*;

/**
 * @author yu_wei
 * @date 2023-2-26
 * description 普通列存储,主要是度量值列
 */
public class NormalColumnStore<T> extends AbstractColumnStore<T>{
    private final List<T> data = new ArrayList<>(16);

    public NormalColumnStore(String name, Class<T> dataType) {
        super(name, dataType);
    }

    @Override
    public void addValue(T value) {
        data.add(value);
    }

    @Override
    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public Set<Integer> search(T value) {
        Set<Integer> returnSet = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).equals(value)){
                returnSet.add(i);
            }
        }
        return returnSet;
    }

    @Override
    public BitSet searchOf(T value) {
        BitSet bitSet = new BitSet();
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).equals(value)){
                bitSet.set(i);
            }
        }
        return bitSet;
    }

    @Override
    public T getValue(int index) {
        if(index < 0 || index >= data.size()){
            throw new IndexOutOfBoundsException();
        }
        return data.get(index);
    }

    @Override
    public boolean contains(T value) {
        return data.contains(value);
    }
}
