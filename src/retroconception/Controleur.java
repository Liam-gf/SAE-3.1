/**
 *
 * @author Equipe 8
 */

package retroconception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import retroconception.ihm.ClassGraphique;
import retroconception.ihm.DiagrammeLien;
import retroconception.ihm.FrameApp;

import retroconception.metier.AnalyserAssociation;
import retroconception.metier.ClasseInfo;
import retroconception.metier.DiagrammeSauvegarde;
import retroconception.metier.LireFichier;
import retroconception.metier.Attribut;
import retroconception.metier.Methode;
import retroconception.metier.CapturerImage;

/**
 * Classe Controleur : Gère la logique métier et les interactions entre la vue
 * et les données
 * Elle met en place l'importation des fichiers Java, la création des diagrammes
 * UML,
 * et la gestion de la sauvegarde et du chargement
 * 
 * @author Equipe 8
 */
public class Controleur implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private FrameApp frameApp;

	/*--- Données Métier --- */
	private ArrayList<ClasseInfo> alCl;
	private ArrayList<String>     alAsso;
	private ArrayList<String>     alRela;

	private transient HashMap<String, ClassGraphique> mapGraphiques;

	/**
	 * Constructeur du Controleur
	 * Initialise les collections et crée la fenêtre principale
	 */
	public Controleur() 
	{
		this.alCl          = new ArrayList<>();
		this.alAsso        = new ArrayList<>();
		this.alRela        = new ArrayList<>();
		this.mapGraphiques = new HashMap<>();

		this.frameApp      = new FrameApp(this);
	}

	public ArrayList<ClasseInfo> getAlCl  () { return this.alCl;   }
	public ArrayList<String>     getAlAsso() { return this.alAsso; }
	public ArrayList<String>     getAlRela() { return this.alRela; }
	/**
	 * Récupère une classe par son identifiant
	 * 
	 * @param id L'index de la classe dans la liste
	 * @return L'objet ClasseInfo ou null si l'index n'est pas valide
	 */
	public ClasseInfo getClasseInfoById(int id) 
	{
		if (id >= 0 && id < this.alCl.size()) { return this.alCl.get(id); }
		
		return null;
	}

	/**
	 * Récupère un attribut d'une classe
	 * 
	 * @param indCl  L'index de la classe
	 * @param indAtt L'index de l'attribut
	 * @return L'objet Attribut demandé
	 */
	public Attribut getAttribut(int indCl, int indAtt) { return this.alCl.get(indCl).getAttributs().get(indAtt); }

	/**
	 * Récupère une méthode d'une classe
	 * 
	 * @param indCl   L'index de la classe
	 * @param indMeth L'index de la méthode
	 * @return L'objet Methode demandé
	 */
	public Methode getMethode(int indCl, int indMeth) { return this.alCl.get(indCl).getMethodes().get(indMeth); }

	/* --- SETTERS --- */
	public void setAlCl  (ArrayList<ClasseInfo> alCl)   { this.alCl = alCl;     }
	public void setAlAsso(ArrayList<String>     alAsso) { this.alAsso = alAsso; }
	public void setAlRela(ArrayList<String>     alRela) { this.alRela = alRela; }

	/**
	 * Importe un dossier contenant des fichiers Java
	 * Parse les fichiers, extrait les classes, attributs et méthodes,
	 * détecte les associations et les relations d'héritage,
	 * puis affiche le diagramme UML.
	 * 
	 * @param chemin Le chemin absolu du dossier contenant les fichiers .java
	 */

	/* --- MÉTIER --- */
	public void importerDossier(String chemin) 
	{
		File repertoire = new File(chemin);

		// Récupère tous les fichiers .java du dossier
		File[] fichiersJava = repertoire.listFiles((dir, name) -> name.toLowerCase().endsWith(".java"));

		if (fichiersJava == null || fichiersJava.length == 0) 
		{
			JOptionPane.showMessageDialog(frameApp, "Aucun fichier Java trouvé dans ce dossier.");
			return;
		}

		// Réinitialise les données pour une nouvelle importation
		this.alCl         .clear();
		this.alRela       .clear();
		this.alAsso       .clear();
		this.mapGraphiques.clear();

		this.frameApp.getPanel().removeAll();
		this.frameApp.getPanel().supprimerLiens();

		// Parcourt chaque fichier Java et crée les objets ClasseInfo qui correspondent
		for (File fichier : fichiersJava)
		{
			ClasseInfo classInfo = new ClasseInfo(fichier.getAbsolutePath());
			this.alCl.add(classInfo);

			LireFichier lF  = new LireFichier(fichier.getAbsolutePath());
			String relation = lF.LireRelation();

			if (relation != null && !relation.isEmpty()) { this.alRela.add(relation); }
		}

		this.alAsso = AnalyserAssociation.lireAssociations(this.alCl);

		// Positionne les classes graphiquement sur le diagramme
		int x_pos      = 10;
		int y_pos      = 10;
		int maxHauteur = 0;

		for (int ind = 0; ind < this.alCl.size(); ind++) 
		{
			String nom = this.alCl.get(ind).getNomClasse();

			ClassGraphique cg = new ClassGraphique(this, ind, x_pos, y_pos, nom);

			this.mapGraphiques.put(nom, cg);
			this.frameApp.getPanel().add(cg);

			x_pos += cg.getLargeur() + 50;
			maxHauteur = Math.max(maxHauteur, cg.getHauteur());

			// Retour à la ligne si dépassement de la largeur
			if (x_pos > 1000) 
			{
				x_pos = 10;
				y_pos += maxHauteur + 50;
				maxHauteur = 0;
			}

		}

		// Crée les liens entre les classes associations, héritages et interfaces
		this.creerLiensAssociations();
		this.creerLiensHeritages   ();

		this.frameApp.getPanel().miseAJourPos();
	}

	/**
	 * Crée les liens d'association entre les classes
	 * Parse les informations d'association et crée des objets DiagramLien
	 */
	private void creerLiensAssociations() 
	{
		for (String assoc : this.alAsso) 
		{
			try
			{
				boolean bidir   = assoc.contains("bidirectionnelle");
				int     indDe   = assoc.indexOf (" de ");
				int     indVers = assoc.indexOf (" vers ");

				if (indDe == -1 || indVers == -1) { continue; }

				String gauche = assoc.substring(indDe + 4, indVers).trim();
				String droite = assoc.substring(indVers + 6).trim();

				// Extrait le nom et la multiplicité de chaque côté
				String nomSource  = extractNom(gauche);
				String multSource = extractMult(gauche);
				String nomCible   = extractNom(droite);
				String multCible  = extractMult(droite);

				ClassGraphique cgSource = mapGraphiques.get(nomSource);
				ClassGraphique cgCible  = mapGraphiques.get(nomCible);

				// Crée le lien entre les deux classes si elles existent
				if (cgSource != null && cgCible != null && cgSource != cgCible) 
				{
					// Pour les unidirectionnelles, ajouter multiplicité par défaut 0..*
					if (!bidir) { multSource = "0..*"; }

					DiagrammeLien lien = new DiagrammeLien(cgSource, cgCible);
					lien.setMultipliciteSource(multSource);
					lien.setMultipliciteCible(multCible);
					lien.setBidirectional(bidir);

					this.frameApp.getPanel().ajoutLien(lien);
				}
			}
			catch (Exception e) 
			{
				System.err.println("Erreur parsing association : " + assoc);
				System.out.println("Erreur dans la méthode creerLiensAssociations de la classe Controleur");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Crée les liens d'héritage et d'implémentation d'interface
	 * Analyse les relations trouvées et crée les liens correspondants
	 */
	private void creerLiensHeritages() 
	{
		for (String relation : this.alRela)
		{
			if (relation == null || relation.isEmpty()) { continue; }
			try
			{
				String[] parts = relation.split(" ");

				if (parts.length < 3) { continue; }

				String nomClasse    = parts[0];
				String typeRelation = parts[1];
				String nomParent    = parts[2];

				ClassGraphique cgSource = mapGraphiques.get(nomClasse);
				ClassGraphique cgCible  = mapGraphiques.get(nomParent);

				// Détermine le type de relation (héritage ou interface)
				if (cgSource != null && cgCible != null)
				{
					DiagrammeLien.TypeLien type = DiagrammeLien.TypeLien.ASSOCIATION;

					if      (typeRelation.equals("hérite"    )) { type = DiagrammeLien.TypeLien.INHERITANCE;    }
					else if (typeRelation.equals("implémente")) { type = DiagrammeLien.TypeLien.INTERFACE_IMPL; } 

					DiagrammeLien lien = new DiagrammeLien(cgSource, cgCible, type);
					this.frameApp.getPanel().ajoutLien(lien);
				}
			}
			catch (Exception e)
			{
				System.err.println("Erreur parsing relation : " + relation);
				System.out.println("Erreur dans la méthode creerLiensHeritages de la classe Controleur");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Extrait le nom d'une classe d'une chaîne contenant des parenthèses
	 * Par exemple : "Classe(0..1)" -> "Classe"
	 * 
	 * @param s La chaîne contenant le nom et la multiplicité
	 * @return Le nom extrait
	 */
	private String extractNom(String s) 
	{
		int p = s.indexOf('(');
		return (p == -1) ? s : s.substring(0, p).trim();
	}

	/**
	 * Extrait la multiplicité d'une chaîne
	 * Par Exemple : "Classe(0..1)" -> "0..1"
	 * 
	 * @param s La chaîne contenant le nom et la multiplicité
	 * @return La multiplicité extraite, ou "0..*" par défaut
	 */
	private String extractMult(String s) 
	{
		int p1 = s.indexOf('(');
		int p2 = s.indexOf(')');
		return (p1 != -1 && p2 > p1) ? s.substring(p1 + 1, p2).trim() : "0..*";
	}

	/**
	 * Sauvegarde le diagramme UML dans un fichier sérialisé
	 * Ouvre un dialogue de sauvegarde et persiste les données du diagramme
	 */
	public void sauvegarderDiagramme()
	{
		JFileChooser fc = new JFileChooser("./sauvegarde");
		if (fc.showSaveDialog(this.frameApp) == JFileChooser.APPROVE_OPTION)
		{
			File fichier = fc.getSelectedFile();
			if (!fichier.getName().toLowerCase().endsWith(".ser"))
				fichier = new File(fichier.getAbsolutePath() + ".ser");

			try (ObjectOutputStream obOutSt = new ObjectOutputStream(new FileOutputStream(fichier)))
			{
				// Crée un objet DiagrammeSauvegarde contenant toutes les données
				DiagrammeSauvegarde sauv = new DiagrammeSauvegarde();
				sauv.classes             = this.alCl;
				sauv.associations        = this.alAsso;
				sauv.relations           = this.alRela;
				sauv.offsetX             = this.frameApp.getPanel().getCoordDeplX();
                sauv.offsetY             = this.frameApp.getPanel().getCoordDeplY();

				// Enregistre les positions des classes graphiques
				sauv.positions = new HashMap<>();
				for (String nom : mapGraphiques.keySet()) 
				{
					ClassGraphique cg = mapGraphiques.get(nom);
					sauv.positions.put(nom, new Point(cg.getCoordX(), cg.getCoordY()));
				}

				obOutSt.writeObject(sauv);
				System.out.println("Diagramme sauvegardé avec succès.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(frameApp, "Erreur lors de la sauvegarde.");
				System.out.println("Erreur dans la méthode sauvegarderDiagramme de la classe Controleur");
			}
		}
	}

	/**
	 * Charge un diagramme UML à partir d'un fichier sérialisé
	 * Restaure les classes, associations et positions des éléments
	 */
	public void chargerDiagramme() 
	{
		JFileChooser fc = new JFileChooser("./sauvegarde");
		if (fc.showOpenDialog(this.frameApp) == JFileChooser.APPROVE_OPTION) 
		{
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) 
			{
				// Désérialise l'objet DiagrammeSauvegarde
				DiagrammeSauvegarde sauv = (DiagrammeSauvegarde) ois.readObject();

				this.alCl   = sauv.classes;
				this.alAsso = sauv.associations;
				this.alRela = sauv.relations;

				// Réinitialise l'affichage
				this.mapGraphiques.clear();
				
				this.frameApp.getPanel().removeAll();
				this.frameApp.getPanel().supprimerLiens();

				// Recrée les composants graphiques avec les positions sauvegardées
				for (int i = 0; i < this.alCl.size(); i++) 
				{
					ClasseInfo info = this.alCl.get(i);
					String     nom  = info.getNomClasse();

					Point pos = sauv.positions.get(nom);
					int x     = (pos != null) ? pos.x : 10;
					int y     = (pos != null) ? pos.y : 10;

					ClassGraphique cg = new ClassGraphique(this, i, x, y, nom);
					
					this.mapGraphiques.put(nom, cg);
					this.frameApp.getPanel().add(cg);
				}

				this.creerLiensAssociations();
				this.creerLiensHeritages   ();
				
				this.frameApp.getPanel().setCoordDeplX((int) sauv.getOffsetX());
                this.frameApp.getPanel().setCoordDeplY((int) sauv.getOffsetY());
				this.frameApp.getPanel().miseAJourVue();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(frameApp, "Erreur lors du chargement ou format de fichier incompatible.");
				System.out.println("Erreur dans la méthode chargerDiagramme de la classe Controleur");
			}
		}
	}

	/**
	 * Met à jour les informations d'association entre deux classes
	 * Modifie la multiplicité et redessine le diagramme
	 * 
	 * @param nomSource  Nom de la classe source
	 * @param nomCible   Nom de la classe cible
	 * @param multSource Multiplicité du côté source
	 * @param multCible  Multiplicité du côté cible
	 */
	public void mettreAJourAssociation(String nomSource, String nomCible, String multSource, String multCible) 
	{
		for (int i = 0; i < alAsso.size(); i++)
		{
			String assoc = alAsso.get(i);

			// Cherche l'association correspondante et la met à jour
			if (assoc.contains(" de " + nomSource) && assoc.contains(" vers " + nomCible)) 
			{
				boolean bidir = assoc.contains("bidirectionnelle");

				String nouvelleAssoc = "Association ";

				if (bidir) { nouvelleAssoc += "bidirectionnelle "; }
				
				nouvelleAssoc += "de " + nomSource + "(" + multSource + ") vers " + nomCible + "(" + multCible + ")";

				alAsso.set(i, nouvelleAssoc);
				break;
			}
		}

		// Met à jour les attributs des classes
		mettreAJourAttributsClasse(nomSource, nomCible, multCible);

		// Redessine les classes concernées
		ClassGraphique cgSource = mapGraphiques.get(nomSource);
		ClassGraphique cgCible  = mapGraphiques.get(nomCible);

		if (cgSource != null) 
		{
			cgSource.afficher(nomSource, false);
			frameApp.getPanel().miseAJourVue();
		}

		if (cgCible != null) 
		{
			cgCible.afficher(nomCible, false);
			frameApp.getPanel().miseAJourVue();
		}
	}

	/**
	 * Met à jour les attributs d'une classe en fonction de la multiplicité d'une
	 * association
	 * Par exemple, si la multiplicité indique plusieurs instances, transforme
	 * l'attribut en ArrayList
	 * 
	 * @param nomSource Nom de la classe source
	 * @param nomCible  Nom de la classe cible
	 * @param multCible Multiplicité du côté cible
	 */
	private void mettreAJourAttributsClasse(String nomSource, String nomCible, String multCible) 
	{
		// Trouve la classe source
		ClasseInfo classeSource = null;
		for (ClasseInfo cl : alCl)
		{
			if (cl.getNomClasse().equals(nomSource)) 
			{
				classeSource = cl;
				break;
			}
		}

		if (classeSource == null)
			return;

		// Cherche l'attribut de type nomCible et le met à jour selon la multiplicité
		for (Attribut att : classeSource.getAttributs()) 
		{
			String typeAtt = att.getType();

			// Vérifie si c'est un attribut collection (ArrayList, List, ...)
			if (typeAtt.contains(nomCible)) 
			{
				// Mettre à jour le type selon la multiplicité
				String nouveauType = determinerTypeMult(nomCible, multCible);
				att.setType(nouveauType);
				break;
			}
		}
	}

	/**
	 * Détermine le type d'un attribut en fonction de la multiplicité.
	 * Retourne le type simple pour une multiplicité de 1,
	 * et ArrayList<> pour une multiplicité > 1 ou contenant "*"
	 * 
	 * @param nomClasse    Nom de la classe référencée
	 * @param multiplicite La multiplicité
	 * @return Le type déterminé
	 */
	private String determinerTypeMult(String nomClasse, String multiplicite) 
	{
		if (multiplicite == null || multiplicite.isEmpty())
			return nomClasse;

		// Si la multiplicité indique plusieurs instances
		if (multiplicite.contains("*") || multiplicite.contains("..")) 
		{
			// Extraire la borne supérieure
			String[] parts = multiplicite.split("\\.\\.");
			if (parts.length == 2 && !parts[1].equals("*")) 
			{
				try
				{
					int max = Integer.parseInt(parts[1].trim());
					if (max == 1)
						return nomClasse;
				}
				catch (NumberFormatException e)
				{
				}
			}

			// Multiplicité multiple -> Collection
			return "ArrayList<" + nomClasse + ">";
		}
		else
		{
			// Vérifier si c'est un nombre simple
			try
			{
				int val = Integer.parseInt(multiplicite.trim());
				if (val == 1)
					return nomClasse;
				else
					return "ArrayList<" + nomClasse + ">";
			} catch (NumberFormatException e) {
				return nomClasse;
			}
		}
	}

	/**
	 * Capture l'écran du diagramme et l'enregistre dans un fichier
	 * 
	 * @param image   L'image capturée du diagramme
	 * @param dossier Le dossier de destination
	 */
	public void capturerEcran(BufferedImage image, File dossier) 
	{
		CapturerImage capturerImage = new CapturerImage();
		capturerImage.capturerEcran(image, dossier);
	}

	/**
	 * Point d'entrée du programme.
	 * 
	 * @param args Les arguments de la ligne de commande
	 */
	public static void main(String[] args) 
	{
		new Controleur();
	}
}