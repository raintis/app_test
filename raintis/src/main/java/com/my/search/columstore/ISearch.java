package com.my.search.columstore;

import java.util.Set;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/2/26 <p>
 * @version v1.0
 */
public interface ISearch {
    Set<Integer> search(String columnName,Object value);
}
