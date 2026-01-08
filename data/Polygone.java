package data;

// Polygone extends FormeAbstraite et implements Dessinable
public class Polygone extends FormeAbstraite implements Dessinable 
{
	private Point[] sommets;
	private int nbSommets;

	public Polygone(Point[] sommets) 
	{
		super();
		this.sommets = sommets;
		this.nbSommets = sommets.length;
	}

	public Polygone(Point[] sommets, String couleur, boolean rempli) 
	{
		super(couleur, rempli);
		this.sommets = sommets;
		this.nbSommets = sommets.length;
	}

	public Point[] getSommets() { return sommets; }
	public int getNbSommets() { return nbSommets; }

	@Override
	public double calculerSurface() 
	{
		// Formule du lacet (Shoelace formula)
		double surface = 0;
		for (int i = 0; i < nbSommets; i++) {
			int j = (i + 1) % nbSommets;
			surface += sommets[i].getX() * sommets[j].getY();
			surface -= sommets[j].getX() * sommets[i].getY();
		}
		return Math.abs(surface) / 2.0;
	}

	@Override
	public double calculerPerimetre() 
	{
		double perimetre = 0;
		for (int i = 0; i < nbSommets; i++) 
		{
			int j = (i + 1) % nbSommets;
			double dx = sommets[j].getX() - sommets[i].getX();
			double dy = sommets[j].getY() - sommets[i].getY();
			perimetre += Math.sqrt(dx * dx + dy * dy);
		}
		return perimetre;
	}

	@Override
	public void dessiner() 
	{
		System.out.println("Dessiner le polygone à " + nbSommets + " sommets");
	}

	@Override
	public void effacer() 
	{
		System.out.println("Effacer le polygone");
	}

	@Override
	public void deplacer(int dx, int dy) 
	{
		for (Point sommet : sommets) 
		{
			sommet.deplacer(dx, dy);
		}
		System.out.println("Polygone déplacé de (" + dx + ", " + dy + ")");
	}

	@Override
	public String toString() 
	{
		return "Polygone[" + nbSommets + " sommets, couleur=" + couleur + "]";
	}
}
