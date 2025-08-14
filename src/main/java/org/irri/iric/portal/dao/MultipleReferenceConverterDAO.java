package org.irri.iric.portal.dao;

import org.irri.iric.ds.chado.domain.MultiReferenceConversion;
import org.irri.iric.ds.chado.domain.MultiReferenceLocus;
import org.irri.iric.portal.genotype.VariantStringData;

public interface MultipleReferenceConverterDAO {

	/**
	 * Convert from NPB position to target reference and contig
	 * 
	 * @param fromPos
	 * @param toReference
	 * @param toContig
	 * @return
	 * @throws Exception
	 */
	public MultiReferenceConversion convertPosition(MultiReferenceConversion fromPos, String toReference,
			String toContig) throws Exception;

	/**
	 * Convert NPB locus to target reference and contig
	 * 
	 * @param variantstringdataNPB
	 * @param npbMultirefLocus
	 * @param origMultiReferenceLocus
	 * @param toContig
	 * @param isOtherRefs
	 * @return
	 * @throws Exception
	 */
	public MultiReferenceLocus convertLocus(MultiReferenceLocus fromLocus, String toReference, String toContig)
			throws Exception;

	/**
	 * Converts positions in variantstringdataNPB to origMultiReferenceLocus
	 * 
	 * @param variantstringdataNPB
	 * @param npbMultirefLocus
	 * @param origMultiReferenceLocus
	 *            (other reference actual query)
	 * @param toContig
	 *            limit only to contig
	 * @param isOtherRefs
	 * @return
	 * @throws Exception
	 */
	VariantStringData convertReferencePositions(VariantStringData variantstringdataNPB,
			MultiReferenceLocus npbMultirefLocus, MultiReferenceLocus origMultiReferenceLocus, String toContig,
			boolean isOtherRefs) throws Exception;

}
