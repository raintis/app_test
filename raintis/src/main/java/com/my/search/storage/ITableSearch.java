package com.my.search.storage;

import java.util.List;

/**
 * 描述: 表检索接口
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
public interface ITableSearch {
    List<List<Object>> search(ColValPairList pairList);
}
