package com.my.serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int age;
	private String name;
	private String gender;
	private List<String> scores = new ArrayList<>();
	
	public User(String name,int age,String gender){
		this.name = name;
		this.age = age;
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getScores() {
		return scores;
	}

	public void setScores(List<String> scores) {
		this.scores = scores;
	}

	public String toString(){
		return String.format("name:%s age:%d", name,age);
	}
	
	private Object readResolve(){
		if(scores == null){
			scores = new ArrayList<>();
		}
		return this;
	}
}
