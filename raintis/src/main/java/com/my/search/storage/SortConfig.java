package com.my.search.storage;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/3/14 <p>
 * @version v1.0
 */
public class SortConfig implements Iterable<SortConfig.SortItem> {
    private final List<SortItem> sortItems = new ArrayList<>(8);
    public SortConfig(){
    }

    public void addSortItem(String columnName,boolean isAsc){
        SortItem sortItem=new SortItem();
        sortItem.columnName=columnName;
        sortItem.isAsc=isAsc;
        this.sortItems.add(sortItem);
    }

    @Override
    public Iterator<SortItem> iterator() {
        return sortItems.iterator();
    }

    static class SortItem{
         String columnName;
         boolean isAsc;
    }
}
