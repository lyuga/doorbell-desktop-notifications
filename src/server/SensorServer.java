import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SensorServer {
	// サーバに同時接続できるクライアント数の上限
	private static final int MAX_CLIENTS = 10;
	private static final int PORT = 9888;
	// サーバに接続しているクライアント数を得るため、各クライアントに対するPrintWriterを格納する
	private static Set<DataOutputStream> writers = new HashSet<DataOutputStream>();
	private static ProcessBuilder pb;
	private static Process p;

	public static void main(String[] args) throws Exception {
		pb = new ProcessBuilder(new String[] { "pi4j", "MCP3008Reader" });
		// エラー出力を標準出力にマージし、標準出力の読み取りで両方の内容を取得する
		pb.redirectErrorStream(true);

		// サーバのリソースが限られているので、クライアントの接続数を制限する
		ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
		try (ServerSocket listener = new ServerSocket(PORT)) {
			System.out.println("Sensor server started.");
			while (true) {
				System.out.println("Waiting for new connection...");
				pool.execute(new ClientHandler(listener.accept()));
			}
		}
	}

	private static class ClientHandler implements Runnable {
		private Socket socket;
		private DataOutputStream socketOut;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				socketOut = new DataOutputStream(socket.getOutputStream());
				System.out.println("Stream established.");

				synchronized (writers) {
					/*
					 * センサの読み取りは、接続しているクライアント数に関わらず、1つの監視用サブプロセスだけが行えばよい。
					 * したがって接続しているクライアント数が0から1に増加する場合にのみ、サブプロセスを開始させる。
					 */
					if (writers.isEmpty()) {
						p = pb.start();
						BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line;
						while ((line = bf.readLine()) != null) {
							System.out.println(line);
						}
					}
					writers.add(socketOut);
				}

				synchronized (p) {
					int exitStatus = p.waitFor();
					p.destroy();
					System.out.println("Exit status : " + exitStatus);
					socketOut.writeInt(exitStatus);
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				/*
				 * サーバはクライアントにインターホンの通知を送信したら、その接続を閉じる。
				 * そのクライアントに対するPrintWriterは不要になるため、集合から取り除く。
				 */
				if (socketOut != null) {
					writers.remove(socketOut);
				}
				try {
					socket.close();
					System.out.println("Scoket closed.");
				} catch (IOException e) {
				}
			}
		}
	}
}
