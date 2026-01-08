/**
 * Cette classe permet d'avoir un affichage d'un objet Methode et d'u objet Attribut
 * @author Equipe 8
 */

package retroconception.ihm;

import java.util.List;
import retroconception.Controleur;

public class Affiche
{
	// Renvoie un String pour avoir une représentation de l'attribut.
	public String afficherAtt(Controleur ctrl, int indCl, int indAtt)
	{
		String  nom        = ctrl.getAlCl().get(indCl).getAttributs().get(indAtt).getNom();
		String  type       = ctrl.getAlCl().get(indCl).getAttributs().get(indAtt).getType();
		String  visibilite = ctrl.getAlCl().get(indCl).getAttributs().get(indAtt).getVisibilite();
		String  portee     = ctrl.getAlCl().get(indCl).getAttributs().get(indAtt).getPortee();
		boolean typeClasse = ctrl.getAlCl().get(indCl).getAttributs().get(indAtt).getTypeClasse();

		String sRet = "";

		if (!typeClasse)
			return "";

		switch (visibilite)
		{
			case "public"    -> sRet += "+ ";
			case "private"   -> sRet += "- ";
			case "protected" -> sRet += "# ";
		}

		sRet += nom;
		boolean estFinal = false;

		if (type.startsWith("final "))
		{
			type     = type.replace("final ", "").trim();
			estFinal = true;
		}
		
		sRet += " : " + type;

		if (portee != null && (portee.equals("classe")))
			sRet += " {static}";

		if (estFinal)
			sRet += " {final}";

		return sRet;
	}

	/*
		Permet de renvoyer un String contenant les information de la méthode.
		Le paramètre limite permet de savoir si on doit réduire
		l'affichage des paramètre à 3 max.
	*/
	public String afficherMeth(Controleur ctrl, int indCl, int indMeth, boolean limite)
	{
		List<String> paramNom   = ctrl.getAlCl().get(indCl).getMethodes().get(indMeth).getParamNom();
		List<String> paramType  = ctrl.getAlCl().get(indCl).getMethodes().get(indMeth).getParamType();
		String       visibilite = ctrl.getAlCl().get(indCl).getMethodes().get(indMeth).getVisibilite();
		String       nomMethode = ctrl.getAlCl().get(indCl).getMethodes().get(indMeth).getNomMethode();
		String       typeRetour = ctrl.getAlCl().get(indCl).getMethodes().get(indMeth).getTypeRetour();
		String       typeMeth   = ctrl.getAlCl().get(indCl).getMethodes().get(indMeth).getTypeMethode();

		String sRet  = "";

		// 1. Visibilité
		switch (visibilite)
		{
			case "public"    -> sRet += "+ ";
			case "private"   -> sRet += "- ";
			case "protected" -> sRet += "# ";
			default          -> sRet += "~ ";
		}

		// 2. Sécurisation et nettoyage de typeMethode (pour isoler les modificateurs)
		int indEsp = typeMeth.indexOf(" ");

		String typrMethNett;

		if (indEsp != -1 && indEsp < typeMeth.length())
			typrMethNett = typeMeth.substring(indEsp + 1).trim(); 
		else
			typrMethNett = typeMeth.trim();
		

		// 3. Nom de la Méthode et Paramètres
		sRet += nomMethode + "(";

		for (int cpt=0; cpt < paramNom.size(); cpt++)
		{
			sRet += paramType.get(cpt) + " " + paramNom.get(cpt);

			if ( cpt != paramNom.size() -1 )
				sRet += ", ";

			if(cpt >= 2 && limite)
			{
				sRet += "... ";
				break;
			}
		}

		sRet += ")";
		boolean estConstructeur = typeRetour.equals("");
		boolean estVoid         = typeRetour != null && typeRetour.equals("void");

		// 4. Type de Retour (Format UML : nom(params) : TypeDeRetour)
		if (!estConstructeur && !estVoid)
			sRet += " : " + typeRetour;

		// 5. Gestion des Modificateurs de Portée (pour le soulignement/italique dans ClassGraphique)
		String modificateurs = "";
		
		if (typrMethNett.contains("static"))
			modificateurs += " {static}";

		if (typrMethNett.contains("abstract"))
			modificateurs += " {abstract}";

		if (typrMethNett.contains("final"))
			modificateurs += " {final}";

		sRet += modificateurs;

		return sRet;
	}
}
