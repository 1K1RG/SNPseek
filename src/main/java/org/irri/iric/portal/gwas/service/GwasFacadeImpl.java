package org.irri.iric.portal.gwas.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.irri.iric.ds.chado.dao.GWASRunDAO;
import org.irri.iric.ds.chado.domain.GWASRun;
import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.VarietyPlusPlus;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantTableArray;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
import org.irri.iric.portal.gwas.GwasFacade;
import org.irri.iric.portal.gwas.dao.ManhattanPlotDAO;
import org.irri.iric.portal.gwas.domain.ManhattanPlot;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.variety.service.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("GwasFacade")
@Scope(value = "singleton")
// @Scope(value="singleton")
public class GwasFacadeImpl implements GwasFacade {

	// protected List traits;
	// protected List subpopulations;
	protected Map<String, Set<String>> mapPop2Trait;
	protected Map<String, Set<String>> mapPop2Coterm;
	// protected Map<String,BigDecimal> mapTraitCoterm2CotermId=new HashMap();
	protected Map<String, String> mapTraitCoterm2Coterm = new HashMap();

	private static Set setATGC = new HashSet();

	@Autowired
	protected GenotypeFacade genotype;
	@Autowired
	protected VarietyFacade variety;
	@Autowired
	protected GWASRunDAO gwasrundao;
	@Autowired
	// @Qualifier("ManhattanPlotDAO")
	@Qualifier("ManhattanPlotDAOFlatfileImpl")
	protected ManhattanPlotDAO manhattandao;

	private String dataset = "3k";

	public GwasFacadeImpl() {
		super();
		setATGC.add("A");
		setATGC.add("T");
		setATGC.add("G");
		setATGC.add("C");
		AppContext.debug("GwasFacadeImpl() loaded");

	}

	@Override
	public String getLegacyTraitname(String coterm) {
		for (String leg : mapTraitCoterm2Coterm.keySet()) {
			if (mapTraitCoterm2Coterm.get(leg).equals(coterm))
				return leg;
		}
		return null;

	}

	@Override
	public Map getMapPop2Trait() {
		if (mapPop2Trait == null) {
			mapPop2Trait = new TreeMap();
			mapPop2Coterm = new TreeMap();

			// TODO Auto-generated constructor stub
			gwasrundao = (GWASRunDAO) AppContext.checkBean(gwasrundao, "GWASRunDAO");
			// manhattandao=(ManhattanPlotDAO)AppContext.checkBean(manhattandao,"ManhattanPlotDAO");
			manhattandao = (ManhattanPlotDAO) AppContext.checkBean(manhattandao, "ManhattanPlotDAOFlatfileImpl");
			// Set setsub=new TreeSet();
			// Set settrait=new TreeSet();
			
			Iterator<GWASRun> itruns = gwasrundao.getGWASRuns().iterator();
			while (itruns.hasNext()) {
				GWASRun run = itruns.next();
				// setsub.add(run.getSubpopulation());
				// settrait.add(run.getTrait());
				Set setTrait = mapPop2Trait.get(run.getSubpopulation());
				Set setCoterm = mapPop2Coterm.get(run.getSubpopulation());
				if (setTrait == null) {
					setTrait = new TreeSet();
					mapPop2Trait.put(run.getSubpopulation(), setTrait);
					setCoterm = new TreeSet();
					mapPop2Coterm.put(run.getSubpopulation(), setCoterm);
				}
				setTrait.add(run.getTrait());
				setCoterm.add(run.getCoterm());

				// mapTraitCoterm2CotermId.put(run.getTrait(),run.getCotermId());
				// mapTraitCoterm2CotermId.put(run.getCoterm(),run.getCotermId());
				mapTraitCoterm2Coterm.put(run.getCoterm(), run.getCoterm());
				mapTraitCoterm2Coterm.put(run.getTrait(), run.getCoterm());

			}
			/*
			 * subpopulations=new ArrayList(); traits=new ArrayList();
			 * traits.addAll(settrait); subpopulations.addAll(setsub);
			 */
			AppContext.debug("GwasFacadeImpl " + mapPop2Trait.size() + " subpopulations");
		}
		return mapPop2Trait;
	}

	@Override
	public Map<MultiReferencePosition, Double> getPos2MinusLogP(String trait, String subpop) {
		return null;
	}

