/*
 * @(#)MemoryUsageTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;

/**   
 * @title: MemoryUsageTest.java 
 * @package com.my.memory 
 * @description: TODO
 * @author: Administrator
 * @date: 2021年10月12日 下午5:04:25 
 * @version: V1.0   
*/
public class MemoryUsageTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2021年10月12日 下午5:04:25
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<>(1000000);
		for(int i=0;i < 1000000;i++){
			list.add(i+"ffffffffff");
		}
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
		long totalMemorySize = memoryUsage.getInit();//初始化的总内存
		long maxMemorySize = memoryUsage.getMax();//最大可用内存
		long usedMemorySize = memoryUsage.getUsed();//已使用的内存
		long commitedMemorySize = memoryUsage.getCommitted();//提交的内存，>=usage
		System.out.println("totolMemory:" + (totalMemorySize/(1024*1024))+"M");
		System.out.println("totolMemory2:" + (Runtime.getRuntime().maxMemory()/(1024*1024))+"M");
		System.out.println("freeMemory:" + ((totalMemorySize-usedMemorySize)/(1024*1024))+"M");
		System.out.println("maxMemory:" + (maxMemorySize/(1024*1024))+"M");
		System.out.println("usedMemory:" + (usedMemorySize/(1024*1024))+"M");
		System.out.println("commitedMemory:" + (commitedMemorySize/(1024*1024))+"M");
		
		System.out.println("getUsedMemory:" + getUsedMemory_MB()+"M");
		System.out.println("getLeftMemory_MB:" + getLeftMemory_MB()+"M");
	}
	
	private static final String OBJECTNAME = "java.lang:type=Memory";
    private static final String ATTRIXBUTENAME = "HeapMemoryUsage";
    private static final String MAX = "max";
    private static final String USED = "used";

    public static long getUsedMemory() {
        Long used = (Long) JmxUtils.getAtt(OBJECTNAME, ATTRIXBUTENAME, USED);
        return used;
    }

    public static long getUsedMemory_MB() {
        return getUsedMemory() / (1024 * 1024);
    }

    public static long getLeftMemory() {
        Long used = (Long) JmxUtils.getAtt(OBJECTNAME, ATTRIXBUTENAME, USED);
        Long max = (Long) JmxUtils.getAtt(OBJECTNAME, ATTRIXBUTENAME, MAX);
        return max.longValue() - used.longValue();
    }

    public static long getLeftMemory_MB() {
        long b = System.nanoTime();
        try{
        return getLeftMemory() / (1024 * 1024);
        }finally{
            System.err.println("jmxtimes(ns)"+(System.nanoTime() - b));
        }
    }

    public static class JmxUtils {
        private static ArrayList<MBeanServer> mbservers = MBeanServerFactory.findMBeanServer((String)null);//没有注册是取不到服务的
       // private static Logger logger = LoggerFactory.getLogger(JmxUtils.class);
        private static Map<String, ObjectName> objectNames;

        public JmxUtils() {
        }

        private static void init() {
            mbservers = MBeanServerFactory.findMBeanServer((String)null);
        }

        public static Object getAtt(String objectName, String attributeName) {
            Iterator var2 = mbservers.iterator();

            while(var2.hasNext()) {
                MBeanServer mbserver = (MBeanServer)var2.next();

                try {
                    ObjectName on;
                    if (objectNames.containsKey(objectName)) {
                        on = (ObjectName)objectNames.get(objectName);
                    } else {
                        on = new ObjectName(objectName);
                        objectNames.put(objectName, on);
                    }

                    Object o = mbserver.getAttribute(on, attributeName);
                    return o;
                } catch (InstanceNotFoundException | MBeanException | ReflectionException | MalformedObjectNameException | AttributeNotFoundException var6) {
                   // logger.warn("mbean get getAtt error " + objectName + "  " + attributeName, var6);
                }
            }

            return null;
        }

        public static Object getAtt(String objectName, String attributeName, String path) {
            Object o = getAtt(objectName, attributeName);
            return o instanceof CompositeDataSupport ? ((CompositeDataSupport)o).get(path) : o;
        }

    }
}
