/**
 * Cette classe permet de creer un Diagramme de sauvegarde
 * @author Equipe 8
 */
package retroconception.metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;

public class DiagrammeSauvegarde implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public ArrayList<ClasseInfo>  classes;
	public ArrayList<String>      associations;
	public ArrayList<String>      relations;
	public HashMap<String, Point> positions;
	public double                 offsetX;
	public double                 offsetY;
	
	public static long            getSerialversionuid() { return serialVersionUID; }
	public ArrayList<ClasseInfo>  getClasses         () { return classes;          }
	public ArrayList<String>      getAssociations    () { return associations;     }
	public ArrayList<String>      getRelations       () { return relations;        }
	public HashMap<String, Point> getPositions       () { return positions;        }
	public double                 getOffsetX         () { return offsetX;          }
	public double                 getOffsetY         () { return offsetY;          }

	public void setClasses     ( ArrayList<ClasseInfo>  classes      ) { this.classes      = classes;      }
	public void setAssociations( ArrayList<String>      associations ) { this.associations = associations; }
	public void setRelations   ( ArrayList<String>      relations    ) { this.relations    = relations;    }
	public void setPositions   ( HashMap<String, Point> positions    ) { this.positions    = positions;    }
}