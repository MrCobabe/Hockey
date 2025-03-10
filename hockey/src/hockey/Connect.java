package hockey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Connect extends Thread {// server things
	int numPlayers;
	int playerNumber;
	int port = 7777;
	String ip = "10.121.150.33";
	BufferedReader br;
	PrintWriter out;
	String playerPositionsRAW;
	String puckPosition;

	public Connect() throws UnknownHostException, IOException {
		Socket s = new Socket(ip, port);
		s.setTcpNoDelay(true);
		InputStream is = s.getInputStream();
		InputStreamReader r = new InputStreamReader(is);
		br = new BufferedReader(r);
		playerNumber = Integer.valueOf(br.readLine().split(":")[1]);
		System.out.println("you are player " + playerNumber);
		out = new PrintWriter(s.getOutputStream());// method of sending

		this.start();
	}

	public void run() {
		while (true) {
			try {
				String a = br.readLine();
				if (a != null) {
					//print a to see what the server is sending
					if (a.contains("player")) {
						numPlayers = Integer.valueOf(a.split(":")[1]);
					}else if(a.contains("puck")) {
						puckPosition = a.substring(4);
					}else {
						playerPositionsRAW = a;
//						System.out.println(a);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendPosition(int x, int y) {
		out.println(Integer.toString(x) + ";" + Integer.toString(y));
		out.flush();
	}
	//restricted ↓
	public void sendPuckPosition(double[] puck) {//server only
		out.println("puck" + (int)puck[0] + "," + (int)puck[1]);
		out.flush();
	}
	
	
}
