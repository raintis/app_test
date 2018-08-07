package com.my.autocloseable;

/**
 * description:ʵ�ֽӿ�AutoCloseable����Ϊ�ɹرյ���Դ����ô��try���ʱ�������Ž��а�װ�����һ���������ӷֺţ�
 * ��ô������ʹ��finally��������Դ�رգ��򻯴���Ҳ��ֹ���ǵ���Դ�ر�
 * @author Administrator
 *
 */
public class MyResource implements AutoCloseable{

	@Override
	public void close() throws Exception {
		System.out.println("�ر���Դ");
	}

	public void readResource(){
		System.out.println("��ȡ��Դ");
	}
	
	
	public static void main(String[] args) {
		try(MyResource rs = new MyResource()){
			rs.readResource();
		}catch(Exception e){
			System.out.println(e);
		}finally{
			System.out.println("finally");
		}
	}
}
