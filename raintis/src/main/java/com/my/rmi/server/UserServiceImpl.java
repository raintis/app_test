package com.my.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.my.rmi.stub.User;
import com.my.rmi.stub.UserService;

public class UserServiceImpl extends UnicastRemoteObject implements UserService{

	protected UserServiceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public User getUserByid(long id) throws RemoteException {
		// TODO Auto-generated method stub
		 User user = new User();
	        user.setAge(10);
	        user.setName("jack");
	        user.setDesc("good man");
	        user.setId(id);
	        return user;
	}

}
