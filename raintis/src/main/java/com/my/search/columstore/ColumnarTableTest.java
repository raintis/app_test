package com.my.search.columstore;

import org.apache.lucene.util.RamUsageEstimator;
import org.ehcache.sizeof.SizeOf;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/2/26 <p>
 * @version v1.0
 */
public class ColumnarTableTest {

    private final String[] subjects = {"数学", "语文", "英语", "物理"};


    @Test
    public void testNormalColumnStore() {
        ColumnarTable table = new ColumnarTable();
        table.addGroupColumn(new NormalColumnStore<>("name", String.class));
        table.addGroupColumn(new NormalColumnStore<>("age", String.class));
        table.addGroupColumn(new NormalColumnStore<>("address", String.class));
        for(String subject : subjects){
            table.addMeasureColumn(new SparseColumnStore<>(subject,Object.class));
        }
        System.out.println("-----------------testNormalColumnStore----------------------");
        buildBigData(table);
        calculateSize(table);
        iterator(table);
        /*System.out.println(table.getColumn("name").getData());
        System.out.println(table.getColumn("age").getData());
        System.out.println(table.getColumn("address").getData());
        System.out.println(table.search("name", "张三"));*/

        search(table);
    }

    @Test
    public void testRleColumnStore() {
        ColumnarTable table = new ColumnarTable();
        table.addGroupColumn(new RLEColumnStore<>("name", String.class));
        table.addGroupColumn(new RLEColumnStore<>("age", String.class));
        table.addGroupColumn(new RLEColumnStore<>("address", String.class));
        for(String subject : subjects){
            table.addMeasureColumn(new SparseColumnStore<>(subject,Object.class));
        }

        System.out.println("-----------------testRleColumnStore----------------------");
        buildBigData(table);
        calculateSize(table);
        iterator(table);
        /*System.out.println(table.getColumn("name").getData());
        System.out.println(table.getColumn("age").getData());
        System.out.println(table.getColumn("address").getData());
        System.out.println(table.search("name", "张三"));*/

        search(table);
    }

    @Test
    public void testDicColumnStore() {
        ColumnarTable table = new ColumnarTable();
        table.addGroupColumn(new DicColumnStore<>("name", String.class));
        table.addGroupColumn(new DicColumnStore<>("age", String.class));
        table.addGroupColumn(new DicColumnStore<>("address", String.class));
        for(String subject : subjects){
            table.addMeasureColumn(new SparseColumnStore<>(subject,Object.class));
        }
        System.out.println("-----------------testDicColumnStore----------------------");
        buildBigData(table);

        calculateSize(table);
        iterator(table);
        /*System.out.println(table.getColumn("name").getData());
        System.out.println(table.getColumn("age").getData());
        System.out.println(table.getColumn("address").getData());
        System.out.println(table.search("name", "张三"));*/

        search(table);
    }

    private void search(ColumnarTable  table){
        Set<Integer> rowIndexes = table.search("name", "张三");
        int count = 0;
        for (int rowIndex : rowIndexes) {
            if(count == 10) break;
            System.out.println(table.getColumn("name").getValue(rowIndex) +"|"+ table.getColumn("age").getValue(rowIndex)+"|"+table.getColumn("address").getValue(rowIndex));
            count++;
        }
    }

    private void iterator(ColumnarTable  table){
        table.getData().forEach(System.out::println);
    }

    private void calculateSize(ColumnarTable  table){
        SizeOf sizeof = new org.ehcache.sizeof.impl.UnsafeSizeOf();

        long size = sizeof.deepSizeOf(table);//统计对象大小
        System.out.println(RamUsageEstimator.humanReadableUnits(size));
    }
    private void buildBigData(ColumnarTable  table){
        String[] names = {"张三", "李四", "王五", "赵六","李里","李A","李B","李C","李里D","李E"};
        String[] ages = {"18", "19", "20", "21"};
        String[] addresses = {"北京", "上海", "广州", "深圳","USA","HK","JAP"};
        long starttime = System.currentTimeMillis();
        Random random = new Random();
        int rowCount = 1_000_00;
        for (int i = 0; i < rowCount; i++) {
            for(int j=0;j<10;j++) {
                table.addBatchMeasureRow(new String[]{names[random.nextInt(names.length)], ages[random.nextInt(ages.length)], addresses[random.nextInt(addresses.length)]},subjects[j % subjects.length],random.nextInt(100));
                //table.addMeasureRow(subjects[j % subjects.length],random.nextInt(100));
            }
        }
        System.out.println("-----------------buildBigData---------------------->" + (System.currentTimeMillis() - starttime));
        /*table.addRow("张三", "18", "北京");
        table.addRow("张三", "18", "北京");
        table.addRow("张三", "18", "北京");
        table.addRow("张三", "18", "北京");
        table.addRow("张三", "18", "北京");
        table.addRow("李四", "19", "上海");
        table.addRow("李四", "19", "上海");
        table.addRow("李四", "19", "上海");
        table.addRow("李四", "19", "上海");
        table.addRow("王五", "20", "广州");
        table.addRow("王五", "20", "广州");
        table.addRow("王五", "20", "广州");
        table.addRow("王五", "20", "广州");
        table.addRow("赵六", "21", "深圳");
        table.addRow("赵六", "21", "深圳");
        table.addRow("赵六", "21", "深圳");
        table.addRow("赵六", "21", "深圳");
        table.addRow("李四", "19", "上海");
        table.addRow("王五", "20", "广州");
        table.addRow("赵六", "21", "深圳");*/
    }
}