	private String getDataset() {
		return VarietyFacade.DATASET_SNPINDELV2_IUPAC;

	}

	@Override
	public Map<String, Double> getPosstr2MinusLogP(String trait, String subpop) {
		
		return getPosstr2MinusLogP(trait, subpop, null, "all");
	}

	private GWASRun getGwasRun(String trait, String subpop) {
		Iterator<GWASRun> itruns = gwasrundao.getGWASRuns().iterator();
		GWASRun therun = null;
		while (itruns.hasNext()) {
			GWASRun run = itruns.next();
			if ((run.getCoterm().equals(trait) || trait.equals(run.getTrait()))
					&& subpop.equals(run.getSubpopulation())) {
				therun = run;
				break;
			}
		}
		return therun;
	}

	@Override
	public Map<String, Double> getPosstr2MinusLogP(String trait, String subpop, Double minlogP, String region) {
		
		GWASRun therun = getGwasRun(trait, subpop);
		if (therun == null)
			return null;

		Map mapPos2P = new LinkedHashMap();
		Iterator<ManhattanPlot> itPlots = manhattandao.getMinusPValues(therun, minlogP, region).iterator();
		while (itPlots.hasNext()) {
			ManhattanPlot plot = itPlots.next();
			mapPos2P.put(plot.getChr() + "-" + plot.getPosition(), plot.getMinusLogPvalue());
		}
		return mapPos2P;
	}

	@Override
	public String getQQPlotFile(String trait, String subpop) {
		
		GWASRun therun = getGwasRun(trait, subpop);
		if (therun == null)
			return null;
		// return AppContext.getFlatfilesDir()+"gwas/qq_" + therun.getGwasRunId() +
		// ".png";
		// return AppContext.getHostname() + "/iric-portal-static/qq_" +
		// therun.getGwasRunId() + ".png";
		return AppContext.getHostname() + "/static/qq_" + therun.getGwasRunId() + ".png";
	}

	@Override
	public List<String> getTraits(String pop, boolean useCOTrait) {
		
		if (mapPop2Trait == null || mapPop2Coterm==null)
			getMapPop2Trait();
		List l = new ArrayList();
		if (useCOTrait) {
			AppContext.debug("getTraits: pop=" + pop  + " mapPop2Coterm=" + mapPop2Coterm.get(pop) );
			l.addAll(mapPop2Coterm.get(pop));
		}
		else {
			AppContext.debug("getTraits: pop=" + pop  + " mapPop2Trait=" + mapPop2Trait.get(pop) );
			l.addAll(mapPop2Trait.get(pop));
		}

		return l;
	}

	@Override
	public List<String> getSubpopulations() {
		
		if (mapPop2Trait == null)
			getMapPop2Trait();
		List l = new ArrayList();
		l.addAll(mapPop2Trait.keySet());
		return l;
	}

	private Map[] getMapsGenotypeVarietycount(VariantTableArray varianttable) {
		Map<Long, String> mapVarid2Genotype = new HashMap();
		Map<String, Integer> mapGenotype2Count = new TreeMap();

		// varianttable.getPosition()

		for (int ivar = 0; ivar < varianttable.getVarid().length; ivar++) {
			// Object allele=varianttable.getVaralleles()[ivar][0];
			StringBuffer buffallele = new StringBuffer();
			String varsnpstring[] = (String[]) varianttable.getVaralleles()[ivar];
			for (int allelelen = 0; allelelen < varianttable.getVaralleles()[ivar].length; allelelen++) {
				String allelei = (String) varsnpstring[allelelen]; // .toString();
				if (allelei.isEmpty())
					buffallele.append('?');
				else
					buffallele.append(AppContext.getIUPAC(allelei));
			}
			String allele = buffallele.toString();

			mapVarid2Genotype.put(varianttable.getVarid()[ivar], allele);
			Integer cnt = mapGenotype2Count.get(allele);
			if (cnt == null) {
				cnt = Integer.valueOf(0);
			}
			mapGenotype2Count.put(allele, cnt + 1);
		}

		return new Map[] { mapVarid2Genotype, mapGenotype2Count };
	}

	@Override
	public Map[] createAlleleDistributions(String sChr, List poslist, String sPhenotype, Collection<Variety> varlist,
			boolean normalize) {
		return createAlleleDistributions(sChr, poslist, sPhenotype, varlist, normalize, null, null, poslist.size());
	}

