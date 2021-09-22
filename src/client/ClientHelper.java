package client;

import data.DataFile;

public class ClientHelper {
	ClientFrame clientFrame;
	ClientHandler clientHandler;
	public final static String FILE_TO_RECEIVED = "C:\\Users\\ASUS\\Documents\\181206209\\k\\prettydocs-v2.3.zip";

	public ClientHelper(ClientFrame clientFrame) {
		// TODO Auto-generated constructor stub
		this.clientFrame = clientFrame;
		try {
			this.clientHandler = new ClientHandler(this);
			this.clientHandler.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connectFail() {
		// TODO Auto-generated method stub
		clientFrame.showError("Can't connect with server");
	}

	public void receiveString(String result) throws Exception {
		// TODO Auto-generated method stub
		clientFrame.updateArea(result);
	}

	public void saveFile(DataFile dataFile) {
		// Hoi UI muon luu file o dau

		// Luu file
		dataFile.saveFile(FILE_TO_RECEIVED);
	}

	public void setProgessBarPercent(int n) {
		clientFrame.setProgressBar(n);
	}

	public void sendString(String text) {
		// TODO Auto-generated method stub
		clientHandler.sendString(text);
		if (text.contains("SEND_FILE")) {
			String fileName = text.split("--")[1];
			try {
				clientHandler.sendFile(fileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
