package org.irri.iric.portal.genomics.service;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.irri.iric.ds.chado.dao.CvTermLocusCountDAO;
import org.irri.iric.ds.chado.dao.CvTermPathDAO;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GeneOntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service("GeneOntologyService")
public class GeneOntologyServiceImpl implements GeneOntologyService{
	@Autowired
	@Qualifier("VGoCvtermpathDAO")
	private CvTermPathDAO cvtermpathDAO;
	@Autowired
	private  CvTermLocusCountDAO cvtermlocuscountdao;
	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemsdao;
	
	@Override
	public String queryAccession(String q) throws Exception {
		

		
		  	//URL u=new URL("http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0003824&format=oboxml");
			URL u=new URL("http://www.ebi.ac.uk/QuickGO/GSearch?q=" + q + "&format=oboxml");
	        // Connect
	        HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();

	        // Parse an XML document from the connection
	        InputStream inputStream = urlConnection.getInputStream();
	        Document xml=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
	        inputStream.close();

	        // XPath is here used to locate parts of an XML document
	        XPath xpath=XPathFactory.newInstance().newXPath();

	        //Locate the term name and print it out
	        //System.out.println("Term name:"+xpath.compile("/obo/term/name").evaluate(xml));
	        return "Term name:"+xpath.compile("/obo/term/name").evaluate(xml);
		
	}
	
	@Override
	public List countLociInTerms(String organism, Collection genelist, String cv)  throws Exception {
	
		 listitemsdao = (ListItemsDAO)AppContext.checkBean( listitemsdao,"ListItems");
		 cvtermlocuscountdao = (CvTermLocusCountDAO)AppContext.checkBean( cvtermlocuscountdao,"CvTermLocusCountDAO");
		 return cvtermlocuscountdao.getCvTermLocusCount( listitemsdao.getOrganismByName(organism).getOrganismId(), genelist, cv);
		 
	}
/*
	@Override
	public String overRepresentationTest(String organism, Collection genelist, String enrichmentType)  throws Exception
	{
		
	}
	*/
	@Override
	public String overRepresentationTest(String organism, Collection genelist, String enrichmentType)  throws Exception
	{
		String response = "";
        try {

//        	http://pantherdb.org/help/PANTHERhelp.jsp#IV.
//        	Search via http post. The base URL is http://www.pantherdb.org/webservices/garuda/enrichment.jsp? The following parameters are required:
//        		organism - Use PANTHER supported organism web service to determine list of suported organisms. The long name is used as a parameter.
//        		geneList - Text file with one id and associated attributes per line. With entries from one of the following sources: (UniProt, Ensemble, RefSeq, EntrezGene, gene symbols).
//        		enrichmentType - (function, process, cellular_location, protein_class, pathway)
//        		The result will have folowing columns:
//        		Id - Id for the associated enrichment type
//        		Name - Name of associated id
//        		GeneId
//        		P-value
            
            HttpClient client = new HttpClient( );
            MultipartPostMethod method = new MultipartPostMethod("http://pantherdb.org/webservices/garuda/enrichment.jsp?");

            //Define name-value pairs to set into the QueryString
            //method.addParameter("organism","Homo sapiens");
            //method.addParameter("type", "enrichment");
            //method.addParameter("enrichmentType", "process");       //"function", "process", "cellular_location", "protein_class", "pathway"
            
 
            
            //method.addParameter("organism","ORYSJ");
            method.addParameter("organism","Oryza sativa");
             
            method.addParameter("type", "enrichment");
            method.addParameter("enrichmentType", enrichmentType);       //"function", "process", "cellular_location", "protein_class", "pathway"
            
            String filename = AppContext.getTempDir() + "genelist-" + AppContext.createTempFilename() + ".txt";
            FileWriter fw = new FileWriter(filename);
            Iterator<Locus> itGenes = genelist.iterator();
            while(itGenes.hasNext()) {
            	String locusname = itGenes.next().getUniquename() ; 
            	fw.append(locusname + "\n");
            	AppContext.debug(locusname+",");
            }
            fw.flush();
            fw.close();
            
            File inputFile = new File(filename);
            method.addPart(new FilePart("geneList", inputFile, "text/plain","ISO-8859-1" ));
            
            // PANTHER does not use the ID type
            //method.addParameter("IdType", "UniProt");
            
            //client.
            
            // Execute and print response
            client.executeMethod( method );
            response = method.getResponseBodyAsString( );
            //System.out.println( response );
            method.releaseConnection( );

        }
        catch( IOException e ){
            e.printStackTrace();
            throw e;
        }
        
        return response;
    }


	@Override
	public List getCVtermAncestors(String cv, String term) {
		
		cvtermpathDAO = (CvTermPathDAO)AppContext.checkBean(cvtermpathDAO, "CvTermPathDAO");
		return cvtermpathDAO.getAncestors(cv, term);
	}

	@Override
	public List getCVtermDescendants(String cv, String term) {
		return null;
	}
	
	@Override
	public List getCVtermDescendants(String cv, String term, Set dataset) {
		
		cvtermpathDAO = (CvTermPathDAO)AppContext.checkBean(cvtermpathDAO, "CvTermPathDAO");
		return cvtermpathDAO.getDescendants(cv, term, dataset);
	}
/*
	@Override
	public List getCVtermDescendants(String cv, String term, String dataset) {
		
		return null;
	}
*/
        
	
	
}