	private Map[] createAlleleDistributions(String sChr, List poslist, String sPhenotype, Collection<Variety> varlist,
			boolean normalize, Map<Long, Object> mapVarid2GenotypeInit, Map<BigDecimal, Object> mapVarid2Phenotype,
			int nfeatures) {

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		try {

			boolean phenIsNumber = true;

			Map mapVarid2Genotype = new HashMap();

			if (mapVarid2GenotypeInit == null || mapVarid2Phenotype == null || mapVarid2GenotypeInit.isEmpty()
					|| mapVarid2Phenotype.isEmpty() || nfeatures == poslist.size()) {

				// GenotypeQueryParams params= new GenotypeQueryParams(null, sChr, null, null,
				// true,false, GenotypeQueryParams.SNP_ALL,
				Set sSnp = new HashSet();
				Set sVariety = new HashSet();
				Set sRun = new HashSet();
				sSnp.add("3kall");
				sVariety.add("3k");

				sRun.addAll(genotype.getGenotyperuns(sVariety, sSnp, "SNP"));
				// sRun.add("3kall");

				GenotypeQueryParams params = new GenotypeQueryParams(null, sChr, null, null, true, false, sSnp,
						sVariety, sRun, false, poslist, null, null, false, false, null);

				if (sPhenotype != null) {
					params.setPhenotype(mapTraitCoterm2Coterm.get(sPhenotype));
				}
				// params.addDataset(VarietyFacade.DATASET_SNPINDELV2_IUPAC);

				/*
				 * if(sPhenotype.startsWith("CO_320")) {
				 * 
				 * } else { // get legacy to cvterm mapping }
				 */

				VariantStringData data = genotype.queryGenotype(params);
				VariantTableArray varianttable = new VariantAlignmentTableArraysImpl();
				varianttable = (VariantTableArray) genotype.fillGenotypeTable(varianttable, data, params);

				int permutations = 2 ^ nfeatures;

				if (mapVarid2GenotypeInit == null)
					mapVarid2GenotypeInit = new HashMap();
				if (mapVarid2Phenotype == null)
					mapVarid2Phenotype = new HashMap();

				Map[] mapgenvarcount = getMapsGenotypeVarietycount(varianttable);
				Map<Object, Integer> mapGenotype2Count = mapgenvarcount[1];
				mapVarid2GenotypeInit.putAll(mapgenvarcount[0]);
				mapVarid2Genotype.putAll(mapgenvarcount[0]);
				mapVarid2Phenotype.clear();

				// mapVarid2Phenotype=null;
				if (params.getPhenotype() != null && !params.getPhenotype().isEmpty()) {
					sPhenotype = params.getPhenotype();
					mapVarid2Phenotype.putAll(variety.getPhenotypeValues(sPhenotype, dataset));
				}

				/*
				 * if( !this.listboxVarietylist.getSelectedItem().getLabel().isEmpty() ) {
				 * mapVarid2Phenotype=new HashMap(); String
				 * varlistname=listboxVarietylist.getSelectedItem().getLabel().trim();
				 * Iterator<Variety> itvarlist=workspace.getVarieties(varlistname).iterator();
				 * while(itvarlist.hasNext()) { VarietyPlusPlus vp =
				 * (VarietyPlusPlus)itvarlist.next(); mapVarid2Phenotype.put( vp.getVarietyId()
				 * , vp.getValue()); } }
				 */
				if (varlist != null) {
					mapVarid2Phenotype = new HashMap();
					Iterator<Variety> itvarlist = varlist.iterator();
					while (itvarlist.hasNext()) {
						VarietyPlusPlus vp = (VarietyPlusPlus) itvarlist.next();
						mapVarid2Phenotype.put(vp.getVarietyId(), vp.getValue());
					}
				}

			} else {
				// new set of varid2genotype
				List randidx = selectRandomIndex(2);
				Map<String, Collection> gen2var = new HashMap();
				Iterator itVarid = mapVarid2GenotypeInit.keySet().iterator();
				while (itVarid.hasNext()) {
					Long varid = (Long) itVarid.next();
					String genotype = (String) mapVarid2GenotypeInit.get(varid);
					Collection colVars = gen2var.get(genotype);
					if (colVars == null) {
						colVars = new HashSet();
						gen2var.put(genotype, colVars);
					}
					colVars.add(varid);
				}

				int newidx[] = new int[randidx.size()];
				Iterator itIdx = randidx.iterator();
				int i = 0;
				List newposlist = new ArrayList();

				while (itIdx.hasNext()) {
					newidx[i] = (Integer) itIdx.next();
					newposlist.add(poslist.get(newidx[i]));
					i++;
				}
				poslist.clear();
				poslist.add(newposlist);

				Map<String, Collection> newgeno2vars = new HashMap();
				Iterator itgenotype = gen2var.keySet().iterator();
				while (itgenotype.hasNext()) {
					String geno = (String) itgenotype.next();
					StringBuffer bufnewgeno = new StringBuffer();
					for (i = 0; i < newidx.length; i++) {
						bufnewgeno.append(geno.charAt(newidx[i]));
					}
					String newgeno = bufnewgeno.toString();
					Collection colVars = newgeno2vars.get(newgeno);
					if (colVars == null) {
						colVars = new HashSet();
						newgeno2vars.put(newgeno, colVars);
					}
					colVars.addAll(gen2var.get(geno));
				}

				mapVarid2Genotype.clear();
				mapVarid2Genotype.putAll(newgeno2vars);

			}

			Map<Object, Map<Object, Collection>> mapGenotype2Phenotype2Count = null;
			Map<Object, Map<Object, Object>> mapAllele2Phenotype2Count = null;
			Map<Object, Map<Object, Collection>> mapAllele2Phenotype2Set = new HashMap();

			mapAllele2Phenotype2Count = new HashMap();
			mapGenotype2Phenotype2Count = new HashMap();

			Map<Object, Integer> mapAllele2AllPhenotypeCount = null;
			Map<Object, Integer> mapGenotype2AllPhenotypeCount = null;

			if (normalize) {
				mapAllele2AllPhenotypeCount = new HashMap();
				mapGenotype2AllPhenotypeCount = new HashMap();
			}

			Iterator<BigDecimal> itVar = mapVarid2Phenotype.keySet().iterator();
			Map<Object, Integer> mapPhen2Count = new TreeMap();
			double maxphen = 0;
			double minphen = Double.MAX_VALUE;
			StringBuffer ballele = new StringBuffer();

			while (itVar.hasNext()) {
				BigDecimal varid = itVar.next();
				Object phen = mapVarid2Phenotype.get(varid);

				// AppContext.debug(phen.getClass().toString() + " " + phen);
				if (phen instanceof String) {
					phenIsNumber = false;
				} else {
					double phenval = ((BigDecimal) phen).doubleValue();
					if (phenval > maxphen)
						maxphen = phenval;
					if (phenval < minphen)
						minphen = phenval;
				}

				Object vargenotype = mapVarid2Genotype.get(varid.longValue());
				Map<Object, Collection> mapphencnt = mapGenotype2Phenotype2Count.get(vargenotype);
				if (mapphencnt == null) {
					mapphencnt = new HashMap();
					mapGenotype2Phenotype2Count.put(vargenotype, mapphencnt);
				}

				if (normalize) {
					Integer genallphencount = mapGenotype2AllPhenotypeCount.get(vargenotype);
					if (genallphencount == null)
						genallphencount = Integer.valueOf(0);
					mapGenotype2AllPhenotypeCount.put(vargenotype, genallphencount + 1);
				}

				Collection phencnt = mapphencnt.get(phen);
				if (phencnt == null) {
					// phencnt=Integer.valueOf(0);
					phencnt = new HashSet();
					mapphencnt.put(phen, phencnt);
				}
				// mapphencnt.put( phen, phencnt+1 );
				phencnt.add(varid);
				mapGenotype2Phenotype2Count.put(vargenotype, mapphencnt);

				// if(params.getPoslist()!=null && params.getPoslist().size()==1) {
				if (poslist != null && poslist.size() == 1) {

					ballele.append(vargenotype).append(" ");
					// if(vargenotype.toString().contains("/")) {
					String strgenotype = vargenotype.toString();
					if (!setATGC.contains(strgenotype) || strgenotype.contains("/")) {

						String alleles[] = null;
						if (strgenotype.contains("/"))
							alleles = strgenotype.split("\\/");
						else {
							String nucs = AppContext.getNucsFromIUPAC(strgenotype);
							if (nucs.length() == 2) {
								alleles = new String[] { nucs.substring(0, 1), nucs.substring(1, 2) };
							} else
								alleles = new String[] { nucs, nucs };
						}

						Map<Object, Object> mapallelecnt = mapAllele2Phenotype2Count.get(alleles[0]);
						Map<Object, Collection> mapalleleset = mapAllele2Phenotype2Set.get(alleles[0]);
						if (mapallelecnt == null) {
							mapallelecnt = new HashMap();
							mapAllele2Phenotype2Count.put(alleles[0], mapallelecnt);
							mapalleleset = new HashMap();
							mapAllele2Phenotype2Set.put(alleles[0], mapalleleset);
						}

						Object phenallelecnt = mapallelecnt.get(phen);
						Collection phenalleleset = mapalleleset.get(phen);
						if (phenallelecnt == null) {
							phenallelecnt = Integer.valueOf(0);
						}
						if (phenalleleset == null) {
							phenalleleset = new HashSet();
							mapalleleset.put(phen, phenalleleset);
						}
						mapallelecnt.put(phen, phenallelecnt);
						phenalleleset.add(varid);
						mapAllele2Phenotype2Count.put(alleles[0], mapallelecnt);

						mapallelecnt = mapAllele2Phenotype2Count.get(alleles[1]);
						mapalleleset = mapAllele2Phenotype2Set.get(alleles[1]);
						if (mapallelecnt == null) {
							mapallelecnt = new HashMap();
							mapAllele2Phenotype2Count.put(alleles[1], mapallelecnt);
							mapalleleset = new HashMap();
							mapAllele2Phenotype2Set.put(alleles[1], mapalleleset);
						}
						phenallelecnt = mapallelecnt.get(phen);
						phenalleleset = mapalleleset.get(phen);
						if (phenallelecnt == null) {
							phenallelecnt = Integer.valueOf(0);
						}
						if (phenalleleset == null) {
							phenalleleset = new HashSet();
							mapalleleset.put(phen, phenalleleset);
						}
						phenalleleset.add(varid);
						mapallelecnt.put(phen, phenallelecnt);
						mapAllele2Phenotype2Count.put(alleles[1], mapallelecnt);

						if (normalize) {
							Integer alleleallphencnt = mapAllele2AllPhenotypeCount.get(alleles[0]);
							if (alleleallphencnt == null)
								alleleallphencnt = Integer.valueOf(0);
							mapAllele2AllPhenotypeCount.put(alleles[0], alleleallphencnt + 1);
							alleleallphencnt = mapAllele2AllPhenotypeCount.get(alleles[1]);
							if (alleleallphencnt == null)
								alleleallphencnt = Integer.valueOf(0);
							mapAllele2AllPhenotypeCount.put(alleles[1], alleleallphencnt + 1);
						}

					} else { // if(!vargenotype.toString().isEmpty()){
						Map<Object, Object> mapallelecnt = mapAllele2Phenotype2Count.get(vargenotype);
						Map<Object, Collection> mapalleleset = mapAllele2Phenotype2Set.get(vargenotype);
						if (mapallelecnt == null) {
							mapallelecnt = new HashMap();
							mapAllele2Phenotype2Count.put(vargenotype, mapallelecnt);
							mapalleleset = new HashMap();
							mapAllele2Phenotype2Set.put(vargenotype, mapalleleset);
						}
						Object phenallelecnt = mapallelecnt.get(phen);
						Collection phenalleleset = mapalleleset.get(phen);
						if (phenallelecnt == null) {
							phenallelecnt = Integer.valueOf(0);
						}
						if (phenalleleset == null) {
							phenalleleset = new HashSet();
							mapalleleset.put(phen, phenalleleset);
						}
						phenalleleset.add(varid);
						mapallelecnt.put(phen, Integer.valueOf((Integer) phenallelecnt + 2));
						mapAllele2Phenotype2Count.put(vargenotype, mapallelecnt);

						if (normalize) {
							Integer alleleallphencnt = mapAllele2AllPhenotypeCount.get(vargenotype);
							if (alleleallphencnt == null)
								alleleallphencnt = Integer.valueOf(0);
							mapAllele2AllPhenotypeCount.put(vargenotype, alleleallphencnt + 2);
						}

					}
				}

			}

			AppContext.debug("alleles[]=" + ballele);

			Map mapAllele2Phenotype2Frequency = null;
			Map mapGenotype2Phenotype2Frequency = null;
			if (normalize) {
				mapAllele2Phenotype2Frequency = normalizeMap(mapAllele2Phenotype2Count, mapAllele2AllPhenotypeCount);
				mapGenotype2Phenotype2Frequency = normalizeMap(mapGenotype2Phenotype2Count,
						mapGenotype2AllPhenotypeCount);
			}

			return new Map[] { mapAllele2Phenotype2Count, mapGenotype2Phenotype2Count, mapAllele2Phenotype2Frequency,
					mapGenotype2Phenotype2Frequency, mapVarid2Phenotype, mapVarid2Genotype, mapAllele2Phenotype2Set };

		} catch (Exception ex) {
			ex.printStackTrace();
			// Messagebox.show(ex.getMessage());
		}

		return null;
	}

