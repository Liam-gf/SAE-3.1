package data;

// Classe abstraite représentant une forme géométrique
public abstract class FormeAbstraite implements Forme 
{
	protected String couleur;
	protected boolean rempli;

	public FormeAbstraite() 
	{
		this.couleur = "Blanc";
		this.rempli = false;
	}

	public FormeAbstraite(String couleur, boolean rempli) 
	{
		this.couleur = couleur;
		this.rempli = rempli;
	}

	public String getCouleur() { return couleur; }
	public boolean estRempli() { return rempli;  }

	public void setCouleur(String couleur) 
	{
		this.couleur = couleur;
	}

	public void setRempli(boolean rempli) 
	{
		this.rempli = rempli;
	}

	// Méthodes abstraites à implémenter par les sous-classes
	public abstract double calculerSurface();
	public abstract double calculerPerimetre();

	@Override
	public String getNom() { return this.getClass().getSimpleName(); }

	public void afficher() 
	{
		System.out.println("Forme: " + getNom() + ", Couleur: " + couleur + ", Rempli: " + rempli);
	}
}
