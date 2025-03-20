package com.my.search.storage;

import com.my.search.columstore.IColumnStore;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 描述: 列式存储表
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public class ColumnStorageTable implements IStorageTable,ITableSearch{
    private final Map<String, IColumnStorage<?>> groupColumns = new LinkedHashMap<>();//分组列
    private final Map<String, IColumnStorage<?>> measureColumns = new LinkedHashMap<>();//聚合列
    private String[] measureNames = null;
    private int rowCount = 0;
    private SortConfig sortConfig = null;
    /**
     * 增加单个度量值
     * @param colVales 确定唯一分组的列值
     * @param measureName 度量列名
     * @param measure 度量值
     */
    public void addMeasureByRow(Object[] colVales, String measureName,Object measure) {
        checkArgument(colVales, (Object) null);
        int findIndex = groupByValues(colVales);
        addMeasureRow(measureName, findIndex, measure);
    }

    public void addSort(String columnName, boolean isAsc){
        if(this.sortConfig == null){
            this.sortConfig = new SortConfig();
        }
        sortConfig.addSortItem(columnName,isAsc);
    }

    /**
     * 添加多个度量值，且在同一行上
     * @param colVales 确定唯一分组的列值
     * @param measures 多个度量值数组
     */
    public void addMultiMeasureByRow(Object[] colVales, Object... measures) {
        checkArgument(colVales, measures);
        String[] measureName = getMeasureNames();
        int findIndex = groupByValues(colVales);
        for (int i = 0; i < measureName.length; i++) {
            addMeasureRow(measureName[i],findIndex, measures[i]);
        }
    }

    public void addGroupColumn(IColumnStorage<?> column) {
        groupColumns.put(column.getName(), column);
    }

    public void addMeasureColumn(IColumnStorage<?> column) {
        measureColumns.put(column.getName(), column);
    }

    @Override
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * 获取所有行数据数据
     * @return List<List<Object>>
     */
    public List<List<Object>> getData() {
        List<Integer> indexes = IntStream.range(0, rowCount).boxed().collect(Collectors.toList());
        if(sortConfig != null){
            Comparator<Integer> current = null;
            for (SortConfig.SortItem sortItem : sortConfig) {
                IColumnStorage<?> c = this.groupColumns.get(sortItem.columnName);
                c = c == null ? this.measureColumns.get(sortItem.columnName) : c;
                if(current == null){
                    current = c.getComparator(sortItem.isAsc);
                }else{
                    current.thenComparing(c.getComparator(sortItem.isAsc));
                }
            }
            if(current != null) {
                indexes = indexes.parallelStream().sorted(current).collect(Collectors.toList());
            }
        }
        List<List<Object>> rows = new ArrayList<>(rowCount);
        for(int i : indexes){
            List<Object> row = new ArrayList<>();
            for (IColumnStorage<?> col : groupColumns.values()) {
                row.add(col.getValue(i));
            }
            for (IColumnStorage<?> col : measureColumns.values()) {
                row.add(col.getValue(i));
            }
            rows.add(row);
        }
        return rows;
    }

    private void addMeasureRow(String columnName,int index, Object value) {
        addValueToMeasureColumn(this.measureColumns.get(columnName),index, value);
    }

    @SuppressWarnings("unchecked")
    private <T> void addValueToMeasureColumn(IColumnStorage<T> column,int index, Object value) {
        column.addValue(index,(T)value);//行号从0开始记录
    }

    private int groupByValues(Object... values) {
        int i = 0;
        RoaringBitmap mergeSet = new RoaringBitmap();
        int findIndex = 0;
        for (IColumnStorage<?> col : groupColumns.values()) {
            if (i ==0) {
                mergeSet.or(searchOf(col, values[i]));
            }else {
                mergeSet.and(searchOf(col, values[i]));
            }
            if(mergeSet.isEmpty()){//未找到完全匹配的行记录
                findIndex = rowCount;
                rowCount++;
                break;
            }
            i++;
        }
        if(mergeSet.isEmpty()){
            i = 0;
            for (IColumnStorage<?> col : groupColumns.values()) {
                addValueToGroupColumn(col, values[i++]);
            }
        }else{
            findIndex = mergeSet.stream().findFirst().orElse(0)-1;//BitSet从1开始记录，这里需要减一
        }
        return findIndex;
    }

    @SuppressWarnings("unchecked")
    private <T> void addValueToGroupColumn(IColumnStorage<T> column, Object value) {
        column.addValue((T)value);
    }

    @SuppressWarnings("unchecked")
    private <T> RoaringBitmap searchOf(IColumnStorage<T> column, Object value) {
        // 添加类型兼容性检查
        if (!column.getDataType().isInstance(value)) {
            throw new IllegalArgumentException("Type mismatch for column: " + column.getName());
        }
        return column.searchOf((T) value);
    }

    private String[] getMeasureNames(){
        if(measureNames == null){
            measureNames = measureColumns.keySet().toArray(new String[0]);
        }
        return  measureNames;
    }

    private void checkArgument(Object[] colVales, Object... measures) {
        if (colVales != null && colVales.length != groupColumns.size()) {
            throw new IllegalArgumentException("size of colVales [%"+colVales.length+"] is not equals need " + groupColumns.size());
        }
        if (measures != null && measures.length != measureColumns.size()) {
            throw new IllegalArgumentException("size of measures [%"+measures.length+"] is not equals need " + measureColumns.size());
        }
    }

    @Override
    public List<List<Object>> search(ColValPairList pairList) {
        RoaringBitmap mergeSet = new RoaringBitmap();
        boolean isFirst = true;
        for (ColValPair<?> pair : pairList) {
            if(isFirst){
                mergeSet = searchOf(groupColumns.get(pair.columnName), pair.value);
                isFirst =false;
                continue;
            }
            mergeSet.and(searchOf(groupColumns.get(pair.columnName), pair.value));
            if(mergeSet.isEmpty()){
                break;
            }
        }
        List<List<Object>> result = new ArrayList<>();
        for (int i : mergeSet) {
            List<Object> row = new ArrayList<>();
            for (IColumnStorage<?> col : groupColumns.values()) {
                row.add(col.getValue(i));
            }
            for (IColumnStorage<?> col : measureColumns.values()) {
                row.add(col.getValue(i));
            }
            result.add(row);
        }
        return result;
    }
}