	// private Map normalizeMap(Map<Object,Map> mapCount, Map<Object,Integer>
	// mapTotal) {
	private Map normalizeMap(Map mapCount, Map mapTotal) {

		Map mapFrequency = new HashMap();
		Iterator it = mapCount.keySet().iterator();
		while (it.hasNext()) {
			Object allele = it.next();
			Map mapphen2cnt = (Map) mapCount.get(allele);
			Object objtotal = mapTotal.get(allele);
			int total = 0;
			if (objtotal instanceof Number)
				total = ((Number) objtotal).intValue();
			else if (objtotal instanceof Collection)
				total = ((Collection) objtotal).size();

			Map<Object, Double> mapphenfreq = new HashMap();
			mapFrequency.put(allele, mapphenfreq);
			Iterator itPhen = mapphen2cnt.keySet().iterator();
			while (itPhen.hasNext()) {
				Object phen = itPhen.next();
				Object objcnt = mapphen2cnt.get(phen);
				int cnt = 0;
				if (objcnt instanceof Number)
					cnt = ((Number) objcnt).intValue();
				else if (objcnt instanceof Collection)
					cnt = ((Collection) objcnt).size();
				mapphenfreq.put(phen, cnt * 1.0 / total);
			}
		}

		return mapFrequency;
	}

