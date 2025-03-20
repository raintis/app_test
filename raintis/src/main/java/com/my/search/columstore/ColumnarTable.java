package com.my.search.columstore;

import org.roaringbitmap.RoaringBitmap;

import java.util.*;

/**
 * 描述:列式存储表
 * @author yu_wei  date:2025/2/26 <p>
 * @version v1.0
 */
public class ColumnarTable implements ISearch{
    private final Map<String, IColumnStore<?>> groupColumns = new LinkedHashMap<>();//分组列
    private final Map<String, IColumnStore<?>> measureColumns = new LinkedHashMap<>();//聚合列
    private String[] measureNames = null;
    private int rowCount = 0;
    private boolean isUsedRoaringBitSet = false;

    public ColumnarTable(){
        this(false);
    }

    public ColumnarTable(boolean isUsedRoaringBitSet){
        this.isUsedRoaringBitSet = isUsedRoaringBitSet;
    }

    public void addGroupColumn(IColumnStore<?> column) {
        groupColumns.put(column.getName(), column);
    }

    public void addMeasureColumn(IColumnStore<?> column) {
        measureColumns.put(column.getName(), column);
    }

    public Set<Map.Entry<String, IColumnStore<?>>> getColumnEntry() {
        return groupColumns.entrySet();
    }

    public IColumnStore<?> getColumn(String columnName) {
        return groupColumns.get(columnName);
    }

    /**
     * 添加一行数据(多度量值的情况，降低查找同行的性能，要求度量维列和度量数一致）
     * @param columnsVals
     * @param measureVals
     */
    public void addBatchMeasureRow(Object[] columnsVals, Object... measureVals) {
        if (columnsVals.length != groupColumns.size()) {
            throw new IllegalArgumentException();
        }
        int findIndex = -1;
        //findIndex = groupBySetStyle(values);
        findIndex = isUsedRoaringBitSet ? groupByRoaringBitMapStyle(columnsVals) : groupByBitsetStyle(columnsVals);
        String[] measureName = getMeasureNames();
        if(measureName == null || measureName.length != measureVals.length){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < measureName.length; i++) {
            addMeasureRow(measureName[i],findIndex, measureVals[i]);
        }
    }

    /**
     * 添加一行数据(单度量值的情况）)
     * @param columnsVals
     * @param measureName
     * @param measureVal
     */
    public void addSingleMeasureRow(Object[] columnsVals,String measureName, Object measureVal) {
        if (columnsVals.length != groupColumns.size()) {
            throw new IllegalArgumentException();
        }
        int findIndex = -1;
        //findIndex = groupBySetStyle(columnsVals);
        findIndex =  groupByBitsetStyle(columnsVals);
        addMeasureRow(measureName,findIndex, measureVal);
    }

    private String[] getMeasureNames(){
        if(measureNames == null){
            measureNames = measureColumns.keySet().toArray(new String[0]);
        }
        return  measureNames;
    }

    private int groupByBitsetStyle(Object... values) {
        int i = 0;
        BitSet mergetSet = new BitSet();
        int findIndex = 0;
        for (IColumnStore<?> col : groupColumns.values()) {
            if (i ==0) {
                mergetSet = searchColumnWithBitset(col, values[i]);
            }else {
                mergetSet.and(searchColumnWithBitset(col, values[i]));
            }
            if(mergetSet.isEmpty()){
                findIndex = rowCount;
                rowCount++;
                break;
            }
            i++;
        }
        if(mergetSet.isEmpty()){
            i = 0;
            for (IColumnStore<?> col : groupColumns.values()) {
                addValueToGroupColumn(col, values[i++]);
            }
        }else{
            findIndex = mergetSet.stream().findFirst().orElse(-1);
        }
        return findIndex;
    }

    private int groupByRoaringBitMapStyle(Object... values) {
        int i = 0;
        RoaringBitmap mergetSet = new RoaringBitmap();
        int findIndex = 0;
        for (IColumnStore<?> col : groupColumns.values()) {
            if (i ==0) {
                mergetSet = searchColumnWithRoaringBitMap(col, values[i]);
            }else {
                mergetSet.and(searchColumnWithRoaringBitMap(col, values[i]));
            }
            if(mergetSet.isEmpty()){
                findIndex = rowCount;
                rowCount++;
                break;
            }
            i++;
        }
        if(mergetSet.isEmpty()){
            i = 0;
            for (IColumnStore<?> col : groupColumns.values()) {
                addValueToGroupColumn(col, values[i++]);
            }
        }else{
            findIndex = mergetSet.stream().findFirst().orElse(-1);
        }
        return findIndex;
    }

    private void groupBySetStyle(Object... values) {
        int i = 0;
        Set<Integer> crossIndex = new HashSet<>(8);
        for (IColumnStore<?> col : groupColumns.values()) {
            if (i ==0) {
                crossIndex.addAll(searchColumn(col, values[i]));
            }else {
                crossIndex.retainAll(searchColumn(col, values[i]));
            }
            if(crossIndex.isEmpty()){
                rowCount++;
                break;
            }
            i++;
        }
        if(crossIndex.isEmpty()){
            i = 0;
            for (IColumnStore<?> col : groupColumns.values()) {
                addValueToGroupColumn(col, values[i++]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void addValueToGroupColumn(IColumnStore<T> column, Object value) {
        column.addValue((T)value);
    }

    private void addMeasureRow(String columnName,int index, Object value) {
        addValueToMeasureColumn(this.measureColumns.get(columnName),index, value);
    }

    @SuppressWarnings("unchecked")
    private <T> void addValueToMeasureColumn(IColumnStore<T> column,int index, Object value) {
        column.addValue(index,(T)value);//行号从0开始记录
    }

    /**
     * 获取所有行数据数据
     * @return List<List<Object>>
     */
    public List<List<Object>> getData() {
        List<List<Object>> rows = new ArrayList<>();
        for(int i=0;i<rowCount;i++){
            List<Object> row = new ArrayList<>();
            for (IColumnStore<?> col : groupColumns.values()) {
                row.add(col.getValue(i));
            }
            for (IColumnStore<?> col : measureColumns.values()) {
                row.add(col.getValue(i));
            }
            rows.add(row);
        }
        return rows;
    }

    public int getRowCount() {
        return rowCount;
    }

    @Override
    public Set<Integer>  search(String columnName, Object value) {
        IColumnStore<?> column = groupColumns.get(columnName);
        if (column == null) {
            throw new IllegalArgumentException("Column not found: " + columnName);
        }
        return searchColumn(column,value);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<Integer> searchColumn(IColumnStore<T> column, Object value) {
        // 添加类型兼容性检查
        if (!column.getDataType().isInstance(value)) {
            throw new IllegalArgumentException("Type mismatch for column: " + column.getName());
        }
        return column.search((T) value);
    }

    @SuppressWarnings("unchecked")
    private <T> BitSet searchColumnWithBitset(IColumnStore<T> column, Object value) {
        // 添加类型兼容性检查
        if (!column.getDataType().isInstance(value)) {
            throw new IllegalArgumentException("Type mismatch for column: " + column.getName());
        }
        return column.searchOf((T) value);
    }

    @SuppressWarnings("unchecked")
    private <T> RoaringBitmap searchColumnWithRoaringBitMap(IColumnStore<T> column, Object value) {
        // 添加类型兼容性检查
        if (!column.getDataType().isInstance(value)) {
            throw new IllegalArgumentException("Type mismatch for column: " + column.getName());
        }
        return column.searchBigDataOf((T) value);
    }
}
