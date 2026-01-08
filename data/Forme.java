package data;

// Interface de base pour toutes les formes géométriques
public interface Forme
{
	double calculerSurface();
	double calculerPerimetre();
	String getNom();
}
