package serv;
import java.io.*;
public class countclient implements Runnable {
int b;
	public countclient(int arr[]){
	b=arr[0];
}
long startTime=System.currentTimeMillis();
	public void run(){
		while(System.currentTimeMillis() - startTime <= 6000){
		
		}
		System.out.println(b);
	}
	
}
