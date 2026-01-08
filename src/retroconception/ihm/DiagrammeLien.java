/**
 * Elle permet de créer les liens des multiplicités entre les classes
 * @author Equipe 8
 */

package retroconception.ihm;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Point;
import java.io.Serializable;

public class DiagrammeLien implements Serializable 
{
	private static final long serialVersionUID = 1L;

	// Enumération pour avoir tous les types de lien
	public enum TypeLien
	{
		ASSOCIATION,
		INHERITANCE,
		INTERFACE_IMPL;
	}

	private TypeLien       typeLien;
	private ClassGraphique source;
	private ClassGraphique cible;
	private Color          coul;
	private boolean        bidirectional;
	private boolean        afficherFleche;

	private String multipliciteSource;
	private String multipliciteCible;

	// Zones cliquables pour les multiplicités
	private Rectangle zoneMultSource;
	private Rectangle zoneMultCible;

	private double sourceAncreX;
	private double sourceAncreY;
	private double cibleAncreX;
	private double cibleAncreY;

	/**
	 * Crée un lien entre une classe source et une classe cible
	 * en utilisant par défaut un lien de type ASSOCIATION.
	 * @param source la classe graphique source du lien
	 * @param cible  la classe graphique cible du lien
	 */
	public DiagrammeLien(ClassGraphique source, ClassGraphique cible) 
	{
		this(source, cible, TypeLien.ASSOCIATION);
	}

	/**
	 * Initialise un lien entre une classe source et une classe cible
	 * en précisant le type de lien (association, héritage, etc.)
	 * Initialise également les valeurs graphiques par défaut
	 * (couleur, flèches, multiplicité, zones graphiques)
	 *
	 * @param source la classe graphique source du lien
	 * @param cible  la classe graphique cible du lien
	 * @param type   le type du lien (ASSOCIATION, HERITAGE, DEPENDANCE, etc.)
	 */
	public DiagrammeLien(ClassGraphique source, ClassGraphique cible, TypeLien type) 
	{
		this.source             = source;
		this.cible              = cible;
		this.typeLien           = type;
		this.coul               = Color.BLACK;
		this.bidirectional      = false;
		this.afficherFleche     = true;
		this.zoneMultSource     = new Rectangle();
		this.zoneMultCible      = new Rectangle();
		this.multipliciteSource = "";
		this.multipliciteCible  = "";

		calculateOptimalAnchors();
	}

	// Calcule les points d'ancrage optimaux
	private void calculateOptimalAnchors() 
	{
		if (this.source == null || this.cible == null) return;

		// Calcul basé sur les coordonnées logiques (coordX) + taille actuelle
		int sx = this.source.getCoordX() + this.source.getLargeur() / 2;
		int sy = this.source.getCoordY() + this.source.getHauteur() / 2;
		int tx = this.cible .getCoordX() + this.cible.getLargeur()  / 2;
		int ty = this.cible .getCoordY() + this.cible.getHauteur()  / 2;

		double dx = tx - sx;
		double dy = ty - sy;

		// Définit les coordonnées d'ancrage
		if (Math.abs(dx) > Math.abs(dy))
		{
			this.sourceAncreX = dx > 0 ? 1.0 : 0.0;
			this.sourceAncreY = 0.5;
		}
		else
		{
			this.sourceAncreX = 0.5;
			this.sourceAncreY = dy > 0 ? 1.0 : 0.0;
		}

		if (Math.abs(dx) > Math.abs(dy))
		{
			this.cibleAncreX = dx > 0 ? 0.0 : 1.0;
			this.cibleAncreY = 0.5;
		}
		else
		{
			this.cibleAncreX = 0.5;
			this.cibleAncreY = dy > 0 ? 0.0 : 1.0;
		}
	}

	// Méthode pour dessiner le lien
	public void dessinner(Graphics2D g2, int offsetX, int offsetY) 
	{
		if (this.source == null || this.cible == null) return;
		
		calculateOptimalAnchors();

		// On ajoute l'offset aux coordonnées logiques pour obtenir les coordonnées écran
		int x1 = (int) (this.source.getCoordX() + this.source.getLargeur() * this.sourceAncreX) + offsetX;
		int y1 = (int) (this.source.getCoordY() + this.source.getHauteur() * this.sourceAncreY) + offsetY;
		int x2 = (int) (this.cible.getCoordX () + this.cible.getLargeur () * this.cibleAncreX)  + offsetX;
		int y2 = (int) (this.cible.getCoordY () + this.cible.getHauteur () * this.cibleAncreY)  + offsetY;

		g2.setColor(this.coul);

		Stroke oldStroke = g2.getStroke();

		// Configure le style de la ligne selon le type du lien
		if (this.typeLien == TypeLien.INTERFACE_IMPL) 
		{
			// Ligne en pointillé
			Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
			g2.setStroke(dashed);
		}
		else
		{
			float lineWidth = this.bidirectional ? 2.0f : 1.5f;
			g2.setStroke(new BasicStroke(lineWidth));
		}

		g2.drawLine(x1, y1, x2, y2);
		
		g2.setStroke(new BasicStroke(1.5f));

		dessinnerLienAsso(g2, x1, y1, x2, y2);
		dessinerMultiplliciter(g2, x1, y1, x2, y2);

		g2.setStroke(oldStroke);
	}

