package serv;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.Desktop;

public class Client {
	
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		final String END = "\r\n";
		String servername = null;
		int serverPort = 8080;
		
		while(true){
			String filePath = "/";

			args=sc.nextLine().split(" ");		
			if(args.length == 1) {
				servername = args[0];
			}
			else if(args.length == 2) {
				servername = args[0];
				try {
					serverPort = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException nfe) {
					System.err.println("CLIENT >> Integer Port required, Default Server port will be used.");
					filePath += args[1];
				}
			}
			else if (args.length == 3) {
				servername = args[0];
				try {
					serverPort = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("CLIENT >> Integer Port required, Default Server port will be used.");
				}
				filePath += args[2];
			}
			else {
				System.err.println("CLIENT >> At least serverHost is required to connect to the server.");
				System.exit(-1);
			}
	
			System.out.println("CLIENT >> Using Server Port: " + serverPort);
			if(filePath.length()>1){
				System.out.println("CLIENT >> Using FilePath: " + filePath);
			}
	
			Socket clientSocket = null;
			BufferedReader socketInStream = null;
			DataOutputStream socketOutStream = null;
			FileOutputStream fos = null;
			
			try {
				InetAddress serverIP = InetAddress.getByName(servername);
				clientSocket = new Socket(serverIP,serverPort);
				System.out.println("CLIENT >> Connected to the server at " + servername + ":" + serverPort);
				
				socketInStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				socketOutStream = new DataOutputStream(clientSocket.getOutputStream());
	
				String requestLine = "GET " + filePath + " HTTP/1.0";
				System.out.println("CLIENT >> Sending HTTP GET request: " + requestLine);
				socketOutStream.writeBytes(requestLine);
				socketOutStream.writeBytes(END);
				socketOutStream.flush();
				
				System.out.println("CLIENT >> Waiting for a response from the server");
				String responseLine = socketInStream.readLine();
				System.out.println("CLIENT >> Received HTTP Response with status line: " + responseLine);
	
				String contentType = socketInStream.readLine();
				System.out.println("CLIENT >> Received " + contentType);
	
				System.out.println("CLIENT >> Received Response Body:");
				StringBuilder content = new StringBuilder();
				String res;
				while((res = socketInStream.readLine()) != null) {
					content.append(res + "\n");
					System.out.println(res);
				}
				String fileName = getFileName(content.toString());
				
				File htmlFile = new File(fileName);
				Desktop.getDesktop().browse(htmlFile.toURI());
	
				fos = new FileOutputStream(fileName);
				fos.write(content.toString().getBytes());
				fos.flush();
				System.out.println("CLIENT >> HTTP Response received.");
	
			} catch (IllegalArgumentException iae) {
				System.err.println("CLIENT >> Exception in connecting to the server: " + iae.getMessage());
			} catch (IOException e) {
				System.err.println("CLIENT >> ERROR " + e);
			}
			finally {
				try {
					if(socketInStream != null) {
						socketInStream.close();
					}
					if(socketOutStream != null) {
						socketOutStream.close();
					}
					if(fos != null) {
						fos.close();
					}
					if(clientSocket != null) {
						clientSocket.close();
						System.out.println("CLIENT >> Closing the Connection");
					}
				}
				catch (IOException ioe) {
					System.err.println("CLIENT >> EXCEPTION in closing resource" + ioe);
				}
			}
		}
	}

	private static String getFileName(String content) {
		String filename = "";
		filename = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		if(filename.equals("")) {
			filename = "index";
		}
		filename = filename + ".htm";
		return filename;
	}
}
