/**
 * Réprésente une classe avec une liste d'attributs et de méthodes
 * @author Equipe 8
 */

package retroconception.metier;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClasseInfo implements Serializable
{
	private static final long   serialVersionUID = 1L;

	private String              nomClasse;
	private ArrayList<Attribut> attributs;
	private ArrayList<Methode>  methodes;

	/**
	 * Construit un Objet ClasseInfo
	 * @param nomClasse Le nom de la classe dont on va récupérer les attributs et méthodes.
	 * @param cheminAbsolue Le chemin absolue pour trouver le fichier à lire
	 */
	public ClasseInfo(String chemin)
	{
		String nom = Paths.get(chemin).getFileName().toString().replace(".java", "");

		this.nomClasse = nom;
		this.attributs = new LireFichier(chemin).LireAttribut();
		this.methodes  = new LireFichier(chemin).LireMethode();
	}

	/* --- Les adds --- */
	public void addAttribut( Attribut att ) { this.attributs.add(att); }
	public void addMethode ( Methode  met ) { this.methodes .add(met); }

	/* --- Les getters --- */
	public String              getNomClasse() { return this.nomClasse; }
	public ArrayList<Attribut> getAttributs() { return this.attributs; }
	public ArrayList<Methode>  getMethodes () { return this.methodes;  }

	/* --- Les setters --- */
	public void setAttributs( ArrayList<Attribut> attributs ) { this.attributs = attributs; }
	public void setMethodes ( ArrayList<Methode>  methodes  ) { this.methodes  = methodes;  }
}
