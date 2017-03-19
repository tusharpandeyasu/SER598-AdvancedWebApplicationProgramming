import java.net.*;
import java.io.*;

class SockClient2 {
	public static void main (String args[]) throws Exception {
		Socket          sock = null;
		OutputStream    out = null;
		InputStream     in = null;
		int i1=0;
		
		if (args.length != 1) {
			System.out.println("USAGE: java SockClient2 [int1/<r> command]");														
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
		
			i1 = args[0].charAt(0);
			out.write(i1);
			switch (i1) {
			case 'r' :
				//Do nothing
				break;
			default :
				ot.writeInt(Integer.parseInt(args[0]));
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
