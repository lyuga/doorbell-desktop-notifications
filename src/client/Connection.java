import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Connection {
	private String address;
	private int port;
	private Socket socket;
	private DataInputStream socketIn;
	// サーバから受け取った値を格納し、0であればインターホンが押されたことを意味する
	private int exitStatus = 1;
	// エラーが発生した場合にGUIで表示するテキスト
	private String errorStatus;
	private boolean successfulConnection = false;

	public Connection(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public boolean hasSuccessfulConnection() {
		return successfulConnection;
	}

	public boolean isDoorbellPressed() {
		return exitStatus == 0 ? true : false;
	}

	public void connect() {
		socket = new Socket();
		try {
			// タイムアウト値の単位はミリ秒
			socket.connect(new InetSocketAddress(address, port), 2000);
			successfulConnection = true;
			System.out.println("Connected to port " + port + " at " + address);
			socketIn = new DataInputStream(socket.getInputStream());
			System.out.println("Input stream established.");
		} catch (SocketTimeoutException e) {
			errorStatus = "接続がタイムアウトしました";
		} catch (IOException e) {
			errorStatus = "接続に問題が生じました";
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			socket.close();
			System.out.println("Disconnected from the server.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receive() {
		try {
			exitStatus = socketIn.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Exit status : " + exitStatus);
	}
}
