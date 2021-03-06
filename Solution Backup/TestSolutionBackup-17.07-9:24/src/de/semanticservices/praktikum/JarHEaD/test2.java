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
	public static final String gemeinde = ":GEMEINDE";
	public static final String länge=":längengrad";
	public static final String breite=":breitengrad";
	public static final boolean debug=false;
	
	
	/**
	 * 	Takes two Gemeinden and calculates the distance between them
	 * @param URI of Gemeinde
	 * @param URI of Gemeinde
	 * @return Double of the distance in Km
	 * @throws QueryEvaluationException 
	 */
	@CallableFromWidget
	public static double distanceDouble(URI left,URI right) throws QueryEvaluationException{
		/*left=Längen-, right=Breitengrad
		 * berechne Abstand zwischen links und rechts/längen und
		 * schreibe den Abstand in die Datenbank (DataProvider)
		 * gebe String zurück"Der abstand ist : XXX Km"
		*/		
		//String SPARQL="Select ?x ?y where{<"+left.toString()+"> :Länge ?x.<"+left.toString()+"> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{<http://www.fluidops.com/resource/test> :Länge ?x.<http://www.fluidops.com/resource/test> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{?? :längengrad ?x.?? :breitengrad ?y}";
				
		
		String SPARQL="Select ?längengrad ?breitengrad where {?? "+länge+" ?längengrad.?? "+breite+" ?breitengrad}";
		
		if(debug)System.out.println(SPARQL);
		if(debug)System.out.println(left+" >-< "+right);
		
		
		List<String> l =query(right, SPARQL,"längengrad");
		String rechtsLänge=null;
		String rechtsBreite=null;
		String linksLänge=null;
		String linksBreite=null;
		
		if(!l.isEmpty()){
			rechtsLänge=literalToString(l.get(0));}//kann sein, dass nichts oder mehr als eins zurück gegeben wird
		
		l =query(right, SPARQL,"breitengrad");
		if (!l.isEmpty()){
		rechtsBreite=literalToString(l.get(0));}
		if (debug)System.out.println(rechtsLänge+" c "+rechtsBreite);
		
		l =query(left, SPARQL,"längengrad");
		if(!l.isEmpty()){
		linksLänge=literalToString(l.get(0));}//kann sein, dass nichts oder mehr als eins zurück gegeben wird
		
		l =query(left, SPARQL,"breitengrad");
		if (!l.isEmpty()){
		linksBreite=literalToString(l.get(0));}
		if (debug)System.out.println(linksLänge+"  "+linksBreite);
		
			
		if(rechtsLänge==null||rechtsBreite==null||linksLänge==null||linksBreite==null){
			if (debug)System.err.println("Error: String is empty");
			if (debug)System.err.println(SPARQL);
			return -1;
			
		}		
		if(rechtsLänge.equals("0")||rechtsBreite.equals("0")||linksLänge.equals("0")||linksBreite.equals("0")){
			if (debug)System.err.println("Error:One or more Coordinates are 0");
			if (debug)System.err.println(SPARQL);
			return -1;
			
		}
		
		return berechneDistance(stringToDouble(rechtsLänge),stringToDouble(rechtsBreite),stringToDouble(linksLänge),stringToDouble(linksBreite));
		
	}
	/**
	 * 	Takes two Gemeinden and calculates the distance between them
	 * @param URI of Gemeinde
	 * @param URI of Gemeinde
	 * @return String of the distance 
	 * @throws QueryEvaluationException 
	 */
	@CallableFromWidget
	public static String distance(URI left,URI right) throws QueryEvaluationException{
		if(debug)System.out.println("test");
		
		return "Der Abstand beträgt "+distanceDouble(left,right)+" Km";
	}
	
	
	/**
	 * Helper Method for Distance, which calculates the Distance
	 * @param ds2 
	 * @param ds 
	 * @param double array with latitude and longitude
	 * @param double array with latitude and longitude
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
	@CallableFromWidget
	public static String literalToString(String literal){
		
		return literal.split("\"")[1];
	}
	
	
	/**
	 * Helper Method for Distance, which converts String to Double
	 * @param String 
	 * @return Double
	 */
	public static double  stringToDouble(String o){
        double out;
        char c;
        int indexKomma=0;
        if (debug)System.out.println(o);
                      
       /* for (int y =1;y<o.length();y++){
            c=o.charAt(y);
            if(c=='"'){
                indexEnde=y;
                y=o.length();
            }            
        }**/
        if (o.equals(0)){
        	if (debug)System.out.println("double null");
        	return 0;
        }
        for (int y =0;y<o.length();y++){
            c=o.charAt(y);
            if(c==','){
                indexKomma=y;
                y=o.length();
            }            
        }               
        
        //String rechtsString=o.substring(1,indexKomma)+"."+o.substring(indexKomma+1, indexEnde);
        String rechtsString=o.substring(0,indexKomma)+"."+o.substring(indexKomma+1, o.length());
        out=Double.parseDouble(rechtsString);
        
        
        
        return out;
    }
    	
	
	
	
	
	/**
	 * 
	 * @param current
	 * @param SPARQL
	 * @param parameter
	 * @return List<String>
	 * @throws QueryEvaluationException 
	 */
	@CallableFromWidget
	public static List<String> query(URI current, String SPARQL,String parameter) throws QueryEvaluationException 
	   {	  
		List<String> out=new ArrayList<String>();
		 TupleQueryResult iterator=getIterator(current,SPARQL,parameter);
	   //Helpermethod that return iterator
	   
	   while (iterator.hasNext()) {
		    BindingSet bindingSet = null;
		    try {
		     bindingSet = iterator.next();
		    } catch (QueryEvaluationException e) {
		    	if (debug)System.err.println(e);

		    }
		    //out= bindingSet.getValue("x").toString();
		    //out+=bindingSet.getValue("y").toString();
		    String s =bindingSet.getValue(parameter).toString();
		    if (!s.isEmpty()){
		    	out.add(bindingSet.getValue(s).toString());
		    }else{
		    	if (debug)System.err.println("Error: URI is null!");
		    }
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
	
	/**
	 * Calling a helper function to get all URIs of rdfs:type type
	 * @param type rdfs:type 
	 * @return List of URIs of type
	 * @throws QueryEvaluationException 
	 */
	public static List<URI> getNewURIs(URI type) throws QueryEvaluationException{
		List<URI>uris=new ArrayList<URI>();
		List<URI> uriStrings=getNode(RDFUtil.uri("y"),"Select ?x where {?x rdf:type "+type+"}","x");
		 for(URI uri:uriStrings){
			 if (debug)System.out.println(uri);
		 uris.add(uri);
		 }
		
		return uris;
	}
	
	/**
	 * Query for getUris
	 * @param current
	 * @param SPARQL
	 * @param parameter
	 * @return
	 * @throws QueryEvaluationException 
	 */
	public static List<URI> getNode(URI current, String SPARQL,String parameter) throws QueryEvaluationException 
	   {	
		List<URI> out=new ArrayList<URI>();
		 TupleQueryResult iterator=getIterator(current,SPARQL,parameter);
	   //Helpermethod that return iterator
	   
	   while (iterator.hasNext()) {
		    BindingSet bindingSet = null;
		    try {
		     bindingSet = iterator.next();
		    } catch (QueryEvaluationException e) {
		    	if (debug)System.err.println(e);

		    }
		    //out= bindingSet.getValue("x").toString();
		    //out+=bindingSet.getValue("y").toString();
		    String s =bindingSet.getValue(parameter).toString();
		    if (!s.isEmpty()){
		    	out.add(RDFUtil.fullUri(s));
		    }else{
		    	if (debug)System.err.println("Error: URI is null!");
		    }
		         }
	   
	  
	  return out;
	 
	 }

/**
 * Query for getUris
 * @param current
 * @param SPARQL
 * @param parameter
 * @return
 */		
public static TupleQueryResult getIterator(URI current,String SPARQL,String parameter){
	  	  
	  ReadDataManager dm = EndpointImpl.api().getDataManager();
	  ValueFactory vf = ValueFactoryImpl.getInstance();
	  // setting URI context for ?? in the query
	  //URI valueContext = vf.createURI(current);
	  if (debug)System.err.println(SPARQL);
	  URI valueContext= current;
	  QueryBuilder<TupleQuery> queryBuilder = QueryBuilder
	    .createTupleQuery(SPARQL).resolveValue(valueContext)
	    .infer(false);
	  List<URI> out =new ArrayList<URI>();
	  TupleQuery query = null;
	  try {
	   query = queryBuilder.build(dm);
	  } catch (MalformedQueryException | RepositoryException e) {
		  if (debug)System.err.println(e);
	   }
	  TupleQueryResult iterator = null;
	  
	  try{
	   iterator = query.evaluate();
	   }catch (QueryEvaluationException e) {
		   if (debug)System.err.println(e);}
	  return iterator;
	  
		
}
	
	
	public static List<Literal> getLiteral(URI current, String SPARQL,String parameter){
		//getNode, which returns a List of Literals
		
		return null;
	}
	
	
}
