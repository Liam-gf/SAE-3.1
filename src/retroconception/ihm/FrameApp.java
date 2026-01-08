/**
 * Cette classe représente la Frame du panel app
 * @author Equipe 8
 */

package retroconception.ihm;

import java.awt.BorderLayout;
import java.awt.Graphics2D;

import java.awt.event.*;
import javax.swing.*;

import retroconception.Controleur;

import java.awt.image.BufferedImage;
import java.io.File;

public class FrameApp extends JFrame implements ActionListener
{
	// Déclaration des variables
	private Controleur ctrl;
	private PanelApp   panelApp;
	private JMenuItem  menuInsererDossier;
	private JMenuItem  menuSauvegarder;
	private JMenuItem  menuCharger;
	private JMenuItem  menueSupprimer;
	private JMenuItem  menuCaptureEcran;


	public FrameApp(Controleur ctrl)
	{
		this.ctrl = ctrl;
		
		this.setTitle              ( "Outil de Rétroconception - SAE" );
		this.setSize               (             1200, 800            );
		this.setLocationRelativeTo (               null               );
		this.setLayout             (       new BorderLayout()         );

		/*-------------------------*/
		/* Création des composants */
		/*-------------------------*/

		JMenuBar menuBar        = new JMenuBar();
		JMenu menuFichier       = new JMenu   ("Fichier");

		this.menuInsererDossier = new JMenuItem("Importer un dossier Java...");
		this.menuSauvegarder    = new JMenuItem("Sauvegarder Diagramme..."   );
		this.menuCharger        = new JMenuItem("Ouvrir Diagramme..."        );

		/*-------------------------------*/
		/* Positionnement des composants */
		/*-------------------------------*/
		
		menuFichier.add         (menuInsererDossier);
		menuFichier.addSeparator();
		menuFichier.add         (menuSauvegarder);
		menuFichier.add         (menuCharger);

		JMenu menuOutils      = new JMenu("Outils");

		this.menueSupprimer   = new JMenuItem("Tout effacer"   );
		this.menuCaptureEcran = new JMenuItem("Capture d'écran");
		
		menuOutils.add(menueSupprimer  );
		menuOutils.add(menuCaptureEcran);

		menuBar.add(menuFichier);
		menuBar.add(menuOutils );

		this.setJMenuBar(menuBar);

		this.panelApp = new PanelApp(ctrl, this);
		this.add(panelApp, BorderLayout.CENTER);

		/*-------------------------------*/
		/* Activation des composants     */
		/*-------------------------------*/

		this.menuInsererDossier.addActionListener(this);
		this.menuSauvegarder   .addActionListener(this);
		this.menuCharger       .addActionListener(this);
		this.menueSupprimer    .addActionListener(this);
		this.menuCaptureEcran  .addActionListener(this);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	// Méthode pour interagir avec le panel 
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.menuInsererDossier)
		{
			String dossier = selectionnerDossier(); // Permet d'importer un dossier .Java

			if (dossier != null) { this.ctrl.importerDossier(dossier); }
		}

		if (e.getSource() == this.menuSauvegarder) { this.ctrl.sauvegarderDiagramme(); } // Pour la sauvegarde d'un diagramme en .ser
		if (e.getSource() == this.menuCharger    ) { this.ctrl.chargerDiagramme    (); } // Pour charger un diagramme en .ser

		if (e.getSource() == this.menueSupprimer ) // Pour la supression d'un diagramme
		{
			this.panelApp.removeAll ();
			this.panelApp.supprimerLiens();
			this.panelApp.repaint   ();
		}

		if (e.getSource() == this.menuCaptureEcran) { capturerImage(); } // Pour effectuer une capture d'écran en png
	}

	// Envoie le nom du dossier selectionné
	private String selectionnerDossier()
	{
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY           );
		fileChooser.setDialogTitle      ("Sélectionner le Dossier du Projet Java");

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { return fileChooser.getSelectedFile().getAbsolutePath(); } 

		return null; 
	}

	// Appelle la methode pour capturer l'image
	private void capturerImage()
	{
		BufferedImage image = new BufferedImage(panelApp.getWidth(), panelApp.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		panelApp.paint(g2);
		g2.dispose();

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choisir le dossier de sauvegarde de la capture");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = chooser.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File dossier = chooser.getSelectedFile();

			this.ctrl.capturerEcran(image, dossier);
		}
	}

	public PanelApp getPanel() {return this.panelApp;}
}