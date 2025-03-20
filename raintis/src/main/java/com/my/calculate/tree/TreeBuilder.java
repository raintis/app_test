/*
 * @(#)TreeBuilder.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.calculate.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**   
 * @title: TreeBuilder.java 
 * @package com.my.compute.tree 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年8月11日 下午2:20:37 
 * @version: V1.0   
*/
public class TreeBuilder {

	public static TreeMap<Integer,List<CalcTask>> buildCalcTree(){
		TreeMap<Integer,List<CalcTask>> calcTree = new TreeMap<>((L1,L2) -> L2-L1);
		CalcNode root = new CalcNode("root");
		CalcNode a = new CalcNode("a",root);
		CalcNode ab = new CalcNode("ab",a);
		CalcNode abc = new CalcNode("abc",ab);
		root.isLeaf = false;
		a.isLeaf = false;
		ab.isLeaf = false;
		
		CalcNode b = new CalcNode("b",root);
		CalcNode bd = new CalcNode("bd",b);
		CalcNode be = new CalcNode("be",b);
		CalcNode bf = new CalcNode("bf",b);
		b.isLeaf = false;
		
		calcTree.computeIfAbsent(1, f -> new ArrayList<>(16)).add(new CalcTask(root));
		calcTree.computeIfAbsent(2, f -> new ArrayList<>(16)).addAll(Arrays.asList(new CalcTask(a),new CalcTask(b)));
		calcTree.computeIfAbsent(3, f -> new ArrayList<>(16)).addAll(Arrays.asList(new CalcTask(ab),new CalcTask(bd),new CalcTask(be),new CalcTask(bf)));
		calcTree.computeIfAbsent(4, f -> new ArrayList<>(16)).add(new CalcTask(abc));
		return calcTree;
	}
	
}
