package com.my.unsafe;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class UnsafeTest {

	@SuppressWarnings({ "restriction", "deprecation" })
	public static void main(String[] args) throws Exception {
		sun.misc.Unsafe unsafe = getUnsafeInstance();
		Object obj = unsafe.allocateInstance(BigDecimal.class);
		// 获取实例字段的偏移地址,偏移最小的那个字段(仅挨着头部)就是对象头的大小
		System.out.println("offset_a-->"+unsafe.objectFieldOffset(VO.class.getDeclaredField("a")));
		System.out.println("offset_b-->"+unsafe.objectFieldOffset(VO.class.getDeclaredField("b")));

		// fieldOffset与objectFieldOffset功能一样,fieldOffset是过时方法,最好不要再使用
		//System.out.println(unsafe.fieldOffset(VO.class.getDeclaredField("b")));

		// 数组第一个元素的偏移地址,即数组头占用的字节数
		int[] intarr = new int[0];
		System.out.println("offset_intarr-->"+unsafe.arrayBaseOffset(intarr.getClass()));

		// 数组中每个元素占用的大小
		System.out.println("offset_intarr_scale-->"+unsafe.arrayIndexScale(intarr.getClass()));

		// 获取类的静态字段偏地址
		System.out.println("offset_c-->"+unsafe.staticFieldOffset(VO.class.getDeclaredField("c")));
		System.out.println("offset_d-->"+unsafe.staticFieldOffset(VO.class.getDeclaredField("d")));

		// 获取静态字段的起始地址,通过起始地址和偏移地址,就可以获取静态字段的值了
		// 只不过静态字段的起始地址,类型不是long,而是Object类型
		Object base1 = unsafe.staticFieldBase(VO.class);
		Object base2 = unsafe.staticFieldBase(VO.class.getDeclaredField("d"));
		System.out.println("base1"+base1);
		System.out.println("base2"+base2);
		System.out.println("base1 == base2-->"+(base1 == base2));// true

		// Report the size in bytes of a native pointer.
		// 返回4或8,代表是32位还是64位操作系统。
		System.out.println(unsafe.addressSize());
		// 返回32或64,获取操作系统是32位还是64位
		System.out.println(System.getProperty("sun.arch.data.model"));

		// 获取实例字段的属性值
		VO vo = new VO();
		vo.a = 10000;
		long aoffset = unsafe.objectFieldOffset(VO.class.getDeclaredField("a"));
		int va = unsafe.getInt(vo, aoffset);
		System.out.println("va=" + va);

		VO.e = 1024;
		Field sField = VO.class.getDeclaredField("e");
		Object base = unsafe.staticFieldBase(sField);
		long offset = unsafe.staticFieldOffset(sField);
		System.out.println(unsafe.getInt(base, offset));// 1024
	}

	@SuppressWarnings({ "restriction"})
	private static sun.misc.Unsafe getUnsafeInstance()
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field theUnsafeInstance = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (sun.misc.Unsafe) theUnsafeInstance.get(sun.misc.Unsafe.class);
	}
}
