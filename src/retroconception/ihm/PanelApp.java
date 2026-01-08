/**
 * Cette classe permet de créer un panel qui est lié à la FrameApp
 * @author Equipe 8
 */

package retroconception.ihm;

import javax.swing.*;

import retroconception.Controleur;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.GridLayout;

import java.awt.event.*;
import java.util.ArrayList;

public class PanelApp extends JPanel
{
	private Controleur ctrl;
	private FrameApp   frame;
	private Image      imageDeFond;

	private ClassGraphique classSelectionnee;

	// Coordonnées pour le déplacement dans la Frame
	private int coordDeplX = 0;
	private int coordDeplY = 0;

	// Variables pour le déplacement de souris to drag : glisser, trainer
	private int deplDebSourisX;
	private int deplDebSourisY;

	// Position initiale lors du clic
	private int initCordX;
	private int initCordY;
	private int initCordDeplX;
	private int initCordDeplY;

	private ArrayList<DiagrammeLien> lstLiens;

	public PanelApp(Controleur ctrl, FrameApp frame) 
	{
		this.ctrl     = ctrl;
		this.frame    = frame;
		this.lstLiens = new ArrayList<DiagrammeLien>();

		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);

		GereSouris gestionnaireSouris = new GereSouris();
		this.addMouseListener(gestionnaireSouris);
		this.addMouseMotionListener(gestionnaireSouris);

