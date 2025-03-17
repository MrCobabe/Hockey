package hockey;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

public class HockeyPanel extends JPanel {
	int sides = 12;// start position
	TableShape shape;
	Polygon poly;
	int sideLength;
	int goalSize;
	int puckSize;
	int puckX;// not actually the puck,
	int puckY;// paddle locations
	Cursor c;
	Connect connect;
	double mouseSpeed;
	double[] tom = { 350, 350 }; // actually the puck; Two0 Optimistic Moms
	double puckDir = Math.PI * 7/8 ;// just me knot you
	int puckSpeed =1;

	public HockeyPanel(Connect connect) {// constructor!!!
		this.connect = connect;
		// cursing stuff
		BufferedImage fakeCursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		c = getToolkit().getDefaultToolkit().createCustomCursor(fakeCursor, new Point(0, 0), "Curses");
		this.setCursor(c);
		// end cursing stuff
		this.setPreferredSize(new Dimension(700, 700));
		this.setBackground(Color.red);
		shape = new TableShape(sides);
		poly = new Polygon(shape.exes, shape.wise, sides);
		sideLength = shape.exes[0] - shape.exes[1];
		goalSize = sideLength / 3;
		puckSize = goalSize / 3;

		Timer timer = new Timer(1, e -> {
			if (connect.numPlayers > 2) {
				if (connect.numPlayers != sides) {
					shape = new TableShape(connect.numPlayers);
					poly = new Polygon(shape.exes, shape.wise, connect.numPlayers);
					sideLength = shape.exes[0] - shape.exes[1];
					goalSize = sideLength / 3;
					puckSize = goalSize / 3;
				}
				sides = connect.numPlayers;

			} else if (connect.numPlayers == 2) {
				sides = 4;
			} else {
//				System.out.println("you're winning");
			}
			connect.sendPosition(puckX, puckY);
			// server only ↓
			if (connect.numPlayers > 2) {
				tom[0] = (Math.cos(puckDir) * puckSpeed + tom[0]);
				tom[1] = (tom[1] + Math.sin(puckDir) * puckSpeed);
			}
			connect.sendPuckPosition(tom);
			// server only ↑
			if (connect.puckPosition != null) {
				String[] tomSplit = connect.puckPosition.split(",");
//			tom[0] = Integer.valueOf(tomSplit[0]); //everyone but server
//			tom[1] = Integer.valueOf(tomSplit[1]); // do this
				double xPoint = tom[0] + puckSize / 2 + puckSize / 2 * Math.cos(puckDir);
				double yPoint = tom[1] + puckSize / 2 + puckSize / 2 * Math.sin(puckDir);
				if (poly.contains(xPoint,yPoint)) {
				} else {
					redirect(xPoint, yPoint);
				}
			}
			repaint();
		}); // ends timer comment because it's helpful
		timer.start();
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				if (checkZone(e.getX(), e.getY())) {
					//todo
					//locals a and b
					int a = e.getX();
					int b = e.getY();
					System.out.println("x difference: " + Math.abs(puckX - a));
					System.out.println("y difference: " + Math.abs(puckY -b));
					puckX = a;
					puckY = b;
					setCursor(c);
				} else {
					setCursor(new Cursor(1));
				}
			}

		});

	}// ends constructor

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.fillPolygon(poly);
		for (int i = 0; i < sides; i++) {
			int midX;
			int midY;
			
//			System.out.println("tomLeadX: " + tomLeadX + " tomLeadY: " + tomLeadY);
			if (i == sides - 1) {
				midX = (shape.exes[i] + shape.exes[0]) / 2;
				midY = (shape.wise[i] + shape.wise[0]) / 2;
			
			} else {
				midX = (shape.exes[i] + shape.exes[i + 1]) / 2;
				midY = (shape.wise[i] + shape.wise[i + 1]) / 2;
				
			}
			g.setColor(new Color(255, 0, 0));
			g.drawOval(midX - goalSize / 2, midY - goalSize / 2, goalSize, goalSize);
			// draw lines for sections
			g.drawLine(shape.exes[i], shape.wise[i], 350, 350);

		} // ends for loop still in paintypaint
		g.setColor(Color.white);
		g.fillOval(puckX - puckSize / 2, puckY - puckSize / 2, puckSize, puckSize);

		// painting other player locations
		if (connect.playerPositionsRAW != null) {
			String positions = connect.playerPositionsRAW;// error positions is null
			positions = positions.substring(1, positions.length() - 1);
			String[] pos = positions.split(", ");
			int counter = 0;
			for (String a : pos) {
				String[] b = a.split(";");
				int tempX = Integer.valueOf(b[0]);
				int tempY = Integer.valueOf(b[1]);
				// subtract 350s to translate to origin
				int[] tempPos = rotatePerson(tempX - 350, tempY - 350, counter);
				tempPos[0] = tempPos[0] + 350;
				tempPos[1] = tempPos[1] + 350;
				counter++;
				g.drawOval(tempPos[0] - puckSize / 2, tempPos[1] - puckSize / 2, puckSize, puckSize);
			}
		}
		// draw Tom
		g.setColor(Color.black);
		int a = (int) tom[0] - 350 + puckSize/2;
		int b = (int) tom[1] - 350 + puckSize/2;
		int[] ab = rotatePerson(a, b, 0);
		g.setColor(new Color(200, 200, 0));
		g.fillOval(ab[0] + 350 - puckSize /2, ab[1] + 350 - puckSize /2 , puckSize, puckSize);
		//test
	}// ends paintComponent

	public boolean checkZone(int x, int y) {
		boolean in = false;
		double m1 = (double) (shape.wise[0] - 350) / (double) (shape.exes[0] - 350);

		double b1 = shape.wise[0] - shape.exes[0] * m1;
		double m2 = (double) (shape.wise[1] - 350) / (double) (shape.exes[1] - 350);

		double b2 = shape.wise[1] - shape.exes[1] * m2;

		if (y > m1 * x + b1) {
			if (y > m2 * x + b2) {
				in = true;
			}
		}
		return in;
	}

	public int[] rotatePerson(int x, int y, int pn) {
		double theta = shape.angle;
		int ourPlayerNumber = connect.playerNumber;
		int shapePosition = pn - ourPlayerNumber;
		int x1 = (int) (x * Math.cos(theta * shapePosition) - y * Math.sin(theta * shapePosition));
		int y1 = (int) (x * Math.sin(theta * shapePosition) + y * Math.cos(theta * shapePosition));
		int[] pos = { x1, y1 };
		return pos;
	}

	// just server
	public void redirect(double pointX, double pointY) {
		int x1 = 0; int x2 = 0; int y1 = 0; int y2 = 0;
		for(int a = 0; a < shape.exes.length - 1; a++) {
//			System.out.println("shape exes: " + shape.exes[a] + "," + shape.exes[a+1]);
//			System.out.println("puck x: " + pointX);
			if(shape.exes[a] < pointX+5 && pointX - 5 < shape.exes[a+1] || shape.exes[a] > pointX - 5 && pointX + 5 > shape.exes[a+1]) {
				if(shape.wise[a] < pointY + 5 && pointY - 5 < shape.wise[a+1] ||shape.wise[a] > pointY + 5 && pointY + 5 > shape.wise[a+1]) {
					x1 = shape.exes[a]; x2 = shape.exes[a+1];y1 = shape.wise[a]; y2 = shape.wise[a+1];  
				}
			}
		}
		if(x1 == 0 && y2 == 0) {
			x1 = shape.exes[shape.exes.length-1]; x2 = shape.exes[0];
			y1 = shape.wise[shape.wise.length-1]; y2 = shape.wise[0];
		}
//		puckDir = Math.atan2(Math.sin(puckDir), Math.cos(puckDir));
		int A = (int)(puckDir * 180 / Math.PI); // angle for triangle based on quadrant
		int A2 = 0; // end angle to add twice for bounce
		while(A < 0) {
			A = A + 360;
		}
		while (A > 360) {
			A = A - 360;
		}
//		System.out.println(A + " PRINTING A");
		
		
		System.out.println("exes: " + x1 + "," + x2);
		System.out.println("wise: " + y1 + "," + y2);
		if(x1-x2==0 || y1-y2==0) {
			if (x1 - x2 == 0) {
				if (A > 0 && A < 90) {
					A = A + 2 * (90 - A);
				} else if (A > 90 && A < 180) {
					A = A - 2 * (A - 90);
				} else if (A > 180 && A < 270) {
					A = A + 2 * (90 - (A - 180));
				} else if (A > 270) {
					A = A - (2 * (A - 270));
				}
				puckDir = A * Math.PI / 180;
				return;
			}
			if (y1 - y2 == 0) {
				if (A > 0 && A < 90) {
					A = A - (2 * A);
				} else if (A > 90 && A < 180) {
					A = A + 2 * (180 - A);
				} else if (A > 180 && A < 270) {
					A = A - 2 * (A - 180);
				} else if (A > 270) {
					A = A + 2 * (360 - A);
				}
				puckDir = A * Math.PI / 180;
				return;
			}
		}else{
			double dx = x1 - x2;
			double dy = y1 - y2;
			double angle = Math.atan2(dy, dx);
//			System.out.println("DY: " + dy + " DX: " + dx);
			if(dy / dx > 0){
				System.out.println("old A: " + A );
				A = A - (int)(angle * 180 / Math.PI);
				System.out.println("A Rotated: " + A);
				if (A > 0 && A < 90) {
					A = A - (2 * A);
				} else if (A > 90 && A < 180) {
					A = A + 2 * (180 - A);
				} else if (A > 180 && A < 270) {
					A = A - 2 * (A - 180);
				} else if (A > 270) {
					A = A + 2 * (360 - A);
				}
				A = A + (int)(angle * 180/Math.PI);
				puckDir = A * Math.PI / 180;
				return;
			}else {
				A = A - (int)(angle * 180 / Math.PI);
				if (A > 0 && A < 90) {
					A = A - (2 * A);
				} else if (A > 90 && A < 180) {
					A = A + 2 * (180 - A);
				} else if (A > 180 && A < 270) {
					A = A - 2 * (A - 180);
				} else if (A > 270) {
					A = A + 2 * (360 - A);
				}
				puckDir = (A+(int)(angle * 180/Math.PI)) * Math.PI / 180;
				return;
			}
		}
	}
}