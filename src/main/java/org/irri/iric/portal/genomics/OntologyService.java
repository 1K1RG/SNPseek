package org.irri.iric.portal.genomics;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Ontology service
 * 
 * @author LMansueto
 *
 */
public interface OntologyService {

	/**
	 * Query accession from ortology website
	 * 
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public String queryAccession(String q) throws Exception;

	/**
	 * Get ancestors of term in cv
	 * 
	 * @param cv
	 * @param term
	 * @return
	 */
	public List getCVtermAncestors(String cv, String term);

	/**
	 * Get descendands of term in cv
	 * 
	 * @param cv
	 * @param term
	 * @return
	 */
	public List getCVtermDescendants(String cv, String term);

	public List getCVtermDescendants(String cv, String term, Set dataset);

	/**
	 * Count number of loci in organism for cv
	 * 
	 * @param organism
	 * @param genelist
	 * @param cv
	 * @return
	 * @throws Exception
	 */
	public List countLociInTerms(String organism, Collection genelist, String cv) throws Exception;

	/**
	 * Submit URL of genelist for organism in gene enrichment analysis sites
	 * 
	 * @param organism
	 * @param genelist
	 * @param enrichmentType
	 * @return
	 * @throws Exception
	 */
	public String overRepresentationTest(String organism, Collection genelist, String enrichmentType) throws Exception;

}
