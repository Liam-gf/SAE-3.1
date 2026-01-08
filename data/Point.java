package data;

public class Point {
	private int x;
	private int y;

	public Point(String nom, int x, int y) {
		this.y = y;
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void deplacer(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	@Override
	public String toString() {
		return "Point(" + x + ", " + y + ")";
	}
}
