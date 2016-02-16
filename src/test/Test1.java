package test;

import java.util.Random;

public class Test1 {
	
	public final static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		int y=(int) Math.random();
		y=(int) Math.random();
		int a = heavyFunc(y);
		/*
		Future<Integer> a = Threader.thread(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return heavyFunc();
			}
		});
		*/ 
		int b = heavyFunc(a);
		y=(int) Math.random();
		a = heavyFunc(y);
		System.out.println((a+b) + " " + (System.currentTimeMillis() - start));
		// System.out.println((a.get()+b) + " " + (System.currentTimeMillis() - start));
	}
	
	public static int heavyFunc(int a) throws InterruptedException {
		Thread.sleep(1000);
		return new Random().nextInt();
	}
}
