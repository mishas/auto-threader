package test;

import java.util.Random;

public class Test1 {
	public @interface ThreadMe {}
	
	public final static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		
		int x = new Random().nextInt(4);
		Integer d = heavyFunc(),f=heavyFunc(),g=heavyFunc(),e=heavyFunc();
		Integer a = heavyFunc();
		Integer b = heavyFunc2();
		
		int y = new Random().nextInt(10);
		Integer c = heavyFunc(x + y);
		int u=4;
		while(u>0){
		 //b= heavyFunc2();
		 //c= 
			 heavyFunc2();
			while(u>0){
				 heavyFunc2();
				u--;
				while(u>0){
						heavyFunc2();
					u--;
					while(u>0){
							heavyFunc2();
						u--;
						}
					}
				}
		u--;
		}
		System.out.println(a + " " + b + " " + c + " " +" "+(System.currentTimeMillis() - start));
		System.out.println(d+" "+f+" "+g+" "+e);
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
