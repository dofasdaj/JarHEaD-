package de.semanticservices.praktikum.JarHEaD;
import com.fluidops.iwb.api.EndpointImpl;
import com.fluidops.iwb.api.ReadDataManager;
import com.fluidops.iwb.api.query.QueryBuilder;
import com.fluidops.iwb.annotation.CallableFromWidget;

import java.lang.*;
import java.util.*;

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
	@CallableFromWidget
	public static String fakultaet(String o){
			int i = Integer.parseInt(o);
			int fak = 1;
			for (int x = 1; x <= i; ++x) 
			{
				fak = fak * x;
			}
			String finish="Fakultät von "+i+" ist : " + fak;
			return finish;
			}
	
	
	
	@CallableFromWidget
	public static String fakultaet2(int o){
			int fak = 1;
			for (int x = 1; x <= o; ++x) 
			{
				fak = fak * x;
			}
			String finish="Fakultät von "+o+" ist : " + fak;
			return finish;
			}
	
	
	@CallableFromWidget
	public static void ausgabe (String i){
			System.out.println(i);
	}
	@CallableFromWidget
	public static int ausgabe2(String i ){
		int x=Integer.parseInt(i);
		int b=x+x;
		return b;
	}
	
	@CallableFromWidget
	public static String distance(URI left,URI right){
		/*left=Längen-, right=Breitengrad
		 * berechne Abstand zwischen links und rechts/längen und
		 * schreibe den Abstand in die Datenbank (DataProvider)
		 * gebe String zurück"Der abstand ist : XXX Km"
		*/		
		//String SPARQL="Select ?x ?y where{<"+left.toString()+"> :Länge ?x.<"+left.toString()+"> :Breite ?y}";
		//String SPARQL="Select ?x ?y where{<http://www.fluidops.com/resource/test> :Länge ?x.<http://www.fluidops.com/resource/test> :Breite ?y}";
		String SPARQL="Select ?x ?y where{?? :Länge ?x.?? :Breite ?y}";
		System.out.println(SPARQL);
		String rechtsString=query(right, SPARQL);
		String linksString=query(left,SPARQL);
		double links[]=StringToDouble(linksString);
		double rechts[]=StringToDouble(rechtsString);
		
		
		System.out.println("Länge ist :"+links[0]+" Breite ist :"+links[1]);
		System.out.println("Länge ist :"+rechts[0]+" Breite ist :"+rechts[1]);
		double x=berechneDistance(rechts,links);
		String distance="Der Abstand beträgt "+x+" Km";
		return distance;
		
	}
	
	
	public static double berechneDistance(double rechts[],double links[]){
		double lat=(rechts[1]+links[1])/2*0.01745;
		
		double dx=111.3*Math.cos(lat)*(rechts[0]-links[0]);
		double dy=111.3*(rechts[1]-links[1]);
		
		double distance=Math.sqrt(dx*dx+dy*dy);
		
		return distance;		
		
	}
	
	
	
	
	public static double [] StringToDouble(String o){
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
    	
	
	
	
	
	
	@CallableFromWidget
	public static String query(URI current, String SPARQL) 
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
	 String out=null;
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
		    out= bindingSet.getValue("x").toString();
		    out+=bindingSet.getValue("y").toString();
		     }
	   
	  } catch (QueryEvaluationException e) {
	   System.err.println(e);
	 
	  }
	  return out;
	 
	 }
	
	
}
