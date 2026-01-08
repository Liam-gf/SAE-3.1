/**
 * Réprésente une Methode d'une classe
 * @author Equipe 8
 */

package retroconception.metier;

import java.io.Serializable;
import java.util.List;

public class Methode implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String       nomMethode;
	private String       visibilite;
	private String       typeRetour;
	private List<String> paramNom;
	private List<String> paramType;
	private String       typeMethode;

	/**
	 * Construit une nouvelle méthode avec des données de base d'une méthode
	 * mais sans ce qu'ont fait à l'intérieur.
	 *
	 * @param nomMethode Le nom de la méthode
	 * @param visibilite La visibilité donc si elle est Ex: public, private, protected
	 * @param typeRetour Le type que va renvoyer la méthode
	 * @param paramNom   Une liste de nom des paramètres
	 * @param paramType  Une liste de type des paramètres
	 * @param type       Le type est peut être static ou non
	 */
	public Methode( String nomMethode, String visibilite, String typeRetour, List<String> paramNom,List<String> paramType,  String typeMethode )
	{
		this.nomMethode  = nomMethode;
		this.visibilite  = visibilite;
		this.typeRetour  = typeRetour;
		this.paramNom    = paramNom;
		this.paramType   = paramType;
		this.typeMethode = typeMethode;
	}

	/* --- Les getters --- */
	public String       getNomMethode()  { return this.nomMethode;  }
	public String       getVisibilite()  { return this.visibilite;  }
	public String       getTypeRetour()  { return this.typeRetour;  }
	public List<String> getParamNom()    { return this.paramNom;    }
	public List<String> getParamType()   { return this.paramType;   }
	public String       getTypeMethode() { return this.typeMethode; }

	/* --- Les setters --- */
	public void setNomMethode  ( String       nomMethode  ) { this.nomMethode  = nomMethode;  }
	public void setVisibilité  ( String       visibilite  ) { this.visibilite  = visibilite;  }
	public void setTypeRetour  ( String       typeRetour  ) { this.typeRetour  = typeRetour;  }
	public void setParamNom    ( List<String> paramNom    ) { this.paramNom    = paramNom;    }
	public void setParamType   ( List<String> paramType   ) { this.paramType   = paramType;   }
	public void setTypeMethode ( String       typeMethode ) { this.typeMethode = typeMethode; }
}