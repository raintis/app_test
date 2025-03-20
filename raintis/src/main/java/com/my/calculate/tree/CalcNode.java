/*
 * @(#)ComputeNode.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.calculate.tree;

import java.util.concurrent.atomic.AtomicInteger;

/**   
 * @title: ComputeNode.java 
 * @package com.my.compute.tree 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年8月11日 下午2:19:19 
 * @version: V1.0   
*/
public class CalcNode {

	public CalcNode(String number){
		this.number = number;
	}
	
	public CalcNode(String number,CalcNode parent){
		this.number = number;
		this.parent = parent;
		if(parent != null){
			parent.subCount.incrementAndGet();
		}
	}
	
	public int decrement(){
		return subCount.decrementAndGet();
	}
	
	public String number;
	public CalcNode parent;
	public boolean isLeaf = true;
	public AtomicInteger subCount = new AtomicInteger(0) ;
}
