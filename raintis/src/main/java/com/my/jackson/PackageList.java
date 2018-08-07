package com.my.jackson;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

//没有setter、getter方法时，需要定义私有变量可见，否则会报错
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)  
public class PackageList {

	private List<IPackage> packages = new ArrayList<>();
	
	public void addPackage(IPackage ipk){
		packages.add(ipk);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(IPackage i : packages){
			sb.append(i.toString());
		}
		return sb.toString();
	}
}
