package org.irri.iric.portal.gwas;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.Variety;

public interface GwasFacade {

	Map<MultiReferencePosition, Double> getPos2MinusLogP(String trait, String subpop);

	Map<String, Double> getPosstr2MinusLogP(String trait, String subpop);

	Map<String, Double> getPosstr2MinusLogP(String trait, String subpop, Double minlogP, String region);

	String getQQPlotFile(String trait, String subpop);

	List<String> getTraits(String pop, boolean useCoTerm);

	List<String> getSubpopulations();

	/**
	 * 
	 * @param sPhenotype
	 * @param varlist
	 * @param normalize
	 * @return Map[]{mapSubpop2Phenotype2Count, mapSubpopGen2Phenotype2Count,
	 *         mapSubpop2Phenotype2Frequency, mapSubpopGen2Phenotype2Frequency,
	 *         mapVarid2Phenotype}
	 */
	Map[] createSubpopDistributions(String sPhenotype, Collection<Variety> varlist, boolean normalize);

	/**
	 * 
	 * @param sChr
	 * @param poslist
	 * @param sPhenotype
	 * @param varlist
	 * @param normalize
	 * @return Map[] mapAllele2Phenotype2Count, mapGenotype2Phenotype2Count,
	 *         mapAllele2Phenotype2Frequency,
	 *         mapGenotype2Phenotype2Frequency,mapVarid2Phenotype,mapVarid2Genotype,mapAllele2Phenotype2Set
	 */
	Map[] createAlleleDistributions(String sChr, List poslist, String sPhenotype, Collection<Variety> varlist,
			boolean normalize);

	Map getMapPop2Trait();

	String getLegacyTraitname(String coterm);

}