	@Override
	// public Map[] createSubpopDistributions (String sChr, Collection poslist,
	// String sPhenotype, Collection<Variety> varlist, boolean normalize) {
	public Map[] createSubpopDistributions(String sPhenotype, Collection<Variety> varlist, boolean normalize) {

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		Map<BigDecimal, String> mapVarid2Subpop = new HashMap();
		Map<BigDecimal, Object> mapVarid2Phenotype = null;
		boolean phenIsNumber = true;

		if (varlist != null) {
			mapVarid2Phenotype = new HashMap();
			Iterator<Variety> itvarlist = varlist.iterator();
			while (itvarlist.hasNext()) {
				VarietyPlusPlus vp = (VarietyPlusPlus) itvarlist.next();
				mapVarid2Phenotype.put(vp.getVarietyId(), vp.getValue());
			}
		} else {
			mapVarid2Phenotype = variety.getPhenotypeValues(sPhenotype, dataset);
		}

		Iterator<Variety> itVarid = variety.getMapId2Variety(getDataset()).values().iterator();
		while (itVarid.hasNext()) {
			Variety var = itVarid.next();
			mapVarid2Subpop.put(var.getVarietyId(), var.getSubpopulation());
		}

		Map<Object, Map<Object, Collection>> mapSubpopGen2Phenotype2Count = new HashMap();
		Map<Object, Map<Object, Collection>> mapSubpop2Phenotype2Count = new HashMap();

		Map mapSubpop2AllPhenotypeCount = null;
		Map mapSubpopGen2AllPhenotypeCount = null;
		if (normalize) {
			mapSubpop2AllPhenotypeCount = new HashMap();
			mapSubpopGen2AllPhenotypeCount = new HashMap();
		}

		Iterator<BigDecimal> itVar = mapVarid2Phenotype.keySet().iterator();
		Map<Object, Integer> mapPhen2Count = new TreeMap();
		double maxphen = 0;
		double minphen = Double.MAX_VALUE;
		while (itVar.hasNext()) {
			BigDecimal varid = itVar.next();
			Object phen = mapVarid2Phenotype.get(varid);

			// AppContext.debug(phen.getClass().toString() + " " + phen);
			if (phen instanceof String) {
				phenIsNumber = false;
			} else {
				double phenval = ((BigDecimal) phen).doubleValue();
				if (phenval > maxphen)
					maxphen = phenval;
				if (phenval < minphen)
					minphen = phenval;
			}

			String subpop = mapVarid2Subpop.get(varid);
			Map mapphencnt = mapSubpop2Phenotype2Count.get(subpop);
			if (mapphencnt == null) {
				mapphencnt = new HashMap();
				mapSubpop2Phenotype2Count.put(subpop, mapphencnt);
			}
			Set phencnt = (Set) mapphencnt.get(phen);
			if (phencnt == null) {
				// phencnt=Integer.valueOf(0);
				phencnt = new HashSet();
				mapphencnt.put(phen, phencnt);
			}
			// mapphencnt.put( phen, phencnt+1 );
			phencnt.add(varid);
			mapSubpop2Phenotype2Count.put(subpop, mapphencnt);

			if (normalize) {
				Set setallphencnt = (Set) mapSubpop2AllPhenotypeCount.get(subpop);
				if (setallphencnt == null) {
					setallphencnt = new HashSet();
					mapSubpop2AllPhenotypeCount.put(subpop, setallphencnt);
				}
				setallphencnt.add(varid);
			}

			String gensubpop = Data.getGeneralSubpopulation(subpop);
			if (gensubpop != null) {
				mapphencnt = mapSubpopGen2Phenotype2Count.get(gensubpop);
				if (mapphencnt == null) {
					mapphencnt = new HashMap();
					mapSubpopGen2Phenotype2Count.put(gensubpop, mapphencnt);
				}
				phencnt = (Set) mapphencnt.get(phen);
				if (phencnt == null) {
					// phencnt=Integer.valueOf(0);
					phencnt = new HashSet();
					mapphencnt.put(phen, phencnt);
				}
				// mapphencnt.put( phen, phencnt+1 );
				phencnt.add(varid);
				mapSubpopGen2Phenotype2Count.put(gensubpop, mapphencnt);
			}

			if (normalize) {
				Set setallphencnt = (Set) mapSubpopGen2AllPhenotypeCount.get(gensubpop);
				if (setallphencnt == null) {
					setallphencnt = new HashSet();
					mapSubpopGen2AllPhenotypeCount.put(gensubpop, setallphencnt);
				}
				setallphencnt.add(varid);
			}

			gensubpop = "all varieties";
			mapphencnt = mapSubpopGen2Phenotype2Count.get(gensubpop);
			if (mapphencnt == null) {
				mapphencnt = new HashMap();
				mapSubpopGen2Phenotype2Count.put(gensubpop, mapphencnt);
			}
			phencnt = (Set) mapphencnt.get(phen);
			if (phencnt == null) {
				// phencnt=Integer.valueOf(0);
				phencnt = new HashSet();
				mapphencnt.put(phen, phencnt);
			}
			// mapphencnt.put( phen, phencnt+1 );
			phencnt.add(varid);
			mapSubpopGen2Phenotype2Count.put(gensubpop, mapphencnt);

			if (normalize) {
				Set setallphencnt = (Set) mapSubpopGen2AllPhenotypeCount.get(gensubpop);
				if (setallphencnt == null) {
					setallphencnt = new HashSet();
					mapSubpopGen2AllPhenotypeCount.put(gensubpop, setallphencnt);
				}
				setallphencnt.add(varid);
			}
		}

		Map mapSubpop2Phenotype2Frequency = null;
		Map mapSubpopGen2Phenotype2Frequency = null;
		if (normalize) {
			mapSubpop2Phenotype2Frequency = normalizeMap(mapSubpop2Phenotype2Count, mapSubpop2AllPhenotypeCount);
			mapSubpopGen2Phenotype2Frequency = normalizeMap(mapSubpopGen2Phenotype2Count,
					mapSubpopGen2AllPhenotypeCount);
		}

		return new Map[] { mapSubpop2Phenotype2Count, mapSubpopGen2Phenotype2Count, mapSubpop2Phenotype2Frequency,
				mapSubpopGen2Phenotype2Frequency, mapVarid2Phenotype };
	}

	private List selectRandomIndex(int npos) {

		Set posSelset = new TreeSet();
		// Random rand = new Random(831);
		Random rand = new Random();
		while (posSelset.size() != npos) {
			posSelset.add(rand.nextInt(npos));
		}

		List randList = new ArrayList();
		randList.addAll(posSelset);
		return randList;
	}

}
