package com.my.search;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 让测试类只创建一个实例
public class DataIndexer {
    // 哈希索引（精确匹配）
    private final static Map<String, List<Data>> hashIndex = new ConcurrentHashMap<>();

    // 跳表索引（范围查询）
    private final static ConcurrentSkipListMap<String, Data> sortedIndex = new ConcurrentSkipListMap<>();

    // 倒排索引（LIKE查询）
    private final static Map<String, Set<Data>> invertedIndex = new ConcurrentHashMap<>();

    // 数据存储
    //private final List<Data> dataStore = new CopyOnWriteArrayList<>();
    private static final String[] arr = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

   // @BeforeClass// 非静态方法也可用
    public static void initData(){
        long start = System.currentTimeMillis();;
        Random random = new Random();;
        for(int i=0;i<1000000;i++){
            String key = arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)];
            Data data = new Data(key);
            indexData(data);
            //dataStore.add(data);
        }
        Data data = new Data("data");
        indexData(data);
        System.out.println("init data---->"+sortedIndex.size()+";--pastime:" + (System.currentTimeMillis() - start));
    }
    //@Test
    public void testIndexData() {
        String[] range = {"a","c"};
        String like = "dat%";
        String equals = "data";
        System.out.println("seed pools---->"+sortedIndex.keySet());
        long start = System.currentTimeMillis();
        System.out.println("search equals["+equals+"]---->"+exactQuery(equals)+";--pastime:" + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        System.out.println("search range["+range[0]+","+range[1]+"]---->"+rangeQuery(range[0], range[1])+";--pastime:" + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        System.out.println("search like["+like+"]---->"+likeQuery(like)+";--pastime:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testBitSet() {
        BitSet bitSet = new BitSet();
        bitSet.set(1,5);
        BitSet bitSet2 = new BitSet();
        bitSet2.set(3,9);
        BitSet bitSet3 = new BitSet();
        bitSet3.set(8,10);
        BitSet bitSet4 = new BitSet();
        bitSet4.set(9,9);
//        System.out.println("bitSet:"+bitSet.cardinality());
//        System.out.println("bitSet2:"+bitSet2.cardinality());
//        bitSet.and(bitSet2);
//        System.out.println("bitSet:"+bitSet.cardinality());
        bitSet.and(bitSet2);
        bitSet.and(bitSet3);
        bitSet.and(bitSet4);
        System.out.println("bitSet:"+bitSet.isEmpty());
        System.out.println("bitSet:"+ Arrays.toString(bitSet.stream().toArray()));
    }

    @Test
    public void testEquals(){
        String equals = "data";
        long start = System.currentTimeMillis();
        System.out.println("search equals["+equals+"]---->"+exactQuery(equals)+";--pastime:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testRange(){
        String[] range = {"a","c"};
        long start = System.currentTimeMillis();
        System.out.println("search range["+range[0]+","+range[1]+"]---->"+rangeQuery(range[0], range[1])+";--pastime:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testLike(){
        String like = "dat%";
        long start = System.currentTimeMillis();
        System.out.println("search like["+like+"]---->"+likeQuery(like)+";--pastime:" + (System.currentTimeMillis() - start)+";--pastime:" + (System.currentTimeMillis() - start));
    }

    protected static void indexData(Data data) {
        // 哈希索引
        hashIndex.computeIfAbsent(data.getValue(), k -> new CopyOnWriteArrayList<>()).add(data);

        // 跳表索引
        sortedIndex.put(data.getValue(), data);

        // 倒排索引（n-gram分词）
        for (int i = 0; i < data.getValue().length() ; i++) {
            String gram = data.getValue().substring(0, i + 1);
            invertedIndex.computeIfAbsent(gram, k -> ConcurrentHashMap.newKeySet()).add(data);
        }
    }

    // 精确查询
    protected List<Data> exactQuery(String value) {
        return hashIndex.getOrDefault(value, Collections.emptyList());
    }

    // 范围查询
    protected List<Data> rangeQuery(String lower, String upper) {
        return new ArrayList<>(sortedIndex.subMap(lower, upper).values());
    }

    // 模糊查询
    protected Set<Data> likeQuery(String pattern) {
        Set<Data> result = new HashSet<>();
        String searchKey = pattern.replace("%", "");
        if (searchKey.length() >= 3) {
            String gram = searchKey.substring(0, 3);
            Set<Data> candidates = invertedIndex.getOrDefault(gram, Collections.emptySet());
            for (Data data : candidates) {
                if (data.getValue().contains(searchKey)) {
                    result.add(data);
                }
            }
        }
        return result;
    }

    static class Data {
        private final String value;

        Data(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
        public String toString() {
            return value;
        }
    }
}
