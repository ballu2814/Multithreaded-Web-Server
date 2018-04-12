package serv;

import java.io.*;
import java.net.*;

public class Server implements Runnable {
	private ServerSocket serverSocket;
	private String serverHost;
	private int serverPort;
	private final String DEFAULT_HOST = "localhost";
	int arr[]=new int[1];
	
	public Server (int port) {
		this.serverHost = DEFAULT_HOST;
		this.serverPort = port;
	}

	public void run() {
		try {
			InetAddress serverInet = InetAddress.getByName(this.serverHost);
			this.serverSocket = new ServerSocket(this.serverPort, 0, serverInet);
			System.out.println("SERVER >> Server started at HOST " + this.serverSocket.getInetAddress() + " PORT " + this.serverSocket.getLocalPort());
			int cID = 1;
		
			while(true){
				Socket clientSocket=this.serverSocket.accept();
				System.out.println("SERVER >> CLIENT " + cID + " - Connection established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				new Thread(new RequestHandler(clientSocket,cID)).start();
				
				cID++;
			}
		}
		catch (UnknownHostException e) {
			System.err.println("SERVER >> UnknownHostException for the hostname: " + this.serverHost);
		}
		catch (IllegalArgumentException iae) {
			System.err.println("SERVER >> Exception while starting the server: " + iae.getMessage());
		}
		catch (IOException e) {
			System.err.println("SERVER >> Exception while starting the server: " + e.getMessage());
		}
		finally {
			try {
				if(this.serverSocket != null){
					this.serverSocket.close();
				}
			}
			catch (IOException e) {
				System.err.println("SERVER >> Exception while closing the server socket" + e);
			}
		}
	}
	
	public void printclient(){
		System.out.println(arr[0]);
	}
}
