package com.my.search.columstore;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractColumnStore<T> implements IColumnStore<T> {
    private final String name;
    private final Class<T> dataType;

    public AbstractColumnStore(String name, Class<T> dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public Class<T> getDataType() {
        return dataType;
    }

    @Override
    public Set<T> getDistinctData() {
        return new HashSet<>(getData());
    }
}
