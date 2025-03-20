package com.my.search.columstore;

import org.apache.lucene.util.RamUsageEstimator;
import org.ehcache.sizeof.SizeOf;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/2/26 <p>
 * @version v1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ColumnarTableBigDataTest {

    private final String[] subjects = {"数学", "语文", "英语", "物理", "化学", "生物", "政治", "体育", "科学", "计算机"};

    private final static int dimSize = 100;//维度数

    private final static Map<String,String[]> dimMembData = new LinkedHashMap<>();//构造每个维度的成员列表，从50到100不等的成员数

     @BeforeClass// 非静态方法也可用
    public static void initDimensionData(){
        int[] dimMenbSize = new Random().ints(dimSize, 50, 100).toArray();
        for (int i = 0; i < dimSize; i++) {
           int membSize = dimMenbSize[i];
            String[] dimData = new String[membSize];
            for (int j = 0; j < membSize; j++) {
                   dimData[j] = "维度"+i +"[" + j +"]";
           }
            dimMembData.put("维度" + i, dimData);
        }
    }


    @Test
    public void testNormalColumnStore() {
        ColumnarTable table = new ColumnarTable();
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new NormalColumnStore<>(dimName, String.class));
        });
        for(String subject : subjects){
            table.addMeasureColumn(new SparseColumnStore<>(subject,Object.class));
        }
        System.out.println("-----------------testNormalColumnStore----------------------");
        buildBigData(table);
        calculateSize(table);
        iterator(table);
        search(table);
    }

    //@Test
    public void testRleColumnStore() {
        ColumnarTable table = new ColumnarTable();
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new RLEColumnStore<>(dimName, String.class));
        });
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
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new DicColumnStore<>(dimName, String.class));
        });
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

    @Test
    public void testRoaringBitMapDicColumnStore() {
        ColumnarTable table = new ColumnarTable(true);
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new DicColumnStore4BigData<>(dimName, String.class));
        });
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

    @Test
    public void testOldDicColumnStore() {
        ColumnarTable table = new ColumnarTable();
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new DicColumnStoreOld<>(dimName, String.class));
        });
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
        Optional<String> op = dimMembData.keySet().stream().findFirst();
        String dim = op.orElse("");
        Set<Integer> rowIndexes = table.search(dimMembData.keySet().stream().findFirst().orElse("维度1"), "维度0[5]");
        int count = 0;
        List<List<Object>> data = table.getData();
        for (int rowIndex : rowIndexes) {
            if(count == 10) break;
            System.out.println(data.get(rowIndex));
            count++;
        }
    }

    private void iterator(ColumnarTable  table){
         List<List<Object>> rows = table.getData();
        for (int i = 0; i < rows.size(); i++) {
            if(i == 10) break;
            System.out.println(rows.get(i));
        }
    }

    private void calculateSize(ColumnarTable  table){
        SizeOf sizeof = new org.ehcache.sizeof.impl.UnsafeSizeOf();

        long size = sizeof.deepSizeOf(table);//统计对象大小
        System.out.println(RamUsageEstimator.humanReadableUnits(size));
        System.out.println("table-rowCount:"+table.getRowCount());
    }
    private void buildBigData(ColumnarTable  table){
        long starttime = System.currentTimeMillis();
        int rowCount = 3_000;
        for (int i = 0; i < rowCount; i++) {
            table.addBatchMeasureRow(buildGroupRowData(),buildMeasureRowData());
        }
        System.out.println("-----------------buildBigData---------------------->" + (System.currentTimeMillis() - starttime)+"ms");
    }

    private Object[] buildMeasureRowData(){
        Object[] members = new Object[subjects.length];
        int i = 0;
        for (String subject : subjects) {
            members[i++] = new Random().nextInt(100);
        }
        return members;
    }

    private Object[] buildGroupRowData(){
        Object[] members = new Object[dimSize];
        int i = 0;
        for (Map.Entry<String, String[]> dim : dimMembData.entrySet()) {
            members[i++] = dimMembData.get(dim.getKey())[new Random().nextInt(dim.getValue().length)];
        }
        return members;
    }
}
