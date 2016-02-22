package test;

import java.util.Random;

public class Test1 {
	public @interface ThreadMe {}
	
	public final static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		
		int x = new Random().nextInt(4);
		
		Integer a = heavyFunc();
		
		int y = new Random().nextInt(10);
		Integer c = heavyFunc2();
		Integer b = heavyFunc(x + y);
		
		System.out.println(a + " " + b + " " + c + " " +" "+(System.currentTimeMillis() - start));
	}
	
	@ThreadMe
	public static Integer heavyFunc() throws InterruptedException {
		Thread.sleep(1000);
		return new Random().nextInt();
	}
	//@ThreadMe
	public static Integer heavyFunc2() throws InterruptedException {
		Thread.sleep(1000);
		return new Random().nextInt();
	}
	
	@ThreadMe
	public static Integer heavyFunc(int x) throws InterruptedException {
		Thread.sleep(1000);
		return new Random().nextInt(x);
	}
}
