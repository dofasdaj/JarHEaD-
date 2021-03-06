package de.semanticservices.praktikum.JarHEaD;
import com.fluidops.iwb.api.EndpointImpl;
import com.fluidops.iwb.api.ReadDataManager;
import com.fluidops.iwb.api.query.QueryBuilder;
import com.fluidops.iwb.util.RDFUtil;
import com.fluidops.iwb.annotation.CallableFromWidget;

import java.lang.*;
import java.util.*;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.ValueFactoryImpl;

public class test2 {
	public static final String gemeinde = ":Gemeinde";
	public static final String länge=":längengrad";
	public static final String breite=":breitengrad";
	
	
	/**
	 * 	Takes two Gemeinden and calculates the distance between them
	 * @param URI of Gemeinde
	 * @param URI of Gemeinde
	 * @return Double of the distance in Km
	 */
	@CallableFromWidget
	public static double distanceDouble(URI left,URI right){
		/*left=Längen-, right=Breitengrad
		 * berechne Abstand zwischen links und rechts/längen und
		 * schreibe den Abstand in die Datenbank (DataProvider)
		 * gebe String zurück"Der abstand ist : XXX Km"
		*/		
		//String SPARQL="Select ?x ?y where{<"+left.toString()+"> :Länge ?x.<"+left.toString()+"> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{<http://www.fluidops.com/resource/test> :Länge ?x.<http://www.fluidops.com/resource/test> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{?? :längengrad ?x.?? :breitengrad ?y}";
				
		
		String SPARQL="Select ?längengrad ?breitengrad where {?? "+länge+" ?längengrad.?? "+breite+" ?breitengrad}";
		
		System.out.println(SPARQL);
		System.out.println(left+" "+right);
		
		
		String rechtsString=literalToString(query(right, SPARQL,"längengrad").get(0));//kann sein, dass nichts oder mehr als eins zurück gegeben wird
		rechtsString="\""+rechtsString+"\""+"\"";
		rechtsString+=" "+literalToString((query(right,SPARQL,"breitengrad").get(0)));
		rechtsString=rechtsString+"\"";
		
		System.out.println("Test");
		String linksString=literalToString(query(left, SPARQL,"längengrad").get(0));//kann sein, dass nichts oder mehr als eins zurück gegeben wird
		linksString="\""+linksString+"\""+"\"";
		linksString+=" "+literalToString((query(left,SPARQL,"breitengrad").get(0)));
		linksString=linksString+"\"";
		
		
		System.out.println(rechtsString+" "+linksString);
		
		double links[]=stringToDouble(linksString);
		double rechts[]=stringToDouble(rechtsString);
		
		
		System.out.println("Länge ist :"+links[0]+" Breite ist :"+links[1]);
		System.out.println("Länge ist :"+rechts[0]+" Breite ist :"+rechts[1]);
		return berechneDistance(rechts,links);
		
	}
	/**
	 * 	Takes two Gemeinden and calculates the distance between them
	 * @param URI of Gemeinde
	 * @param URI of Gemeinde
	 * @return String of the distance 
	 */
	@CallableFromWidget
	public static String distance(URI left,URI right){
		System.out.println("test");
		
		return "Der Abstand beträgt "+distanceDouble(left,right)+" Km";
	}
	
	
	/**
	 * Helper Method for Distance, which calculates the Distance
	 * @param double array with latitude and longitude
	 * @param double array with latitude and longitude
	 * @return double of the distance in Km
	 */
	public static double berechneDistance(double rechts[],double links[]){
		double lat=(rechts[1]+links[1])/2*0.01745;
		
		double dx=111.3*Math.cos(lat)*(rechts[0]-links[0]);
		double dy=111.3*(rechts[1]-links[1]);
		
		double distance=Math.sqrt(dx*dx+dy*dy);
		
		distance=Math.floor(distance*100)/100;// to convert it to 2 numbers after the dot
		return distance;		
		
	}
	@CallableFromWidget
	public static String literalToString(String literal){
		
		return literal.split("\"")[1];
	}
	
	
	/**
	 * Helper Method for Distance, which converts String to Double
	 * @param String 
	 * @return Double
	 */
	public static double [] stringToDouble(String o){
        double[] rechts=new double [2];
        int indexEnde=0;
        char c;
        int indexKomma=0;
                      
        for (int y =1;y<o.length();y++){
            c=o.charAt(y);
            if(c=='"'){
                indexEnde=y;
                y=o.length();
            }            
        }
        for (int y =1;y<o.length();y++){
            c=o.charAt(y);
            if(c==','){
                indexKomma=y;
                y=o.length();
            }            
        }        
       
        
        String rechtsString=o.substring(1,indexKomma)+"."+o.substring(indexKomma+1, indexEnde);        
        rechts[0]=Double.parseDouble(rechtsString);
        
        
        
        for (int y =indexEnde;y<o.length();y++){
            c=o.charAt(y);
            if(c==','){
                indexKomma=y;
                y=o.length();
            }            
        }  
        
        String linksString=o.substring(indexEnde+2,indexKomma)+"."+o.substring(indexKomma+1, o.length()-1);
        System.out.println(linksString);
        rechts[1]=Double.parseDouble(linksString);
        
        
        return rechts;
    }
    	
	
	
	
	
