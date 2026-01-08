package data;

// Interface pour les objets qui peuvent être dessinés
public interface Dessinable 
{
	void dessiner();
	void effacer();
	void deplacer(int dx, int dy);
}
