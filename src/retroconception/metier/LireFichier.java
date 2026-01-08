/**
 * Cette classe permet de lire des Fichiers pour récupérer leurs méthodes, attributs
 * et vérifier si ils ont une interface ou un héritage
 * @author Equipe 8
 */

package retroconception.metier;

import java.nio.file.Paths;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LireFichier
{

	private String              chemin;
	private ArrayList<Attribut> alAttribut;
	private ArrayList<Methode>  alMethode;

	/**
	 * Construit un objet LireFichier qui va initialiser une liste de Methode
	 * et une autre d'Attribut.
	 * @param chemin Le chemin absolue vers le fichier qu'on va lire
	 */
	public LireFichier(String chemin)
	{
		this.chemin     = chemin;
		this.alAttribut = new ArrayList<Attribut>();
		this.alMethode  = new ArrayList<Methode>();
	}

	/**
	 * Cette classe permet de récupérer tous les attributs de la classe
	 * @return On retourne une ArrayList d'Attribut contenant tous les attributs de la classe
	 */
	public ArrayList<Attribut> LireAttribut()
	{
		Scanner scFic, scLigne;

		String visibilite, type, portee, nom = "";

		try
		{
			scFic = new Scanner(new FileInputStream(this.chemin), "UTF8");
			if(this.estInterface(scFic))
					return new ArrayList<>();

			while (scFic.hasNextLine())
			{
				String ligne = scFic.nextLine();

				scLigne = new Scanner(ligne);

				if (ligne.startsWith("\t") && !ligne.startsWith("\t\t") &&
				    ligne.endsWith(";")    && !ligne.contains("abstract"))
				{
					scLigne.useDelimiter("\\s+");
					
					visibilite = scLigne.next();

					String mot = scLigne.next();

					if (mot.equals("static"))
					{
						portee = "classe";
						mot = scLigne.next();
					}
					else
						portee = "instance";

					if (mot.equals("final"))
					{
						String tmp = scLigne.next();

						if(tmp.contains("HashMap"))
						{
							String typeComplet = tmp;
							while (!typeComplet.contains(">") && scLigne.hasNext()) 
								typeComplet += " " + scLigne.next();

							type = "final " + typeComplet;
						}
						else
							type = "final " + tmp;
					}
					else
					{
						String tmp = mot;
						if(tmp.contains("HashMap"))
						{
							String typeComplet = tmp;
							while (!typeComplet.contains(">") && scLigne.hasNext()) 
								typeComplet += " " + scLigne.next();

							type = typeComplet;
						}
						else
							type = mot;
					}

					if(scLigne.hasNext())
					{
						String tmp = scLigne.next();

						if(tmp.contains("="))
							nom = tmp.substring(0, tmp.indexOf("="));
						else if(tmp.contains(";"))
							nom = tmp.substring(0, tmp.indexOf(";"));
						else
							nom = tmp;
					}

					nom = nom.trim();

					this.alAttribut.add(new Attribut(this.alAttribut.size(), nom, type, visibilite, portee));
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Erreur dans la méthode LireAttribut de la classe LireFichier");
			e.printStackTrace();
		}

		return this.alAttribut;
	}

	/**
	 * Cette classe permet de récupérer toutes les méthodes de la classe
	 * @return On retourne une ArrayList d'Attribut contenant toutes les methodes de la classe
	 */
	public ArrayList<Methode> LireMethode()
	{
		Scanner scFic, scLigne;

		try
		{
			scFic = new Scanner(new FileInputStream(this.chemin), "UTF8");

			while (scFic.hasNextLine())
			{
				String ligne = scFic.nextLine();

				scLigne      = new Scanner(ligne);

				if (ligne.startsWith("\t") && !ligne.startsWith("\t\t") &&
				    ligne.contains(")")    && !ligne.contains("new"))
				{
					List<String> lstNom  = new ArrayList<String>();
					List<String> lstType = new ArrayList<String>();

					String nom, visibilite, typeDeRetour;
					String typeMethode = "instance";

					if (lstNom.size() != 0)
					{
						lstNom .clear();
						lstType.clear();
					}

					scLigne.useDelimiter("\\s+");

					int indDebutParam;
					int indFinParam;

					visibilite = scLigne.next();

					int indParen = ligne.indexOf("(");
					String avant = ligne.substring(0, indParen).trim();
					
					int indVisibilite = avant.indexOf(visibilite);
					String apresVis = avant.substring(indVisibilite + visibilite.length()).trim();
					
					int indDerEsp = apresVis.lastIndexOf(" ");
					
					if (indDerEsp != -1)
					{
						String potTypeRet = apresVis.substring(0, indDerEsp).trim();
						nom = apresVis.substring(indDerEsp + 1).trim();
						
						if (potTypeRet.equals("static") || potTypeRet.equals("final") || potTypeRet.equals("abstract"))
						{
							typeMethode = potTypeRet;

							String sansMod       = apresVis.substring(potTypeRet.length()).trim();
							int derEspSansMod = sansMod.lastIndexOf(" ");
							if (derEspSansMod != -1)
							{
								typeDeRetour = sansMod.substring(0, derEspSansMod).trim();
								nom = sansMod.substring(derEspSansMod + 1).trim();
							}
							else
							{
								typeDeRetour = sansMod;
								nom = "";
							}
						}
						else
						{
							if (potTypeRet.contains("static") && potTypeRet.contains("final"))
							{
								typeMethode = "static final";
								String apresStaticFinal = potTypeRet.replace("static", "").replace("final", "").trim();
								typeDeRetour = apresStaticFinal;
							}
							else
								typeDeRetour = potTypeRet;
						}
					}
					else
					{
						nom = apresVis;
						typeDeRetour = "";
					}
					
					indDebutParam = ligne.indexOf("(");
					indFinParam = ligne.indexOf(")");

					String ensembleParam = ligne.substring(indDebutParam + 1, indFinParam);

					if (indFinParam - indDebutParam != 1)
					{
						Scanner scParam = new Scanner(ensembleParam);
						scParam.useDelimiter(",");

						while (scParam.hasNext())
						{
							String paramComp = scParam.next().trim();
							int indEsp = paramComp.indexOf(" ");

							if(indEsp != -1)
							{
								String typeParam = paramComp.substring(0, indEsp);
								String nomParam  = paramComp.substring(indEsp + 1);

								lstType.add(typeParam);
								lstNom .add(nomParam);
							}
						}
					}
					this.alMethode.add(new Methode(nom, visibilite, typeDeRetour, lstNom, lstType, typeMethode));
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Erreur dans la méthode LireMethode de la classe LireFichier");
			e.printStackTrace();
		}

		return this.alMethode;
	}

	/**
	 * Cette classe permet de vérifier si une classe contient un héritage
	 * @return On retourne une ArrayList des héritages présents dans la classe
	 */
	public String LireRelation()
	{
		String relat = "";
		Scanner scFic, scLigne;

		try
		{
			scFic = new Scanner(new FileInputStream(this.chemin), "UTF8");

			while (scFic.hasNextLine())
			{
				String prec = "", actu = "";
				String nomClass = Paths.get(this.chemin).getFileName().toString().replace(".java", "");

				String lig = scFic.nextLine();

				scLigne = new Scanner(lig);
				scLigne.useDelimiter("\\s+");
				
				if(scLigne.hasNext())
					prec = scLigne.next();

				while (scLigne.hasNext())
				{
					actu = scLigne.next();

					if(prec.contains("extends"))
						relat = nomClass + " hérite " + actu;
					if (prec.contains("implements"))
						relat = nomClass + " implémente " + actu;

					prec = actu;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Erreur dans la méthode LireRelation de la classe LireFichier");
			e.printStackTrace();
		}

		return relat;
	}

	public boolean estInterface(Scanner scFic)
	{
		try
		{
			while (scFic.hasNextLine()) 
			{
				String lig = scFic.nextLine().trim();

				Scanner scLigne = new Scanner(lig);
				while (scLigne.hasNext())
				{
					String tmp = scLigne.next();

					if (tmp.equals("interface"))
					{
						scLigne.close();
						return true; 
					}
				}
				scLigne.close();

				if (lig.contains("class") || lig.contains("{"))
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Erreur dans la méthode estInterface de la classe LireFichier");
			e.printStackTrace();
		}

		return false;
	}
}