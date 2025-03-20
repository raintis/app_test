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
            //System.out.println("����Զ�̶������쳣��");
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            //System.out.println("�����ظ��󶨶����쳣��");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            //System.out.println("URL�쳣��");
            e.printStackTrace();
        }
    }
}
