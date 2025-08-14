package org.irri.iric.portal.genomics.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.irri.iric.ds.chado.dao.CvDAO;
import org.irri.iric.ds.chado.dao.FeatureInteractionDAO;
import org.irri.iric.ds.chado.dao.LocusCvTermDAO;
import org.irri.iric.ds.chado.dao.LocusDAO;
import org.irri.iric.ds.chado.dao.LocusPromoterDAO;
import org.irri.iric.ds.chado.dao.OrganismDAO;
import org.irri.iric.ds.chado.dao.QtlDAO;
import org.irri.iric.ds.chado.dao.SnpsAllvarsPosDAO;
import org.irri.iric.ds.chado.dao.SnpsEffectDAO;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.LocusPromoter;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.irri.iric.ds.chado.domain.Organism;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.PositionLogPvalue;
import org.irri.iric.ds.chado.domain.SnpsEffect;
import org.irri.iric.ds.chado.domain.impl.LocalAlignmentImpl;
import org.irri.iric.ds.chado.domain.impl.MarkerAnnotationByLocusImpl;
import org.irri.iric.ds.chado.domain.impl.MarkerAnnotationImpl;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genomics.LocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("LocusService")
public class LocusServiceImpl implements LocusService {

	@Autowired
	private OrganismDAO orgdao;

	@Autowired
	private CvDAO cvdao;

	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemsdao;

	@Autowired
	@Qualifier("LocusNotesDAO")
	private LocusDAO locusnotesDAO;

	@Autowired
	@Qualifier("LocusCvtermCvtermpathDAO")
	// @Qualifier("VLocusCvtermDAO")
	private LocusCvTermDAO locuscvtermDAO;
	// private CvTermDAO locuscvtermDAO;

	@Autowired
	@Qualifier("VLocusCvtermpathIricDAO")
	// @Qualifier("VLocusCvtermDAO")
	private LocusCvTermDAO locusiriccvtermDAO;

	@Autowired
	@Qualifier("VLocusCvtermpathMsu7DAO")
	private LocusCvTermDAO locusmsu7cvtermDAO;

	@Autowired
	@Qualifier("VLocusCvtermpathRapDAO")
	private LocusCvTermDAO locusrapcvtermDAO;

	@Autowired
	// @Qualifier("VQtlDAO")
	private QtlDAO qtldao;

	@Autowired
	private LocusPromoterDAO promoterdao;

	@Autowired
	private SnpsEffectDAO snpeffectdao;
	// @Autowired
	// private FeatureInteractionDAO interactiondao;

	// @Autowired
	// @Qualifier("VLocusCvtermDAO")
	// private LocusDAO locuspatotermDAO;

