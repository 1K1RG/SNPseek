package org.irri.iric.portal.dao;

import java.util.List;

import org.irri.iric.portal.genomics.LocalAlignmentQuery;

public interface LocalAlignmentDAO {

	/**
	 * Align query with database
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	List alignWithDB(LocalAlignmentQuery query) throws Exception;

}