		ToolTipManager.sharedInstance().registerComponent(this);
	}

	// Applique le décalage aux composants lors du déplacement
	public void miseAJourVue()
	{
		for (Component comp : this.getComponents())
		{
			if (comp instanceof ClassGraphique)
			{
				ClassGraphique cg = (ClassGraphique) comp;
				// Position écran = Position Logique + Décalage global
				cg.setLocation(cg.getCoordX() + this.coordDeplX, cg.getCoordY() + this.coordDeplY);
			}
		}
		this.revalidate();
		this.repaint();
	}

	public void reinitVue()
	{
		this.coordDeplX = 0;
		this.coordDeplY = 0;
		miseAJourVue();
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (this.imageDeFond != null)
			g.drawImage(this.imageDeFond, 0, 0, this.getWidth(), this.getHeight(), this);

		// Lissage des éléments
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// On passe l'offset aux liens pour qu'ils suivent les classes
		for (DiagrammeLien lien : this.lstLiens)
			lien.dessinner(g2, this.coordDeplX, this.coordDeplY);
	}

	// Classe interne pour gérer la souris
	private class GereSouris extends MouseAdapter
	{
		// Lors du maintien de la souris
		public void mousePressed(MouseEvent e)
		{
			Point pointEcran = new Point(e.getX(), e.getY());

			for (DiagrammeLien lien : PanelApp.this.lstLiens)
				{
				if (lien.contientPointSource(pointEcran))
				{
					editerMultiplicite(lien, true);
					return;
				}
				else if (lien.contientPointCible(pointEcran))
				{
					editerMultiplicite(lien, false);
					return;
				}
			}

			PanelApp.this.deplDebSourisX = e.getX();
			PanelApp.this.deplDebSourisY = e.getY();

			Component[] comps = getComponents();
			// Parcours inversé (Z-order)
			for (int i = comps.length - 1; i >= 0; i--)
			{
				if (comps[i] instanceof ClassGraphique)
				{
					ClassGraphique cg = (ClassGraphique) comps[i];
					if (cg.getBounds().contains(e.getPoint()))
					{
						PanelApp.this.classSelectionnee = cg;
						PanelApp.this.initCordX = cg.getCoordX();
						PanelApp.this.initCordY = cg.getCoordY();
						setComponentZOrder(cg, 0); // Met au premier plan

						String nom = cg.getElementsUML().get(0);
						if (SwingUtilities.isRightMouseButton(e))
							cg.afficher(nom, true); // Affiche la version complète de la classe
						else
							cg.afficher(nom, false);// Affiche la version réduite de la classe

						// IMPORTANT : On remet à jour la vue après changement de taille
						miseAJourVue();

						repaint();
						return;
					}
				}
			}

			// Si on n'a pas cliqué sur une classe, on prépare le déplacement du fond
			PanelApp.this.classSelectionnee = null;
			PanelApp.this.initCordDeplX = PanelApp.this.coordDeplX;
			PanelApp.this.initCordDeplY = PanelApp.this.coordDeplY;
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}

		public void mouseDragged(MouseEvent e)
		{
			int deltaX = e.getX() - PanelApp.this.deplDebSourisX;
			int deltaY = e.getY() - PanelApp.this.deplDebSourisY;

			if (PanelApp.this.classSelectionnee != null)
			{
				// Déplacement d'une classe
				// On modifie ses coordonnées logiques
				int nvCoordX = PanelApp.this.initCordX + deltaX;
				int nvCoordY = PanelApp.this.initCordY + deltaY;

				PanelApp.this.classSelectionnee.setCoordX(nvCoordX);
				PanelApp.this.classSelectionnee.setCoordY(nvCoordY);

				// On applique le déplacement visuel
				miseAJourVue();
				miseAJourLiens();
			}
			else
			{
				PanelApp.this.coordDeplX = PanelApp.this.initCordDeplX + deltaX;
				PanelApp.this.coordDeplY = PanelApp.this.initCordDeplY + deltaY;

				miseAJourVue();
			}
			repaint();
		}

		// Methode quand le clique est relâché
		public void mouseReleased(MouseEvent e)
		{
			PanelApp.this.classSelectionnee = null;
			setCursor(Cursor.getDefaultCursor());
		}

		public void mouseMoved(MouseEvent e)
		{
			Component[] comps = getComponents();
			boolean surClasse = false;
			for (Component c : comps)
			{
				// Affichage de l'info bulle
				if (c instanceof ClassGraphique && c.getBounds().contains(e.getPoint())) 
				{
					ClassGraphique cg = (ClassGraphique) c;
					int indCl = cg.getIndClasse();
					String nomClasse = PanelApp.this.ctrl.getClasseInfoById(indCl).getNomClasse();
					String msg = "<html><b>" + nomClasse + "</b></html>";
					PanelApp.this.setToolTipText(msg);
					surClasse = true;
					break;
				}
			}

			if (!surClasse)
			{
				PanelApp.this.setToolTipText(null);
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	// Ajoute des liens
	public void ajoutLien(DiagrammeLien lien)
	{
		for (DiagrammeLien existing : this.lstLiens)
		{
			if (existing.getSource() == lien.getSource() && existing.getCible() == lien.getCible())
				return;
		}

		this.lstLiens.add(lien);
		repaint();
	}

	// Mise à jour des liens
	public void miseAJourLiens()
	{
		for (DiagrammeLien lien : this.lstLiens)
			lien.recalculerAncre();

		repaint();
	}

	// Mise à jour des Positions
	public void miseAJourPos() 
	{
		for (Component comp : this.getComponents()) 
		{
			if (comp instanceof ClassGraphique) 
			{
				ClassGraphique cg = (ClassGraphique) comp;
				cg.setLocation(cg.getCoordX(), cg.getCoordY());
			}
		}

		this.revalidate();
		this.repaint();
	}

	// Modifie une multiplicite
	private void editerMultiplicite(DiagrammeLien lien, boolean isSource) 
	{
		int indClasseSource = lien.getCible().getIndClasse();
		int indClasseCible  = lien.getSource().getIndClasse();
		String nomSource    = this.ctrl.getClasseInfoById(indClasseSource).getNomClasse();
		String nomCible     = this.ctrl.getClasseInfoById(indClasseCible).getNomClasse();

		String ancienneMult = isSource ? lien.getMultipliciteSource() : lien.getMultipliciteCible();

		String titre = isSource
				? "Multiplicité de " + nomSource + " vers " + nomCible
				: "Multiplicité de " + nomCible + " vers " + nomSource;

		// Analyse la multiplicité actuelle pour pré-remplir les champs
		String min = "0";
		String max = "*";

		if (ancienneMult != null && !ancienneMult.isEmpty())
		{
			if (ancienneMult.contains(".."))
			{
				String[] parts = ancienneMult.split("\\.\\.");
				min = parts[0].trim();
				max = parts.length > 1 ? parts[1].trim() : "*";
			}
			else if (ancienneMult.equals("*"))
			{
				min = "0";
				max = "*";
			}
			else
			{
				min = ancienneMult.trim();
				max = ancienneMult.trim();
			}
		}

		// Crée un panel avec 2 champs de saisie
		JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
		panel.add(new JLabel("Min :"));
		JTextField champMin = new JTextField(min, 10);
		panel.add(champMin);

		panel.add(new JLabel("Max :"));
		JTextField champMax = new JTextField(max, 10);
		panel.add(champMax);

		panel.add(new JLabel(""));
		panel.add(new JLabel("(utilisez * pour illimité)"));

		int result = JOptionPane.showConfirmDialog( this, panel, titre, JOptionPane.OK_CANCEL_OPTION,
		                                                                JOptionPane.PLAIN_MESSAGE    );

		if (result == JOptionPane.OK_OPTION)
		{
			String minVal = champMin.getText().trim();
			String maxVal = champMax.getText().trim();

			// la nouvelle multiplicité
			String nouvelleMult;
			nouvelleMult = minVal + ".." + maxVal;

			// Mise à jour de la multiplicité dans le lien
			if (isSource)
				lien.setMultipliciteSource(nouvelleMult);
			else
				lien.setMultipliciteCible(nouvelleMult);

			// Mise à jour des associations dans le Controleur
			ctrl.mettreAJourAssociation(nomSource, nomCible,
					lien.getMultipliciteSource(), lien.getMultipliciteCible());

			// Rafraîchie l'affichage
			miseAJourVue();
			miseAJourLiens();
			repaint();
		}
	}

	public void supprimerLiens()
	{
		this.lstLiens.clear();
		repaint();
	}

	public ArrayList<DiagrammeLien> getLiens()      { return this.lstLiens;   }
	public int                      getCoordDeplX() { return this.coordDeplX; }
	public int                      getCoordDeplY() { return this.coordDeplY; }
	
	public void setCoordDeplX(int x) { this.coordDeplX = x; }
	public void setCoordDeplY(int y) { this.coordDeplY = y; }
}