package test;

import java.util.Random;

public class Test1 {
	
	public final static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();

		Integer a = heavyFunc();
		Integer b = heavyFunc();
		Integer c = heavyFunc();
		System.out.println(a + " " + b + " " + c + " " + (System.currentTimeMillis() - start));
	}
	
	public static Integer heavyFunc() throws InterruptedException {
		Thread.sleep(1000);
		return new Random().nextInt();
	}
}
