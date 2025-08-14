package org.irri.iric.portal.genomics;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.impl.LocalAlignmentImpl;
import org.irri.iric.ds.utils.TextSearchOptions;

/**
 * Gene loci query
 * 
 * @author LMansueto
 *
 */
public interface LocusService {

	/**
	 * Get locu with name
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Locus getLocusByName(String name) throws Exception;

	public Set<Locus> getLocusByName(Collection<String> name) throws Exception;

	/**
	 * Get loci with note (description, gene function, annotation)
	 * 
	 * @param note
	 * @param organism
	 * @return
	 */
	public List<Locus> getLocusByNotes(TextSearchOptions note, String organism);

	public List<Locus> getLocusByNotes(TextSearchOptions note, String organism, String genemodel);

	/**
	 * Get loci with synonym (locus name, feature name, gene symbol)
	 * 
	 * @param note
	 * @param organism
	 * @return
	 */
	public List<Locus> getLocusBySynonyms(TextSearchOptions synonyms, String organism);

	public List<Locus> getLocusBySynonyms(TextSearchOptions synonyms, String organism, String genemodel);

	/**
	 * Get loci from LocalAlignment result
	 * 
	 * @param alignments
	 * @return
	 * @throws Exception
	 */
	public List<Locus> getLocusFromAlignment(Collection<LocalAlignmentImpl> alignments) throws Exception;

	/**
	 * Get loci in region
	 * 
	 * @param contig
	 * @param start
	 * @param end
	 * @param organism
	 * @return
	 */
	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism);

	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism, String genemodel);

	public List<Locus> getCDSByRegion(String contig, Long start, Long end, String organism);

	public List<Locus> getCDSByRegion(String contig, Long start, Long end, String organism, String genemodel);

	/**
	 * Get loci in list of positions
	 * 
	 * @param contig
	 * @param posset
	 * @param organism
	 * @return
	 */
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus);

	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel);

	public List getCDSByContigPositions(String contig, Collection posset, String organism, Integer plusminus);

	public List getCDSByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel);

	/**
	 * Get loci linked to cvterm
	 * 
	 * @param goterm
	 * @param organism
	 * @param cvname
	 * @return
	 */
	public List getLocusByCvTerm(String goterm, String organism, String cvname);

	public List getLocusByCvTerm(String goterm, String organism, String cvname, String genemodel);

	// public List<MarkerAnnotation> getMarkerAnnotsByContigPositions(String contig,
	// Collection colPos, Integer plusminus, String genemodel, Set annotations,
	// Integer maxInteractingGenes);

	public List getCvTermByLocus(Collection<Locus> colLocus, String organism, String cvname);

	public List getCvTermByLocus(Collection<Locus> colLocus, String organism, String cvname, String genemodel);

	/**
	 * Get features interacting (with relation) to list of loci
	 * 
	 * @param colLocus
	 * @param organism
	 * @param type
	 * @param genemodel
	 * @return
	 */
	public List getInteractionByLocus(Collection<Locus> colLocus, String organism, int type, Integer max);

	public List getInteractionByLocus(Collection<Locus> colLocus, String organism, int type, String genemodel,
			Integer max);

	/**
	 * Get loci with promoters in contig and list of positions
	 * 
	 * @param contig
	 * @param posset
	 * @param db
	 * @param plusminus
	 * @return
	 */
	public List getLocusByPromoter(String contig, Collection posset, int db, Integer plusminus);

	public List<MarkerAnnotation> getMarkerAnnotsByGene(List<MarkerAnnotation> markers, int plusminus);

	public List<MarkerAnnotation> getMarkerAnnotsByQTL(List<MarkerAnnotation> markers, int plusminus);

	List getExonByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel);

	List get5UTRByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel);

	List get3UTRByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel);

	List getPromoterByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel);

	List<Locus> getExonByRegion(String contig, Long start, Long end, String organism);

	List<Locus> get3PUTRByRegion(String contig, Long start, Long end, String organism);

	List<Locus> getPromoterByRegion(String contig, Long start, Long end, String organism);

	List<Locus> get5PUTRExonByRegion(String contig, Long start, Long end, String organism);

	Map[] getMarkerGenomicAnnotsByContigPositions(String contig, Collection colPos, Integer plusminus,
			String genemodel, String organism);

	Map[] getMarkerGenomicAnnotsByRegion(String contig, Collection colPos, Long start, Long stop, String genemodel, String organism);

	List<MarkerAnnotation> getMarkerAnnotsByContigPositions(String contig, Collection colPos, Integer plusminus,
			String genemodel, Set annotations, Integer maxInteractingGenes, Set excludeAnnotation);

}
