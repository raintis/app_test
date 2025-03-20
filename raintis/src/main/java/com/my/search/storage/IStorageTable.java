package com.my.search.storage;

/**
 * 描述: 存储表接口
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public interface IStorageTable {
    /**
     * 增加单个度量值
     * @param colVales 确定唯一分组的列值
     * @param measureName 度量列名
     * @param measure 度量值
     */
    void addMeasureByRow(Object[] colVales, String measureName,Object measure);
    /**
     * 添加多个度量值，且在同一行上
     * @param colVales 确定唯一分组的列值
     * @param measures 多个度量值数组
     */
    void addMultiMeasureByRow(Object[] colVales, Object... measures);

    /**
     * 增加分组列
     * @param column 列
     */
    void addGroupColumn(IColumnStorage<?> column);
    /**
     * 增加度量列
     * @param column 列
     */
    void addMeasureColumn(IColumnStorage<?> column);

    int getRowCount();
}
