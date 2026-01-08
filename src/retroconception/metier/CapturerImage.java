/**
 * Cette classe permet de capturer une image
 * @author Equipe 8
 */

package retroconception.metier;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

public class CapturerImage
{
	public void capturerEcran(BufferedImage image, File dossier)
	{
		GregorianCalendar cal = new GregorianCalendar();

		String annee   = String.valueOf( cal.get(GregorianCalendar.YEAR)         );
		String mois    = String.valueOf( cal.get(GregorianCalendar.MONTH) + 1    );
		String jour    = String.valueOf( cal.get(GregorianCalendar.DAY_OF_MONTH) );
		String heure   = String.valueOf( cal.get(GregorianCalendar.HOUR_OF_DAY)  );
		String minute  = String.valueOf( cal.get(GregorianCalendar.MINUTE)       );
		String seconde = String.valueOf( cal.get(GregorianCalendar.SECOND)       );


		File fichier = new File( dossier, "capture_du_" + jour + "-" + mois + "-" + annee + " " +
		                         heure + "-" + minute + "-" + seconde + ".png" );

		try
		{
			ImageIO.write(image, "png", fichier);
		}
		catch (IOException e)
		{
			System.out.println("Erreur dans la méthode capturerEcran de la classe CapturerImage");
			e.printStackTrace();
		}
	}
}