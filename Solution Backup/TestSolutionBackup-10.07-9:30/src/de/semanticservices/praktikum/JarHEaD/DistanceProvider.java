package de.semanticservices.praktikum.JarHEaD;
import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

import com.fluidops.iwb.model.ParameterConfigDoc;
import com.fluidops.iwb.model.Vocabulary;
import com.fluidops.iwb.provider.AbstractFlexProvider;
import com.fluidops.iwb.provider.ProviderUtils;
import com.fluidops.iwb.util.RDFUtil;

/**
 * Generic provider skeleton. Can be used as a basis for implementing any type
 * of data provider.
 */
public class DistanceProvider extends AbstractFlexProvider<DistanceProvider.Config> {
	
	private static final long serialVersionUID = 684345323098327777L;

	public static class Config implements Serializable {
		private static final long serialVersionUID = -6759601022040845557L;

		// in the provider configuration, the user specifies a directory
		@ParameterConfigDoc(desc = "directory name", required = true)
		public String directory;
	}

	@Override
	public Class<? extends Config> getConfigClass() {
		return Config.class;
	}

	@Override
	public void gather(final List<Statement> res) throws Exception {
				//Liste von URI´s die alle Gemeinden der Datenbanken zurückgeben
				//Helper funktion, die aus SPARQLstatement liste von Gemeinde URI´s erstellt /Query umbauen
				//Distance über alle Gemeinden laufen
		
		
				URI links=RDFUtil.uri("GEMEINDE_Mainz");
				URI rechts=RDFUtil.uri("GEMEINDE_Alzey");
				
				String distance =test2.distance(links,rechts);
				
				//test2.query(current, SPARQL);
				
				// convert the file name to a URI
				//URI fileURI = ProviderUtils.objectToUri("test2");

				// using the URI, type the file as foaf:Document ...
				//res.add(ProviderUtils.createStatement(links, RDF.TYPE,
					//	Vocabulary.FOAF.DOCUMENT));
				res.add(ProviderUtils.createStatement(links, RDFUtil.uri("Abstand"),
						RDFUtil.literal(distance)));
				
				//Abfrag ob Abstand><10/50/100km> ist wenn ja:
				res.add(ProviderUtils.createStatement(links, RDFUtil.uri("Abstand.10Km"),
						rechts));
				// ... assign a label ...
				//res.add(ProviderUtils.createLiteralStatement(fileURI,
					//	RDFS.LABEL, "Label"));
				
				
			
		}

		// that's all, the triples add to res will automatically added to the
		// repository by the surrounding provider framework
	
}