package org.irri.iric.portal.genomics.service;

import java.util.List;

import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.dao.LocalAlignmentDAO;
import org.irri.iric.portal.genomics.LocalAlignmentQuery;
import org.irri.iric.portal.genomics.LocalAlignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("LocalAlignmentService")
public class LocalAlignmentBLASTServiceImpl implements LocalAlignmentService {

	@Autowired
	// @Qualifier("LocalAlignmentDAORemoteBlast")
	@Qualifier("LocalAlignmentDAO")
	private LocalAlignmentDAO localblastdao;

	@Override
	public List alignWithDB(LocalAlignmentQuery query) throws Exception {

		localblastdao = (LocalAlignmentDAO) AppContext.checkBean(localblastdao, "LocalAlignmentDAO");
		// setup arguments
		return localblastdao.alignWithDB(query);
	}

}
