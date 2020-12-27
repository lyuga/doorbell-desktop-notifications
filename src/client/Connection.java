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

	public void connect() {
		socket = new Socket();
		try {
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
}
