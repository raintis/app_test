package com.my.search.columstore;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/2/27 <p>
 * @version v1.0
 */
public class KeyValue {
    public String key;
    public Object value;

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }
    public String toString() {
        return "KeyValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    public String getKey(){
        return key;
    }
}
