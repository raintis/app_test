package com.my.md5;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Client {
	private static Map users = new HashMap();// 用于模拟数据库

	public static void main(String[] args) {
		String userName = "zyg";
		String password = "123";
		registerUser(userName, password);

		userName = "changong";
		password = "456";
		registerUser(userName, password);

		String loginUserId = "zyg";
		String pwd = "123";
		try {
			if (loginValid(loginUserId, pwd)) {
				System.out.println("欢迎登陆！！！");
			} else {
				System.out.println("口令错误，请重新输入！！！");
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 注册用户
	 * 
	 * @param userName
	 * @param password
	 */
	public static void registerUser(String userName, String password) {
		String encryptedPwd = null;
		try {
			encryptedPwd = MyMD5Util.getEncryptedPwd(password);

			users.put(userName, encryptedPwd);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 验证登陆
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean loginValid(String userName, String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String pwdInDb = (String) users.get(userName);
		if (null != pwdInDb) { // 该用户存在
			return MyMD5Util.validPassword(password, pwdInDb);
		} else {
			System.out.println("不存在该用户！！！");
			return false;
		}
	}
}
