package com.my.search;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 测试 ConcurrentSkipListMap的范围检索性能
 */
public class ConcurrentSkipListMapTest {
    private static final String[] arr = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    @Test
    public void testConcurrentSkipListMap(){
        ConcurrentSkipListMap<String,String> skipListMap = new ConcurrentSkipListMap<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            String key = arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)]+arr[random.nextInt(arr.length)];
            skipListMap.put(key,key);
        }
        skipListMap.put("AA","AA");
        skipListMap.put("bobby","bobby");
        int count = 0;
        long start = System.currentTimeMillis();
        //注意传入的范围的key值是否是一个从小到大的顺序
        System.out.println("search-rs:" + skipListMap.subMap("a",true, "c",false).keySet().size() + " pastime:"+(System.currentTimeMillis()-start));
        for (String s : skipListMap.subMap("A",true, "c",false).keySet()) {
            System.out.println(s);
            count++;
            /*if(s.equals("bobby")){
                System.out.println("bobby"+"------------------------------>>>>>>>>>>>>>>>>>");
                //break;
            }*/
        }
        System.out.println("===========>"+count);
        System.out.println(skipListMap.keySet());
        System.out.println("a".compareTo("b") +"||" + "AA".compareTo("a"));
    }
}
