package com.my.search.columstore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * description: 字典列存储
 * @author yu_wei date: 2025/2/23
 * @param <T>
 */
public class DicColumnStoreOld<T> extends AbstractColumnStore<T> {

    private final Map<T,Integer> valueDictionary = new HashMap<>();
    private final List<Integer> encodedData = new ArrayList<>();
    private final List<T> reverseDictionary = new ArrayList<>();

    public DicColumnStoreOld(String name, Class<T> dataType) {
        super(name, dataType);
    }

    @Override
    public void addValue(T value) {
        int code = valueDictionary.computeIfAbsent(value, k -> {
            reverseDictionary.add(k);
            return reverseDictionary.size() - 1;
        });
        encodedData.add(code);
    }


    @Override
    public List<T> getData() {
        return encodedData.stream()
                .map(reverseDictionary::get)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Integer> search(T value) {
        Integer index = valueDictionary.get(value);
        Set<Integer> result = new HashSet<>();
        if (index != null) {
            for (int i = 0; i < encodedData.size(); i++) {
                if(encodedData.get(i).equals(index)){
                    result.add(i);
                }
            }
        }
        return result;
    }

    @Override
    public BitSet searchOf(T value) {
        Integer index = valueDictionary.get(value);
        BitSet result = new BitSet();
        if (index != null) {
            for (int i = 0; i < encodedData.size(); i++) {
                if(encodedData.get(i).equals(index)){
                    result.set(i);
                }
            }
        }
        return result;
    }

    @Override
    public T getValue(int index) {
        if(index < 0 || index >= encodedData.size()){
            throw new IndexOutOfBoundsException();
        }
        return reverseDictionary.get(encodedData.get(index));
    }

    @Override
    public boolean contains(T value) {
        return valueDictionary.containsKey(value);
    }
}
