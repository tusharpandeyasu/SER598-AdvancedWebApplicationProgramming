import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SockServer6 {
	// Map<Integer, Integer> totals = new HashMap<Integer, Integer>();
	// Replaced the above code in HashMap with the ConcurrentHashMap, as per 2nd
	// part of the Problem 6 in order to increase the
	// throughout as it only locks a portion of the collection on update.
	ConcurrentHashMap<Integer, Integer> totals = new ConcurrentHashMap<Integer, Integer>();
	InputStream in = null;
	OutputStream out = null;
	static int sleepSeconds = 0;

	public static void main(String args[]) throws Exception {
		SockServer6 s6 = new SockServer6();
		try {
			if (args.length != 0) {
				sleepSeconds = Integer.parseInt(args[0]);
			}
			s6.runServer6();
		} catch (NumberFormatException nfe) {
			System.out.println("Command line args must be integers");
			System.exit(2);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (s6.out != null)
				s6.out.close();
			if (s6.in != null)
				s6.in.close();
			if (s6.in != null)
				s6.in.close();
		}
	}

	public void runServer6() throws Exception {
		ServerSocket server = null;
		try {
			server = new ServerSocket(8888);
		} catch (IOException e) {
			System.out.println("Could not listen on port 8888");
			System.exit(-1);
		}
		System.out.println("SockServer6 Ready...");
		while (true) {
			ServerThread w;
			try {
				// server.accept returns a client connection
				w = new ServerThread(server.accept());
				Thread t = new Thread(w);
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 8888");
				System.exit(-1);
			}
		}
	}

	public class ServerThread extends Thread {
		Socket sock;

		ServerThread(Socket sock) {
			this.sock = sock;
		}

		public void run() {
			InputStream in = null;
			OutputStream out = null;
			int clientId = 0;
			try {
				in = sock.getInputStream();
				out = sock.getOutputStream();

				DataInputStream it;
				// create read stream to read information
				it = new DataInputStream(in);

				DataOutputStream ot;
				// create write stream to send information
				ot = new DataOutputStream(out);

				char c = (char) in.read();

				switch (c) {
				case 'r':
					System.out.println("Server received " + c);
					clientId = it.readInt();
					totals.put(clientId, 0);
					System.out.println(
							"Thread: " + Thread.currentThread().getId() + " - Client: " + clientId + " - : Sleeping..");
					Thread.sleep(sleepSeconds);
					System.out.println(
							"Thread: " + Thread.currentThread().getId() + " - Client: " + clientId + " - : Woke up..");
					ot.writeInt(0);
					break;

				default:
					try {
						clientId = it.readInt();
						int x = it.readInt();
						System.out.println("Server received " + x + " from client " + clientId);
						Integer total = totals.get(clientId);
						if (total == null) {
							total = 0;
						}
						totals.put(clientId, total + x);
						System.out.println("Thread: " + Thread.currentThread().getId() + " - Client: " + clientId
								+ " - : Sleeping..");
						Thread.sleep(sleepSeconds);
						System.out.println("Thread: " + Thread.currentThread().getId() + " - Client: " + clientId
								+ " - : Woke up..");
						ot.writeInt(totals.get(clientId));
					} catch (Exception e) {
						System.out.println("Arguments must be integers or <r> to reset");
					}
					break;
				}
				System.out.println("");
				out.flush();
				this.sock.close();
			} catch (NumberFormatException nfe) {
				System.out.println("Command line args must be integer");
				System.exit(2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}