import java.net.*;
import java.io.*;
import java.util.*;

class SockServer4 {
	public static void main(String args[]) throws Exception {
		ServerSocket serv = null;
		InputStream in = null;
		OutputStream out = null;
		Socket sock = null;
		int clientId = 0;
		Map<Integer, Integer> totals = new HashMap<Integer, Integer>();

		try {
			serv = new ServerSocket(8888);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (serv.isBound() && !serv.isClosed()) {
			System.out.println("SockServer4 Ready...");
			try {
				sock = serv.accept();
				in = sock.getInputStream();
				out = sock.getOutputStream();

				DataInputStream it;
				// create read stream to read information
				it = new DataInputStream(in);

				DataOutputStream ot;
				// create write stream to send information
				ot = new DataOutputStream(out);

				char c = (char) in.read();
				if (args.length > 0)
					Thread.sleep(Long.parseLong(args[0]));
				switch (c) {
				case 'r':
					System.out.print("Server received " + c);
					clientId = it.readInt();
					totals.put(clientId, 0);
					ot.writeInt(0);
					break;

				default:
					try {
						clientId = it.readInt();
						int x = it.readInt();
						System.out.print("Server received " + x + " from client " + clientId);
						Integer total = totals.get(clientId);
						if (total == null) {
							total = 0;
						}
						totals.put(clientId, total + x);
						ot.writeInt(totals.get(clientId));
					} catch (Exception e) {
						System.out.println("Arguments must be integers or <r> to reset");
					}
					break;
				}
				System.out.println("");
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (sock != null)
					sock.close();
			}
		}
	}
}