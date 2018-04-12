package serv;

import java.io.*;
import java.net.*;

public class RequestHandler implements Runnable {
	private Socket clientSocket;
	private int clientID;
	private final String END = "\r\n";

	public RequestHandler(Socket csoc, int cID) {
		this.clientSocket = csoc;
		this.clientID = cID;
	}

	public void run() {
		BufferedReader socketInStream = null;
		DataOutputStream socketOutStream = null;
		FileInputStream fis = null;

		try {
			socketInStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			socketOutStream = new DataOutputStream(clientSocket.getOutputStream());
			String packet = socketInStream.readLine();

			if(packet != null){
				System.out.println("SERVER >> CLIENT " + clientID + " - Received a request: " + packet);
				String[] message = packet.split(" ");
				if(message[0].equals("GET") && message.length == 3) {
					String filePath = message[1];
					if(filePath.equals("/")) {
						System.out.println("SERVER >> CLIENT " + clientID + " - No specific filePath requested");
						System.out.println("SERVER >> CLIENT " + clientID + " - Respond with default /index.htm file");
						filePath = filePath.concat("index.htm");
					}
					else {
						System.out.println("SERVER >> CLIENT "+ clientID + " - Requested filePath: " + filePath);
					}
					filePath = "." + filePath;

					File file = new File(filePath);
					try {
						if(file.isFile() && file.exists()) {
							String responseLine = "HTTP/1.0 200 OK" + END;
							socketOutStream.writeBytes(responseLine);
							socketOutStream.writeBytes("Content-type: " + "text/html" + END);

							fis = new FileInputStream(file);
							byte[] buffer = new byte[1024];
							int bytes = 0;
							while((bytes = fis.read(buffer)) != -1 ) {
								socketOutStream.write(buffer, 0, bytes);
							}
							System.out.println("SERVER >> CLIENT " + clientID + " - Sending Response with status line: " + responseLine);
							socketOutStream.flush();
							System.out.println("SERVER >> CLIENT " + clientID + " - HTTP Response sent");
						}
						else {
							System.out.println("SERVER >> CLIENT " + clientID + " - ERROR: Requested filePath " + filePath + " does not exist");
							String responseLine = "HTTP/1.0 404 Not Found" + END;
							socketOutStream.writeBytes(responseLine);
							socketOutStream.writeBytes("Content-type: text/html" + END);
							socketOutStream.writeBytes(getErrorFile());
							System.out.println("SERVER >> CLIENT " + clientID + " - Sending Response with status line: " + responseLine);
							socketOutStream.flush();
							System.out.println("SERVER >> CLIENT " + clientID + " - HTTP Response sent");
						}
					}
					catch (FileNotFoundException fne) {
						System.err.println("SERVER >> CLIENT " + clientID + " - Requested filePath " + filePath + " does not exist");
					}
					catch (IOException e) {
						System.err.println("SERVER >> CLIENT " + clientID + " - Exception in processing request: " + e.getMessage());
					}
				}
				else {
					System.err.println("SERVER >> CLIENT " + clientID + " - Invalid HTTP GET Request: " + message[0]);
				}
			}
			else {
				//System.err.println("SERVER >> CLIENT " + clientID + " - Discarding an unknown HTTP request.");
			}
		}
		catch (IOException e) {
			System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in processing request." + e.getMessage());

		}
		finally {
			try {
				if(fis != null) {
					fis.close();
				}
				if(socketInStream != null) {
					socketInStream.close();
				}
				if(socketOutStream != null) {
					socketOutStream.close();
				}
				if(this.clientSocket != null) {
					this.clientSocket.close();
					System.out.println("SERVER >> CLIENT " + this.clientID + " - Closing the connection");
				}
			}
			catch (IOException e) {
				System.err.println("SERVER >> CLIENT " + this.clientID + " - Exception in closing resource: " + e);
			}
		}
	}

	private String getContentType(String filePath) {
		
		if(filePath.endsWith(".html") || filePath.endsWith(".htm")) {
			return "text/html";
		}
		return "other";
	}

	private String getErrorFile() {
		String content = 	"<!doctype html>" + "\n" +
				"<html lang=\"en\">" + "\n" +
				"<head>" + "\n" +
				"    <meta charset=\"UTF-8\">" + "\n" +
				"    <title>Error 404</title>" + "\n" +
				"</head>" + "\n" +
				"<body>" + "\n" +
				"    <b>ErrorCode:</b> 404" + "\n" +
				"    <br>" + "\n" +
				"    <b>Error Message:</b> The requested file does not exist on this server." + "\n" +
				"</body>" + "\n" +
				"</html>";
		return content;
	}
}
