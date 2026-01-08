/**
 * Vérifie si il y a des Associations entre deux classes
 * @author Equipe 8
 */

package retroconception.metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyserAssociation
{
	/**
	 * Cette classe permet de vérifier si un type est une conarutollection
	 * 
	 * @param type Le type de l'attribut à tester
	 * @return On renvoie si true si il appartient à une
	 *         Collection et false dans le cas contraire
	 */
	private static boolean estTypeCollection(String type)
	{
		if (type == null) { return false; }
		return type.endsWith("[]") || type.matches( ".*(List|Set|Map|Collection|ArrayList|LinkedList|HashSet|TreeSet|HashMap|TreeMap|Iterable)(<.*>)?$");
	}

	/**
	 * Cette méthode va lire toute les classes de la list en paramètre
	 * et nous dire quelle association existe
	 * 
	 * @param classes La list de toutes les objets ClasseInfo
	 * @return Nous renvoie un ArrayList de chaîne de caractères contenant les
	 *         associations
	 */
	public static ArrayList<String> lireAssociations(List<ClasseInfo> classes)
	{
		// Crée un ensemble des noms de classes
		Set<String>             classNames = new HashSet<>();
		Map<String, ClasseInfo> mapParName = new HashMap<>();

		ArrayList<String>       lstSt      = new ArrayList<>();

		for (ClasseInfo c : classes)
		{
			String nm = c.getNomClasse();
			classNames.add(nm   );
			mapParName.put(nm, c);
		}

		// Map pour stocker les associations et leurs multiplicités
		Map<String, Map<String, String>> multMap = new HashMap<>();

		for (ClasseInfo a : classes)
		{
			String classA = a.getNomClasse();

			for (Attribut att : a.getAttributs())
			{
				String type = att.getType();
				if (type == null) { continue; }

				type = type.trim();
				boolean estCollection = estTypeCollection(type);

				String reference      = null;

				if (type.endsWith("[]"))
				{
					reference = type.substring(0, type.length() - 2).trim(); // Retirer "[]"
				}

				if (type.contains("<") && type.contains(">"))
				{
					String interieur = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>')).trim(); // Retirer <>

					if (interieur.contains(",")) { interieur = interieur.substring(0, interieur.indexOf(',')); } // Premier type pour Map<K,V>
					
					reference = interieur.trim();
				}

				if (!estCollection) { reference = type; } // Type simple

				if (reference == null) { continue; }

				if (reference.contains(".")) { reference = reference.substring(reference.lastIndexOf('.') + 1); } // Retire le package

				if (reference.contains("<")) { reference = reference.substring(0, reference.indexOf('<')).trim(); } // Retire les génériques

				// Vérifie que c'est bien une classe du projet (pas un type primitif ou String)
				if (!classNames.contains(reference)) { continue; }

				att.setTypeClasse(false);

				String multTo = estCollection ? "0..*" : "1..1";
				multMap.computeIfAbsent(classA, k -> new HashMap<>()).put(reference, multTo);
			}
		}

		Set<String> handled = new HashSet<>();
		int id = 1;

		// Itère sur les paires dans multMap
		for (String a : multMap.keySet()) // Classe source
		{
			for (String b : multMap.get(a).keySet()) // Classe cible
			{
				String pairKey = a + "#" + b;
				String revKey  = b + "#" + a;
				
				if (handled.contains(pairKey)) { continue; }

				boolean estAtoB = multMap.containsKey(a) && multMap.get(a).containsKey(b);
				boolean estBtoA = multMap.containsKey(b) && multMap.get(b).containsKey(a);

				String mAtoB = estAtoB ? multMap.get(a).get(b) : null;
				String mBtoA = estBtoA ? multMap.get(b).get(a) : null;

				if (!estBtoA) 
				{
					// Unidirectionnelle: a -> b uniquement
					// Format: "Association X : unidirectionnelle de ClasseA() vers
					// ClasseB(multiplicité)"
					// La multiplicité source est vide pour les associations unidirectionnelles
					lstSt.add("Association " + id + " : unidirectionnelle de " + a +
							"() vers " + b + "(" + mAtoB + ")");
					id++;
					handled.add(pairKey);
					continue;
				}

				// Bidirectionnelle: a <-> b
				lstSt.add("Association " + id + " : bidirectionnelle de " + a + "(" + mAtoB + ") vers " + b + "(" + mBtoA + ")");

				id++;

				handled.add(pairKey);
				handled.add(revKey);
			}
		}

		return lstSt;
	}
}