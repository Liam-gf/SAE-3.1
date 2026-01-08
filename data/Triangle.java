package data;

// Triangle extends FormeAbstraite
public class Triangle extends FormeAbstraite 
{
	private Point sommet1;
	private Point sommet2;
	private Point sommet3;

	public Triangle(Point sommet1, Point sommet2, Point sommet3) 
	{
		super();
		this.sommet1 = sommet1;
		this.sommet2 = sommet2;
		this.sommet3 = sommet3;
	}

	public Triangle(Point sommet1, Point sommet2, Point sommet3, String couleur, boolean rempli) 
	{
		super(couleur, rempli);
		this.sommet1 = sommet1;
		this.sommet2 = sommet2;
		this.sommet3 = sommet3;
	}

	public Point getSommet1() {	return sommet1; }
	public Point getSommet2() {	return sommet2; }
	public Point getSommet3() {	return sommet3; }
	
	private double calculerDistance(Point p1, Point p2) 
	{
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public double calculerSurface() 
	{
		// Formule de Héron
		double a = calculerDistance(sommet1, sommet2);
		double b = calculerDistance(sommet2, sommet3);
		double c = calculerDistance(sommet3, sommet1);
		double s = (a + b + c) / 2;
		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	@Override
	public double calculerPerimetre() 
	{
		double a = calculerDistance(sommet1, sommet2);
		double b = calculerDistance(sommet2, sommet3);
		double c = calculerDistance(sommet3, sommet1);
		return a + b + c;
	}

	@Override
	public String toString() 
	{
		return "Triangle[" + sommet1 + ", " + sommet2 + ", " + sommet3 + ", couleur=" + couleur + "]";
	}
}
