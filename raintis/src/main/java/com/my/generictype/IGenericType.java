package com.my.generictype;

public interface IGenericType<T,R> {
	T getInstance(R r);
}
