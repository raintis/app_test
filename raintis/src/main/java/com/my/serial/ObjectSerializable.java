package com.my.serial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ObjectSerializable {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		User user ;//= new User("wei doudou",2);
		user = new User("wei doudou",2,"men");
		List<User> list,list2;
		try(ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File("c:\\serial.txt")))){
			list = new ArrayList<>();
			list.add(user);
			list2 = new ArrayList<>();
			list2.add(user);
			out.writeObject(list);
			out.writeObject(list2);
		}catch(Exception io){
			System.out.println(io);
		}
		System.out.println(user);
		try(ObjectInput in = new ObjectInputStream(new FileInputStream(new File("c:\\serial.txt")))){
			list = (List<User>)in.readObject();
			list2 = (List<User>)in.readObject();
			System.out.println(list.get(0) == list2.get(0));
			System.out.println(String.format("list.get(0) addr->%s", list.get(0).hashCode()));
			System.out.println(String.format("list2.get(0) addr->%s", list2.get(0).hashCode()));
		}catch(Exception io){
			System.out.println(io);
		}
		System.out.println(user);
	}
}
