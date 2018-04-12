package serv; 

public class Main {
	public static void main(String[] args) {
		int port = 8080;
		if(args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException nfe) {
				System.err.println("SERVER >> Integer Port is not provided. Server will start at default port.");
			}
		}
		System.out.println("SERVER >> Using Port "+port);
		new Thread(new Server(port)).start();
	}
}
