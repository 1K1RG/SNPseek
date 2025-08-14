package org.irri.iric.portal.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.irri.iric.ds.chado.domain.Cv;
import org.irri.iric.ds.chado.domain.Gene;
import org.irri.iric.ds.chado.domain.Scaffold;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.portal.genotype.GenotypeQueryParams;

/**
 * Cache frequently used lists of objects, the list is queried from the database
 * on first use. This class is used by the VarietyFacade, GenomicsFacade and
 * GenotypeFacade implementations
 * 
 * @author LMansueto
 *
 */
public interface ListItemsDAO {

	public static final int PHENOTYPETYPE_NONE = 0;
	public static final int PHENOTYPETYPE_QUAL = 1;
	public static final int PHENOTYPETYPE_QUAN = 2;

	// Variety

	public List<String> getGenenames();

	Map getIrisId2Variety();

	Map getIrisId2Variety(String dataset);

	List getVarietyNames();

	List getVarietyNames(String dataset);

	List getIrisIds();

	List getIrisIds(String dataset);

	Variety getGermplasmByIrisId(String name);

	// Variety getGermplasmByIrisId(String name, String dataset);
	// List<Variety> getGermplasmByIrisIds(Collection names, String dataset);
	List<Variety> getGermplasmByIrisIds(Collection names, Set dataset);

	Variety getGermplasmByAccession(String name, String dataset);
	// public List<Variety> getGermplasmByAccessions(Collection accession, String
	// dataset);

	// Variety getGermplasmByNameLike(String name);
	// Variety getGermplasmByName(String name);
	public List<Variety> getGermplasmByNameLike(String name, Set dataset);

	public List<Variety> getGermplasmByNamesLike(Collection names, Set dataset);

	public List<Variety> getGermplasmByName(String name, String dataset);

	public List<Variety> getGermplasmByNames(Collection names, String dataset);

	// Set getGermplasmByCountry(String country);
	// Set getGermplasmByCountry(String country, String dataset);

	// Set getGermplasmBySubpopulation(String subpopulation);
	Set getGermplasmBySubpopulation(String subpopulation, String dataset);

	public Set getGermplasmBySubpopulation(String subpopulation, Set dataset);

	// Set getGermplasm();
	Set getGermplasm(Set dataset);

	List getCountries(String dataset);

	// List getSubpopulations();
	List getSubpopulations(String dataset);

	// Set getGermplasmByExample(Variety germplasm);
	// Set getGermplasmByExample(Variety germplasm, String dataset);

	Map<BigDecimal, Variety> getMapId2Variety(String dataset);

	Map<BigDecimal, StockSample> getMapId2Sample(String dataset);

	Map<String, Variety> getMapVarname2Variety(String dataset);

	Map<String, StockSample> getMapAssay2Sample(String dataset);

	Map<String, BigDecimal> getPhenotypeDefinitions(String dataset);

	Map<String, BigDecimal> getPassportDefinitions(String dataset);

	Gene findGeneFromName(String genename, String organism);

	public List<Gene> findGeneFromName(Collection<String> genenames, String organism);

	// Integer getFeatureLength(String feature);

	// for multi-reference
	List getOrganisms() throws Exception;

	List getContigs(String organism);

	Long getFeatureLength(String feature, String organism);

	Scaffold getFeature(String feature, String organism);

	List getGOTermsWithLoci(String cv, String organism);

	public Collection getGenenames(String organism);

	public Organism getOrganismByName(String name);

	public List<String> getPATOTermsWithLoci(String cv, String organism);

	public Organism getOrganismById(Integer id);

	public Cv getCvByName(String cv);

	public Map<String, BigDecimal> getPtocoDefinitions(String dataset);

	// public List getAccessions();

	// public List getAccessions(String dataset);

	public Collection<? extends Variety> getGermplasmByExample(Variety example, Set dataset);

	// Set<Variety> getGermplasmsByName(String names, String dataset);
	Set<Variety> getGermplasmsByName(String names, Set dataset);

	// Variety getGermplasmByAccession(String accession, String dataset);

	Set<Variety> getGermplasmsByIrisId(String irisid, String dataset);

	public Set getGermplasmsByNameAccession(String varname, String accession, String dataset);

	public List<String> getAccessions(String dataset);

	public List getOrganismScaffolds(String organism);

	public void cleanDatasetCache(String dataset);

	public void cleanOrganismCache(String organism);

	public Set getPlatforms(String type);

	public Set getPlatforms(Set setds, Set setvs, String type);

	public Set getDatasets(String type);

	public Set getDatasets();

	public List getRunsByType(String type);

	public Set getDatasets(String type, String reference);

	public Set getSnpsets(String dataset);

	public Set getSnpsets(Set dataset, String type);

	public Set getSnpsets();

	public Map getMapId2Variety(Set dataset);

	public Map<String, Map<BigDecimal, StockSample>> getMapId2Sample(Set dataset);

	public List getSubpopulations(Set dataset);

	public List getAccessions(Set dataset);

	public Map getPhenotypeDefinitions(Set dataset);

	public List getVarietyNames(Set dataset);

	public Set getQuantTraits(Set dataset);

	public Map getPassportDefinitions(Set dataset);

	public Map getPtocoDefinitions(Set dataset);

	public List getIrisIds(Set dataset);

	public List getCountries(Set dataset);

	public Map<String, Variety> getMapVarname2Variety(Set dataset);

	public Map<String, StockSample> getMapAssay2Sample(Set dataset);

	public Map getUIforDataset(Set dataset);

	public Map<Integer, BigDecimal> getMapIdx2Sample(GenotypeQueryParams params);

	public boolean hasNonsynData(Integer organismId, Set variantset);

	public Collection getSnpsets(String varietyset, String type);

	public List getIRGCVarietyNames();

	public List getIRGCAccessions();

	public Set getGermplasmByCountry(String country, Set dataset);

	public Map<String, BigDecimal> getTraits(Set<String> dataset, boolean selected);

	public Map getCOTerms(Set dataset);

	public Map getCOTerms(String dataset);

	public Map<BigDecimal, String> getAllTraits(Set<String> dataset, boolean legacyPhenotype);

	/*
	 * public Map getPhenotypeValues(String sPhenotype, Set dataset); public
	 * Object[] getPhenotypeUniqueValues(String phenname, Set dataset); public
	 * Variety getGermplasmByAccession(String trim, Set dataset); public
	 * List<Variety> getGermplasmByName(String var1, Set dataset); public Collection
	 * getGermplasmsByName(String varname, Set dataset); public Collection
	 * getGermplasmsByAccession(String varname, Set dataset); public Set
	 * getGermplasmBySubpopulation(String subpopulation, Set dataset); public Set
	 * checkVariety(String varstr, Set dataset); public Variety
	 * getGermplasmByIrisId(String name, Set dataset); public Collection
	 * getGermplasmsByNameAccession(String varname, String accession, Set dataset);
	 * 
	 * //public List<String> getVarietyAccessions(String dataset);
	 */

}