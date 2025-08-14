package org.irri.iric.portal.genomics;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.irri.iric.ds.chado.domain.CvTermLocusCount;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.object.VariantSequenceQuery;
import org.irri.iric.ds.chado.domain.object.WebsiteQuery;
import org.irri.iric.ds.utils.TextSearchOptions;

/**
 * API functions used mostly by Gene Locus Query
 * 
 * @author LMansueto
 *
 */

public interface GenomicsFacade {

	public static String GENEMODEL_ALL = "all";
	public static String GENEMODEL_IRIC = "iric";
	public static String GENEMODEL_MSU7 = "msu7";
	public static String GENEMODEL_RAP = "rap";
	public static String GENEMODEL_FGENESH = "fgenesh";
	public static String GENEMODEL_MSU7_ONLY = "msu7only";
	public static String GENEMODEL_RAP_ONLY = "raponly";
	public static String GENEMODEL_IRIC_ONLY = "iriconly";

	public static String SNPANNOT_SYNONYMOUS = "S";
	public static String SNPANNOT_NONSYNONYMOUS = "N";
	public static String SNPANNOT_EXON = "E";
	public static String SNPANNOT_GENE = "G";
	public static String SNPANNOT_3PUTR = "3";
	public static String SNPANNOT_5PUTR = "5";
	public static String SNPANNOT_PROMOTER = "P";
	public static String SNPANNOT_SPLICEACCEPTOR = "A";
	public static String SNPANNOT_SPLICEDONOR = "D";
	public static String SNPANNOT_CDS = "C";

	public static Map SNPANNOT_RANK = Collections.unmodifiableMap(new HashMap<String, Integer>() {
		{
			// Unnamed init block.
			put(SNPANNOT_NONSYNONYMOUS, 1);
			put(SNPANNOT_SYNONYMOUS, 2);
			put(SNPANNOT_SPLICEACCEPTOR, 3);
			put(SNPANNOT_SPLICEDONOR, 4);
			put(SNPANNOT_CDS, 5);
			put(SNPANNOT_3PUTR, 6);
			put(SNPANNOT_5PUTR, 7);
			put(SNPANNOT_EXON, 8);
			put(SNPANNOT_GENE, 9);
			put(SNPANNOT_PROMOTER, 10);
		}
	});

	public static String FEATURETYPE_GENE = "gene";
	public static String FEATURETYPE_CDS = "CDS";
	public static String FEATURETYPE_EXON = "exon";
	public static String FEATURETYPE_5PUTR = "five_prime_UTR";
	public static String FEATURETYPE_3PUTR = "three_prime_UTR";
	public static String FEATURETYPE_300BPUPS = "300bp_uptream";

	public static String QTL_ALL = "all";
	public static String QTL_QTARO = "qtaro";
	public static String QTL_GRAMENE = "gramene";

	/**
	 * Get locus based on description (gene annotation)
	 * 
	 * @param name
	 * @return
	 */
	public List<Locus> getLociByDescription(TextSearchOptions name);

	public List<Locus> getLociByDescription(TextSearchOptions description, String organism);

	public List<Locus> getLociByDescription(TextSearchOptions description, String organism, String genemodel);

	/**
	 * Get locus based on names (locus name, synonyms, symbols)
	 * 
	 * @param synonym
	 * @param organism
	 * @return
	 */
	public List<Locus> getLociBySynonym(TextSearchOptions synonym, String organism);

	public List<Locus> getLociBySynonym(TextSearchOptions synonym, String organism, String genemodel);

	/**
	 * Get locus by name
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Locus getLocusByName(String name) throws Exception;

	/**
	 * Get loci with the collection of names (capitalized)
	 * 
	 * @param names
	 * @return
	 * @throws Exception
	 */
	public Set<Locus> getLocusByName(Collection<String> names) throws Exception;

	/**
	 * Get organisms (References)
	 * 
	 * @return
	 * @throws Exception
	 */
	public List getOrganisms() throws Exception;

	/**
	 * 
	 * @param organism
	 * @return
	 */
	List getContigsByOrganism(String organism);

	/**
	 * Get GO terms associated with genes in organism
	 * 
	 * @param cv
	 * @param organism
	 * @return
	 */
	List<String> getGotermsByOrganism(String cv, String organism);

	/**
	 * Get TO, PO, QTARO terms associated with genes in organism
	 * 
	 * @param cv
	 * @param organism
	 * @return
	 */
	List<String> getPatotermsByOrganism(String cv, String organism);

