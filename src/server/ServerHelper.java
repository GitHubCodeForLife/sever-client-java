package server;

public class ServerHelper {

	ServerHandler serverHandler;
	public final static String FILE_TO_SEND = "C:\\Users\\ASUS\\Documents\\Tools\\Passport-nodejs-master.zip"; // you
																												// may
	// change this

	public ServerHelper(ServerHandler serverHandler) {
		this.serverHandler = serverHandler;
	}

	public void handle(String str) throws Exception {
		// TODO Auto-generated method stub
		if (str.equals("Send File")) {
			System.out.println("Server is sending file");
			serverHandler.sendFile(FILE_TO_SEND);
			return;
		}
		System.out.println("Server Helper String : " + str);
		serverHandler.sendString("From sever: " + str);
	}

	public void connectFail() {
		// TODO Auto-generated method stub
		System.out.println("Connect to client fail, client was disconnected.");
	}

	public void showProgressBar(double d) {
		// TODO Auto-generated method stub
		System.out.println("Percent : " + d);

	}

	public void setProgessBarPercent(int i) {
		// TODO Auto-generated method stub
		System.out.println("Da gui duoc: " + i + "%");
	}
}
