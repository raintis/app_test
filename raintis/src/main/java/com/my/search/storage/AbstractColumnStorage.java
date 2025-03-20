package com.my.search.storage;

/**
 * 描述:列式存储抽象类，构造方法提供列名，数据类型
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public abstract class AbstractColumnStorage<T> implements IColumnStorage<T>{
    private final String name;
    private final Class<T> dataType;

    public AbstractColumnStorage(String name, Class<T> dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<T> getDataType() {
        return this.dataType;
    }

    public String toString(){
        return "name:"+this.name+",dataType:"+this.dataType;
    }
}
