/**
 * Elle permet d'afficher les classes enregistrées dans le fichier
 * @author Equipe 8
 */

package retroconception.ihm;

import retroconception.Controleur;

import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.util.ArrayList;


public class ClassGraphique extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Controleur ctrl;

	private int indCl;
	private int coordX;
	private int coordY;
	private int largeur;
	private int hauteur;

	private int largeurMaxAtt  = 0;
	private int largeurMaxMeth = 0;

	private ArrayList<String> elementsUML;
	private FontMetrics fm;

	public ClassGraphique(Controleur ctrl, int indCl, int x, int y, String Nom)
	{
		this.ctrl  = ctrl;
		this.indCl = indCl;
		
		this.coordX = x;
		this.coordY = y;

		this.setLayout(null);
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
		
		this.elementsUML = new ArrayList<String>();
		
		this.setBounds(this.coordX, this.coordY, this.largeur, this.hauteur);

		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		this.fm = g2.getFontMetrics();
		this.elementsUML.add(Nom);
		this.elementsUML.add("coupure");
		
		this.afficher(Nom, false);
	}

	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		// Initialise l'arrière plan
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.largeur, this.hauteur);

		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(Color.BLACK);
		g2.drawRect(0, 0, this.largeur - 1, this.hauteur - 1);

		Font policeNomClasse = new Font("SansSerif", Font.BOLD, 14);
		Font policeItalique  = new Font("SansSerif", Font.ITALIC, 14);
		Font policeNormale   = new Font("SansSerif", Font.PLAIN, 14);

		g2.setFont(policeNormale);
		this.fm = g2.getFontMetrics(policeNormale);

		int hauteurLigne = this.fm.getHeight();
		int yCourant     = this.fm.getAscent() + 5;
		int xAlignement  = 10;

		// initialise les éléments 
		for (String element : this.elementsUML)
		{
			// Titre
			if (element.equals(this.elementsUML.get(0)))
			{
				g2.setFont(policeNomClasse);
				this.fm  = g2.getFontMetrics(policeNomClasse);
				int xNom = (this.largeur - this.fm.stringWidth(element)) / 2;

				g2.drawString(element, xNom, yCourant);
				yCourant += hauteurLigne;
			}
			else if (element.equals("coupure"))
			{
				int ySeparateur = yCourant - (hauteurLigne / 2);
				g2.drawLine(0, ySeparateur, this.largeur, ySeparateur);

				yCourant += hauteurLigne;
			}
			else
			{
				// Attributs
				if (element.contains("{abstract}")) // si element contient "abstract" alors on écrit en italique
				{
					g2.setFont(policeItalique);
					g2.drawString(element.replace("{abstract}", "").trim(), xAlignement, yCourant);
				}
				else
				{
					g2.setFont(policeNormale);

					int indDeuxPoint = element.lastIndexOf(":");
					if(indDeuxPoint == -1 )
						g2.drawString(element, xAlignement, yCourant);
					else
					{
						String nom  = element.substring(0, indDeuxPoint).trim();
						String type = element.substring(indDeuxPoint   );

						g2.drawString(nom, xAlignement, yCourant);
						int xType = 0;

						if(element.contains("("))
							xType = xAlignement + this.largeurMaxMeth + 10 ;
						else
							xType = xAlignement + this.largeurMaxAtt + 10 ;

						g2.drawString(type, xType, yCourant);
					}
				}

				yCourant += hauteurLigne;
			}
		}
	}


	public void afficher(String nomClasse, boolean aff)
	{
		// On réinitialise la liste pour éviter les doublons à chaque clic
		this.elementsUML = new ArrayList<String>();
		this.elementsUML.add(nomClasse);
		this.elementsUML.add("coupure");
		this.largeurMaxAtt  = 0;
		this.largeurMaxMeth = 0;

		Affiche affiche = new Affiche();

		int largeurContenu  = this.fm.stringWidth(nomClasse);
		int largeurAttribut = 0;
		int largeurMethode  = 0;
		int cpt             = 0;

		if (aff)
		{
			/* --- MODE D'AFFICHAGE COMPLET --- */ 
			if (this.ctrl.getClasseInfoById(this.indCl).getAttributs() != null)
			{
				int tailleLstAtt  = this.ctrl.getClasseInfoById(this.indCl).getAttributs().size();
				int tailleLstMeth = this.ctrl.getClasseInfoById(this.indCl).getMethodes().size();

				// Ecriture complète des attributs
				for (int ind = 0; ind < tailleLstAtt; ind++)
				{
					
					String represAtt = affiche.afficherAtt(this.ctrl, this.indCl, ind);
					if(!represAtt.equals("") )
					{
						int indDeuxPoint = represAtt.lastIndexOf(":");
						String nomAtt = represAtt.substring(0, indDeuxPoint).trim();

						this.largeurMaxAtt = Math.max(this.largeurMaxAtt, this.fm.stringWidth(nomAtt));

						this.elementsUML.add(represAtt);
						largeurAttribut = Math.max(largeurAttribut, this.fm.stringWidth(represAtt));
					}
				}

				this.elementsUML.add("coupure");
				
				
				cpt = 0;
				for (int ind = 0; ind < tailleLstMeth; ind++)
				{
					// Ecriture complète des méthodes
					String represMeth = affiche.afficherMeth(this.ctrl, this.indCl, ind, false);
					if (!represMeth.equals(""))
					{
						int indDeuxPoint = represMeth.lastIndexOf(":");

						if(indDeuxPoint != -1)
						{
							String nomMeth = represMeth.substring(0, indDeuxPoint).trim();
							this.largeurMaxMeth = Math.max(this.largeurMaxMeth, this.fm.stringWidth(nomMeth));
						}
						else
							this.largeurMaxMeth = Math.max(this.largeurMaxMeth, this.fm.stringWidth(represMeth));

						this.elementsUML.add(represMeth);
						largeurMethode = Math.max(largeurMethode, this.fm.stringWidth(represMeth));
					}
				}
			}
		}
		else
		{
			/* --- MODE D'AFFICHAGE RÉDUIT --- */ 
			if (this.ctrl.getClasseInfoById(this.indCl).getAttributs() != null)
			{
				int tailleLstAtt  = this.ctrl.getClasseInfoById(this.indCl).getAttributs().size();
				int tailleLstMeth = this.ctrl.getClasseInfoById(this.indCl).getMethodes().size();

				for (int ind = 0; ind < tailleLstAtt; ind++)
				{
					// Version réduite de l'écriture des attributs ( "..." après 3 attributs )
					String represAtt = affiche.afficherAtt(this.ctrl, this.indCl, ind);
					if(!represAtt.equals("") )
					{
						int indDeuxPoint = represAtt.lastIndexOf(":");
						String nomAtt = represAtt.substring(0, indDeuxPoint).trim();

						this.largeurMaxAtt = Math.max(this.largeurMaxAtt, this.fm.stringWidth(nomAtt));

						this.elementsUML.add(represAtt);
						largeurAttribut = Math.max(largeurAttribut, this.fm.stringWidth(represAtt));
						cpt++;
						if(cpt >= 3 )
						{
							this.elementsUML.add("...");
							break;
						}
					}
				}

				this.elementsUML.add("coupure");

				cpt = 0;
				for (int ind = 0; ind < tailleLstMeth; ind++)
				{
					// Version réduite de l'écriture des méthodes ( "..." après 3 attributs )
					String represMeth = affiche.afficherMeth(this.ctrl, this.indCl, ind, true);
					if (!represMeth.equals(""))
					{
						int indDeuxPoint = represMeth.lastIndexOf(":");

						if(indDeuxPoint != -1)
						{
							String nomMeth = represMeth.substring(0, indDeuxPoint).trim();
							this.largeurMaxMeth = Math.max(this.largeurMaxMeth, this.fm.stringWidth(nomMeth));
						}
						else
							this.largeurMaxMeth = Math.max(this.largeurMaxMeth, this.fm.stringWidth(represMeth));

						this.elementsUML.add(represMeth);
						largeurMethode = Math.max(largeurMethode, this.fm.stringWidth(represMeth));
						cpt++;
						if(cpt >= 3 )
						{
							this.elementsUML.add("...");
							break;
						}
					}
				}
			}
		}

		int largeurTotaleAtt  = 10 + this.largeurMaxAtt  + 30 + (largeurAttribut - this.largeurMaxAtt);
		int largeurTotaleMeth = 10 + this.largeurMaxMeth + 30 + (largeurMethode  - this.largeurMaxMeth);

		// On prend le maximum entre le nom de la classe, les attributs et les méthodes
		this.largeur = Math.max(largeurContenu, Math.max(largeurTotaleAtt, largeurTotaleMeth)) + 170;
		this.hauteur = (this.elementsUML.size() * fm.getHeight()) + 10;

		// Mise à jour physique du composant Swing
		this.setSize(this.largeur, this.hauteur);
		this.revalidate();
		this.repaint();
	}

	// Getteurs et setteurs
	public int               getCoordX     () { return this.coordX;      }
	public int               getCoordY     () { return this.coordY;      }
	public int               getLargeur    () { return this.largeur;     }
	public int               getHauteur    () { return this.hauteur;     }
	public int               getIndClasse  () { return this.indCl;       }
	public ArrayList<String> getElementsUML() { return this.elementsUML; }

	public void setCoordX(int x) { this.coordX = x; }
	public void setCoordY(int y) { this.coordY = y; }
}