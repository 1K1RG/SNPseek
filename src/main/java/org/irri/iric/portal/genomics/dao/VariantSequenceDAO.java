package org.irri.iric.portal.genomics.dao;

import org.irri.iric.ds.chado.domain.object.VariantSequenceQuery;

/**
 * Get alternate sequence
 * 
 * @author LMansueto
 *
 */
public interface VariantSequenceDAO {

	public String getFile(VariantSequenceQuery query) throws Exception;
}