	// Methode pour desinner les multiplicités
	private void dessinerMultiplliciter(Graphics2D g2, int x1, int y1, int x2, int y2) 
	{
		Font oldFont = g2.getFont();
		g2.setFont(new Font("SansSerif", Font.BOLD, 12));

		FontMetrics fm = g2.getFontMetrics();

		double dx  = x2 - x1, dy  = y2 - y1;
		double len = Math.max(1, Math.hypot(dx, dy));
		double ux  = dx / len, uy = dy / len;
		double nx  = -uy, ny      = ux;

		double distAlong = 30;
		
		if (Math.abs(dx) > Math.abs(dy))
		{
			nx = 0; ny = -18;
		}
		else
		{
			nx *= 18; ny *= 18;
		}

		// Positionne et affihce les multiplicités
		if (!this.multipliciteSource.isEmpty())
		{
			int xPos = (int) (x1 + ux * distAlong + nx);
			int yPos = (int) (y1 + uy * distAlong + ny);

			dessinnerMultipliciteTexte(g2, fm, this.multipliciteSource, xPos, yPos, this.zoneMultSource);
		}

		if (!this.multipliciteCible.isEmpty())
		{
			int xPos = (int) (x2 - ux * distAlong + nx);
			int yPos = (int) (y2 - uy * distAlong + ny);

			dessinnerMultipliciteTexte(g2, fm, this.multipliciteCible, xPos, yPos, this.zoneMultCible);
		}

		g2.setFont(oldFont);
	}

	// Dessine le texte de multiplicité dans une boite arrondie
	private void dessinnerMultipliciteTexte(Graphics2D g2, FontMetrics fm, String texte, int x, int y, Rectangle zone)
	{
		int w = fm.stringWidth(texte), h = fm.getHeight();
		int padding = 4;

		// Met à jour de la zone cliquable
		zone.setBounds(x - w/2 - padding, y - h/2 + 3, w + padding * 2, h);
		
		g2.setColor(new Color(255, 255, 255, 250));
		g2.fillRoundRect(x - w/2 - padding, y - h/2 + 3, w + padding * 2, h, 5, 5);
		
		g2.setColor(new Color(0, 0, 150));
		g2.drawString(texte, x - w/2, y + h/2);
	}

	// Dessine les liens selon leur association
	private void dessinnerLienAsso(Graphics2D g2, int x1, int y1, int x2, int y2)
	{
		// Ne pas dessiner de flèche si afficherFleche est false
		if (!this.afficherFleche)
			return;

		double angle = Math.atan2(y2 - y1, x2 - x1);

		switch (this.typeLien != null ? this.typeLien : TypeLien.ASSOCIATION)
		{
			case INHERITANCE:
				dessinerTriangle(g2, x2, y2, angle, false);
				break;
			case INTERFACE_IMPL:
				dessinerTriangle(g2, x2, y2, angle, false);
				break;
			case ASSOCIATION:
			default:
				if (this.bidirectional) 
				{
					dessinerFleche(g2, x1, y1, angle + Math.PI);
					dessinerFleche(g2, x2, y2, angle);
				}
				else
				{
					dessinerFleche(g2, x2, y2, angle);
				}
				break;
		}
	}

	// Dessine les fleches
	private void dessinerFleche(Graphics2D g2, int x, int y, double angle)
	{
		int arrowSize = 12;
		int x1 = x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
		int y1 = y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
		int x2 = x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
		int y2 = y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

		g2.drawLine(x, y, x1, y1);
		g2.drawLine(x, y, x2, y2);
	}


	// Permet de dessiner des triangles
	private void dessinerTriangle(Graphics2D g2, int x, int y, double angle, boolean filled)
	{
		int size = 15;
		int x1   = x - (int) (size * Math.cos(angle - Math.PI / 6));
		int y1   = y - (int) (size * Math.sin(angle - Math.PI / 6));
		int x2   = x - (int) (size * Math.cos(angle + Math.PI / 6));
		int y2   = y - (int) (size * Math.sin(angle + Math.PI / 6));

		int[] xPoints = {x, x1, x2};
		int[] yPoints = {y, y1, y2};

		Color old = g2.getColor();
		g2.setColor(Color.WHITE);
		g2.fillPolygon(xPoints, yPoints, 3);
		g2.setColor(old);
		g2.drawPolygon(xPoints, yPoints, 3);
	}

	// Méthodes pour détecter les clics sur les multiplicités
	public boolean contientPointSource(Point p) { return !this.multipliciteSource.isEmpty() && this.zoneMultSource.contains(p); }
	public boolean contientPointCible (Point p) { return !this.multipliciteCible.isEmpty() && this.zoneMultCible.contains(p);   }

	// Getters
	public String         getMultipliciteSource() { return this.multipliciteSource; }
	public String         getMultipliciteCible () { return this.multipliciteCible;  }
	public ClassGraphique getSource            () { return this.source;             }
	public ClassGraphique getCible             () { return this.cible;              }

	//  Setters
	public void setMultipliciteSource(String mult  ) { this.multipliciteSource = mult; }
	public void setMultipliciteCible (String mult  ) { this.multipliciteCible = mult;  }
	public void setBidirectional     (boolean bid  ) { this.bidirectional = bid;       }
	public void setTypeLien          (TypeLien type) { this.typeLien = type;           }
	public void setAfficherFleche    (boolean aff  ) { this.afficherFleche = aff;      }

	public void recalculerAncre() { calculateOptimalAnchors(); }
}