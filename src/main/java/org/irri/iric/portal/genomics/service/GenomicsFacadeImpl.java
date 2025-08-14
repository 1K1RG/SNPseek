package org.irri.iric.portal.genomics.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import org.irri.iric.ds.chado.domain.CvTermLocusCount;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.irri.iric.ds.chado.domain.Organism;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.SnpsEffect;
import org.irri.iric.ds.chado.domain.object.VariantSequenceQuery;
import org.irri.iric.ds.chado.domain.object.WebsiteQuery;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.JobsFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GeneOntologyService;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genomics.LocalAlignmentQuery;
import org.irri.iric.portal.genomics.LocalAlignmentService;
import org.irri.iric.portal.genomics.LocusService;
import org.irri.iric.portal.genomics.OntologyService;
import org.irri.iric.portal.genomics.dao.VariantSequenceDAO;
import org.irri.iric.portal.genomics.dao.WebsiteDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Executions;

@Service("GenomicsFacade")
@EnableAsync
public class GenomicsFacadeImpl implements GenomicsFacade {

	@Autowired
	private LocusService locusService;
	@Autowired
	@Qualifier("GeneOntologyService")
	private OntologyService goService;

	@Autowired
	@Qualifier("PATOGenesOntologyService")
	private OntologyService patoService;

	@Autowired
	private VariantSequenceDAO variantsequenceService;

	@Autowired
	private WebsiteDAO websiteService;

	@Autowired
	private LocalAlignmentService localalignmentService;

	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemsdao;

	@Autowired
	@Qualifier("JobsFacade")
	private JobsFacade jobsfacade;

	@Override
	public Locus getLocusByName(String name) throws Exception {
		

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItemsDAO");
		return listitemsdao.findGeneFromName(name, AppContext.getDefaultOrganism());

		/*
		 * locusService = (LocusService)AppContext.checkBean(locusService,
		 * "LocusService"); return locusService.getLocusByName(name);
		 */
	}

	@Override
	public Set<Locus> getLocusByName(Collection<String> names) throws Exception {
		

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItemsDAO");
		return new LinkedHashSet(listitemsdao.findGeneFromName(names, AppContext.getDefaultOrganism()));
		/*
		 * locusService = (LocusService)AppContext.checkBean(locusService,
		 * "LocusService"); return locusService.getLocusByName(names);
		 */
	}

	@Override
	public List<Locus> getLociByDescription(TextSearchOptions description) {
		
		return getLociByDescription(description, AppContext.getDefaultOrganism());
	}

