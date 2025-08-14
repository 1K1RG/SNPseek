package org.irri.iric.portal.dao;

import java.util.List;

import org.irri.iric.ds.chado.domain.Snps2VarsCountmismatch;
import org.irri.iric.portal.genotype.VariantStringData;

public interface Snps2VarsCountMismatchDAO {

	public List<Snps2VarsCountmismatch> countMismatch(VariantStringData data);

	// /**
	// * Count number of allele nismatches between 2 varieties within region
	// * @param chr chromosome
	// * @param start region start
	// * @param end region end
	// * @return
	// */
	// public List<Snps2VarsCountmismatch> countMismatch(Integer chr, BigDecimal
	// start , BigDecimal end);
	//
	// /**
	// * Count number of allele nismatches between 2 varieties within region.
	// * Returning only the top N largest distance pairs
	// * @param chr chromosome
	// * @param start region start
	// * @param end region end
	// * @param topN
	// * @return
	// */
	// public List<Snps2VarsCountmismatch> countMismatch(Integer chr, BigDecimal
	// start , BigDecimal end, int topN);
	//
	// /**
	// * Count number of allele nismatches between 2 varieties within region.
	// * Returning only varieties in Set of Variety IDs
	// * @param chr chromosome
	// * @param start region start
	// * @param end region end
	// * @param varieties
	// * @return
	// */
	// public List<Snps2VarsCountmismatch> countMismatch(Integer chr, BigDecimal
	// start , BigDecimal end, Set<BigDecimal> varieties);
	//
	// /**
	// * Count number of allele nismatches between 2 varieties within region.
	// * Returning only the top N largest distance pairs and only varieties in Set
	// of Variety IDs
	// * @param chr chromosome
	// * @param start region start
	// * @param end region end
	// * @param topN
	// * @return
	// */
	// public List<Snps2VarsCountmismatch> countMismatch(Integer chr, BigDecimal
	// start , BigDecimal end, int topN, Set<BigDecimal> varieties);
	//
	// List<Snps2VarsCountmismatch> countMismatch(Integer chr, BigDecimal start,
	// BigDecimal end, int topN, Set<BigDecimal> varieties, boolean isCore);
	//

}
