package hockey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class HockeyClientHandler extends Thread {
	public int numPlayers;
	PrintWriter out;
	BufferedReader in;
	int playerNumber;
	ArrayList<String> playerPositions;
	ArrayList<HockeyClientHandler> players;

	public HockeyClientHandler(Socket s, ArrayList<String> playerPositions_, int playerNumber_,ArrayList<HockeyClientHandler> players_) throws IOException {
		players = players_;
		playerNumber = playerNumber_;
		out = new PrintWriter(s.getOutputStream());
		out.println("playerNumber:" + playerNumber);
		out.flush();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		playerPositions = playerPositions_;
		this.start();
	}

	public void updatePlayers(int a) {
		out.println("players:" + a);
		out.flush();
	}
	public void sendAll(String a) {
		for(HockeyClientHandler b : players) {
			b.out.println(a);
			b.out.flush();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				String a = in.readLine();
				if (a != null) {
					if (a.contains("puck")) {
//						System.out.println("PUNK FROM SERVER SIDE " + a);
						sendAll(a);
					} else {
						playerPositions.set(playerNumber, a);
//						System.out.println(playerPositions);
						out.println(playerPositions.toString());
						out.flush();// fixed it
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.out.println("someone probably left");
			}
		}
	}
}
