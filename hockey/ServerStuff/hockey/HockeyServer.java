package hockey;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HockeyServer extends Thread{
	ServerSocket s;
	int numPlayers = 0;
	int port = 7777;
	ArrayList<HockeyClientHandler> players;
	ArrayList<String> playerPositions;
	int puckX = 350;
	int puckY = 350;
	
	public HockeyServer() throws IOException {
		players = new ArrayList<HockeyClientHandler>();
		playerPositions = new ArrayList<String>();
		s = new ServerSocket(port);
		this.start();
	}
	
	
	public void run() {  
		while(true) {
			Socket socket;
			try {
				socket = s.accept();
				playerPositions.add("");//ensuring size of list
			HockeyClientHandler player = new HockeyClientHandler(socket,playerPositions,numPlayers,players);
//			player.playerNumber = numPlayers;
			players.add(player);
//			System.out.println(s.getInetAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			numPlayers++;
			for(HockeyClientHandler player : players) {
				player.numPlayers = numPlayers;
				player.updatePlayers(numPlayers);
			}
		}
	}

}
