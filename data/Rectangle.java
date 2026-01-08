package data;
import java.util.List;

public class Rectangle 
{
	private String nom;
	private List<Point> lstPoints;

	public Rectangle(String nom, List<Point> lstPoints)
	{
		this.nom       = nom;
		this.lstPoints = lstPoints;
	}

	public String      getNom()       {return nom;}
	public List<Point> getLstPoints() {return lstPoints;}

	public void setNom      (String nom)            {this.nom = nom;}
	public void setLstPoints(List<Point> lstPoints) {this.lstPoints = lstPoints;}
}
