package data;

// Cercle extends FormeAbstraite
public class Cercle extends FormeAbstraite implements Comparable<Cercle> 
{
	private double rayon;
	private Point centre;

	public Cercle(double rayon, Point centre) 
	{
		super();
		this.rayon = rayon;
		this.centre = centre;
	}

	public Cercle(double rayon, Point centre, String couleur, boolean rempli) 
	{
		super(couleur, rempli);
		this.rayon = rayon;
		this.centre = centre;
	}

	public double getRayon() { return rayon;  }
	public Point getCentre() { return centre; }

	public void setRayon(double rayon) 
	{
		this.rayon = rayon;
	}

	public void setCentre(Point centre) 
	{
		this.centre = centre;
	}

	@Override
	public double calculerSurface() 
	{
		return Math.PI * rayon * rayon;
	}

	@Override
	public double calculerPerimetre() 
	{
		return 2 * Math.PI * rayon;
	}

	@Override
	public int compareTo(Cercle autre) 
	{
		return Double.compare(this.rayon, autre.rayon);
	}

	@Override
	public String toString() {
		return "Cercle[rayon=" + rayon + ", centre=" + centre + ", couleur=" + couleur + "]";
	}
}
