package hockey;

public class TableShape{
	int[] exes;
	int[] wise;
	int centerX;
	int centerY;
	double angle;
	
	public TableShape(int sides) {
		exes = new int[sides];
		wise = new int[sides];
		angle = 2 * Math.PI / sides;
		int r = 300;
		for(int i = 0; i < sides; i++) {
			int x = (int) (r * Math.cos(angle * i + (Math.PI - angle)/2));
			int y = (int) (r * Math.sin(angle * i + (Math.PI - angle)/2));
			x = x + 350;
			y = y + 350;
			exes[i] = x;
			wise[i] = y;
		}
	}
	
	
	
}
