package com.my.list;

import java.util.ArrayList;
import java.util.List;


public class CloneObject implements Cloneable{

	public String name;
	public List<String> persons = new ArrayList<>();
	
	protected CloneObject clone(){
		try {
			return (CloneObject)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
