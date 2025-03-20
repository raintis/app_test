package com.my.rmi.server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import com.my.rmi.stub.UserService;

public class Server {

	public static void main(String[] args) {
        try {
            UserService userService = new UserServiceImpl();
            LocateRegistry.createRegistry(9090);
            Naming.bind("rmi://localhost:9090/user", userService);
            System.err.println("binding success");
        } catch (RemoteException e) {
            //System.out.println("创建远程对象发生异常！");
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            //System.out.println("发生重复绑定对象异常！");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            //System.out.println("URL异常！");
            e.printStackTrace();
        }
    }
}
