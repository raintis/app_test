package com.my.md5

class KotlinTest {
	
	fun main(args: Array<String>){
		var demo1 = Demo1()
		demo1.testFun("content")
	}

	class Demo1 {
		var name: String = ""

		fun testFun(name: String) {
			this.name = name
			println(this.name)
		}
	}
}