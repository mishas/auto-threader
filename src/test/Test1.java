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
		Integer k=null,j = null,i,l;
		System.out.println(x);
		if(x<9){
			x++;
			x-=10;
		}
		else{
			x+=10;
		}
		System.out.println(x);
		int y = new Random().nextInt(10);
		Integer c = heavyFunc(y*Math.abs(x));
		int u=4;
		while(u>0){
			l=1;
			l+=l;
			l*=l;
			l/=l;
			 i=heavyFunc2();
			 
			while(u>0){
				j=heavyFunc2();
				u--;
				while(u>0){
					k=heavyFunc2();
					u--;
					while(u>0){
							heavyFunc();
						u--;
						}
					k=0;
					}
				}
			System.out.println(i);
		u--;
		}
		System.out.println(a + " " + b + " "  +c+ " " +" "+(System.currentTimeMillis() - start));
		System.out.println(d+" "+f+" "+g+" "+e+" "+j+" "+k);
	}
	
	@ThreadMe
	public static Integer heavyFunc() throws InterruptedException {
		Thread.sleep(1001);
		return new Random().nextInt();
	}
	@ThreadMe
	public static Integer heavyFunc2() throws InterruptedException {
		Thread.sleep(1002);
		return new Random().nextInt();
	}
	
	@ThreadMe
	public static Integer heavyFunc(int x) throws InterruptedException {
		Thread.sleep(1003);
		return new Random().nextInt(x);
	}
}