	/**
	 * Get GO definition
	 * 
	 * @param term
	 * @return
	 * @throws Exception
	 */
	public String queryGO(String term) throws Exception;

	List<String> getCVtermAncestors(String cv, String cvterm);

	List<String> getCVtermDescendants(String cv, String cvterm);

	public String overRepresentationTest(String organism, Collection genelist, String enrichmentType) throws Exception;

	/**
	 * Perform blast
	 * 
	 * @param sequence
	 * @param organism
	 * @param querytype
	 * @param dbtype
	 * @param evalue
	 * @return List<LocalAlignment>
	 * @throws Exception
	 */
	public List getLociBySequence(String sequence, String organism, String querytype, String dbtype, double evalue)
			throws Exception;

	public List getLociBySequence(String sequence, String organism, String querytype, String dbtype, double evalue,
			boolean topHitOnly, boolean includeSequence) throws Exception;

	/**
	 * 
	 * @param alignments
	 *            Collection<LocalAlignment>
	 * @return List<Locus>
	 * @throws Exception
	 */
	public List getLociFromAlignment(Collection alignments) throws Exception;

	public List<CvTermLocusCount> getGOTermLociCounts(String organism, Set loci, String cv) throws Exception;

	/**
	 * Get TO,PO definition
	 * 
	 * @param term
	 * @return
	 * @throws Exception
	 */
	String queryPATO(String term) throws Exception;

	/**
	 * 
	 * @param cvterm
	 * @param organism
	 * @param cvname
	 * @param genemodel
	 * @return
	 */
	public List getLociByCvTerm(String cvterm, String organism, String cvname, String genemodel);

	/**
	 * 
	 * @param contig
	 * @param start
	 * @param end
	 * @param organism
	 * @param genemodel
	 * @return
	 */
	public List<Locus> getLociByRegion(String contig, Long start, Long end, String organism, String genemodel);

	public List getLociByRegion(String contig, Long start, Long end, String organism);

	public List<Locus> getCDSByRegion(String contig, Long start, Long end, String organism, String genemodel);

	public List getCDSByRegion(String contig, Long start, Long end, String organism);

	/**
	 * 
	 * @param contig
	 * @param colPos
	 * @param organism
	 * @param genemodel
	 * @return
	 */
	public List<Locus> getLociByContigPositions(String contig, Collection colPos, String organism, Integer plusminus,
			String genemodel);

	public List getLociByContigPositions(String contig, Collection posset, String organism, Integer plusminus);

	public List<Locus> getCDSByContigPositions(String contig, Collection colPos, String organism, Integer plusminus,
			String genemodel);

	public List getCDSByContigPositions(String contig, Collection posset, String organism, Integer plusminus);

	/**
	 * 
	 * @param contig
	 * @param colPos
	 * @param organism
	 * @param genemodel
	 * @return
	 */

	// public List<MarkerAnnotation> getMarkerAnnotsByContigPositions(String contig,
	// Collection colPos, String organism, Integer plusminus, String genemodel, Set
	// annotations, Integer maxInteractingGenes);
	public List<MarkerAnnotation> getMarkerAnnotsByContigPositions(String contig, Collection colPos, String organism,
			Integer plusminus, String genemodel, Set annotations, Integer maxInteractingGenes, Set excludeAnnotations);

	public List<MarkerAnnotation> getMarkerAnnotsByGene(List<MarkerAnnotation> markers, int plusminus);

	public List<MarkerAnnotation> getMarkerAnnotsByQTL(List<MarkerAnnotation> markers, int plusminus);

	/**
	 * 
	 * @param chr
	 * @param listPos
	 * @param organism
	 * @param genemodel
	 * @return setPos[CDSUTR, exon, gene, prom]
	 */
	public Map[] getMarkerGenomicsAnnotsByContigPositions(String chr, Collection listPos, String organism,
			String genemodel);

	public Map<Position, List<Locus>>[] getMarkerGenomicsAnnotsByRegion(String chr, List<SnpsAllvarsPos> listPos,
			Long start, Long stop, String organism, String genemodel);

	/**
	 * Generate FASTA
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public String createVariantsFasta(VariantSequenceQuery query) throws Exception;

	public Future<String> createVariantsFastaAsync(VariantSequenceQuery query) throws Exception;

	// public Future<List<String>> queryWebsiteAsync(WebsiteQuery query, boolean
	// display, Component comp) throws Exception;
	public List<String> queryWebsite(WebsiteQuery query, boolean display) throws Exception;

	public Future<List<String>> queryWebsiteAsync(WebsiteQuery query, boolean display) throws Exception;

}
