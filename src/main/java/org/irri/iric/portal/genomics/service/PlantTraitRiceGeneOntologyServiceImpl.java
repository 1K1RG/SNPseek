package org.irri.iric.portal.genomics.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.irri.iric.ds.chado.dao.CvTermLocusCountDAO;
import org.irri.iric.ds.chado.dao.CvTermPathDAO;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("PATOGenesOntologyService")
public class PlantTraitRiceGeneOntologyServiceImpl implements OntologyService {

	@Override
	public List getCVtermDescendants(String cv, String term) {
		return null;
	}

	@Autowired
	// @Qualifier("CvTermPathDAO")

	@Qualifier("VGoCvtermpathDAO")
	private CvTermPathDAO cvtermpathDAO;
	@Autowired
	private CvTermLocusCountDAO cvtermlocuscountdao;
	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemsdao;

	@Override
	public String queryAccession(String q) throws Exception {
		return null;
	}

	@Override
	public String overRepresentationTest(String organism, Collection genelist, String enrichmentType) throws Exception {
		return null;
	}

	@Override
	public List countLociInTerms(String organism, Collection genelist, String cv) throws Exception {
		return null;
	}

	@Override
	public List getCVtermAncestors(String cv, String term) {
		
		cvtermpathDAO = (CvTermPathDAO) AppContext.checkBean(cvtermpathDAO, "CvTermPathDAO");
		return cvtermpathDAO.getAncestors(cv, term);
	}

	@Override
	public List getCVtermDescendants(String cv, String term, Set dataset) {
		
		cvtermpathDAO = (CvTermPathDAO) AppContext.checkBean(cvtermpathDAO, "CvTermPathDAO");
		return cvtermpathDAO.getDescendants(cv, term, dataset);
	}
	/*
	 * @Override public List getCVtermDescendants(String cv, String term, String
	 * dataset) {  return null; }
	 */

}