	@Override
	public List<Locus> getLociByDescription(TextSearchOptions description, String organism) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByNotes(description, organism);
	}

	@Override
	public List<Locus> getLociByDescription(TextSearchOptions description, String organism, String genemodel) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByNotes(description, organism, genemodel);
	}

	@Override
	public List<Locus> getLociBySynonym(TextSearchOptions synonym, String organism) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusBySynonyms(synonym, organism);
	}

	@Override
	public List<Locus> getLociBySynonym(TextSearchOptions synonym, String organism, String genemodel) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusBySynonyms(synonym, organism, genemodel);
	}

	@Override
	public List<Locus> getLociByRegion(String contig, Long start, Long end, String organism, String genemodel) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByRegion(contig, start, end, organism, genemodel);
	}

	@Override
	public List<String> getGotermsByOrganism(String cv, String organism) {
		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");
		return listitemsdao.getGOTermsWithLoci(cv, organism);
	}

	@Override
	public List<String> getPatotermsByOrganism(String cv, String organism) {
		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");
		return listitemsdao.getPATOTermsWithLoci(cv, organism);
	}

	@Override
	public List<String> getCVtermAncestors(String cv, String cvterm) {
		
		if (cv.equals("molecular_function") || cv.equals("biological_process") || cv.equals("cellular_component")) {
			goService = (OntologyService) AppContext.checkBean(goService, "GeneOntologyService");
			return goService.getCVtermAncestors(cv, cvterm);
		} else {
			patoService = (OntologyService) AppContext.checkBean(patoService, "PATOntologyService");
			return patoService.getCVtermAncestors(cv, cvterm);

		}
	}

	@Override
	public List<String> getCVtermDescendants(String cv, String cvterm) {
		

		if (cv.equals("molecular_function") || cv.equals("biological_process") || cv.equals("cellular_component")) {
			goService = (OntologyService) AppContext.checkBean(goService, "GeneOntologyService");
			return goService.getCVtermDescendants(cv, cvterm);
		} else {
			patoService = (OntologyService) AppContext.checkBean(patoService, "PATOntologyService");
			return patoService.getCVtermDescendants(cv, cvterm);

		}

	}

	@Override
	public List getContigsByOrganism(String organism) {
		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");
		return listitemsdao.getContigs(organism);
	}

	@Override
	public List getLociByRegion(String contig, Long start, Long end, String organism) {
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByRegion(contig, start, end, organism);
	}

	/*
	 * @Override public List getSublociByRegion(String contig, Long start, Long end,
	 * String organism, String model) { locusService =
	 * (LocusService)AppContext.checkBean(locusService, "LocusService"); return
	 * locusService.getSublocusByRegion( contig, start, end, organism); }
	 */

	@Override
	public List getLociByContigPositions(String contig, Collection posset, String organism, Integer plusminus) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByContigPositions(contig, posset, organism, plusminus);
	}

	@Override
	public List getLociByCvTerm(String goterm, String organism, String cvname, String genemodel) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByCvTerm(goterm, organism, cvname, genemodel);
	}

	@Override
	public List getLociFromAlignment(Collection alignments) throws Exception {
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusFromAlignment(alignments);
	}

	@Override
	public List getLociBySequence(String sequence, String organism, String querytype, String dbtype, double evalue)
			throws Exception {
		return getLociBySequence(sequence, organism, querytype, dbtype, evalue, false, false);
	}

	@Override
	public List getLociBySequence(String sequence, String organism, String querytype, String dbtype, double evalue,
			boolean tophitonly, boolean includesequence) throws Exception {

		localalignmentService = (LocalAlignmentService) AppContext.checkBean(localalignmentService,
				"LocalAlignmentService");
		// String queryseq, String dbname, String querytype
		String dbname = "msu7";

		if (organism.toLowerCase().equals(AppContext.getDefaultOrganism().toLowerCase()))
			dbname = "msu7";
		else if (organism.toLowerCase().equals("ir64-21"))
			dbname = "ir6421v1";
		else if (organism.toLowerCase().equals("93-11"))
			dbname = "9311v1";
		else if (organism.toLowerCase().equals("dj123"))
			dbname = "dj123v1";
		else if (organism.toLowerCase().equals("kasalath"))
			dbname = "kasrapv1";
		else if (organism.toLowerCase().equals("minghui 63"))
			dbname = "mh63v1";
		else if (organism.toLowerCase().equals("zeshan 97"))
			dbname = "zs97v1";
		else if (organism.toLowerCase().equals("n22"))
			dbname = "n22v1";
		else if (organism.toLowerCase().equals("ir8"))
			dbname = "ir8v1";
		else if (organism.toLowerCase().equals("all"))
			dbname = "allosav1";

		if (dbtype.equals("dna"))
			dbname += "dna";
		else if (dbtype.equals("cdna"))
			dbname += "cdna";
		else if (dbtype.equals("cds"))
			dbname += "cds";
		else if (dbtype.equals("pep"))
			dbname += "pep";
		else if (dbtype.equals("upstream3000"))
			dbname += "up3k";

		LocalAlignmentQuery query = new LocalAlignmentQuery(sequence, dbname, querytype);
		query.setEvalue(evalue);
		query.setTophitonly(tophitonly);
		query.setSequence(includesequence);

		AppContext.logQuery(query.toString());

		return localalignmentService.alignWithDB(query);
	}

	@Override
	public List<String> getOrganisms() throws Exception {
		
		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");
		Collection orgs = listitemsdao.getOrganisms();
		Iterator<Organism> itOrgs = orgs.iterator();
		List listNames = new ArrayList();
		while (itOrgs.hasNext()) {
			listNames.add(itOrgs.next().getName());
		}

		return listNames;
	}

	@Override
	public String queryGO(String term) throws Exception {
		goService = (OntologyService) AppContext.checkBean(goService, "GeneOntologyService");
		return goService.queryAccession(term);
	}

	@Override
	public String queryPATO(String term) throws Exception {
		patoService = (OntologyService) AppContext.checkBean(patoService, "PATOntologyService");
		return patoService.queryAccession(term);
	}

	@Override
	public String overRepresentationTest(String organism, Collection genelist, String enrichmentType) throws Exception {
		goService = (GeneOntologyService) AppContext.checkBean(goService, "GeneOntologyService");
		return goService.overRepresentationTest(organism, genelist, enrichmentType);
	}

	@Override
	public List<CvTermLocusCount> getGOTermLociCounts(String organism, Set loci, String cv) throws Exception {
		
		goService = (GeneOntologyService) AppContext.checkBean(goService, "GeneOntologyService");
		return goService.countLociInTerms(organism, loci, cv);
	}

	@Override
	public String createVariantsFasta(VariantSequenceQuery query) throws Exception {

		variantsequenceService = (VariantSequenceDAO) AppContext.checkBean(variantsequenceService,
				"VariantSeuqenceService");
		return variantsequenceService.getFile(query);

	}

	@Override
	// @Async
	// @Async("simpleAsyncTaskExecutor")
	@Async("threadPoolTaskExecutor")
	public Future<String> createVariantsFastaAsync(VariantSequenceQuery query) throws Exception {
		/*
		 * variantsequenceService =
		 * (VariantSequenceDAO)AppContext.checkBean(variantsequenceService,
		 * "VariantSeuqenceService"); String dir =variantsequenceService.getFile(query);
		 * return new AsyncResult<String>(dir);
		 */

		jobsfacade = (JobsFacade) AppContext.checkBean(jobsfacade, "JobsFacade");
		AppContext.resetTimer(
				"createVariantsFastaAsync started using configured executor: " + Thread.currentThread().getName());
		jobsfacade.startSubmitter(query.getSubmitter());
		try {

			variantsequenceService = (VariantSequenceDAO) AppContext.checkBean(variantsequenceService,
					"VariantSeuqenceService");
			String dir = variantsequenceService.getFile(query);

			if (dir != null) {
				AppContext.resetTimer("createVariantsFastaAsync completed");
				jobsfacade.doneSubmitter(query.getSubmitter());
			} else {
				AppContext.resetTimer("createVariantsFastaAsync error");
				jobsfacade.errorSubmitter(query.getSubmitter(), query.getFilename(), "dir==null");
			}
			return new AsyncResult<String>(dir);

			/*
			 * VariantStringData data = _queryVariantStringData(params);
			 * 
			 * if(data!=null) {
			 * AppContext.resetTimer("_queryVariantStringDataAsync completed");
			 * jobsfacade.doneSubmitter(params.getSubmitter()); } else {
			 * AppContext.resetTimer("_queryVariantStringDataAsync error");
			 * jobsfacade.errorSubmitter(params.getSubmitter()); } return new
			 * AsyncResult<VariantStringData>(null);
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			AppContext.resetTimer("createVariantsFastaAsync exception");

			StringBuffer buff = new StringBuffer();
			buff.append(ex.toString() + "\n" + ex.getMessage() + "\n");
			StackTraceElement traces[] = ex.getStackTrace();
			for (int i = 0; i < traces.length; i++) {
				buff.append(traces[i].toString() + "\n");
			}
			jobsfacade.errorSubmitter(query.getSubmitter(), query.getFilename(), buff.toString());

			return new AsyncResult<String>(null);
		}
	}

	@Override
	@Async("simpleAsyncTaskExecutor")
	public Future<List<String>> queryWebsiteAsync(WebsiteQuery query, boolean display) throws Exception {
		AppContext.debug("async wesite query started...");
		List<String> list = queryWebsite(query, display);
		/*
		 * if(list!=null) { ((Button)comp).setHref(list.get(0)); }
		 */
		AppContext.debug("async wesite query done.");
		return new AsyncResult<List<String>>(list);
	}

	@Override
	public List<String> queryWebsite(WebsiteQuery query, boolean display) throws Exception {

		System.out.println("QUERY >>>> " + query.getSite().size());
		websiteService = (WebsiteDAO) AppContext.checkBean(websiteService, "WebsiteDAO");
		List<String> listURLs = websiteService.getURL(query);
		System.out.println("List >>>> " + query);

		
		if (listURLs != null && display) {
			Iterator<String> itUrl = listURLs.iterator();
			int sitei = 0;
			while (itUrl.hasNext()) {
				String url = itUrl.next();
				String windowname = "_" + ((String) query.getSite().get(sitei)).replace(" ", "_").toLowerCase();
				// Executions.getCurrent().sendRedirect(url, windowname);
				Executions.getCurrent().sendRedirect(url, "_blank");
				sitei++;
			}
		}
		return listURLs;
	}

	@Override
	public List<Locus> getLociByContigPositions(String contig, Collection colPos, String org, Integer plusminus,
			String genemodel) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getLocusByContigPositions(contig, colPos, org, plusminus, genemodel);
	}

	@Override
	public Map[] getMarkerGenomicsAnnotsByContigPositions(String chr, Collection listPos, String organism,
			String genemodel) {
		
		if (!organism.equals(AppContext.REFERENCE_NIPPONBARE()) && !organism.equals(AppContext.MH63()))
			throw new RuntimeException("getMarkerGenomicsAnnotsByContigPositions organism=" + organism);
		if (!genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY))
			throw new RuntimeException("getMarkerGenomicsAnnotsByContigPositions genemodel=" + genemodel);
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getMarkerGenomicAnnotsByContigPositions(chr, listPos, 0, genemodel, organism);
	}

	@Override
	public Map<Position, List<Locus>>[] getMarkerGenomicsAnnotsByRegion(String chr, List<SnpsAllvarsPos> listPos,
			Long start, Long stop, String organism, String genemodel) {
		
		if (!organism.equals(AppContext.REFERENCE_NIPPONBARE()) && !organism.equals(AppContext.MH63()))
			throw new RuntimeException("getMarkerGenomicsAnnotsByRegion organism=" + organism);
		if (!genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY))
			throw new RuntimeException("getMarkerGenomicsAnnotsByRegion genemodel=" + genemodel);
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getMarkerGenomicAnnotsByRegion(chr, listPos, start, stop, genemodel, organism);
	}

	/*
	 * @Override public List<MarkerAnnotation>
	 * getMarkerAnnotsByContigPositions(String contig, Collection colPos, String
	 * organism, Integer plusminus, String genemodel, Set annotations, Integer
	 * maxInteractingGenes) {
	 * 
	 * if(!organism.equals(Organism.REFERENCE_NIPPONBARE)) throw new
	 * RuntimeException("getMarkerAnnotsByContigPositions organism=" + organism);
	 * locusService = (LocusService)AppContext.checkBean(locusService,
	 * "LocusService"); return locusService.getMarkerAnnotsByContigPositions(
	 * contig, colPos, plusminus , genemodel, annotations, maxInteractingGenes); }
	 */
	@Override
	public List<MarkerAnnotation> getMarkerAnnotsByContigPositions(String contig, Collection colPos, String organism,
			Integer plusminus, String genemodel, Set annotations, Integer maxInteractingGenes, Set excludeAnnotations) {

		if (!organism.equals(AppContext.REFERENCE_NIPPONBARE()))
			throw new RuntimeException("getMarkerAnnotsByContigPositions organism=" + organism);
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getMarkerAnnotsByContigPositions(contig, colPos, plusminus, genemodel, annotations,
				maxInteractingGenes, excludeAnnotations);
	}

	@Override
	public List<MarkerAnnotation> getMarkerAnnotsByGene(List<MarkerAnnotation> markers, int plusminus) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getMarkerAnnotsByGene(markers, plusminus);
	}

	@Override
	public List<MarkerAnnotation> getMarkerAnnotsByQTL(List<MarkerAnnotation> markers, int plusminus) {
		
		locusService = (LocusService) AppContext.checkBean(locusService, "LocusService");
		return locusService.getMarkerAnnotsByQTL(markers, plusminus);
	}

	/*
	 * @Override public List<MarkerAnnotation> getMarkerAnnotsByRegion(String
	 * contig, Collection colPos, String organism, String genemodel, Integer
	 * plusminus) { locusService = (LocusService)AppContext.checkBean(locusService,
	 * "LocusService"); return locusService.getMarkerAnnotsByContigPositions(
	 * contig, colPos, genemodel, plusminus); }
	 */

	/*
	 * public static String[] MarkerAnnotationTable(MarkerAnnotation annot, Set
	 * annotations) { return MarkerAnnotationTable( annot, annotations, null); }
	 */

	public static String[] MarkerAnnotationTable(MarkerAnnotation annot, Set annotations, String celldelimiter) { // ,
																													// Map<String,PositionLogPvalue>
																													// mapPos2Pval)
																													// {

		int colcnt = 0;
		// String[] row=new String[annotations.size()+1];
		int ancols = annotations.size();
		if (!annotations.contains(MarkerAnnotation.MARKER_POSITION))
			ancols++;
		if (!annotations.contains(MarkerAnnotation.GENE_MODEL))
			ancols++;
		// if(mapPos2Pval!=null) ancols++;
		String[] row = new String[ancols];
		Collection colPos = annot.getAnnotation(MarkerAnnotation.MARKER_POSITION);
		if (colPos == null || colPos.isEmpty()) {
			row[0] = annot.getContigPosition().toString();
		} else {
			StringBuffer buffpos = new StringBuffer();
			Iterator<Position> itPos = new TreeSet(colPos).iterator();
			while (itPos.hasNext()) {
				buffpos.append(itPos.next());
				if (itPos.hasNext())
					buffpos.append(celldelimiter);
			}
			row[0] = buffpos.toString();
		}
		colcnt++;
		// AppContext.debug("annotname=MARKER_POSITION" + ": " + row[0]);

		/*
		 * if(mapPos2Pval!=null) { row[1]= AppContext.decf.format( mapPos2Pval.get(
		 * annot.getChr()+"-"+annot.getPosition() ).getMinusLogPvalue() ); colcnt++; }
		 */

		// if(annot.getGenes()!=null && !annot.getGenes().isEmpty()) {
		if (annot.getAnnotation(MarkerAnnotation.GENE_MODEL) != null
				&& !annot.getAnnotation(MarkerAnnotation.GENE_MODEL).isEmpty()) {
			// Iterator<Locus> itLocus=annot.getGenes().iterator();
			Iterator<Locus> itLocus = annot.getAnnotation(MarkerAnnotation.GENE_MODEL).iterator();
			StringBuffer buff = new StringBuffer();
			while (itLocus.hasNext()) {
				Locus loc = itLocus.next();
				buff.append(
						loc.getUniquename() + " " + loc.getContig() + " [" + loc.getFmin() + "-" + loc.getFmax() + "]");
				if (loc.getDescription() != null && !loc.getDescription().isEmpty()) {
					buff.append(" -" + loc.getDescription());
				}

				if (loc instanceof MergedLoci) {
					MergedLoci ml = (MergedLoci) loc;
					StringBuffer loci = new StringBuffer();
					if (ml.getMSU7Name() != null && !loc.getUniquename().startsWith("LOC_"))
						loci.append(ml.getMSU7Name());
					if (ml.getRAPRepName() != null
							&& !(loc.getUniquename().startsWith("Os0") || loc.getUniquename().startsWith("Os1"))) {
						if (loci.length() > 0)
							loci.append(",");
						loci.append(ml.getRAPRepName());
					}
					if (ml.getRAPPredName() != null
							&& !(loc.getUniquename().startsWith("Os0") || loc.getUniquename().startsWith("Os1"))) {
						if (loci.length() > 0)
							loci.append(",");
						loci.append(ml.getRAPPredName());
					}
					if (ml.getIRICName() != null && !loc.getUniquename().startsWith("OsNippo")) {
						if (loci.length() > 0)
							loci.append(",");
						loci.append(ml.getIRICName());
					}
					if (loci.length() == 0 && ml.getFGeneshName() != null)
						loci.append(ml.getFGeneshName());

					if (loci.length() > 0)
						buff.append(" (" + loci + ")");
				}

				if (itLocus.hasNext())
					buff.append(celldelimiter);
			}

			row[colcnt] = buff.toString();

		} else
			row[colcnt] = "";
		colcnt++;
		// AppContext.debug("annotname=GENE_MODEL" + ": " + row[1]);

		// AppContext.debug(annot.getContig() + " " + annot.getPosition() + " " +
		// annot.getAnnotations());

		Iterator itAnnots = annotations.iterator();
		while (itAnnots.hasNext()) {
			String annotname = (String) itAnnots.next();
			// if(annot.getAnnotation(annotname).size()>0)
			// AppContext.debug(annot.getAnnotation(annotname).size() + " " + annotname + "
			// annotations");
			if (annotname.equals(MarkerAnnotation.GENE_MODEL))
				continue;
			if (annotname.equals(MarkerAnnotation.MARKER_POSITION))
				continue;
			if (annot.getAnnotation(annotname) != null) {

				// AppContext.debug("annotname=" + annotname + ": " +
				// annot.getAnnotation(annotname));
				// if(annotname.equals(MarkerAnnotation.MARKER_SNPEFF)) {
				// Iterator<Position> itLocus= annot.getAnnotation(annotname).iterator();
				// StringBuffer buff=new StringBuffer();
				// while(itLocus.hasNext()) {
				// Position loc= itLocus.next();
				// //buff.append( loc.getAnnotation() );
				// /*
				// buff.append( loc.getUniquename() + " " + loc.getContig() + "[" +
				// loc.getFmin() + "-" + loc.getFmax() + "]" );
				// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
				// buff.append(" - " + loc.getDescription() );
				// }
				// */
				// if(itLocus.hasNext()) buff.append("; ");
				// }
				// row[colcnt]=buff.toString();
				// }
				// else {
				// Iterator<Locus> itLocus= annot.getAnnotation(annotname).iterator();
				// StringBuffer buff=new StringBuffer();
				// while(itLocus.hasNext()) {
				// Locus loc= itLocus.next();
				// buff.append( loc.getUniquename() + " " + loc.getContig() + "[" +
				// loc.getFmin() + "-" + loc.getFmax() + "]" );
				// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
				// buff.append(" - " + loc.getDescription() );
				// }
				// if(itLocus.hasNext()) buff.append("; ");
				// }
				// row[colcnt]=buff.toString();
				// }

				Iterator<Locus> itLocus = annot.getAnnotation(annotname).iterator();
				StringBuffer buff = new StringBuffer();
				while (itLocus.hasNext()) {
					Object marker = itLocus.next();
					if (marker instanceof SnpsEffect) {
						SnpsEffect loc = (SnpsEffect) marker;
						buff.append(loc.getUniquename());
						if (loc.getDescription() != null && !loc.getDescription().isEmpty()) {
							buff.append(" - " + loc.getDescription());
						}
					} else if (marker instanceof Locus) {
						Locus loc = (Locus) marker;
						buff.append(loc.getUniquename() + " " + loc.getContig() + "[" + loc.getFmin() + "-"
								+ loc.getFmax() + "]");
						if (loc.getDescription() != null && !loc.getDescription().isEmpty()) {
							buff.append(" - " + loc.getDescription());
						}
					} else if (marker instanceof Position) {
						Position loc = (Position) marker;
						buff.append(loc.getContig() + "-" + loc.getPosition());
						buff.append(" - " + loc.getClass().getName());
					} else {
						buff.append(marker.toString());
					}
					if (itLocus.hasNext())
						buff.append(celldelimiter);
				}
				row[colcnt] = buff.toString();

				/*
				 * Iterator itLocus= annot.getAnnotation(annotname).iterator(); StringBuffer
				 * buff=new StringBuffer(); while(itLocus.hasNext()) { //Locus loc=
				 * itLocus.next(); Object marker=itLocus.next(); if(marker instanceof
				 * MarkerAnnotationLocusImpl) { MarkerAnnotation loc=(MarkerAnnotation)marker;
				 * buff.append( loc.getUniquename() + " " + loc.getContig() + "[" +
				 * loc.getFmin() + "-" + loc.getFmax() + "]" ); if(loc.getDescription()!=null &&
				 * !loc.getDescription().isEmpty()) { buff.append(" - " + loc.getDescription()
				 * ); } } else if(marker instanceof MarkerAnnotationImpl) { MarkerAnnotationImpl
				 * loc=(MarkerAnnotationImpl)marker; buff.append( loc.getUniquename() + " " +
				 * loc.getContig() + "[" + loc.getFmin() + "-" + loc.getFmax() + "]" );
				 * if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
				 * buff.append(" - " + loc.getDescription() ); } } if(itLocus.hasNext())
				 * buff.append("; "); } row[colcnt]=buff.toString();
				 */

			} else
				row[colcnt] = "";

			// AppContext.debug("annotname=" + annotname + ": " + row[colcnt]);
			colcnt++;

		}

		return row;
	}

	@Override
	public List<Locus> getCDSByRegion(String contig, Long start, Long end, String organism, String genemodel) {
		return null;
	}

	@Override
	public List getCDSByRegion(String contig, Long start, Long end, String organism) {
		return null;
	}

	@Override
	public List<Locus> getCDSByContigPositions(String contig, Collection colPos, String organism, Integer plusminus,
			String genemodel) {
		return null;
	}

	@Override
	public List getCDSByContigPositions(String contig, Collection posset, String organism, Integer plusminus) {
		return null;
	}

	/*
	 * @Override public List<SnpsAllvarsPos> removeNoncoding(Collection colPos,
	 * String genemodel) {
	 * 
	 * }
	 */

}
