package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.DataFile;

public class ClientHandler extends Thread {

	private Socket socket;
	OutputStream os;
	InputStream is;
	private boolean isStop = false;

	public final static String SERVER_IP = "127.0.0.1";
	public final static int SERVER_PORT = 10;
	ClientHelper clientHelper;

	private long fileSize;
	private String fileName;
	private long currentSize;
	DataFile m_dtf;
	public final static int FILE_SIZE = 6022386; // file size temporary hard coded
													// should bigger than the file to be downloaded

	public ClientHandler(ClientHelper clientHelper) throws Exception {
		try {
			this.clientHelper = clientHelper;
			socket = new Socket(SERVER_IP, SERVER_PORT);
			// Connect to server
			System.out.println("Connected: " + socket);

			os = socket.getOutputStream();
			is = socket.getInputStream();

			m_dtf = new DataFile();

		} catch (Exception e) {
			// TODO: handle exception
			// clientHelper.connectFail();
			System.out.println("Can't connect to server");
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!isStop) {
			try {
				readData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				connectServerFail();
				e.printStackTrace();
				break;
			}
		}
		closeSocket();
	}

	void readData() throws Exception {
		try {

			System.out.println("Recieving...");
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();

			if (obj instanceof String) {
				String str = readString(obj);
				clientHelper.handle(str);
			} else if (obj instanceof DataFile) {
				readFile(obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			connectServerFail();
			closeSocket();
		}
	}

	private void connectServerFail() {
		// TODO Auto-generated method stub
		clientHelper.connectFail();
		isStop = true;
		closeSocket();
	}

	private void closeSocket() {
		isStop = true;
		try {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
			if (socket != null)
				socket.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public String readString(Object obj) throws Exception {
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
			File f = new File(fileName);
			System.out.println(f.getName());
			m_dtf.saveFile("C:\\Users\\ASUS\\Documents\\demo\\" + f.getName());
			m_dtf = new DataFile();
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
			clientHelper.setProgessBarPercent((int) ((total * 100) / lenghtOfFile));
			// os.write(buf, 0, len);
			DataFile dtf = new DataFile();
			dtf.data = buf;
			sendMessage(dtf);
		}
		sendString("END_FILE");
	}

	void readFile(Object obj) throws Exception {
		DataFile dtf = (DataFile) obj;
		currentSize += 512;

		int percent = (int) (currentSize * 100 / fileSize);
		// System.out.println(currentSize + " : " + fileSize);
		m_dtf.appendByte(dtf.data);
		clientHelper.setProgessBarPercent(percent);
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
