/*
 * @(#)CalcStatus.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.calculate.tree;

/**   
 * @title: CalcStatus.java 
 * @package com.my.compute.tree 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年8月11日 下午2:53:47 
 * @version: V1.0   
*/
public class CalcTask {

	public CalcTask(CalcNode node){
		this.node = node;
	}
	
	public CalcNode node;
	public CalcStatusEnum status = CalcStatusEnum.REDEAD;
}
