package com.my.jackson;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value="APackage")
public class APackage implements IPackage,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonIgnore
	private String id;
	private String name;
	
	//忽略属性输出
	@JsonIgnore
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@JsonCreator  //如果无默认构造函数时，则需要指定使用什么变量属性来作为参数构造对象
	public APackage(@JsonProperty("id") String id,@JsonProperty("name") String name){
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return "{[" + id +"]" + "[" + name + "]}";
	}

	@Override
	public String toString(){
		return toJson();
	}
}
