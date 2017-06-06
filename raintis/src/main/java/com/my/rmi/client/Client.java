package com.my.rmi.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.my.rmi.stub.UserService;

public class Client {

	public static void main(String[] args) {
        try { 
            UserService userService = (UserService) Naming.lookup("rmi://localhost:9090/user"); 
            System.out.println(userService.getUserByid(1000l)); 
        } catch (NotBoundException e) { 
            e.printStackTrace(); 
        } catch (MalformedURLException e) { 
            e.printStackTrace(); 
        } catch (RemoteException e) { 
            e.printStackTrace();   
        } 
    } 
}
