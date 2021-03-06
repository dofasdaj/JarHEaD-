package de.semanticservices.praktikum.JarHEaD;

import org.openrdf.model.URI;
import org.openrdf.query.QueryEvaluationException;

import com.fluidops.iwb.annotation.CallableFromWidget;

/**
 * Util Class which contains all utility methods
 * 
 */
@SuppressWarnings("unused")
public class Util {
	public static final String gemeinde = ":GEMEINDE";
	public static final String länge=":längengrad";
	public static final String breite=":breitengrad";
	public static final String distributor=":personenid";
	public static final String srSPARQL="select ?srpempfohlenerpreis where {?? :srpempfohlenerpreis ?srpempfohlenerpreis}";
	public static boolean debug=true;
	
	/**
	 * 	Takes two municipalities and calculates the distance between them
	 * @param URI of the coordinates of a municipality
	 * @param URI of the coordinates of a municipality
	 * @return Double of the distance in Km
	 * @throws QueryEvaluationException 
	 */
	public static double distanceDouble(URI left,URI right) throws QueryEvaluationException{
		/*left=Längen-, right=Breitengrad
		 * berechne Abstand zwischen links und rechts/längen und
		 * schreibe den Abstand in die Datenbank (DataProvider)
		 * gebe String zurück"Der abstand ist : XXX Km"
		*/		
		//String SPARQL="Select ?x ?y where{<"+left.toString()+"> :Länge ?x.<"+left.toString()+"> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{<http://www.fluidops.com/resource/test> :Länge ?x.<http://www.fluidops.com/resource/test> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{?? :längengrad ?x.?? :breitengrad ?y}";
				
		
		String SPARQL="Select ?längengrad ?breitengrad where {?? "+Util.länge+" ?längengrad.?? "+Util.breite+" ?breitengrad}";
		
		if(Util.debug)System.out.println(SPARQL);
		if(Util.debug)System.out.println(left+" >-< "+right);
		
		
		String[] l =Helper.query(right, SPARQL,"längengrad","breitengrad");
		String rechtsLänge=null;
		String rechtsBreite=null;
		String linksLänge=null;
		String linksBreite=null;
				
		if (l[0]!=null&&l[1]!=null&&!(l[1].isEmpty())&&!l[0].isEmpty()&&!l[0].equals("0")&&!l[1].equals("0")){
			if(Util.debug)System.out.println("Ausgabe von l: "+l);
		rechtsBreite=Helper.literalToString(l[1]);
		rechtsLänge=Helper.literalToString(l[0]);
		}
		
		if (Util.debug)System.out.println(rechtsLänge+" c "+rechtsBreite);
		
		l =Helper.query(left, SPARQL,"längengrad","breitengrad");
		
		if (l[0]!=null&&l[1]!=null&&!(l[1].isEmpty())&&!l[0].isEmpty()&&!l[0].equals("0")&&!l[1].equals("0")){
			if(Util.debug)System.out.println("Ausgabe von l: "+l);
		linksBreite=Helper.literalToString(l[1]);
		linksLänge=Helper.literalToString(l[0]);}//kann sein, dass nichts oder mehr als eins zurück gegeben wird
		
		
		if (Util.debug)System.out.println(linksLänge+" O "+linksBreite);
		
			
		if(rechtsLänge==null||rechtsBreite==null||linksLänge==null||linksBreite==null){
			String name = new Object(){}.getClass().getEnclosingMethod().getName();
			if (Util.debug)System.err.println("Error: String is empty"+name);
			if (Util.debug)System.err.println(SPARQL);
			return -1;
			
		}		
		if(rechtsLänge.equals("0")||rechtsBreite.equals("0")||linksLänge.equals("0")||linksBreite.equals("0")){
			if (Util.debug)System.err.println("Error:One or more Coordinates are 0");
			if (Util.debug)System.err.println(SPARQL);
			return -1;
			
		}
		
		return Util.berechneDistance(Double.parseDouble(rechtsLänge),Double.parseDouble(rechtsBreite),Double.parseDouble(linksLänge),Double.parseDouble(linksBreite));
		
	}

	/**
	 * Calculates the distance between two coordinates after this formula:
	 * https://www.kompf.de/gps/distcalc.html
	 * @param double of the longitude coordinate of the first municipality
	 * @param double of the Latitude coordinate of the first municipality
	 * @param double of the longitude coordinate of the second municipality
	 * @param double of the Latitude coordinate of the second municipality
	 * @return double of the distance in Km
	 */
	public static double berechneDistance(double rechtsLänge,double rechtsBreite, double linksLänge, double linksBreite){
		if (debug)System.out.println("anfang der berechne distance");
		if (debug)System.out.println(rechtsLänge+" "+rechtsBreite+" "+linksLänge+" "+linksBreite);
		double lat=(rechtsBreite+linksBreite)/2*0.01745;
		
		double dx=111.3*Math.cos(lat)*(rechtsLänge-linksLänge);
		double dy=111.3*(rechtsBreite-linksBreite);
		
		double distance=Math.sqrt(dx*dx+dy*dy);
		
		distance=Math.floor(distance*100)/100;// to convert it to 2 numbers after the dot
		if (debug)System.out.println(distance+"ende der berechne distance");
		return distance;		
	
	}
	
}
