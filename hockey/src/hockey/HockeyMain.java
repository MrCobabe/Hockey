package hockey;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class HockeyMain {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println(InetAddress.getLocalHost());//server only
		HockeyServer hs = new HockeyServer();//server only
		Connect connect = new Connect();
		JFrame frame = new JFrame("Hockey Pockey");
		HockeyPanel panel = new HockeyPanel(connect); 
		frame.add(panel); 
		frame.pack();
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
	}

}
