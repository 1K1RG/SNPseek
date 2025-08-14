package org.irri.iric.portal.genomics.dao;

import java.util.List;

import org.irri.iric.ds.chado.domain.object.WebsiteQuery;

public interface WebsiteDAO {

	public List<String> getURL(WebsiteQuery query);
}
