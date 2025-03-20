package com.my.search.columstore;

import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/2/27 <p>
 * @version v1.0
 */
public class MultiColumnStore<T extends KeyValue> extends AbstractColumnStore<T>{
    private final ColumnarTable table = new ColumnarTable();

    public MultiColumnStore(String name, Class<T> dataType) {
        super(name, dataType);
    }

    public void addColumn(IColumnStore<?> column) {
        if(table.getColumn(column.getName()) != null){
            throw new IllegalArgumentException("Column already exists: " + column.getName());
        }
        table.addGroupColumn(column);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addValue(T value) {
        IColumnStore<Object> column = (IColumnStore<Object>) table.getColumn(value.key);
        addInnerColumn(column,value.value);
    }

    private <V> void addInnerColumn(IColumnStore<V> column,V value) {
        column.addValue(value);
    }

    @Override
    public List<T> getData() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<Integer> search(T value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BitSet searchOf(T value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public T getValue(int index) {
        try {
            // 获取所有列的值集合
            List<Object> values = table.getColumnEntry().stream()
                    .map(e -> e.getValue().getValue(index))
                    .collect(java.util.stream.Collectors.toList());

            // 通过反射创建具体泛型类型的实例
            return this.getDataType().getConstructor(String.class, List.class)
                    .newInstance(this.getName(), values);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create value instance", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Object getValue(String columnName, int index) {
        IColumnStore<Object> column = (IColumnStore<Object>) table.getColumn(columnName);
        return column.getValue(index);
    }

    @Override
    public boolean contains(T value) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
