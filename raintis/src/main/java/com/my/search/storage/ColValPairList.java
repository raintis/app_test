package com.my.search.storage;

import java.util.ArrayList;

/**
 * 描述: 多列值对
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public class ColValPairList extends ArrayList<ColValPair<?>> {
    public static <V> ColValPairList of(String columnName, V value)
    {
        ColValPairList colValPairList = new ColValPairList();
        colValPairList.add(ColValPair.of(columnName, value));
        return colValPairList;
    }
}
