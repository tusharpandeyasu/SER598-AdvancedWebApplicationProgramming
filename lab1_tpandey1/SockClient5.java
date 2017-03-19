import java.net.*;
import java.io.*;

class SockClient5 {
	public static void main (String args[]) throws Exception {
		Socket          sock = null;
		OutputStream    out = null;
		InputStream     in = null;
		int i1=0, i2=0;
		char cmd = ' ';

		if (args.length < 2 || args.length > 3) {
			System.out.println("USAGE: java SockClient5 Client ID<int1> [int2/<r> command]");														
			System.exit(1);
		}

		try {
			sock = new Socket("localhost", 8888);
			out = sock.getOutputStream();
			in = sock.getInputStream();
			
			DataOutputStream ot;
			//create write stream to send information
			ot=new DataOutputStream(out);
			
			DataInputStream it;
			//create read stream to read information
			it=new DataInputStream(in);
		
			cmd = args[1].charAt(0);
			out.write(cmd);
			switch (cmd) {
			case 'r':
				i1 = Integer.parseInt(args[0]);
				ot.writeInt(i1);
				break;
			default: 
				i1 = Integer.parseInt(args[0]);
				ot.writeInt(i1);
				i2 = Integer.parseInt(args[1]);
				ot.writeInt(i2);
			}
			int result = it.readInt();
			System.out.println("Result is " + result);
		} catch (NumberFormatException nfe) {
			System.out.println("Command line args must be integers or <r> to reset");
			System.exit(2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)  out.close();
			if (in != null)   in.close();
			if (sock != null) sock.close();
		}
	}
}