	/**
	 * 
	 * @param current
	 * @param SPARQL
	 * @param parameter
	 * @return
	 */
	@CallableFromWidget
	public static List<String> query(URI current, String SPARQL,String parameter) 
	   {	  
	  
	  ReadDataManager dm = EndpointImpl.api().getDataManager();
	  ValueFactory vf = ValueFactoryImpl.getInstance();
	  // setting URI context for ?? in the query
	  //URI valueContext = vf.createURI(current);
	  System.err.println(SPARQL);
	  URI valueContext= current;
	  QueryBuilder<TupleQuery> queryBuilder = QueryBuilder
	    .createTupleQuery(SPARQL).resolveValue(valueContext)
	    .infer(false);
	  List<String> out =new ArrayList<String>();
	  TupleQuery query = null;
	  try {
	   query = queryBuilder.build(dm);
	  } catch (MalformedQueryException | RepositoryException e) {
	   System.err.println(e);
	   }
	  TupleQueryResult iterator = null;
	  try {
	   iterator = query.evaluate();
	   
	   while (iterator.hasNext()) {
		    BindingSet bindingSet = null;
		    try {
		     bindingSet = iterator.next();
		    } catch (QueryEvaluationException e) {
		     System.err.println(e);

		    }
		    //out= bindingSet.getValue("x").toString();
		    //out+=bindingSet.getValue("y").toString();
		    out.add(bindingSet.getValue(parameter).toString());
		    //out.add(bindingSet.getValue("breitengrad").toString());
		     }
	   
	  } catch (QueryEvaluationException e) {
	   System.err.println(e);
	 
	  }
	  return out;
	 
	 }


	/**
	 * 
	 * @param uri RDFs type 
	 * @returnURI list of RDFs type
	 */
	public static List<URI> getURIs(URI uri) {
		List<URI> uris=new ArrayList<URI>();
		uris.add(RDFUtil.uri("GEMEINDE_Apen"));
		uris.add(RDFUtil.uri("GEMEINDE_Maroth"));
		uris.add(RDFUtil.uri("GEMEINDE_Saxler"));
		uris.add(RDFUtil.uri("GEMEINDE_Maroth"));
		 //List<String> uriStrings=query(RDFUtil.uri("GEMEINDE_Apen"),"Select ?x where {?x rdf:type :GEMEINDE}","x");
		 //for(String s:uriStrings){
		 //System.out.println(s);
		 //uris.add(RDFUtil.uri(s));
		
		 
		return uris;
	}
	public static List<URI> getNewURIs(URI uri){
		
		return null;
	}
	
	
}
