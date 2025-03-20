package com.my.search.storage;

import org.roaringbitmap.RoaringBitmap;

import java.util.*;

/**
 * 描述:稀疏列存储，主要是度量值的存储
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public class SparseColumnStorage<T> extends AbstractColumnStorage<T>{
    //稀疏列使用map<行号，值>存储数据,链表的目的是保证值顺序写入的（是否有必要?）
    private final LinkedHashMap<Integer, T> data = new LinkedHashMap<>();

    public SparseColumnStorage(String name, Class<T> dataType) {
        super(name, dataType);
    }

    @Override
    public void addValue(T value) {
        throw new UnsupportedOperationException("please use function addValue(int index,T value)");
    }
    public void addValue(int index,T value){
        data.put(index,value);
    }

    @Override
    public List<T> getData() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Set<T> getDistinctData() {
        return new HashSet<>(data.values());
    }

    @Override
    public RoaringBitmap searchOf(T value) {
        RoaringBitmap bitSet = new RoaringBitmap();
        for (Map.Entry<Integer, T> entry : data.entrySet()) {
            if(entry.getValue().equals(value)){
                bitSet.add(entry.getKey()+1);//索引从1开始
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
        return data.containsValue(value);
    }

    @Override
    public Comparator<Integer> getComparator(boolean isAsc) {
        return (o1, o2) ->{
            T v1 = data.get(o1);
            T v2 = data.get(o2);
            if(v1==null && v2==null){
                return 0;
            }else if(v1 == null){
                return -1;
            }else if (v2 == null){
                return 1;
            }
            if(v1.equals(v2)){
                return 0;
            }
            if(v1 instanceof Comparable && v2 instanceof Comparable){
                return isAsc ? ((Comparable) v1).compareTo(v2) : ((Comparable) v2).compareTo(v1);
            }
            return isAsc ? v1.toString().compareTo(v2.toString()) : v2.toString().compareTo(v1.toString());
        };
    }
}
