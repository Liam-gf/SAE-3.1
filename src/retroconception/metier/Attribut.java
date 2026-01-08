/**
 * Réprésente un attribut d'une classe
 * @author Equipe 8
 */

package retroconception.metier;

import java.io.Serializable;

public class Attribut implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int     numAttribut;
	private String  nom;
	private String  type;
	private String  visibilite;
	private String  portee;
	private boolean typeClasse;
	
	/**
	 * Construction d'un nouvel attribut avec les données d'un attibut d'une classe quelconque.
	 *
	 * @param numAttribut le numéro de l'attribut de la classe
	 * @param nom le nom de l'attribut
	 * @param type le type que prend l'attribut
	 * @param visibilite La visibilite que prend le l'attribut Ex:  private, public, proctected
	 * @param portee La portée de l'attribut. Si static -> classe sinon instance 
	 */
	public Attribut( int numAttribut, String nom, String type, String visibilite, String portee )
	{
		this.numAttribut = numAttribut;
		this.nom         = nom;
		this.type        = type;
		this.visibilite  = visibilite;
		this.portee      = portee;
		this.typeClasse  = true;
	}

	/* --- Les getters --- */
	public int     getNumAttribut() { return this.numAttribut; }
	public String  getNom()         { return this.nom;         }
	public String  getType()        { return this.type;        }
	public String  getVisibilite()  { return this.visibilite;  }
	public String  getPortee()      { return this.portee;      }
	public boolean getTypeClasse()  { return this.typeClasse;  }

	/* --- Les setters --- */
	public void setNumAttribut( int     numAttribut ) { this.numAttribut = numAttribut; }
	public void setNom        ( String  nom         ) { this.nom         = nom;         }
	public void setType       ( String  type        ) { this.type        = type;        }
	public void setVisibilite ( String  visibilite  ) { this.visibilite  = visibilite;  }
	public void setPortee     ( String  portee      ) { this.portee      = portee;      }
	public void setTypeClasse ( boolean typeClasse  ) { this.typeClasse  = typeClasse;  }
}