	@Override
	public List getLocusByNotes(TextSearchOptions note, String organism) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByDescription(note, organism);
	}

	@Override
	public List<Locus> getLocusBySynonyms(TextSearchOptions synonyms, String organism) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusBySynonyms(synonyms, organism);
	}

	@Override
	public List<Locus> getLocusBySynonyms(TextSearchOptions synonyms, String organism, String genemodel) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusBySynonyms(synonyms, organism, genemodel);
	}

	@Override
	public List getLocusByCvTerm(String goterm, String organism, String cvname) {
		
		AppContext.debug("getting locus for " + goterm + "  " + organism);
		// locuscvtermDAO =
		// (LocusDAO)AppContext.checkBean(locuscvtermDAO,"VLocusCvtermCvtermpathDAO");
		locuscvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locuscvtermDAO, "LocusCvtermCvtermpathDAO");
		// locuscvtermDAO =
		// (LocusCvTermDAO)AppContext.checkBean(locuscvtermDAO,"LocusCvtermDAO");
		return locuscvtermDAO.getLocusByDescription(goterm,
				orgdao.getMapName2Organism().get(organism).getOrganismId().intValue(),
				cvdao.getMapName2Cv().get(cvname).getCvId().intValue());
	}

	@Override
	public List getLocusByCvTerm(String goterm, String organism, String cvname, String genemodel) {
		
		AppContext.debug("getting locus for " + goterm + "  " + organism + " " + cvname + "  " + genemodel);
		// locuscvtermDAO =
		// (LocusDAO)AppContext.checkBean(locuscvtermDAO,"VLocusCvtermCvtermpathDAO");

		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE()))
			return getLocusByCvTerm(goterm, organism, cvname);

		LocusCvTermDAO locusgenemodelcvtermDAO = null;

		if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC))
			locusgenemodelcvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locusiriccvtermDAO,
					"VLocusCvtermpathIricDAO");
		else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7))
			locusgenemodelcvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locusmsu7cvtermDAO,
					"VLocusCvtermpathMsu7DAO");
		if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP))
			locusgenemodelcvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locusrapcvtermDAO,
					"VLocusCvtermpathRapDAO");

		// locuscvtermDAO =
		// (LocusCvTermDAO)AppContext.checkBean(locuscvtermDAO,"LocusCvtermDAO");
		return locusgenemodelcvtermDAO.getLocusByDescription(goterm,
				orgdao.getMapName2Organism().get(organism).getOrganismId().intValue(),
				cvdao.getMapName2Cv().get(cvname).getCvId().intValue());
	}

	@Override
	public List getInteractionByLocus(Collection<Locus> colLocus, String organism, int type, Integer max) {

		List list = null;
		FeatureInteractionDAO interactiondao = null;
		if (type == FeatureInteractionDAO.INTERACTION_RICENETV2)
			interactiondao = (FeatureInteractionDAO) AppContext.checkBean(interactiondao, "VLocusIntxnRicenetv2DAO");
		else if (type == FeatureInteractionDAO.INTERACTION_RICENETV1)
			interactiondao = (FeatureInteractionDAO) AppContext.checkBean(interactiondao, "VLocusIntxnRicenetv1DAO");
		else if (type == FeatureInteractionDAO.INTERACTION_PRINEXPT)
			interactiondao = (FeatureInteractionDAO) AppContext.checkBean(interactiondao, "VLocusIntxnPrinexptDAO");
		else if (type == FeatureInteractionDAO.INTERACTION_PRINPRED)
			interactiondao = (FeatureInteractionDAO) AppContext.checkBean(interactiondao, "VLocusIntxnPrinpredDAO");

		try {
			AppContext.debug("type=" + type + "  " + colLocus.size() + " gene loci: " + colLocus);
			list = interactiondao.getInteractingFeatures(colLocus, type, max);
			AppContext.debug(list.size() + " interactions");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	@Override
	public List getInteractionByLocus(Collection<Locus> colLocus, String organism, int type, String genemodel,
			Integer max) {
		return getInteractionByLocus(colLocus, organism, type, max);

		/*
		 * if(type==FeatureInteractionDAO.INTERACTION_RICENETV2) interactiondao =
		 * (FeatureInteractionDAO)AppContext.checkBean(interactiondao,
		 * "FeatureInteractionDAO"); return
		 * interactiondao.getInteractingFeatures(colLocus, type);
		 */
	}

	@Override
	public List getLocusByPromoter(String contig, Collection posset, int db, Integer plusminus) {

		promoterdao = (LocusPromoterDAO) AppContext.checkBean(promoterdao, "LocusPromoterDAO");

		// String contig, posset, int db, String organism, Integer plusminus, String
		// genemode
		Set loc = promoterdao.getLociWithPromoters(contig, posset, db, AppContext.getDefaultOrganism(), plusminus,
				null);
		AppContext.debug(loc.size() + " loci with promoters");
		List list = new ArrayList();
		list.addAll(loc);
		return list;

	}

	@Override
	public List getCvTermByLocus(Collection<Locus> colLocus, String organism, String cvname) {
		
		AppContext.debug("getting cvterms for " + colLocus.size() + " loci " + organism);
		// locuscvtermDAO =
		// (LocusDAO)AppContext.checkBean(locuscvtermDAO,"VLocusCvtermCvtermpathDAO");
		locuscvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locuscvtermDAO, "LocusCvtermCvtermpathDAO");
		// locuscvtermDAO =
		// (LocusCvTermDAO)AppContext.checkBean(locuscvtermDAO,"LocusCvtermDAO");
		List list = locuscvtermDAO.filterLocusWithCvterm(colLocus,
				orgdao.getMapName2Organism().get(organism).getOrganismId().intValue(),
				cvdao.getMapName2Cv().get(cvname).getCvId().intValue());

		AppContext.debug(list.size() + " cvterms");
		return list;
	}

	@Override
	public List getCvTermByLocus(Collection<Locus> colLocus, String organism, String cvname, String genemodel) {
		

		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE()))
			return getCvTermByLocus(colLocus, organism, cvname);

		LocusCvTermDAO locusgenemodelcvtermDAO = null;

		if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC))
			locusgenemodelcvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locusiriccvtermDAO,
					"VLocusCvtermpathIricDAO");
		else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7))
			locusgenemodelcvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locusmsu7cvtermDAO,
					"VLocusCvtermpathMsu7DAO");
		if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP))
			locusgenemodelcvtermDAO = (LocusCvTermDAO) AppContext.checkBean(locusrapcvtermDAO,
					"VLocusCvtermpathRapDAO");

		// locuscvtermDAO =
		// (LocusCvTermDAO)AppContext.checkBean(locuscvtermDAO,"LocusCvtermDAO");
		// return locusgenemodelcvtermDAO.getLocusByDescription(goterm,
		// orgdao.getMapName2Organism().get(organism).getOrganismId().intValue(),
		// cvdao.getMapName2Cv().get(cvname).getCvId().intValue());

		List list = locusgenemodelcvtermDAO.filterLocusWithCvterm(colLocus,
				orgdao.getMapName2Organism().get(organism).getOrganismId().intValue(),
				cvdao.getMapName2Cv().get(cvname).getCvId().intValue());

		AppContext.debug(list.size() + " cvterms");
		return list;

	}

	@Override
	public Locus getLocusByName(String name) throws Exception {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		List listlocus = locusnotesDAO.getLocusByName(name);

		if (listlocus.size() == 0)
			return null;
		if (listlocus.size() > 1)
			throw new RuntimeException("Multiple values for locus name " + name);

		// AppContext.debug("LocusServiceImpl:" + listlocus);

		return (Locus) listlocus.get(0);

	}

	@Override
	public Set<Locus> getLocusByName(Collection<String> name) throws Exception {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return new HashSet(locusnotesDAO.getLocusByName(name));

	}

	@Override
	public List<Locus> getLocusFromAlignment(Collection<LocalAlignmentImpl> alignments) throws Exception {
		

		Set setLocusNames = new LinkedHashSet();
		Set setLocus = new LinkedHashSet();
		Iterator<LocalAlignmentImpl> itAlign = alignments.iterator();
		while (itAlign.hasNext()) {
			String locusname = itAlign.next().getSacc().toUpperCase();

			if (locusname.contains("."))
				locusname = locusname.split("\\.")[0];

			if (setLocusNames.contains(locusname))
				continue;

			setLocus.add(getLocusByName(locusname));
			setLocusNames.add(locusname);

		}
		List listLocus = new ArrayList();
		listLocus.addAll(setLocus);
		return listLocus;
	}

	@Override
	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism) {
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_MSU7,
				GenomicsFacade.FEATURETYPE_GENE);
	}

	@Override
	public List<Locus> getCDSByRegion(String contig, Long start, Long end, String organism) {
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_MSU7,
				GenomicsFacade.FEATURETYPE_CDS);
	}

	@Override
	public List<Locus> getExonByRegion(String contig, Long start, Long end, String organism) {
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_MSU7,
				GenomicsFacade.FEATURETYPE_EXON);
	}

	@Override
	public List<Locus> get3PUTRByRegion(String contig, Long start, Long end, String organism) {
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_MSU7,
				GenomicsFacade.FEATURETYPE_3PUTR);
	}

	@Override
	public List<Locus> getPromoterByRegion(String contig, Long start, Long end, String organism) {
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_MSU7,
				GenomicsFacade.FEATURETYPE_300BPUPS);
	}

	@Override
	public List<Locus> get5PUTRExonByRegion(String contig, Long start, Long end, String organism) {
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_MSU7,
				GenomicsFacade.FEATURETYPE_5PUTR);
	}

	/*
	 * private Map getMapContig2SNPPos(Collection<MultiReferencePosition> posset) {
	 * Iterator<MultiReferencePosition> itMultiPos = posset.iterator(); Map<String,
	 * Set> mapChr2Pos = new TreeMap(); while(itMultiPos.hasNext()) {
	 * MultiReferencePosition refpos = itMultiPos.next(); Set setPos =
	 * mapChr2Pos.get(refpos.getContig()); if(setPos==null) { setPos=new TreeSet();
	 * mapChr2Pos.put( refpos.getContig() , setPos); }
	 * setPos.add(refpos.getPosition() ); } return mapChr2Pos; }
	 */

	@Override
	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism, String genemodel) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, genemodel);
	}

	@Override
	public List<Locus> getCDSByRegion(String contig, Long start, Long end, String organism, String genemodel) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByRegion(contig, start, end, organism, genemodel, GenomicsFacade.FEATURETYPE_CDS);
	}

	@Override
	public List<Locus> getLocusByNotes(TextSearchOptions description, String organism, String genemodel) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByDescription(description, organism, genemodel);
	}

	private List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel, String featuretype) {
		
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		return locusnotesDAO.getLocusByContigPositions(contig, posset, organism, plusminus, genemodel, featuretype);
		/*
		 * if(contig.toLowerCase().equals("any")) { List listLoci = new ArrayList();
		 * Map<String, Set<BigDecimal>> mapChr2Pos =
		 * MultiReferencePositionImpl.getMapContig2SNPPos(posset); Iterator<String>
		 * itChr = mapChr2Pos.keySet().iterator(); while(itChr.hasNext()) { String chr =
		 * itChr.next(); listLoci.addAll( locusnotesDAO.getLocusByContigPositions(chr,
		 * mapChr2Pos.get(chr) , organism , plusminus ,genemodel, featuretype) ); }
		 * return listLoci; } else {
		 * 
		 * return locusnotesDAO.getLocusByContigPositions(contig, posset, organism,
		 * plusminus , genemodel, featuretype); }
		 */
	}

	@Override
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		
		return getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
				GenomicsFacade.FEATURETYPE_GENE);
	}

	@Override
	public List getCDSByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		
		return getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
				GenomicsFacade.FEATURETYPE_CDS);
	}

	@Override
	public List getExonByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		
		return getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
				GenomicsFacade.FEATURETYPE_EXON);
	}

	@Override
	public List get5UTRByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		
		return getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
				GenomicsFacade.FEATURETYPE_5PUTR);
	}

	@Override
	public List get3UTRByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		
		return getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
				GenomicsFacade.FEATURETYPE_3PUTR);
	}

	@Override
	public List getPromoterByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		
		return getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
				GenomicsFacade.FEATURETYPE_300BPUPS);
	}

	@Override
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus) {
		return null;
	}

	@Override
	public List getCDSByContigPositions(String contig, Collection posset, String organism, Integer plusminus) {
		return null;
	}

	// mergeAnnotations(contig, strchr, colPos, plusminus, mapCvterm2Loci,
	// mapAnnotcount, mapGenemodel2Interacting2Loci, mapPromoterLoci, mapSnpEff);
	private List<MarkerAnnotation> mergeAnnotations(String contig, Long chr, Collection colPos, Integer plusminus,
			Map<String, Collection> mapName2Col, Map<String, Long> mapAnnotcount,
			Map<String, Map<String, Collection>> mapIntearctingLoci, Map<String, Collection> mapName2Promoters,
			Map<String, Collection> mapName2Snpeff) {

		if (contig.equals("any"))
			throw new RuntimeException("mergeAnnotations contig.equals(\"any\")");

		colPos = AppContext.convertPos2Position(colPos);

		Map<BigDecimal, MarkerAnnotation> mapPos2Annots = new TreeMap();
		Iterator<BigDecimal> itPos = colPos.iterator();
		while (itPos.hasNext()) {
			BigDecimal pos = itPos.next();

			Set<Locus> setPosGenes = new HashSet();
			Iterator<String> itAnnotname = mapName2Col.keySet().iterator();
			while (itAnnotname.hasNext()) {
				String annotname = itAnnotname.next();
				Collection colLocs = mapName2Col.get(annotname);
				Iterator<Locus> itLoc = colLocs.iterator();
				while (itLoc.hasNext()) {
					Locus loc = itLoc.next();
					if (loc.getContig().equals(contig) && loc.getFmin() - plusminus <= pos.intValue()
							&& loc.getFmax() + plusminus >= pos.intValue()) {
						MarkerAnnotation annots = mapPos2Annots.get(pos);
						if (annots == null) {
							annots = new MarkerAnnotationImpl(contig, chr, pos);
							mapPos2Annots.put(pos, annots);
						}
						annots.addOntologyGene(annotname, loc);
						setPosGenes.add(loc);
					}
				}

				Long count = mapAnnotcount.get(annotname);
				if (count == null)
					count = new Long(0);
				if ((MarkerAnnotation) mapPos2Annots.get(pos) != null
						&& ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname) != null)
					mapAnnotcount.put(annotname,
							count + ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname).size());
			}

			Iterator<Locus> itGenes = setPosGenes.iterator();
			while (itGenes.hasNext()) {
				Locus gene = itGenes.next();

				Map<String, Collection> mapGeneInteractions = mapIntearctingLoci.get(gene.getUniquename());
				if (mapGeneInteractions == null)
					continue;

				itAnnotname = mapGeneInteractions.keySet().iterator();
				while (itAnnotname.hasNext()) {
					String annotname = itAnnotname.next();
					Collection colLocs = mapGeneInteractions.get(annotname);
					if (colLocs != null) {
						Iterator<Locus> itLoc = colLocs.iterator();
						while (itLoc.hasNext()) {
							Locus loc = itLoc.next();
							// if(loc.getContig().equals(contig) && loc.getFmin()-plusminus <=
							// pos.intValue() && loc.getFmax()+plusminus >= pos.intValue()) {
							MarkerAnnotation annots = mapPos2Annots.get(pos);
							if (annots == null) {
								annots = new MarkerAnnotationImpl(contig, chr, pos);
								mapPos2Annots.put(pos, annots);
							}
							// annots.addOntologyGene(annotname, loc);
							annots.addNetworkGene(annotname, loc);
							// }
						}
					}

					Long count = mapAnnotcount.get(annotname);
					if (count == null)
						count = new Long(0);
					if ((MarkerAnnotation) mapPos2Annots.get(pos) != null
							&& ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname) != null)
						mapAnnotcount.put(annotname,
								count + ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname).size());
				}
			}

			itAnnotname = mapName2Promoters.keySet().iterator();
			while (itAnnotname.hasNext()) {
				String annotname = itAnnotname.next();
				Collection colLocs = mapName2Promoters.get(annotname);
				Iterator<Locus> itLoc = colLocs.iterator();
				while (itLoc.hasNext()) {
					LocusPromoter loc = (LocusPromoter) itLoc.next();

					if (loc.getContig().equals(contig) && loc.getPromoterStart() - plusminus <= pos.intValue()
							&& loc.getPromoterEnd() + plusminus >= pos.intValue()) {
						MarkerAnnotation annots = mapPos2Annots.get(pos);
						if (annots == null) {
							annots = new MarkerAnnotationImpl(contig, chr, pos);
							mapPos2Annots.put(pos, annots);
						}
						annots.addPromoterGene(annotname, loc);
					}
				}

				Long count = mapAnnotcount.get(annotname);
				if (count == null)
					count = new Long(0);
				if ((MarkerAnnotation) mapPos2Annots.get(pos) != null
						&& ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname) != null)
					mapAnnotcount.put(annotname,
							count + ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname).size());

			}

			itAnnotname = mapName2Snpeff.keySet().iterator();
			while (itAnnotname.hasNext()) {
				String annotname = itAnnotname.next();
				Collection colLocs = mapName2Snpeff.get(annotname);
				Iterator<SnpsEffect> itLoc = colLocs.iterator();
				while (itLoc.hasNext()) {
					SnpsEffect loc = (SnpsEffect) itLoc.next();

					if (loc.getContig().equals(contig)
							&& Math.abs(loc.getFmin().intValue() - pos.intValue()) <= plusminus) {
						MarkerAnnotation annots = mapPos2Annots.get(pos);
						if (annots == null) {
							annots = new MarkerAnnotationImpl(contig, chr, pos);
							// annots = new MarkerAnnotationByLocusImpl( loc);
							mapPos2Annots.put(pos, annots);
						}
						annots.addMarker(annotname, loc);
					}
				}

				Long count = mapAnnotcount.get(annotname);
				if (count == null)
					count = new Long(0);
				if ((MarkerAnnotation) mapPos2Annots.get(pos) != null
						&& ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname) != null)
					mapAnnotcount.put(annotname,
							count + ((MarkerAnnotation) mapPos2Annots.get(pos)).getAnnotation(annotname).size());

			}

		}
		// AppContext.debug("mapAnnotcount: " + mapAnnotcount.toString());

		List result = new ArrayList();
		result.addAll(mapPos2Annots.values());
		return result;
	}

	private List<MarkerAnnotation> getAnnotations(String contig, Collection colPos, Integer plusminus, String genemodel,
			Map<String, Long> mapAnnotcount, Integer maxInteractingGenes,
			Map<String, PositionLogPvalue> mapPos2Pvalue) {
		return getAnnotations(null, contig, colPos, plusminus, genemodel, mapAnnotcount, maxInteractingGenes,
				mapPos2Pvalue);
	}

	private List<MarkerAnnotation> getAnnotations(Collection loci, Integer plusminus, String genemodel,
			Map<String, Long> mapAnnotcount, Integer maxInteractingGenes,
			Map<String, PositionLogPvalue> mapPos2Pvalue) {
		return getAnnotations(loci, null, null, plusminus, genemodel, mapAnnotcount, maxInteractingGenes,
				mapPos2Pvalue);
	}

	private List<MarkerAnnotation> getAnnotations(Collection loci, String contig, Collection colPos, Integer plusminus,
			String genemodel, Map<String, Long> mapAnnotcount, Integer maxInteractingGenes,
			Map<String, PositionLogPvalue> mapPos2Pvalue) {
		// private List<MarkerAnnotation> getAnnotations( String contig, Collection
		// colPos, Integer plusminus, String genemodel, Map<String, Long> mapAnnotcount,
		// Integer maxInteractingGenes) {

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItemsDAO");
		Map mapCvterm2Loci = new LinkedHashMap();
		Map<String, Map<String, Collection>> mapGenemodel2Interacting2Loci = new LinkedHashMap();

		List listLoci = new ArrayList();
		if (loci == null) {
			listLoci.addAll(locusnotesDAO.getLocusByContigPositions(contig, colPos, AppContext.REFERENCE_NIPPONBARE(),
					plusminus, genemodel));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_MODEL, listLoci);
		} else {
			listLoci.addAll(new LinkedHashSet(listitemsdao.findGeneFromName(loci, AppContext.REFERENCE_NIPPONBARE())));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_MODEL, listLoci);
		}

		if (listLoci.size() > 0) {

			Map<String, Set> mapGenemodel2Names = new HashMap();
			Set<String> locallnamesets = new HashSet();
			Iterator<Locus> itLocus = listLoci.iterator();
			while (itLocus.hasNext()) {
				Locus loc = itLocus.next();
				Set locnamesets = new HashSet();
				locnamesets.add(loc.getUniquename().toUpperCase());
				if (loc instanceof MergedLoci) {
					MergedLoci mloc = (MergedLoci) loc;
					/*
					 * if(mloc.getMSU7Name()!=null && !mloc.getMSU7Name().isEmpty())
					 * locnamesets.add(mloc.getMSU7Name().toUpperCase());
					 * if(mloc.getRAPRepName()!=null && !mloc.getRAPRepName().isEmpty())
					 * locnamesets.add(mloc.getRAPRepName().toUpperCase());
					 * if(mloc.getRAPPredName()!=null && !mloc.getRAPPredName().isEmpty())
					 * locnamesets.add(mloc.getRAPPredName().toUpperCase());
					 */
					locallnamesets.addAll(mloc.getOverlappingGenes());
				}
				locallnamesets.addAll(locnamesets);
				mapGenemodel2Names.put(loc.getUniquename(), locnamesets);

			}
			Set locnameset = new HashSet();
			Iterator<String> itLocname = locallnamesets.iterator();
			while (itLocname.hasNext()) {
				String names[] = itLocname.next().split(",");
				for (int iname = 0; iname < names.length; iname++)
					locnameset.add(names[iname].trim());
			}

			Set<Locus> locset = new LinkedHashSet(
					listitemsdao.findGeneFromName(locnameset, AppContext.REFERENCE_NIPPONBARE()));

			// if(AppContext.isIRRILAN()) {
			if (true) {

				Map<String, Locus> mapName2Loci = new HashMap();
				itLocus = locset.iterator();
				while (itLocus.hasNext()) {
					Locus loc = itLocus.next();
					mapName2Loci.put(loc.getUniquename(), loc);
				}

				Map<String, Set> mapGenemodel2Loci = new HashMap();
				itLocname = mapGenemodel2Names.keySet().iterator();
				while (itLocname.hasNext()) {
					String genemodelname = itLocname.next();
					Set<String> genemodelnames = mapGenemodel2Names.get(genemodelname);
					itLocname = genemodelnames.iterator();
					Set<Locus> genemodelgenes = new HashSet();
					while (itLocname.hasNext()) {
						String names[] = itLocname.next().split(",");
						for (int iname = 0; iname < names.length; iname++) {
							Locus nameloci = mapName2Loci.get(names[iname].trim());
							if (nameloci == null) {
								AppContext.debug(names[iname].trim() + " not in map: " + mapName2Loci);
							} else
								genemodelgenes.add(nameloci);
						}
					}

					Map mapInteracting2Loci = mapGenemodel2Interacting2Loci.get(genemodelname);
					if (mapInteracting2Loci == null) {
						mapInteracting2Loci = new HashMap();
						mapGenemodel2Interacting2Loci.put(genemodelname, mapInteracting2Loci);
						AppContext.debug("getting interactions for " + genemodelname + ", " + genemodelgenes);
						if (genemodelgenes.size() > 0) {
							String tops = "";
							if (maxInteractingGenes != null)
								tops = " (TOP " + maxInteractingGenes + ")";
							mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_RICENETV2 + tops,
									getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
											FeatureInteractionDAO.INTERACTION_RICENETV2, maxInteractingGenes));
							if (AppContext.isOracle()) {
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_RICENETV1 + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_RICENETV1, maxInteractingGenes));
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_PRINEXPT + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_PRINEXPT, maxInteractingGenes));
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_PRINPRED + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_PRINPRED, maxInteractingGenes));
							}
						}
					}
				}

			}

			/*
			 * AppContext.debug("getting genes with names " + locnameset.size() + "  " +
			 * locnameset.toString()); AppContext.debug("genes: " + locset );
			 * AppContext.debug("gene objects = " + locset.size());
			 */
			mapCvterm2Loci.put(MarkerAnnotation.GENE_QTARO,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "qtaro_gene_traits"));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_TO,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "plant_trait_ontology"));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_PO,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "plant_anatomy"));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_PO_DEVT,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "plant_structure_development_stage"));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_GO_BP,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "biological_process"));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_GO_MF,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "molecular_function"));
			mapCvterm2Loci.put(MarkerAnnotation.GENE_GO_CC,
					getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "cellular_component"));

		}
		// List listQtlGramene = new ArrayList();
		List listQtlQtaro = new ArrayList();
		listQtlQtaro.addAll(qtldao.getLocusByContigPositions(contig, colPos, AppContext.REFERENCE_NIPPONBARE(), plusminus,
				QtlDAO.QTL_QTARO));
		// listQtlGramene.addAll( qtldao.getLocusByContigPositions(contig, colPos,
		// Organism.REFERENCE_NIPPONBARE, plusminus, QtlDAO.QTL_GRAMENE) );
		mapCvterm2Loci.put(MarkerAnnotation.QTL_QTARO, listQtlQtaro);
		// mapCvterm2Loci.put(MarkerAnnotation.QTL_GRAMENE, listQtlGramene);

		Map<String, Collection> mapPromoterLoci = new LinkedHashMap();
		// if(AppContext.isIRRILAN()) {
		if (true) {
			mapPromoterLoci.put(MarkerAnnotation.PROM_PLANTPROMDB,
					getLocusByPromoter(contig, colPos, LocusPromoterDAO.PROMOTER_PLANTPROMDB_PRED, plusminus));
			mapPromoterLoci.put(MarkerAnnotation.PROM_FGENESH1K,
					getLocusByPromoter(contig, colPos, LocusPromoterDAO.PROMOTER_FGENESH, plusminus));
		}

		Map mapSnpEff = new LinkedHashMap();
		Set ds = new HashSet();
		ds.add(SnpsAllvarsPosDAO.TYPE_3KALLSNP_HDF5_V2);
		mapSnpEff.put(MarkerAnnotation.MARKER_SNPEFF, snpeffectdao.getSNPsIn(contig, colPos, ds));

		// annotations.addAll( mapCvterm2Loci.keySet());

		Long strchr = Long.valueOf(AppContext.guessChrFromString(contig)); // Long.valueOf(
																			// contig.toLowerCase().replaceAll("chr0","").replaceAll("chr",""));
		// return mergeAnnotations(contig, strchr, colPos, plusminus, listLoci,
		// listQtlQtaro, listQtlGramene, mapCvterm2Loci, annotations);
		return mergeAnnotations(contig, strchr, colPos, plusminus, mapCvterm2Loci, mapAnnotcount,
				mapGenemodel2Interacting2Loci, mapPromoterLoci, mapSnpEff);
	}

	@Override
	public List<MarkerAnnotation> getMarkerAnnotsByGene(List<MarkerAnnotation> markers, int plusminus) {

		Map<Locus, MarkerAnnotation> mapGeneAnnots = new HashMap();
		Iterator<MarkerAnnotation> itMarkers = markers.iterator();
		while (itMarkers.hasNext()) {
			MarkerAnnotation marker = itMarkers.next();

			Iterator<String> itannots = marker.getAnnotations().iterator();
			StringBuffer buf = new StringBuffer();
			buf.append("marker.getAnnotations() " + marker.getContig() + " " + marker.getPosition() + ": ");
			while (itannots.hasNext()) {
				String annot = itannots.next();
				buf.append(annot + " " + marker.getAnnotation(annot).size() + ", ");
			}
			// AppContext.debug(buf.toString());

			Collection setGene = marker.getAnnotation(MarkerAnnotation.GENE_MODEL);
			if (setGene != null && !setGene.isEmpty()) {
				Iterator<Locus> itLoc = setGene.iterator();
				// AppContext.debug("setGene=" + setGene);
				while (itLoc.hasNext()) {
					Locus gene = itLoc.next();
					MarkerAnnotation geneannots = mapGeneAnnots.get(gene);
					if (geneannots == null) {
						geneannots = new MarkerAnnotationByLocusImpl(gene, plusminus);
						geneannots.addGene(MarkerAnnotation.GENE_MODEL, gene);
						mapGeneAnnots.put(gene, geneannots);
					}

					Iterator<String> itAnnots = marker.getAnnotations().iterator();
					while (itAnnots.hasNext()) {
						String annot = itAnnots.next();
						// if(annot.equals(MarkerAnnotation.GENE_MODEL)) continue;
						if (MarkerAnnotation.ONTOLOGY_GENES.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug(col.size() + " added to " + annot + " " +
							// gene.getUniquename() );
							geneannots.addOntologyGene(annot, col);
						}

						if (MarkerAnnotation.INT_GENES.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug(col.size() + " added to " + annot + " " +
							// gene.getUniquename());
							geneannots.addNetworkGene(annot, marker.getAnnotation(annot));
						}

						if (MarkerAnnotation.PROMOTERS.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug(col.size() + " added to " + annot + " " +
							// gene.getUniquename());
							geneannots.addPromoterGene(annot, marker.getAnnotation(annot));
						}

						if (MarkerAnnotation.MARKERS.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug(col.size() + " added to " + annot + " " +
							// gene.getUniquename());
							geneannots.addMarker(annot, marker.getAnnotation(annot));
						}

					}

					itannots = geneannots.getAnnotations().iterator();
					buf = new StringBuffer();
					buf.append(gene.getUniquename() + ":");
					while (itannots.hasNext()) {
						String annot = itannots.next();
						buf.append(annot + " " + geneannots.getAnnotation(annot).size() + ", ");
					}
					// AppContext.debug(buf.toString());

					/*
					 * Collection setPos = geneannots.getAnnotation(MarkerAnnotation.MARKER_POSITION
					 * ); if(setPos==null) { setPos=new HashSet();
					 * geneannots.addMarker(MarkerAnnotation.MARKER_POSITION, setPos); } setPos.add(
					 * marker );
					 */

				}
			}
		}

		List listGeneAnnots = new ArrayList();
		listGeneAnnots.addAll(new TreeSet(mapGeneAnnots.values()));
		AppContext.debug(markers.size() + " markers to " + listGeneAnnots.size() + " genes");
		return listGeneAnnots;

	}

	@Override
	public List<MarkerAnnotation> getMarkerAnnotsByQTL(List<MarkerAnnotation> markers, int plusminus) {

		Map<Locus, MarkerAnnotation> mapGeneAnnots = new HashMap();
		Iterator<MarkerAnnotation> itMarkers = markers.iterator();
		while (itMarkers.hasNext()) {
			MarkerAnnotation marker = itMarkers.next();
			Collection setQtls = new HashSet();
			Collection setq = marker.getAnnotation(MarkerAnnotation.QTL_QTARO);
			if (setq != null)
				setQtls.addAll(setq);
			// setq=marker.getAnnotation(MarkerAnnotation.QTL_GRAMENE);
			// if(setq!=null) setQtls.addAll(setq);

			if (setQtls != null && !setQtls.isEmpty()) {
				Iterator<Locus> itLoc = setQtls.iterator();
				while (itLoc.hasNext()) {
					Locus gene = itLoc.next();
					MarkerAnnotation geneannots = mapGeneAnnots.get(gene);
					if (geneannots == null) {
						geneannots = new MarkerAnnotationByLocusImpl(gene, plusminus);
						geneannots.addQTL(MarkerAnnotation.QTL_QTARO, gene);
						mapGeneAnnots.put(gene, geneannots);
					}

					Iterator<String> itAnnots = marker.getAnnotations().iterator();
					while (itAnnots.hasNext()) {
						String annot = itAnnots.next();
						// if(annot.equals(MarkerAnnotation.GENE_MODEL)) continue;

						if (MarkerAnnotation.ONTOLOGY_GENES.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug("added " + col.size() + " genes");
							geneannots.addOntologyGene(annot, col);
						}

						if (MarkerAnnotation.INT_GENES.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug("added " + col.size() + " genes");
							geneannots.addNetworkGene(annot, col);
						}

						if (MarkerAnnotation.PROMOTERS.contains(annot)) {
							Collection col = marker.getAnnotation(annot);
							// AppContext.debug("added " + col.size() + " genes");
							geneannots.addPromoterGene(annot, col);
						}

						if (MarkerAnnotation.MARKERS.contains(annot))
							geneannots.addMarker(annot, marker.getAnnotation(annot));

					}
					// geneannots.addMarker( MarkerAnnotation.MARKER_POSITION ,
					// marker.getContigPosition());
					geneannots.addGene(MarkerAnnotation.GENE_MODEL, marker.getAnnotation(MarkerAnnotation.GENE_MODEL));

					// AppContext.debug("genemodels for " + gene.getUniquename() + ": " +
					// geneannots.getAnnotation(MarkerAnnotation.GENE_MODEL ));
				}
			}
		}

		List listGeneAnnots = new ArrayList();
		listGeneAnnots.addAll(new TreeSet(mapGeneAnnots.values()));
		AppContext.debug(markers.size() + " markers to " + listGeneAnnots.size() + " QTLs");
		return listGeneAnnots;

	}

	@Override
	public Map[] getMarkerGenomicAnnotsByRegion(String contig, Collection colPos, Long start, Long stop,
			String genemodel, String organism) {
		return _getMarkerGenomicAnnotsByContigPositions(contig, colPos, start, stop, 0, genemodel, organism);
	}

	@Override
	public Map[] getMarkerGenomicAnnotsByContigPositions(String contig, Collection colPos, Integer plusminus,
			String genemodel, String organism) {
		// if(!AppContext.isIRRILAN()) return null;
		return _getMarkerGenomicAnnotsByContigPositions(contig, colPos, null, null, plusminus, genemodel, organism);
	}

	private Map[] _getMarkerGenomicAnnotsByContigPositions(String contig, Collection colPos, Long start, Long stop,
			Integer plusminus, String genemodel, String organism) {

		Map<Position, List> setCDSUTRSnp = new HashMap();
		Map<Position, List> setExonSnp = new HashMap();
		Map<Position, List> setGeneSnp = new HashMap();
		Map<Position, List> setPromSnp = new HashMap();

		Set set3p5pcds = new HashSet();
		set3p5pcds.add(GenomicsFacade.FEATURETYPE_CDS);
		set3p5pcds.add(GenomicsFacade.FEATURETYPE_5PUTR);
		set3p5pcds.add(GenomicsFacade.FEATURETYPE_3PUTR);
		set3p5pcds.add(GenomicsFacade.FEATURETYPE_GENE);
		set3p5pcds.add(GenomicsFacade.FEATURETYPE_EXON);

		List listAll = null;
		if (start != null && stop != null)
			listAll = locusnotesDAO.getLocusByRegion(contig, start, stop, organism, genemodel,
					set3p5pcds);
		else
			listAll = locusnotesDAO.getLocusByContigPositions(contig, colPos, organism, plusminus,
					genemodel, set3p5pcds);

		List setCDSUTRPromLoc = new ArrayList();
		List le = new ArrayList();
		List lg = new ArrayList();

		Iterator<Locus> itLocall = listAll.iterator();
		while (itLocall.hasNext()) {
			Locus loc = itLocall.next();
			if (loc.getFeatureType().equals(GenomicsFacade.FEATURETYPE_GENE))
				lg.add(loc);
			else if (loc.getFeatureType().equals(GenomicsFacade.FEATURETYPE_EXON))
				le.add(loc);
			else
				setCDSUTRPromLoc.add(loc);

		}

		/*
		 * List setCDSUTRPromLoc =locusnotesDAO.getLocusByContigPositions(contig, colPos
		 * , Organism.REFERENCE_NIPPONBARE, plusminus, genemodel,set3p5pcds); List
		 * le=locusnotesDAO.getLocusByContigPositions(contig, colPos ,
		 * Organism.REFERENCE_NIPPONBARE, plusminus, genemodel,
		 * GenomicsFacade.FEATURETYPE_EXON); List
		 * lp=locusnotesDAO.getLocusByContigPositions(contig, colPos ,
		 * Organism.REFERENCE_NIPPONBARE, 400, genemodel,
		 * GenomicsFacade.FEATURETYPE_GENE); List
		 * lg=locusnotesDAO.getLocusByContigPositions(contig, colPos ,
		 * Organism.REFERENCE_NIPPONBARE, plusminus, genemodel,
		 * GenomicsFacade.FEATURETYPE_GENE);
		 */

		List lp = null;
		if (start != null && stop != null)
			lp = locusnotesDAO.getLocusByRegion(contig, start - 400, stop + 400, organism,
					genemodel, GenomicsFacade.FEATURETYPE_GENE);
		else
			lp = locusnotesDAO.getLocusByContigPositions(contig, colPos, organism, plusminus + 400,
					genemodel, GenomicsFacade.FEATURETYPE_GENE);

		Iterator<Position> itPos = colPos.iterator();
		while (itPos.hasNext()) {
			Position pos = itPos.next();
			float fpos = pos.getPosition().floatValue();

			boolean inCDSUTR = false;
			Iterator<Locus> itLoc = setCDSUTRPromLoc.iterator();
			while (itLoc.hasNext()) {
				Locus loc = itLoc.next();
				if (pos.getChr() == loc.getChr()
						&& ((fpos <= loc.getFmax().floatValue() && fpos >= loc.getFmin().floatValue())
								|| (fpos >= loc.getFmax().floatValue() && fpos <= loc.getFmin().floatValue()))) {
					// setCDSUTRSnp.put(pos,loc);
					List l = setCDSUTRSnp.get(pos);
					if (l == null) {
						l = new ArrayList();
						setCDSUTRSnp.put(pos, l);
					}
					l.add(loc);
					inCDSUTR = true;
					// break;
				}
			}
			boolean inExon = false;
			// if(!inExon) {
			if (true) {
				itLoc = le.iterator();
				while (itLoc.hasNext()) {
					Locus loc = itLoc.next();
					if (pos.getChr() == loc.getChr()
							&& ((fpos <= loc.getFmax().floatValue() && fpos >= loc.getFmin().floatValue())
									|| (fpos >= loc.getFmax().floatValue() && fpos <= loc.getFmin().floatValue()))) {
						// setExonSnp.put(pos,loc);
						List l = setExonSnp.get(pos);
						if (l == null) {
							l = new ArrayList();
							setExonSnp.put(pos, l);
						}
						l.add(loc);
						inExon = true;
						// break;
					}
				}
			}
			boolean inGene = false;
			// if(!inExon && !inCDSUTR) {
			if (true) {
				itLoc = lg.iterator();
				while (itLoc.hasNext()) {
					Locus loc = itLoc.next();
					if (pos.getChr() == loc.getChr()
							&& ((fpos <= loc.getFmax().floatValue() && fpos >= loc.getFmin().floatValue())
									|| (fpos >= loc.getFmax().floatValue() && fpos <= loc.getFmin().floatValue()))) {
						// setGeneSnp.put(pos,loc);
						List l = setGeneSnp.get(pos);
						if (l == null) {
							l = new ArrayList();
							setGeneSnp.put(pos, l);
						}
						l.add(loc);
						inGene = true;
						// break;
					}
				}
			}

			// if(!inExon && !inCDSUTR && !inGene) {
			if (true) {
				itLoc = lp.iterator();
				while (itLoc.hasNext()) {
					Locus loc = itLoc.next();
					if (pos.getChr() == loc.getChr() && ((loc.getStrand() > 0 && fpos < loc.getFmin().floatValue()
							&& fpos >= loc.getFmin().floatValue() - 300)
							|| (loc.getStrand() < 0 && fpos > loc.getFmax().floatValue()
									&& fpos <= loc.getFmax().floatValue() + 300))) {
						// setPromSnp.put(pos,loc);
						List l = setPromSnp.get(pos);
						if (l == null) {
							l = new ArrayList();
							setPromSnp.put(pos, l);
						}
						l.add(loc);
						// break;
					}
				}
			}
		}

		return new Map[] { setCDSUTRSnp, setExonSnp, setGeneSnp, setPromSnp };
	}

	@Override
	public List<MarkerAnnotation> getMarkerAnnotsByContigPositions(String contig, Collection colPos, Integer plusminus,
			String genemodel, Set annotations, Integer maxInteractingGenes, Set excludeAnnotation) {
		

		qtldao = (QtlDAO) AppContext.checkBean(qtldao, "QtlDAO");
		locusnotesDAO = (LocusDAO) AppContext.checkBean(locusnotesDAO, "LocusNotesDAO");
		List listMarkers = new ArrayList();

		Map<String, Long> mapAnnotcount = new LinkedHashMap();

		Map<String, PositionLogPvalue> mapPos2Pval = new HashMap();
		Iterator<Position> itPos = colPos.iterator();
		while (itPos.hasNext()) {
			Position ipos = itPos.next();
			if (ipos instanceof PositionLogPvalue) {
				mapPos2Pval.put(ipos.getChr() + "-" + ipos.getPosition(), (PositionLogPvalue) ipos);
			} else {
				AppContext.debug(ipos.getClass() + "  " + ipos);
			}
		}
		if (mapPos2Pval.isEmpty())
			mapPos2Pval = null;
		if (mapPos2Pval != null)
			AppContext.debug("mapPos2Pval " + mapPos2Pval.size() + ":");

		if (contig.toLowerCase().equals("any")) {

			Map<String, Set<BigDecimal>> mapChr2Pos = MultiReferencePositionImpl.getMapContig2SNPPos(colPos);
			Iterator<String> itChr = mapChr2Pos.keySet().iterator();
			while (itChr.hasNext()) {
				String chr = itChr.next();
				/*
				 * List listLoci = new ArrayList(); List listQtlGramene = new ArrayList(); List
				 * listQtlQtaro = new ArrayList(); //listLoci.addAll(
				 * locusnotesDAO.getLocusByContigPositions(chr, mapChr2Pos.get(chr) , genemodel,
				 * plusminus) ); listLoci.addAll( getLocusByContigPositions(chr,
				 * mapChr2Pos.get(chr) , Organism.REFERENCE_NIPPONBARE, plusminus , genemodel )
				 * ); listQtlQtaro.addAll( qtldao.getLocusByContigPositions(chr,
				 * mapChr2Pos.get(chr) , Organism.REFERENCE_NIPPONBARE, plusminus,
				 * QtlDAO.QTL_QTARO) ); listQtlGramene.addAll(
				 * qtldao.getLocusByContigPositions(chr, mapChr2Pos.get(chr) ,
				 * Organism.REFERENCE_NIPPONBARE, plusminus, QtlDAO.QTL_GRAMENE) ); Long strchr
				 * = Long.valueOf(
				 * chr.toLowerCase().replaceAll("chr0","").replaceAll("chr",""));
				 * listMarkers.addAll(mergeAnnotations(chr, strchr, mapChr2Pos.get(chr),
				 * plusminus, listLoci, listQtlQtaro, listQtlGramene));
				 */
				listMarkers.addAll(getAnnotations(chr, mapChr2Pos.get(chr), plusminus, genemodel, mapAnnotcount,
						maxInteractingGenes, mapPos2Pval, excludeAnnotation));
			}

		} else {
			listMarkers.addAll(getAnnotations(contig, colPos, plusminus, genemodel, mapAnnotcount, maxInteractingGenes,
					mapPos2Pval, excludeAnnotation));
			/*
			 * List listLoci = new ArrayList(); List listQtlGramene = new ArrayList(); List
			 * listQtlQtaro = new ArrayList(); listLoci.addAll(
			 * locusnotesDAO.getLocusByContigPositions(contig, colPos ,
			 * Organism.REFERENCE_NIPPONBARE, plusminus, genemodel) ); listQtlQtaro.addAll(
			 * qtldao.getLocusByContigPositions(contig, colPos,
			 * Organism.REFERENCE_NIPPONBARE, plusminus, QtlDAO.QTL_QTARO) );
			 * listQtlGramene.addAll( qtldao.getLocusByContigPositions(contig, colPos,
			 * Organism.REFERENCE_NIPPONBARE, plusminus, QtlDAO.QTL_GRAMENE) ); Long strchr
			 * = Long.valueOf(
			 * contig.toLowerCase().replaceAll("chr0","").replaceAll("chr",""));
			 * listMarkers.addAll(mergeAnnotations(contig, strchr, colPos, plusminus,
			 * listLoci, listQtlQtaro, listQtlGramene));
			 */
		}

		long pvalcnt = 0;
		if (mapPos2Pval != null) {
			Iterator<MarkerAnnotation> itAnnots = listMarkers.iterator();
			while (itAnnots.hasNext()) {
				MarkerAnnotation marker = itAnnots.next();
				PositionLogPvalue pval = mapPos2Pval.get(marker.getChr() + "-" + marker.getPosition());
				if (pval != null) {
					marker.setMinusLogPvalue(pval.getMinusLogPvalue());
					pvalcnt++;
				}
			}
		}

		AppContext.debug(pvalcnt + " markers with pvalue");
		// listMarkers

		if (AppContext.showBlankAnnotations()) {
			annotations.addAll(mapAnnotcount.keySet());
		} else {
			Iterator<String> itAnnotname = mapAnnotcount.keySet().iterator();
			while (itAnnotname.hasNext()) {
				String annotname = itAnnotname.next();
				Long anotcnt = mapAnnotcount.get(annotname);
				if (anotcnt.longValue() > 0)
					annotations.add(annotname);
			}
		}

		annotations.removeAll(excludeAnnotation);

		AppContext.debug(listMarkers.size() + " markers with annotation");
		AppContext.debug("mapAnnotcount: " + mapAnnotcount);
		// AppContext.debug( listMarkers.toString());
		return listMarkers;
	}

	private List<MarkerAnnotation> getAnnotations(String contig, Collection colPos, Integer plusminus, String genemodel,
			Map<String, Long> mapAnnotcount, Integer maxInteractingGenes, Map<String, PositionLogPvalue> mapPos2Pvalue,
			Set excludeAnnotation) {
		// private List<MarkerAnnotation> getAnnotations( String contig, Collection
		// colPos, Integer plusminus, String genemodel, Map<String, Long> mapAnnotcount,
		// Integer maxInteractingGenes) {

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItemsDAO");
		Map mapCvterm2Loci = new LinkedHashMap();
		Map<String, Map<String, Collection>> mapGenemodel2Interacting2Loci = new LinkedHashMap();
		List listLoci = new ArrayList();
		listLoci.addAll(locusnotesDAO.getLocusByContigPositions(contig, colPos, AppContext.REFERENCE_NIPPONBARE(),
				plusminus, genemodel));
		mapCvterm2Loci.put(MarkerAnnotation.GENE_MODEL, listLoci);

		if (listLoci.size() > 0 &&

				!(excludeAnnotation.contains(MarkerAnnotation.GENE_INT_PRINEXPT)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_INT_PRINPRED)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_INT_RICENETV2)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_INT_RICENETV1)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_QTARO)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_TO)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_PO)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_PO_DEVT)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_GO_BP)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_GO_CC)
						&& excludeAnnotation.contains(MarkerAnnotation.GENE_GO_MF))) {

			Map<String, Set> mapGenemodel2Names = new HashMap();
			Set<String> locallnamesets = new HashSet();
			Iterator<Locus> itLocus = listLoci.iterator();
			while (itLocus.hasNext()) {
				Locus loc = itLocus.next();
				Set locnamesets = new HashSet();
				locnamesets.add(loc.getUniquename().toUpperCase());
				if (loc instanceof MergedLoci) {
					MergedLoci mloc = (MergedLoci) loc;
					/*
					 * if(mloc.getMSU7Name()!=null && !mloc.getMSU7Name().isEmpty())
					 * locnamesets.add(mloc.getMSU7Name().toUpperCase());
					 * if(mloc.getRAPRepName()!=null && !mloc.getRAPRepName().isEmpty())
					 * locnamesets.add(mloc.getRAPRepName().toUpperCase());
					 * if(mloc.getRAPPredName()!=null && !mloc.getRAPPredName().isEmpty())
					 * locnamesets.add(mloc.getRAPPredName().toUpperCase());
					 */
					locallnamesets.addAll(mloc.getOverlappingGenes());
				}
				locallnamesets.addAll(locnamesets);
				mapGenemodel2Names.put(loc.getUniquename(), locnamesets);

			}
			Set locnameset = new HashSet();
			Iterator<String> itLocname = locallnamesets.iterator();
			while (itLocname.hasNext()) {
				String names[] = itLocname.next().split(",");
				for (int iname = 0; iname < names.length; iname++)
					locnameset.add(names[iname].trim());
			}

			Set<Locus> locset = new LinkedHashSet(
					listitemsdao.findGeneFromName(locnameset, AppContext.REFERENCE_NIPPONBARE()));

			// if(AppContext.isIRRILAN()) {
			// if(true) {
			if (!excludeAnnotation.contains(MarkerAnnotation.GENE_INT_PRINEXPT)
					&& !excludeAnnotation.contains(MarkerAnnotation.GENE_INT_PRINPRED)
					&& !excludeAnnotation.contains(MarkerAnnotation.GENE_INT_RICENETV2)
					&& !excludeAnnotation.contains(MarkerAnnotation.GENE_INT_RICENETV1)) {

				Map<String, Locus> mapName2Loci = new HashMap();
				itLocus = locset.iterator();
				while (itLocus.hasNext()) {
					Locus loc = itLocus.next();
					mapName2Loci.put(loc.getUniquename(), loc);
				}

				Map<String, Set> mapGenemodel2Loci = new HashMap();
				itLocname = mapGenemodel2Names.keySet().iterator();
				while (itLocname.hasNext()) {
					String genemodelname = itLocname.next();
					Set<String> genemodelnames = mapGenemodel2Names.get(genemodelname);
					itLocname = genemodelnames.iterator();
					Set<Locus> genemodelgenes = new HashSet();
					while (itLocname.hasNext()) {
						String names[] = itLocname.next().split(",");
						for (int iname = 0; iname < names.length; iname++) {
							Locus nameloci = mapName2Loci.get(names[iname].trim());
							if (nameloci == null) {
								AppContext.debug(names[iname].trim() + " not in map: " + mapName2Loci);
							} else
								genemodelgenes.add(nameloci);
						}
					}

					Map mapInteracting2Loci = mapGenemodel2Interacting2Loci.get(genemodelname);
					if (mapInteracting2Loci == null) {
						mapInteracting2Loci = new HashMap();
						mapGenemodel2Interacting2Loci.put(genemodelname, mapInteracting2Loci);
						AppContext.debug("getting interactions for " + genemodelname + ", " + genemodelgenes);
						if (genemodelgenes.size() > 0) {
							String tops = "";
							if (maxInteractingGenes != null)
								tops = " (TOP " + maxInteractingGenes + ")";
							if (!excludeAnnotation.contains(MarkerAnnotation.GENE_INT_RICENETV2))
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_RICENETV2 + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_RICENETV2, maxInteractingGenes));
							if (AppContext.isOracle()) {
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_RICENETV1 + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_RICENETV1, maxInteractingGenes));
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_PRINEXPT + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_PRINEXPT, maxInteractingGenes));
								mapInteracting2Loci.put(MarkerAnnotation.GENE_INT_PRINPRED + tops,
										getInteractionByLocus(genemodelgenes, AppContext.REFERENCE_NIPPONBARE(),
												FeatureInteractionDAO.INTERACTION_PRINPRED, maxInteractingGenes));
							}
						}
					}
				}

			}

			/*
			 * AppContext.debug("getting genes with names " + locnameset.size() + "  " +
			 * locnameset.toString()); AppContext.debug("genes: " + locset );
			 * AppContext.debug("gene objects = " + locset.size());
			 */
			if (!excludeAnnotation.contains(MarkerAnnotation.GENE_QTARO))
				mapCvterm2Loci.put(MarkerAnnotation.GENE_QTARO,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "qtaro_gene_traits"));

			if (!excludeAnnotation.contains(MarkerAnnotation.GENE_TO))
				mapCvterm2Loci.put(MarkerAnnotation.GENE_TO,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "plant_trait_ontology"));
			if (!excludeAnnotation.contains(MarkerAnnotation.GENE_PO))
				mapCvterm2Loci.put(MarkerAnnotation.GENE_PO,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "plant_anatomy"));
			if (!excludeAnnotation.contains(MarkerAnnotation.GENE_PO_DEVT))
				mapCvterm2Loci.put(MarkerAnnotation.GENE_PO_DEVT,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "plant_structure_development_stage"));
			if (!excludeAnnotation.contains(MarkerAnnotation.GENE_GO_BP)) {
				mapCvterm2Loci.put(MarkerAnnotation.GENE_GO_BP,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "biological_process"));
				mapCvterm2Loci.put(MarkerAnnotation.GENE_GO_MF,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "molecular_function"));
				mapCvterm2Loci.put(MarkerAnnotation.GENE_GO_CC,
						getCvTermByLocus(locset, AppContext.REFERENCE_NIPPONBARE(), "cellular_component"));
			}

		}
		// List listQtlGramene = new ArrayList();
		List listQtlQtaro = new ArrayList();
		listQtlQtaro.addAll(qtldao.getLocusByContigPositions(contig, colPos, AppContext.REFERENCE_NIPPONBARE(), plusminus,
				QtlDAO.QTL_QTARO));
		// listQtlGramene.addAll( qtldao.getLocusByContigPositions(contig, colPos,
		// Organism.REFERENCE_NIPPONBARE, plusminus, QtlDAO.QTL_GRAMENE) );
		mapCvterm2Loci.put(MarkerAnnotation.QTL_QTARO, listQtlQtaro);
		// mapCvterm2Loci.put(MarkerAnnotation.QTL_GRAMENE, listQtlGramene);

		Map<String, Collection> mapPromoterLoci = new LinkedHashMap();
		// if(AppContext.isIRRILAN()) {
		if (true) {
			mapPromoterLoci.put(MarkerAnnotation.PROM_PLANTPROMDB,
					getLocusByPromoter(contig, colPos, LocusPromoterDAO.PROMOTER_PLANTPROMDB_PRED, plusminus));
			mapPromoterLoci.put(MarkerAnnotation.PROM_FGENESH1K,
					getLocusByPromoter(contig, colPos, LocusPromoterDAO.PROMOTER_FGENESH, plusminus));
		}

		Map mapSnpEff = new LinkedHashMap();
		mapSnpEff.put(MarkerAnnotation.MARKER_SNPEFF, snpeffectdao.getSNPsIn(contig, colPos));

		// annotations.addAll( mapCvterm2Loci.keySet());

		Long strchr = Long.valueOf(AppContext.guessChrFromString(contig)); // Long.valueOf(
																			// contig.toLowerCase().replaceAll("chr0","").replaceAll("chr",""));
		// return mergeAnnotations(contig, strchr, colPos, plusminus, listLoci,
		// listQtlQtaro, listQtlGramene, mapCvterm2Loci, annotations);
		return mergeAnnotations(contig, strchr, colPos, plusminus, mapCvterm2Loci, mapAnnotcount,
				mapGenemodel2Interacting2Loci, mapPromoterLoci, mapSnpEff);
	}

}
