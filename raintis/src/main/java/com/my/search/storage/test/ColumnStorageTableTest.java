package com.my.search.storage.test;

import com.my.search.storage.*;
import org.apache.lucene.util.RamUsageEstimator;
import org.ehcache.sizeof.SizeOf;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

/**
 * 描述:
 *
 * @author yu_wei  date:2025/3/4 <p>
 * @version v1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ColumnStorageTableTest {
    private final String[] subjects = {"数学"/*, "语文", "英语", "物理", "化学", "生物", "政治", "体育", "科学", "计算机"*/};

    private final static int dimSize = 10;//维度数

    private final static Map<String,String[]> dimMembData = new LinkedHashMap<>();//构造每个维度的成员列表，从50到100不等的成员数

    @BeforeClass// 非静态方法也可用
    public static void initDimensionData(){
        int[] dimMenbSize = new Random().ints(dimSize, 50, 100).toArray();
        for (int i = 0; i < dimSize; i++) {
            int membSize = dimMenbSize[i];
            String[] dimData = new String[membSize];
            for (int j = 0; j < membSize; j++) {
                dimData[j] = "维度"+i +"-" + j ;
            }
            dimMembData.put("维度" + i, dimData);
        }
    }

    //@Test
    public void testRleColumnStorage() {
        ColumnStorageTable table = new ColumnStorageTable();
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new RLEColumnStorage<>(dimName, String.class));
        });
        for(String subject : subjects){
            table.addMeasureColumn(new SparseColumnStorage<>(subject,Object.class));
        }

        System.out.println("-----------------testRleColumnStorage----------------------");
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
    public void testDicColumnStorage() {
        ColumnStorageTable table = new ColumnStorageTable();
        dimMembData.keySet().forEach(dimName -> {
            table.addGroupColumn(new DictionaryColumnStorage<>(dimName, String.class));
        });
        for(String subject : subjects){
            table.addMeasureColumn(new SparseColumnStorage<>(subject,Object.class));
        }
        System.out.println("-----------------testDicColumnStorage----------------------");
        buildBigData(table);

        calculateSize(table);
        iterator(table);
        search(table);
        //table.addSort("维度0", true);
        //table.addSort("维度0", false);
        table.addSort("数学", false);
        System.out.println(Arrays.toString(table.getData().toArray()));
    }

    private void search(ColumnStorageTable  table){
        Optional<String> op = dimMembData.keySet().stream().findFirst();
        String dim = op.orElse("");
        ColValPairList pairList = ColValPairList.of(dim, "维度0-3");
        //pairList.add(ColValPair.of("维度1", "维度1[5]"));
        List<List<Object>> rows = table.search(pairList);

        for (List<Object> row : rows) {
            System.out.println(row);
        }
    }

    private void iterator(ColumnStorageTable  table){
        List<List<Object>> rows = table.getData();
        for (int i = 0; i < rows.size(); i++) {
            if(i == 10) break;
            System.out.println(rows.get(i));
        }
    }

    private void calculateSize(ColumnStorageTable  table){
        SizeOf sizeof = new org.ehcache.sizeof.impl.UnsafeSizeOf();

        long size = sizeof.deepSizeOf(table);//统计对象大小
        System.out.println(RamUsageEstimator.humanReadableUnits(size));
        System.out.println("table-rowCount:"+table.getRowCount());
    }
    private void buildBigData(ColumnStorageTable  table){
        long starttime = System.currentTimeMillis();
        int rowCount = 1_0;
        for (int i = 0; i < rowCount; i++) {
            table.addMultiMeasureByRow(buildGroupRowData(),buildMeasureRowData());
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
