/**
 * Cette classe permet de creer un diagramme d'etat
 * @author Equipe 8
 */

package retroconception.metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;

import retroconception.ihm.DiagrammeLien;

public class DiagrammeEtat implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ArrayList<ClasseInfo>    listeClasses;
	private HashMap<String, Point>   positionsGraphiques;
	private ArrayList<DiagrammeLien> liens;

	public DiagrammeEtat( ArrayList<ClasseInfo> listeClasses, HashMap<String, Point> positionsGraphiques, ArrayList<DiagrammeLien> liens )
	{
		this.listeClasses        = listeClasses;
		this.positionsGraphiques = positionsGraphiques;
		this.liens               = liens;
	}

	public ArrayList<ClasseInfo>    getListeClasses       () { return this.listeClasses;        }
	public HashMap<String, Point>   getPositionsGraphiques() { return this.positionsGraphiques; }
	public ArrayList<DiagrammeLien> getLiens              () { return this.liens;               }
}