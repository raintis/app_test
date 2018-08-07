package com.my.lang.builder;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class HashBuilderTest {

	public static void main(String[] args) {
		Student s1 = new Student("w1","NO1");
		Student s2 = new Student("w2","NO2");
		Student s3 = new Student("w1","NO1");
		
		List<Student> students = Lists.newArrayList(s1, s2,s3);
		Set<Student> set = Sets.newHashSet(s1, s2,s3);
		System.out.println("student List Size:" + students.size());
		System.out.println("student Set Size:" + set.size());
		System.out.println("s1 == s3:" + s1.equals(s3));
		System.out.println(s1.toString());
	}
	
	static class Student{
		private String name;
		private String stuNo;
		private String sign;
		
		public Student(String name,String stuNo){
			this.name = name;
			this.stuNo = stuNo;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(name).append(stuNo).append(sign).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			boolean isEqual = false;
			if(obj != null && obj instanceof Student){
				Student other = (Student)obj;
				isEqual = new EqualsBuilder().append(name, other.name).append(stuNo, other.stuNo).append(sign, other.sign).isEquals();
			}
			return isEqual;
		}

		@Override
		public String toString() {
			//return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE).append(name).append(stuNo).append(sign).build();
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}
}
