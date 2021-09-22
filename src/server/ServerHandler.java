package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.DataFile;

public class ServerHandler extends Thread {

	private Socket socket;
	private ServerHelper serverHelper;
	OutputStream os;
	InputStream is;
	private boolean isStop = false;
	private String fileName;
	private int fileSize;
	private int currentSize;
	private DataFile m_dtf;

	public ServerHandler(Socket socket) throws Exception {
		this.socket = socket;
		os = socket.getOutputStream();
		is = socket.getInputStream();

		serverHelper = new ServerHelper(this);
	}

	@Override
	public void run() {
		System.out.println("Processing: " + socket);
		// TODO Auto-generated method stub
		while (!isStop) {
			try {
				readData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				connectClientFail();
				e.printStackTrace();
				break;
			}
		}
		System.out.println("Complete processing: " + socket);

		closeSocket();

	}

	void readData() throws Exception {
		try {

			System.out.println("Recieving...");
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();

			if (obj instanceof String) {
				String str = readString(obj);
				serverHelper.handle(str);
			} else if (obj instanceof Integer) {
				readFile(obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			connectClientFail();
			closeSocket();
		}
	}

	private void connectClientFail() {
		// TODO Auto-generated method stub
		serverHelper.connectFail();
		isStop = true;
		closeSocket();
	}

	private void closeSocket() {
		isStop = true;
		try {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
			if (socket != null)
				socket.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String readString(Object obj) {
		String str = obj.toString();
		if (str.equals("Stop"))
			isStop = true;
		if (str.contains("SEND_FILE")) {
			fileName = str.split("--")[1];
			fileSize = Integer.parseInt(str.split("--")[2]);
			currentSize = 0;
		}
		if (str.contains("END_FILE")) {
			System.out.println("Save File");
			// reset thong tin
			m_dtf.saveFile("C:\\Users\\ASUS\\Documents\\demo\\h.zip");
			currentSize = 0;
		}
		return str;
	}

	void sendString(String str) {
		sendMessage(str);
	}

	void sendFile(String fileName) throws Exception {
		File source = new File(fileName);
		InputStream fin = new FileInputStream(source);
		long lenghtOfFile = source.length();
		byte[] buf = new byte[512];
		int len;
		long total = 0;
		sendString("SEND_FILE--" + fileName + "--" + lenghtOfFile);
		while ((len = fin.read(buf)) != -1) {
			total += len;
			serverHelper.setProgessBarPercent((int) ((total * 100) / lenghtOfFile));
			DataFile dtf = new DataFile();
			dtf.data = buf;
			sendMessage(dtf);
		}
		sendString("END_FILE");
	}

	void readFile(Object obj) throws Exception {
		DataFile dtf = (DataFile) obj;
		currentSize += 512;

		int percent = currentSize * 100 / fileSize;
		// System.out.println(currentSize + " : " + fileSize);
		m_dtf.appendByte(dtf.data);
		serverHelper.setProgessBarPercent(percent);
	}

	// void send Message
	public synchronized void sendMessage(Object obj) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			// only send text
			if (obj instanceof String) {
				String message = obj.toString();
				oos.writeObject(message);
				oos.flush();
			}
			// send attach file
			else if (obj instanceof DataFile) {
				oos.writeObject(obj);
				oos.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
