package org.irri.iric.portal.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import org.irri.iric.ds.chado.dao.CvTermByDatasetDAO;
import org.irri.iric.ds.chado.dao.CvTermDAO;
import org.irri.iric.ds.chado.dao.GeneDAO;
import org.irri.iric.ds.chado.dao.GenotypeRunPlatformDAO;
import org.irri.iric.ds.chado.dao.ScaffoldDAO;
import org.irri.iric.ds.chado.dao.SnpsNonsynAllvarsDAO;
import org.irri.iric.ds.chado.dao.StockSampleDAO;
import org.irri.iric.ds.chado.dao.VarietyDAO;
import org.irri.iric.ds.chado.dao.access.OrganismDAO;
import org.irri.iric.ds.chado.domain.CvTerm;
import org.irri.iric.ds.chado.domain.CvTermDataset;
import org.irri.iric.ds.chado.domain.Gene;
import org.irri.iric.ds.chado.domain.Scaffold;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.model.Cv;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.variety.VarietyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListItemsDAO
 * 
 * @author LMansueto
 *
 */
// @Service(value={"ListItemsDAO","ListItemsDAOPostges"})
// @Service("OrigListItems")
// @Service("OrigListItems")
@Service("ListItems")
@Scope(value = "singleton")
public class ListItemsDAOAllImpl implements ListItemsDAO {

	
	@Autowired
	private SnpsNonsynAllvarsDAO nonsyndao;

	@Autowired
	private CvDAO cvDAO;

	@Autowired
	private GeneDAO geneDAO;

	@Autowired
	@Qualifier("VarietyDAO")
	private VarietyDAO germdao;

	@Autowired
	private StockSampleDAO sampledao;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("VarietyHdraDAO") private VarietyDAO hdradao;
	 * 
	 * @Autowired
	 * 
	 * @Qualifier("VarietyGQ92DAO") private VarietyDAO gq92dao;
	 * 
	 * @Autowired
	 * 
	 * @Qualifier("VarietyWissuwaDAO") private VarietyDAO wissuwadao;
	 */

	/*
	 * @Autowired
	 * 
	 * @Qualifier("IricStockDAO") //@Qualifier("VIricstockBoxcodeDAO") private
	 * VarietyDAO iricstockdao;
	 */

	// @Autowired
	// @Qualifier("VCvPassportDAOPostges")
	private CvTermByDatasetDAO cvtermsPassportdao;

	// @Autowired
	// @Qualifier("VCvPhenotypeDAOPostges")
	private CvTermByDatasetDAO cvtermsPhenotypedao;

	private CvTermByDatasetDAO cvtermsPtocodao;

	@Autowired
	// @Qualifier("ScaffoldDAO")
	private ScaffoldDAO scaffolddao;

	@Autowired
	// @Qualifier("OrganismDAO")
	private OrganismDAO organismdao;

	// @Autowired
	// @Qualifier("VCvtermDbxrefDAO")
	// private CvTermDAO cvtermlocusdao;

	// @Autowired
	// private VLocusCvtermDAO cvtermlocusdao;

	// @Autowired
	// @Qualifier("VGoOrganismDAOPostges")
	private CvTermDAO gotermorganismdao;
	// private VGoOrganismDAO gotermorganismdao;

	// @Autowired
	// @Qualifier("VPatoOrganismDAOPostges")
	private CvTermDAO patotermorganismdao;

	@Autowired
	@Qualifier("GenotypeRunPlatformDAO")
	private GenotypeRunPlatformDAO genotyperundao;

	private Map<String, LinkedHashMap<String, Long>> mapOrganismScaffolds = new HashMap();
	private Map<String, List> mapCVOrg2Cvterms = new HashMap();

	// private java.util.List<String> genenames;
	private Map<String, Collection> mapOrg2Loci = new HashMap();

	/*
	 * private java.util.List germnames; private java.util.List germaccessions;
	 * private java.util.List countries; private java.util.List subpopulations;
	 * private java.util.List irisid;
	 */

	private Map<String, List> mapDataset2germnames = new HashMap();
	private Map<String, List> mapDataset2germaccessions = new HashMap();
	private Map<String, List> mapDataset2countries = new HashMap();
	private Map<String, List> mapDataset2subpopulations = new HashMap();
	private Map<String, List> mapDataset2irisid = new HashMap();

	private Map<String, Map<String, Variety>> mapDataset2Varname2Variety = new HashMap();
	private Map<String, Map<BigDecimal, Variety>> mapDataset2Id2Variety = new HashMap();
	private Map<String, Map<String, StockSample>> mapDataset2Assay2Sample = new HashMap();
	private Map<String, Map<BigDecimal, StockSample>> mapDataset2Id2Sample = new HashMap();

	private Map<String, Map<String, BigDecimal>> mapDataset2passportDefinitions = new HashMap();
	private Map<String, Map<String, BigDecimal>> mapDataset2phenotypeLegacyDefinitions = new HashMap();
	private Map<String, Map<String, BigDecimal>> mapDataset2phenotypeCOTermDefinitions = new HashMap();
	private Map<String, Map<String, BigDecimal>> mapDataset2phenotypeCOTermPhenIdDefinitions = new HashMap();

	private Map<String, Map<String, BigDecimal>> mapDataset2ptocoDefinitions = new HashMap();
	private Map<String, Boolean> mapDataset2Hasnonsyn = new HashMap();

	// private java.util.Map<String,Variety> mapVarname2Variety;
	// private Map<BigDecimal, Variety> mapId2Variety;

	private Map<String, BigDecimal> passportDefinitions;
	private Map<String, BigDecimal> phenotypeDefinitions;
	private Map<String, BigDecimal> ptocoDefinitions;

	private Map<String, Organism> mapOrgname2Org;

	/*
	 * private Map<String,BigDecimal> passportDefinitionsHdra; private
	 * Map<String,BigDecimal> phenotypeDefinitionsHdra; private
	 * Map<String,BigDecimal> ptocoDefinitionsHdra; private java.util.List
	 * germnameshdra; private java.util.List germaccessionshdra; private
	 * java.util.List countrieshdra; private java.util.List subpopulationshdra;
	 * private java.util.List irisidhdra; private java.util.Map<String,Variety>
	 * mapVarname2VarietyHdra; private Map<BigDecimal, Variety> mapId2VarietyHdra;
	 * 
	 * private Map<String,BigDecimal> passportDefinitionsWissuwa; private
	 * Map<String,BigDecimal> phenotypeDefinitionsWissuwa; private
	 * Map<String,BigDecimal> ptocoDefinitionsWissuwa; private java.util.List
	 * germnamesWissuwa; private java.util.List germaccessionsWissuwa; private
	 * java.util.List countriesWissuwa; private java.util.List
	 * subpopulationsWissuwa; private java.util.List irisidWissuwa; private
	 * java.util.Map<String,Variety> mapVarname2VarietyWissuwa; private
	 * Map<BigDecimal, Variety> mapId2VarietyWissuwa;
	 * 
	 * 
	 * private Map<String,BigDecimal> passportDefinitionsGq92; private
	 * Map<String,BigDecimal> phenotypeDefinitionsGq92; private
	 * Map<String,BigDecimal> ptocoDefinitionsGq92; private java.util.List
	 * germnamesgq92; private java.util.List germaccessionsgq92; private
	 * java.util.List countriesgq92; private java.util.List subpopulationsgq92;
	 * private java.util.List irisidgq92; //private java.util.List accessionshdra;
	 * private java.util.Map<String,Variety> mapVarname2VarietyGq92; private
	 * Map<BigDecimal, Variety> mapId2VarietyGq92;
	 */

	public static boolean lockGenenameReader = false;
	public static boolean lockVarietyReader = false;

	private List setIRGCNames;
	private List setIRGCAccessions;

	// private Map<String,Organism> mapOrgname2Org;

	public ListItemsDAOAllImpl() {
		super();
		// TODO Auto-generated constructor stub
		germdao = (VarietyDAO) AppContext.checkBean(germdao, "VarietyDAO");
	}

	private void initIRGC() {

		germdao = (VarietyDAO) AppContext.checkBean(germdao, "VarietyDAO");

		Set sName = new TreeSet();
		Set sAcc = new TreeSet();
		Set<Variety> s = germdao.getIRGCStocks();
		for (Variety v : s) {
			sAcc.add(v.getAccession());
			sName.add(v.getName());
		}
		setIRGCNames = new ArrayList();
		setIRGCAccessions = new ArrayList();
		setIRGCNames.addAll(sName);
		setIRGCAccessions.addAll(sAcc);
	}

	@Override
	public List getIRGCVarietyNames() {
		if (setIRGCNames == null)
			initIRGC();
		return setIRGCNames;
	}

	@Override
	public List getIRGCAccessions() {
		if (setIRGCAccessions == null)
			initIRGC();
		return setIRGCAccessions;
	}

	/**
	 * Generate all variety name lists
	 */
	private void initNames(String dataset) {
		if (lockVarietyReader) {

			try {
				while (true) {
					Thread.sleep(5000);
					if (!lockVarietyReader)
						return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		AppContext.debug("initNames " + dataset);

		lockVarietyReader = true;

		// Map mapVarname2Variety=mapDataset2Varname2Variety.get(dataset);
		// Map mapId2Variety=mapDataset2Id2Variety.get(dataset);

		// if(mapVarname2Variety!=null) return;

		germdao = (VarietyDAO) AppContext.checkBean(germdao, "VarietyDAO");

		Map mapVarname2Variety = new java.util.HashMap<String, Variety>();
		Map mapId2Variety = new java.util.HashMap<BigDecimal, Variety>();
		mapDataset2Varname2Variety.put(dataset, mapVarname2Variety);
		mapDataset2Id2Variety.put(dataset, mapId2Variety);

		Set germnames = new TreeSet<String>();
		Set germaccessions = new TreeSet<String>();
		Set countries = new TreeSet<String>();
		Set subpopulations = new TreeSet<String>();
		Set irisid = new TreeSet<String>();

		int germcount = 0;

		List setvars = (List) germdao.findVarietiesByDataset(dataset);
		
		AppContext.debug(setvars.size() + " vars from stock_basicprop");
		java.util.Iterator<Variety> itgerm = setvars.iterator();

		while (itgerm.hasNext()) {
			Variety germ = itgerm.next();
			if (germ == null)
				throw new RuntimeException("germ==null");

			//AppContext.info("Getting Variety StockSample");
			mapId2Variety.put(germ.getStockSampleId(), germ);
			germcount++;

			if (germ.getName() == null) {
				AppContext.info("variety.getVarnameOfGenStockSrc()==null");
				continue;
			}

			mapVarname2Variety.put(germ.getName().toUpperCase(), germ);

			if (germ.getCountry() != null) // throw new RuntimeException("germ..getCountry()==null");
			{
				// countries.add( germ.getCountry().toLowerCase() );
				countries.add(germ.getCountry().toUpperCase());
			}

			if (germ.getSubpopulation() != null) // throw new RuntimeException("germ..getSubpopulation()==null");
			{
				subpopulations.add(germ.getSubpopulation().toLowerCase());
				// subpopulations.add( germ.getSubpopulation().toUpperCase() );
			}

			if (germ.getIrisId() != null) // throw new RuntimeException("germ..getSubpopulation()==null");
			{
				// irisid.add( germ.getIrisId().toLowerCase() );
				irisid.add(germ.getIrisId().toUpperCase());
			}

			// germnames.add( germ.getName().toLowerCase() );
			germnames.add(germ.getName().toUpperCase());
			if (germ.getAccession() != null && !germ.getAccession().isEmpty())
				germaccessions.add(germ.getAccession().toUpperCase());

		}
		List lgermnames = new ArrayList();
		lgermnames.addAll(germnames);
		List lgermaccessions = new ArrayList();
		lgermaccessions.addAll(germaccessions);
		List lcountries = new ArrayList();
		lcountries.addAll(countries);
		List lsubpopulations = new ArrayList();
		lsubpopulations.addAll(subpopulations);
		List lirisid = new ArrayList();
		lirisid.addAll(irisid);
		mapDataset2germnames.put(dataset, lgermnames);
		mapDataset2germaccessions.put(dataset, lgermaccessions);
		mapDataset2countries.put(dataset, lcountries);
		mapDataset2subpopulations.put(dataset, lsubpopulations);
		mapDataset2irisid.put(dataset, lirisid);

		mapDataset2Varname2Variety.put(dataset, mapVarname2Variety);
		mapDataset2Id2Variety.put(dataset, mapId2Variety);

		AppContext.info(mapId2Variety.size() + " variety Ids;  " + germnames.size() / 2 + "  names");

		/*
		 * this.germnames = new java.util.ArrayList(); this.germnames.addAll(germnames);
		 * this.germaccessions = new java.util.ArrayList();
		 * this.germaccessions.addAll(germaccessions); this.countries = new
		 * java.util.ArrayList(); this.countries.addAll(countries); this.subpopulations
		 * = new java.util.ArrayList(); //this.subpopulations.add("");
		 * //this.subpopulations.add("all varieties");
		 * this.subpopulations.add("all indica");
		 * //this.subpopulations.add("ALL INDICA");
		 * this.subpopulations.add("all japonica");
		 * //this.subpopulations.add("ALL JAPONICA");
		 * this.subpopulations.addAll(subpopulations); this.irisid = new
		 * java.util.ArrayList(); this.irisid.addAll(irisid);
		 */

		lockVarietyReader = false;
	}

	private void initSamples(String dataset) {
		if (lockVarietyReader) {

			try {
				while (true) {
					Thread.sleep(5000);
					if (!lockVarietyReader)
						return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		AppContext.debug("initNames " + dataset);

		lockVarietyReader = true;

		Map mapVarname2Variety = mapDataset2Assay2Sample.get(dataset);
		Map mapId2Variety = mapDataset2Id2Sample.get(dataset);

		// if(mapVarname2Variety!=null) return;

		sampledao = (StockSampleDAO) AppContext.checkBean(sampledao, "StockSampleDAO");

		mapVarname2Variety = new java.util.HashMap<String, StockSample>();
		mapId2Variety = new java.util.HashMap<BigDecimal, StockSample>();
		mapDataset2Assay2Sample.put(dataset, mapVarname2Variety);
		mapDataset2Id2Sample.put(dataset, mapId2Variety);

		/*
		 * Set germnames= new TreeSet<String>(); Set germaccessions= new
		 * TreeSet<String>(); Set countries= new TreeSet<String>(); Set subpopulations=
		 * new TreeSet<String>(); Set irisid= new TreeSet<String>();
		 */

		int germcount = 0;

		Set ds = new HashSet();
		ds.add(dataset);
		Set setvars = (Set) sampledao.getSamples(ds);
		AppContext.debug(setvars.size() + " samples from sample_basicprop");
		java.util.Iterator<StockSample> itgerm = setvars.iterator();

		while (itgerm.hasNext()) {
			StockSample germ = itgerm.next();
			if (germ == null)
				throw new RuntimeException("germ==null");

			mapId2Variety.put(germ.getStockSampleId(), germ);
			germcount++;

			if (germ.getAssay() == null) {
				AppContext.info("variety.getAssay()==null");
				continue;
			}

			mapVarname2Variety.put(germ.getAssay().toUpperCase(), germ);

		}
		AppContext.info(mapId2Variety.size() + " sample Ids;  "); // + germnames.size()/2 + " names");
		

		/*
		 * this.germnames = new java.util.ArrayList(); this.germnames.addAll(germnames);
		 * this.germaccessions = new java.util.ArrayList();
		 * this.germaccessions.addAll(germaccessions); this.countries = new
		 * java.util.ArrayList(); this.countries.addAll(countries); this.subpopulations
		 * = new java.util.ArrayList(); //this.subpopulations.add("");
		 * //this.subpopulations.add("all varieties");
		 * this.subpopulations.add("all indica");
		 * //this.subpopulations.add("ALL INDICA");
		 * this.subpopulations.add("all japonica");
		 * //this.subpopulations.add("ALL JAPONICA");
		 * this.subpopulations.addAll(subpopulations); this.irisid = new
		 * java.util.ArrayList(); this.irisid.addAll(irisid);
		 */

		lockVarietyReader = false;
	}

	@Override
	public List<String> getGenenames() {
		return getGenenames(AppContext.getDefaultOrganism());
	}

	@Override
	public List<String> getGenenames(String organism) {

		while (lockGenenameReader) {
			try {
				Thread.sleep(5000);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Collection genenames = mapOrg2Loci.get(organism);

		if (genenames == null || genenames.size() == 0) {
			lockGenenameReader = true;

			genenames = new java.util.ArrayList();

			if (AppContext.reloadFromDB("v_gene_" + organism) || !new File(AppContext.getFlatfilesDir() + "v_gene_"
					+ getOrganismByName(organism).getOrganismId().intValue() + ".csv").exists()) {
				geneDAO = (GeneDAO) AppContext.checkBean(geneDAO, "GeneDAO");
				java.util.Iterator<Gene> it = geneDAO
						.findAllGene(getOrganismByName(organism).getOrganismId().intValue()).iterator();
				while (it.hasNext()) {
					Gene gene = it.next();
					genenames.add(gene.getUniquename().toLowerCase());
				}
				try {
					BufferedWriter br = new BufferedWriter(new FileWriter(AppContext.getFlatfilesDir() + "v_gene_"
							+ getOrganismByName(organism).getOrganismId().intValue() + ".csv"));
					Iterator<String> itname = genenames.iterator();
					while (itname.hasNext()) {
						br.append(itname.next()).append("\n");
					}
					br.flush();
					br.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (genenames.size() == 0) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(AppContext.getFlatfilesDir() + "v_gene_"
							+ getOrganismByName(organism).getOrganismId().intValue() + ".csv"));
					String l = null;
					br.readLine();
					while ((l = br.readLine()) != null) {
						genenames.add(l.replace("\"", "").toLowerCase());
					}
					br.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			mapOrg2Loci.put(organism, genenames);
			lockGenenameReader = false;
		}

		AppContext.debug(genenames.size() + " genenames for " + organism);

		return (List) genenames;
	}

	@Override
	public Gene findGeneFromName(String genename, String organism) {
		geneDAO = (GeneDAO) AppContext.checkBean(geneDAO, "GeneDAO");
		return geneDAO.findGeneByName(genename.toUpperCase(), getOrganismByName(organism).getOrganismId().intValue());
	}

	@Override
	public List<Gene> findGeneFromName(Collection<String> genenames, String organism) {
		geneDAO = (GeneDAO) AppContext.checkBean(geneDAO, "GeneDAO");
		Set setCap = new HashSet();
		Iterator<String> itName = genenames.iterator();
		while (itName.hasNext())
			setCap.add(itName.next().toUpperCase());
		return geneDAO.findGeneWithNames(setCap, getOrganismByName(organism).getOrganismId().intValue());
	}

	@Override
	public Map getIrisId2Variety() {
		return null;
	}

	@Override
	public Map getIrisId2Variety(String dataset) {

		java.util.Map irisid = new java.util.HashMap();

		Set setvars = (Set) germdao.findVarietiesByDataset(dataset);
		java.util.Iterator<Variety> itgerm = setvars.iterator();

		while (itgerm.hasNext()) {
			Variety germ = itgerm.next();
			if (germ.getIrisId() == null || germ.getIrisId().isEmpty())
				continue;
			irisid.put(germ.getIrisId().toUpperCase(), germ);
			/*
			 * if(germ==null) throw new RuntimeException("germ==null");
			 * if(germ.getIrisId()!=null && ) // throw new
			 * RuntimeException("germ..getSubpopulation()==null"); irisid.put(
			 * germ.getIrisId().toUpperCase() , germ);
			 */
		}
		return irisid;
	}

	@Override
	public java.util.List getAccessions(String dataset) {

		List acc = mapDataset2germaccessions.get(dataset);
		if (acc == null || acc.size() == 0)
			initNames(dataset);
		return mapDataset2germaccessions.get(dataset);
	}

	@Override
	public java.util.List getIrisIds() {
		return null;
	}

	@Override
	public Variety getGermplasmByIrisId(String dataset) {
		List acc = mapDataset2irisid.get(dataset);
		if (acc == null || acc.size() == 0)
			initNames(dataset);
		return (Variety) mapDataset2irisid.get(dataset).get(0);
	}

	/*
	 * @Override public Variety getGermplasmByNameLike(String name,String dataset) {
	 * 
	 * 
	 * Variety var = getVarietyDAO(dataset).findVarietyByNameLike(name);
	 * if(var==null) { //var = iricstockdao.findVarietyByNameLike(name); } return
	 * var; }
	 * 
	 * 
	 * @Override public Variety getGermplasmByName(String name,String dataset) {
	 * 
	 * Variety var = germdao.findVarietyByName(name); if(var==null) { //var=
	 * iricstockdao.findVarietyByName(name); } return var; }
	 */
	/*
	 * @Override public List getGermplasmsByNameOrIrisid(String names) { // TODO
	 * Auto-generated method stub //irisIds = irisIds.replace("_"," ");
	 * 
	 * 
	 * //return germdao.findVarietyByNameOrIrisId(String names, irisIds); return
	 * null; }
	 */
	/*
	 * @Override public java.util.Set getGermplasmByCountry(String country, String
	 * dataset) { return germdao.findAllVarietyByCountry(country); }
	 */

	/*
	 * @Override public Set getGermplasmBySubpopulation(String subpopulation) { //
	 * 
	 * return getGermplasmBySubpopulation(subpopulation, VarietyFacade.DATASET_3K);
	 * 
	 * }
	 */

	@Override
	public Set getGermplasmBySubpopulation(String subpopulation, Set dataset) {
		return germdao.findAllVarietyBySubpopulation(subpopulation, dataset);
	}

	@Override
	public java.util.Set getGermplasmBySubpopulation(String subpopulation, String dataset) {

		AppContext.debug("querying " + subpopulation + " in " + dataset);
		germdao = (VarietyDAO) AppContext.checkBean(germdao, "VarietyDAO");
		if (dataset.equals(VarietyFacade.DATASET_SNPINDELV2_IUPAC)) {

			if (subpopulation.toUpperCase().equals("ALL INDICA")) {
				Set allvar = new LinkedHashSet();
				/*
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind1")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind2")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind3")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("indx")) ;
				 */

				/*
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind1A")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind1B")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind2")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("ind3")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("indx")) ;
				 */

				Set s = new HashSet();
				s.add(dataset);
				allvar.addAll(germdao.findAllVarietyBySubpopulation("ind1A", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("ind1B", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("ind2", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("ind3", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("indx", s));
				return allvar;
			} else if (subpopulation.toUpperCase().equals("ALL JAPONICA")) {
				Set allvar = new LinkedHashSet();
				/*
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("temp")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("trop")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("temp/trop")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("trop/temp")) ;
				 */
				/*
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("temp")) ;
				 * //allvar.addAll( germdao.findAllVarietyBySubpopulation("trop1")) ;
				 * //allvar.addAll( germdao.findAllVarietyBySubpopulation("trop2")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("trop")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("subtrop")) ;
				 * allvar.addAll( germdao.findAllVarietyBySubpopulation("japx")) ;
				 */
				Set s = new HashSet();
				s.add(dataset);
				allvar.addAll(germdao.findAllVarietyBySubpopulation("temp", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("trop", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("subtrop", s));
				allvar.addAll(germdao.findAllVarietyBySubpopulation("japx", s));
				return allvar;
			} else
				return germdao.findAllVarietyBySubpopulation(subpopulation);
		} else {
			Set s = new HashSet();
			s.add(dataset);
			return germdao.findAllVarietyBySubpopulation(subpopulation, s);

			// return getVarietyDAO(dataset).findAllVarietyBySubpopulation(subpopulation);
		}
	}

	private Collection toUpper(Collection col) {
		Collection u = new HashSet();
		Iterator it = col.iterator();
		while (it.hasNext()) {
			u.add(((String) it.next()).toUpperCase());
		}

		return u;
	}

	@Override
	public java.util.List getCountries(String dataset) {
		List l = this.mapDataset2countries.get(dataset);
		if (l == null || l.size() == 0)
			initNames(dataset);
		return mapDataset2countries.get(dataset);
	}

	/*
	 * @Override public Set getGermplasmByExample(Variety germplasm) { return
	 * getGermplasmByExample(germplasm, VarietyFacade.DATASET_3K); }
	 */

	@Override
	public Set getGermplasmByExample(Variety germplasm, Set dataset) {

		// VarietyDAO dao = this.getVarietyDAO(dataset);
		germdao = (VarietyDAO) AppContext.checkBean(germdao, "VarietyDAO");

		if (germplasm.getName() != null && !germplasm.getName().isEmpty())
			return getGermplasmsByName(germplasm.getName(), dataset);

		if (germplasm.getCountry() != null && !germplasm.getCountry().isEmpty() && germplasm.getSubpopulation() != null
				&& !germplasm.getSubpopulation().isEmpty()) {
			
			// UPDATED 02-24
			AppContext.debug("set Dataset: " + dataset.size());
//			Set s = new HashSet();
//			s.add(dataset);
			return (Set) germdao.findAllVarietyByCountryAndSubpopulationDatasets(germplasm.getCountry(),
					germplasm.getSubpopulation(), dataset);
		}

		if (germplasm.getCountry() != null && !germplasm.getCountry().isEmpty())
			return getGermplasmByCountry(germplasm.getCountry(), dataset);

		if (germplasm.getSubpopulation() != null && !germplasm.getSubpopulation().isEmpty())
			return getGermplasmBySubpopulation(germplasm.getSubpopulation(), dataset);
		return null;
	}

	@Override
	public java.util.Map<String, Variety> getMapVarname2Variety(String dataset) {

		Map m = this.mapDataset2Varname2Variety.get(dataset);
		if (m == null || m.size() == 0)
			initNames(dataset);
		return mapDataset2Varname2Variety.get(dataset);

	}

	@Override
	public Map<BigDecimal, Variety> getMapId2Variety(String dataset) {

		// AppContext.debug("getMapId2Variety:" + dataset);
		Map m = this.mapDataset2Id2Variety.get(dataset);
		if (m == null || m.isEmpty())
			initNames(dataset);
		return mapDataset2Id2Variety.get(dataset);

	}

	private void initMoreConstraints2() {
		mapDataset2passportDefinitions = new HashMap();
		mapDataset2phenotypeLegacyDefinitions = new HashMap<>();
		mapDataset2phenotypeCOTermDefinitions = new HashMap<>();

		mapDataset2ptocoDefinitions = new HashMap();
	}

	private void initMoreConstraints() {

		// cvtermsPassportdao = (CvTermDAO)AppContext.checkBean(cvtermsPassportdao,
		// "VCvPassportDAO");
		// cvtermsPhenotypedao = (CvTermDAO)AppContext.checkBean(cvtermsPhenotypedao,
		// "VCvPhenotypeDAO");
		// cvtermsPtocodao = (CvTermDAO)AppContext.checkBean(cvtermsPtocodao,
		// "VCvPtocoDAO");

		cvtermsPassportdao = null;
		cvtermsPhenotypedao = null;
		cvtermsPassportdao = (CvTermByDatasetDAO) AppContext.checkBean(cvtermsPassportdao, "VCvPassportByDatasetDAO");
		cvtermsPhenotypedao = (CvTermByDatasetDAO) AppContext.checkBean(cvtermsPhenotypedao,
				"VCvPhenotypeByDatasetDAO");

		mapDataset2passportDefinitions = new HashMap();
		List listCVPassport = cvtermsPassportdao.getAllTerms();
		AppContext.debug("listCVPassport=" + listCVPassport.size());
		Iterator<CvTermDataset> itTerm = listCVPassport.iterator();
		while (itTerm.hasNext()) {
			CvTermDataset term = itTerm.next();

			Map passportDefinitions = this.mapDataset2passportDefinitions.get(term.getDataset());
			if (passportDefinitions == null) {
				passportDefinitions = new TreeMap<String, BigDecimal>();
				mapDataset2passportDefinitions.put(term.getDataset(), passportDefinitions);
			}
			if (term.getDefinition().length() > 100)
				passportDefinitions.put(term.getDefinition().substring(0, 99) + "...", term.getCvTermId());
			else
				passportDefinitions.put(term.getDefinition(), term.getCvTermId());

		}
		// System.out.println("passportDefinitions");
		// System.out.println(passportDefinitions);

		mapDataset2phenotypeLegacyDefinitions = new HashMap();
		mapDataset2phenotypeCOTermDefinitions = new HashMap();
		mapDataset2phenotypeCOTermPhenIdDefinitions = new HashMap();
		List listCVPhenotype = cvtermsPhenotypedao.getAllTerms(); // .getAllTerms();
		AppContext.debug("listCVPhenotype=" + listCVPhenotype.size());
		Iterator<CvTermDataset> itTerm2 = listCVPhenotype.iterator();
		while (itTerm2.hasNext()) {
			CvTermDataset term = itTerm2.next();

			Map phenotypeDefinitions = this.mapDataset2phenotypeLegacyDefinitions.get(term.getDataset());
			Map phenotypeCOTermDefinitions = this.mapDataset2phenotypeCOTermDefinitions.get(term.getDataset());
			Map phenotypeCOTermPhenIdDefinitions = this.mapDataset2phenotypeCOTermPhenIdDefinitions
					.get(term.getDataset());

			if (phenotypeDefinitions == null) {
				phenotypeDefinitions = new TreeMap<String, BigDecimal>();
				mapDataset2phenotypeLegacyDefinitions.put(term.getDataset(), phenotypeDefinitions);
			}

			if (phenotypeCOTermDefinitions == null) {
				phenotypeCOTermDefinitions = new TreeMap<String, BigDecimal>();
				mapDataset2phenotypeCOTermDefinitions.put(term.getDataset(), phenotypeCOTermDefinitions);
			}

			if (phenotypeCOTermPhenIdDefinitions == null) {
				phenotypeCOTermPhenIdDefinitions = new TreeMap<String, BigDecimal>();
				mapDataset2phenotypeCOTermPhenIdDefinitions.put(term.getDataset(), phenotypeCOTermPhenIdDefinitions);
			}

			/*
			 * if(term.getDefinition().length()>100)
			 * phenotypeDefinitions.put(term.getDefinition().substring(0,99)+"...",
			 * term.getCvTermId()); else phenotypeDefinitions.put(term.getDefinition(),
			 * term.getCvTermId());
			 */
			String[] coName = term.getName().split("::");

			if (coName[1].length() > 100)
				phenotypeDefinitions.put(coName[1].substring(0, 99) + "...", term.getCvTermId());
			else
				phenotypeDefinitions.put(coName[1], term.getCvTermId());

			if (term.getName().length() > 100)
				phenotypeCOTermDefinitions.put(term.getName().substring(0, 99) + "...", term.getCvTermId());
			else
				phenotypeCOTermDefinitions.put(term.getName(), term.getCvTermId());

			if (coName[0].length() > 100)
				phenotypeCOTermPhenIdDefinitions.put(coName[0].substring(0, 99) + "...", term.getCvTermId());
			else
				phenotypeCOTermPhenIdDefinitions.put(coName[0], term.getCvTermId());

		}

		mapDataset2ptocoDefinitions = new HashMap();
		/*
		 * if(AppContext.reloadFromDB("ptoco") || !new File(AppContext.getFlatfilesDir()
		 * + "v_cv_ptoco_path_allstocks.tsv").exists() ) {
		 * 
		 * cvtermsPtocodao = (CvTermByDatasetDAO)AppContext.checkBean(cvtermsPtocodao,
		 * "VCvPtocoByDatasetDAO"); List listCvProco = cvtermsPtocodao.getAllTerms();
		 * AppContext.debug("listCvProco=" + listCvProco.size());
		 * Iterator<CvTermDataset> itTerm3=listCvProco.iterator();
		 * while(itTerm3.hasNext()) { CvTermDataset term = itTerm3.next();
		 * 
		 * Map ptocoDefinitions=this.mapDataset2ptocoDefinitions.get(term.getDataset());
		 * if(ptocoDefinitions==null) { ptocoDefinitions = new
		 * TreeMap<String,BigDecimal>();
		 * mapDataset2ptocoDefinitions.put(term.getDataset(), ptocoDefinitions); }
		 * if(term.getDefinition().length()>100)
		 * ptocoDefinitions.put(term.getDefinition().substring(0,99)+"...",
		 * term.getCvTermId()); else ptocoDefinitions.put(term.getDefinition(),
		 * term.getCvTermId()); } } if(mapDataset2ptocoDefinitions.isEmpty()) {
		 * 
		 * List listCvProco=new ArrayList(); try { BufferedReader br=new
		 * BufferedReader(new FileReader(AppContext.getFlatfilesDir() +
		 * "v_cv_ptoco_path_allstocks.tsv")); String l=null; br.readLine(); while(
		 * (l=br.readLine())!=null) { String cols[]=l.split("\t"); listCvProco.add(new
		 * String[]{cols[5],cols[3] +" (" + cols[0] + ":" +cols[1]+")",cols[2] }); }
		 * br.close(); AppContext.debug("listCvProco=" + listCvProco.size());
		 * Iterator<String[]> itTerm3=listCvProco.iterator(); while(itTerm3.hasNext()) {
		 * String[] term = itTerm3.next();
		 * 
		 * Map ptocoDefinitions=this.mapDataset2ptocoDefinitions.get(term[0]);
		 * if(ptocoDefinitions==null) { ptocoDefinitions = new
		 * TreeMap<String,BigDecimal>(); mapDataset2ptocoDefinitions.put(term[0],
		 * ptocoDefinitions); } if(term[1].length()>100)
		 * ptocoDefinitions.put(term[1].substring(0,99)+"...",
		 * BigDecimal.valueOf(Long.valueOf(term[2]))); else
		 * ptocoDefinitions.put(term[1], BigDecimal.valueOf(Long.valueOf(term[2]))); }
		 * 
		 * } catch(Exception ex) { ex.printStackTrace(); } }
		 */

		/*
		 * cvtermsPtocodao = (CvTermByDatasetDAO)AppContext.checkBean(cvtermsPtocodao,
		 * "VCvPtocoByDatasetDAO"); List listCvProco = cvtermsPtocodao.getAllTerms();
		 * //List listCvProco = cvtermsPtocodao.getAllTermsByDataset(dataset)
		 * .getAllTerms(); ptocoDefinitions = new TreeMap<String,BigDecimal>();
		 * ptocoDefinitionsHdra = new TreeMap<String,BigDecimal>(); //
		 * phenotypeDefinitions.put("", BigDecimal.ZERO); Iterator<CvTermDataset>
		 * itTerm3=listCvProco.iterator(); while(itTerm3.hasNext()) { CvTermDataset term
		 * = itTerm3.next(); if(term.getDataset().equals("3k"))
		 * ptocoDefinitions.put(term.getName(), term.getCvTermId()); else
		 * if(term.getDataset().equals("hdra")) ptocoDefinitionsHdra.put(term.getName(),
		 * term.getCvTermId()); }
		 */

	}

	@Override
	public Map<String, BigDecimal> getPhenotypeDefinitions(String dataset) {
		Map phenotypeDefinitions = this.mapDataset2phenotypeLegacyDefinitions.get(dataset);
		if (phenotypeDefinitions == null)
			initMoreConstraints();
		Map m = mapDataset2phenotypeLegacyDefinitions.get(dataset);
		if (m == null)
			return new HashMap();
		else
			return m;
	}

	@Override
	public Map<String, BigDecimal> getCOTerms(String dataset) {

		Map CoTermPhenIdDefinitions = this.mapDataset2phenotypeCOTermPhenIdDefinitions.get(dataset);

		if (CoTermPhenIdDefinitions == null)
			initMoreConstraints();

		Map m = mapDataset2phenotypeCOTermPhenIdDefinitions.get(dataset);
		if (m == null)
			return new HashMap();
		else
			return m;
	}

	@Override
	public Map<String, BigDecimal> getPassportDefinitions(String dataset) {
		Map passportDefinitions = this.mapDataset2passportDefinitions.get(dataset);
		if (passportDefinitions == null)
			initMoreConstraints();
		Map m = mapDataset2passportDefinitions.get(dataset);
		if (m == null)
			return new HashMap();
		else
			return m;

	}

	@Override
	public Map<String, BigDecimal> getPtocoDefinitions(String dataset) {

		Map ptocoDefinitions = this.mapDataset2ptocoDefinitions.get(dataset);
		if (ptocoDefinitions == null)
			initMoreConstraints();
		Map m = mapDataset2ptocoDefinitions.get(dataset);
		if (m == null)
			return new HashMap();
		else
			return m;

	}

	@Override
	public List getOrganisms() throws Exception {
		organismdao = (OrganismDAO) AppContext.checkBean(organismdao, "OrganismDAO");
		if (mapOrgname2Org == null)
			mapOrgname2Org = organismdao.getMapName2Organism();
		AppContext.debug("mapOrgname2Org=" + mapOrgname2Org.size());
		return new ArrayList(mapOrgname2Org.values());
	}

	@Override
	public List getContigs(String organism) {

		if (!mapOrganismScaffolds.containsKey(organism)) {

			BigDecimal orgid = getOrganismByName(organism).getOrganismId();
			// List contignames = new ArrayList();
			LinkedHashMap contignames = new LinkedHashMap();

			// TODO value for 19
			if (orgid.equals(BigDecimal.valueOf(9)) || orgid.equals(BigDecimal.valueOf(16))|| orgid.equals(BigDecimal.valueOf(23))) {
				scaffolddao = (ScaffoldDAO) AppContext.checkBean(scaffolddao, "ScaffoldDAO");
				Iterator<Scaffold> it = scaffolddao.getScaffolds(orgid).iterator();
				while (it.hasNext()) {
					Scaffold s = it.next();
					// contignames.add( it.next().getName());
					contignames.put(s.getName().toUpperCase(), s.getLength());
				}
			} else {

				try {
					BufferedReader br = new BufferedReader(
							new FileReader(AppContext.getFlatfilesDir() + "v_scaffolds_" + orgid.intValue() + ".tsv"));
					String l = null;
					br.readLine();
					while ((l = br.readLine()) != null) {
						String cols[] = l.split("\t");
						contignames.put(cols[1], null);
					}
					br.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (contignames.isEmpty()) {
					Iterator<Scaffold> it = scaffolddao.getScaffolds(orgid).iterator();
					while (it.hasNext()) {
						Scaffold s = it.next();
						// contignames.add( it.next().getName());
						contignames.put(s.getName().toUpperCase(), s.getLength());
					}
				}

			}

			if (contignames.size() < 100)
				mapOrganismScaffolds.put(organism, contignames);
			List l = new ArrayList();
			l.addAll(contignames.keySet());
			return l;

		} else {
			Set list = mapOrganismScaffolds.get(organism).keySet();
			AppContext.debug(list.size() + " contigs for " + organism);
			List l = new ArrayList();
			l.addAll(list);
			return l;
		}
	}

	@Override
	public Long getFeatureLength(String feature, String organism) {

		Map contigs = mapOrganismScaffolds.get(organism);
		Long len = null;
		if (contigs != null) {
			if (feature.substring(3, 4).equals("0"))
				feature = feature.replace("0", "");
			len = (Long) contigs.get(feature.toUpperCase());
		}
		if (len == null) {
			scaffolddao = (ScaffoldDAO) AppContext.checkBean(scaffolddao, "ScaffoldDAO");
			AppContext.debug("geting length of " + feature + "  org=" + organism);
			len = scaffolddao.getScaffoldLength(feature, getOrganismByName(organism).getOrganismId());
		}
		return len;
	}

	@Override
	public Scaffold getFeature(String feature, String organism) {
		scaffolddao = (ScaffoldDAO) AppContext.checkBean(scaffolddao, "ScaffoldDAO");
		organismdao = (OrganismDAO) AppContext.checkBean(organismdao, "OrganismDAO");
		Map mapOrgname2Org = organismdao.getMapName2Organism();
		return scaffolddao.getScaffold(feature, ((Organism) mapOrgname2Org.get(organism)).getOrganismId());
	}

	private List createCvtermMap(List cvterms) {
		// Map mapCvid2Term=new LinkedHashMap();
		List terms = new ArrayList();
		Iterator<CvTerm> itCvterm = cvterms.iterator();
		while (itCvterm.hasNext()) {
			CvTerm cvterm = itCvterm.next();
			// mapCvid2Term.put( cvterm.getCvTermId(), cvterm.getName());
			terms.add(cvterm.getName());
		}
		return terms;
	}

	@Override
	public List getGOTermsWithLoci(String cv, String organism) {
		// cvtermlocusdao = (VLocusCvtermDAO)AppContext.checkBean(cvtermlocusdao,
		// "VLocusCvtermDAO");
		// return cvtermlocusdao.getAllTerms(organism);

		List cvterms = mapCVOrg2Cvterms.get(cv + "-" + organism);
		if (cvterms == null) {
			cvterms = cvtermsFromFile("cvterms_" + cv + "_" + getOrganismByName(organism).getOrganismId() + ".tsv");
			if (cvterms == null || cvterms.isEmpty()) {
				gotermorganismdao = (CvTermDAO) AppContext.checkBean(gotermorganismdao, "VGoOrganismDAO");
				// cvterms = gotermorganismdao.getAllTerms(cv, organism);
				cvterms = createCvtermMap(gotermorganismdao.getAllTerms(this.getCvByName(cv).getCvId(),
						this.getOrganismByName(organism).getOrganismId()));
			}
			mapCVOrg2Cvterms.put(cv + "-" + organism, cvterms);
		}
		AppContext.debug("getting cv terms " + cv + " for " + organism);

		return cvterms;
	}

	private List cvtermsFromFile(String filename) {
		try {
			Set listnames = new TreeSet();
			File cvtermsFile = new File(AppContext.getFlatfilesDir() + filename);
			if (cvtermsFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(AppContext.getFlatfilesDir() + filename));
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.isEmpty())
						continue;
					listnames.add(line.split("\t")[3]);
				}
				br.close();
			}else
				AppContext.info(AppContext.getFlatfilesDir() + filename + "NOT FOUND");
			List arr = new ArrayList();
			arr.addAll(listnames);
			return arr;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getPATOTermsWithLoci(String cv, String organism) {
		List cvterms = mapCVOrg2Cvterms.get(cv + "-" + organism);
		if (cvterms == null) {
			cvterms = cvtermsFromFile("cvterms_" + cv + "_" + getOrganismByName(organism).getOrganismId() + ".tsv");
			if (cvterms == null) {
				patotermorganismdao = (CvTermDAO) AppContext.checkBean(patotermorganismdao, "VPatoOrganismDAO");
				// cvterms = patotermorganismdao.getAllTerms(cv, organism);
				AppContext.debug("get cvid for " + cv);
				if (patotermorganismdao == null)
					AppContext.debug("patotermorganismdao==null");
				if (getCvByName(cv) == null)
					AppContext.debug("getCvByName(cv)==null");
				List allterms = patotermorganismdao.getAllTerms(this.getCvByName(cv).getCvId(),
						this.getOrganismByName(organism).getOrganismId());
				if (allterms == null)
					AppContext.debug("allterms==null");
				cvterms = createCvtermMap(allterms);
			}
			mapCVOrg2Cvterms.put(cv + "-" + organism, cvterms);
		}

		AppContext.debug("getting cv terms " + cv + " for " + organism);

		return cvterms;
	}

	@Override
	public Organism getOrganismByName(String name) {

		organismdao = (OrganismDAO) AppContext.checkBean(organismdao, "OrganismDAO");
		Map<String, Organism> mapOrg = organismdao.getMapName2Organism();
		// AppContext.debug("getting organism " + name + " from " + mapOrg);

		return mapOrg.get(name);
	}

	@Override
	public Organism getOrganismById(Integer id) {

		organismdao = (OrganismDAO) AppContext.checkBean(organismdao, "OrganismDAO");
		return organismdao.getOrganismByID(id);
	}

	@Override
	public Cv getCvByName(String cv) {
		cvDAO = (CvDAO) AppContext.checkBean(cvDAO, "CvDAO");
		return cvDAO.getMapName2Cv().get(cv);
	}

	@Override
	public List getVarietyNames() {
		AppContext.debug("getVarietyNames: all");
		germdao = (VarietyDAO) AppContext.checkBean(germdao, "VarietyDAO");
		Set s = germdao.findAllVariety();
		List l = new ArrayList();
		for (Object var : s) {
			l.add(((Variety) var).getName().toUpperCase());
		}
		return l;
	}

	@Override
	public List getVarietyNames(Set dataset) {
		if (dataset.size() == 0)
			return getVarietyNames((String) dataset.iterator().next());
		Set allnames = new TreeSet();
		for (Object set : dataset) {
			List l = this.mapDataset2germnames.get((String) set);
			if (l == null || l.size() == 0)
				initNames((String) set);
			allnames.addAll(mapDataset2germnames.get((String) set));
		}
		List l = new ArrayList();
		l.addAll(allnames);

		AppContext.debug(l.size() + " names");

		return l;
	}

	@Override
	public List getVarietyNames(String dataset) {
		AppContext.debug("getVarietyNames: " + dataset);
		List l = this.mapDataset2germnames.get(dataset);
		if (l == null || l.size() == 0)
			initNames(dataset);
		return mapDataset2germnames.get(dataset);

	}

	@Override
	public List getIrisIds(String dataset) {
		List l = this.mapDataset2irisid.get(dataset);
		if (l == null || l.size() == 0)
			initNames(dataset);
		return mapDataset2irisid.get(dataset);
	}

	@Override
	public List getSubpopulations(String dataset) {
		List l = this.mapDataset2subpopulations.get(dataset);
		if (l == null || l.size() == 0)
			initNames(dataset);
		return mapDataset2subpopulations.get(dataset);
	}

	@Override
	public List getOrganismScaffolds(String organism) {
		scaffolddao = (ScaffoldDAO) AppContext.checkBean(scaffolddao, "ScaffoldDAO");
		return scaffolddao.getScaffolds(getOrganismByName(organism).getOrganismId());
	}

	@Override
	public void cleanDatasetCache(String dataset) {

		mapDataset2germnames.remove(dataset);
		mapDataset2germaccessions.remove(dataset);
		mapDataset2countries.remove(dataset);
		mapDataset2subpopulations.remove(dataset);
		mapDataset2irisid.remove(dataset);

		mapDataset2Varname2Variety.remove(dataset);
		mapDataset2Id2Variety.remove(dataset);

		mapDataset2passportDefinitions.remove(dataset);
		mapDataset2phenotypeLegacyDefinitions.remove(dataset);
		mapDataset2phenotypeCOTermDefinitions.remove(dataset);
		mapDataset2ptocoDefinitions.remove(dataset);
	}

	@Override
	public void cleanOrganismCache(String organism) {
		mapOrg2Loci.remove(organism);
		mapOrganismScaffolds.remove(organism);
		mapOrgname2Org.remove(organism);
	}

	@Override
	public Set getPlatforms(String type) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getPlatforms(type);
	}

	@Override
	public Set getPlatforms(Set setds, Set setvs, String type) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getPlatforms(setds, setvs, type);
	}

	@Override
	public Set getDatasets(String type) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getDatasets(type);
	}
	
	@Override
	public List getRunsByType(String type) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getRunsByType(type);
	}

	@Override
	public Set getDatasets(String type,String reference) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getDatasets(type,reference);
	}

	@Override
	public Set getDatasets() {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getDatasets();
	}

	@Override
	public Set getSnpsets(String dataset) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getVariantsets(dataset);
	}

	@Override
	public Set getSnpsets(Set dataset, String type) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getVariantsets(dataset, type);
	}

	@Override
	public Set getSnpsets(String dataset, String type) {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getVariantsets(dataset, type);
	}

	@Override
	public Set getSnpsets() {
		genotyperundao = (GenotypeRunPlatformDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		return genotyperundao.getVariantsets();
	}

	@Override
	public List<Variety> getGermplasmByIrisIds(Collection names, Set dataset) {
		return germdao.findVarietyByIrisIds(names, dataset);
	}

	@Override
	public Variety getGermplasmByAccession(String name, String dataset) {
		Set s = new HashSet();
		s.add(dataset);
		return germdao.findVarietyByAccession(name, s);
	}

	@Override
	public List<Variety> getGermplasmByNameLike(String name, Set dataset) {
		Set sn = new HashSet();
		sn.add(name);
		return germdao.findVarietyByNamesLike(sn, dataset);
	}

	@Override
	public List<Variety> getGermplasmByNamesLike(Collection names, Set dataset) {
		Set s = new HashSet();
		s.add(dataset);
		return germdao.findVarietyByNamesLike(names, s);
	}

	@Override
	public List<Variety> getGermplasmByName(String name, String dataset) {
		Set s = new HashSet();
		s.add(dataset);
		Set sn = new HashSet();
		sn.add(name);
		return germdao.findVarietyByNames(sn, s);
	}

	@Override
	public List<Variety> getGermplasmByNames(Collection names, String dataset) {
		Set s = new HashSet();
		s.add(dataset);
		return germdao.findVarietyByNames(names, s);
	}

	@Override
	public Set getGermplasmByCountry(String country, Set dataset) {
		return germdao.findAllVarietyByCountry(country, dataset);
	}

	@Override
	public Set getGermplasm(Set dataset) {
		return germdao.findAllVariety(dataset);
	}

	@Override
	public Set<Variety> getGermplasmsByName(String names, Set dataset) {
		return germdao.findVarietiesByName(names, dataset);
	}

	@Override
	public Set<Variety> getGermplasmsByIrisId(String irisid, String dataset) {
		Set s = new HashSet();
		s.add(dataset);
		return germdao.findVarietiesByIrisId(irisid, s);
	}

	@Override
	public Set getGermplasmsByNameAccession(String varname, String accession, String dataset) {
		Set s = new HashSet();
		s.add(dataset);
		return germdao.findVarietiesByNameAccession(varname, accession, s);
	}

	@Override
	public Set getQuantTraits(Set dataset) {
		return null;
	}

	@Override
	public Map<String, Variety> getMapVarname2Variety(Set dataset) {
		if (dataset.size() == 1)
			return getMapVarname2Variety((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			String ds = (String) it.next();
			Map mds = this.mapDataset2Varname2Variety.get(ds);
			if (mds == null)
				initNames(ds);
			m.putAll(this.mapDataset2Varname2Variety.get(ds));
		}
		return m;
	}

	@Override
	public Map getMapId2Variety(Set dataset) {
		if (dataset.size() == 1)
			return getMapId2Variety((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			String ds = (String) it.next();
			Map mds = this.mapDataset2Id2Variety.get(ds);
			if (mds == null)
				initNames(ds);
			m.putAll(this.mapDataset2Id2Variety.get(ds));
		}
		return m;
	}

	@Override
	public List getSubpopulations(Set dataset) {
		if (dataset.size() == 1)
			return getSubpopulations((String) dataset.iterator().next());
		Set set = new TreeSet();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			String ds = (String) it.next();
			List lds = mapDataset2subpopulations.get(ds);
			if (lds == null) {
				initNames(ds);
				lds = mapDataset2subpopulations.get(ds);
			}
			set.addAll(lds);

		}
		List l = new ArrayList();
		l.addAll(set);
		return l;
	}

	@Override
	public List getAccessions(Set dataset) {
		if (dataset.size() == 1)
			return getAccessions((String) dataset.iterator().next());
		Set set = new TreeSet();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			String ds = (String) it.next();
			List lds = this.mapDataset2germaccessions.get(ds);
			if (lds == null) {
				initNames(ds);
				lds = this.mapDataset2germaccessions.get(ds);
			}
			set.addAll(lds);
		}
		List l = new ArrayList();
		l.addAll(set);
		return l;
	}

	@Override
	public Map getPhenotypeDefinitions(Set dataset) {
		if (dataset.size() == 1)
			return getPhenotypeDefinitions((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			Map mds = getPhenotypeDefinitions((String) it.next());
			if (mds != null)
				m.putAll(mds);
		}
		return m;

	}

	@Override
	public Map getCOTerms(Set dataset) {
		if (dataset.size() == 1)
			return getCOTerms(((String) dataset.iterator().next()));
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			Map mds = getCOTerms((String) it.next());
			if (mds != null)
				m.putAll(mds);
		}
		return m;

	}

	@Override
	public Map getUIforDataset(Set dataset) {
		return null;
	}

	@Override
	public Map getPassportDefinitions(Set dataset) {
		if (dataset.size() == 1)
			return getPassportDefinitions((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			Map mds = getPassportDefinitions((String) it.next());
			if (mds != null)
				m.putAll(mds);
		}
		return m;
	}

	@Override
	public Map getPtocoDefinitions(Set dataset) {
		if (dataset.size() == 1)
			return getPtocoDefinitions((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			Map mds = getPtocoDefinitions((String) it.next());
			if (mds != null)
				m.putAll(mds);
		}
		return m;
	}

	@Override
	public List getIrisIds(Set dataset) {
		if (dataset.size() == 1)
			return getIrisIds((String) dataset.iterator().next());
		Set set = new TreeSet();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			List lds = this.mapDataset2irisid.get(it.next());
			if (lds != null)
				set.addAll(lds);
		}
		List l = new ArrayList();
		l.addAll(set);
		return l;
	}

	@Override
	public List getCountries(Set dataset) {
		if (dataset.size() == 1)
			return getCountries((String) dataset.iterator().next());
		Set set = new TreeSet();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			List lds = this.mapDataset2countries.get(it.next());
			if (lds != null)
				set.addAll(lds);
		}
		List l = new ArrayList();
		l.addAll(set);
		return l;
	}

	@Override
	public boolean hasNonsynData(Integer organismId, Set variantset) {
		boolean hasAll = true;
		for (Object ds : variantset) {
			Boolean hasNonsyn = mapDataset2Hasnonsyn.get(ds);
			if (hasNonsyn == null) {
				nonsyndao = (SnpsNonsynAllvarsDAO) AppContext.checkBean(nonsyndao, "SnpsNonsynAllvarsDAO");
				hasNonsyn = nonsyndao.hasData((String) ds, organismId);
				hasAll = hasAll && hasNonsyn;
				mapDataset2Hasnonsyn.put((String) ds, hasNonsyn);

			}
		}
		return hasAll;
	}

	@Override
	public Map<BigDecimal, StockSample> getMapId2Sample(String dataset) {
		Map m = this.mapDataset2Id2Sample.get(dataset);
		if (m == null)
			initSamples(dataset);
		return mapDataset2Id2Sample.get(dataset);
	}

	@Override
	public Map<Integer, BigDecimal> getMapIdx2Sample(GenotypeQueryParams params) {
		sampledao = (StockSampleDAO) AppContext.checkBean(sampledao, "StockSampleDAO");
		Set samples = null;
		if (params.hasVarlist()) {
			samples = sampledao.getSamplesById(new HashSet(params.getColVarIds()));
		} else if (params.hasVaridRange()) {
			Integer varidstartend[] = params.getVaridStartEnd();
			Set setVarids = new HashSet();
			for (int i = varidstartend[0]; i <= varidstartend[1]; i++)
				setVarids.add(BigDecimal.valueOf(i));
			samples = sampledao.getSamplesBySampleIdInDataset(params.getDataset(), setVarids);
		} else {
			samples = sampledao.getSamples(params.getDataset());
		}
		Map mapIdx2Sample = null;
		if (samples != null) {
			mapIdx2Sample = new HashMap();
			Iterator itSample = samples.iterator();
			while (itSample.hasNext()) {
				StockSample s = (StockSample) itSample.next();
				mapIdx2Sample.put(s.getHdf5Index() + 1, s.getStockSampleId());
			}
		}
		return mapIdx2Sample;
	}

	@Override
	public Map<String, StockSample> getMapAssay2Sample(String dataset) {
		Map m = this.mapDataset2Assay2Sample.get(dataset);
		if (m == null || m.size() == 0)
			initSamples(dataset);
		return mapDataset2Assay2Sample.get(dataset);
	}

	@Override
	public Map getMapId2Sample(Set dataset) {
		// ********************** OLD CODES
		// if (dataset.size() == 1)
		// return getMapId2Sample((String) dataset.iterator().next());
		// Map m = new HashMap();
		// Iterator it = dataset.iterator();
		// while (it.hasNext()) {
		// String ds = (String) it.next();
		// Map mds = this.mapDataset2Id2Sample.get(ds);
		// if (mds == null)
		// initSamples(ds);
		// m.putAll(this.mapDataset2Id2Sample.get(ds));
		// AppContext.debug("Dataset: "+ ds +" - " +
		// this.mapDataset2Id2Sample.get(ds).size());
		//
		// }
		//
		// AppContext.debug("PUT IN MAPPING: "+ dataset +" - " + m.size());
		// return m;
		// ********************** ENDOLD CODES

		// if (dataset.size() == 1)
		// return getMapId2Sample((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			String ds = (String) it.next();
			Map mds = this.mapDataset2Id2Sample.get(ds);
			if (mds == null)
				initSamples(ds);

			m.put(ds, this.mapDataset2Id2Sample.get(ds));
			// AppContext.debug("Dataset: " + ds + " - " +
			// this.mapDataset2Id2Sample.get(ds).size());

		}

		AppContext.debug("PUT IN MAPPING: " + dataset + " - " + m.size());
		return m;
	}

	@Override
	public Map<String, StockSample> getMapAssay2Sample(Set dataset) {
		if (dataset.size() == 1)
			return getMapAssay2Sample((String) dataset.iterator().next());
		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			String ds = (String) it.next();
			Map mds = this.mapDataset2Assay2Sample.get(ds);
			if (mds == null)
				initSamples(ds);
			m.putAll(this.mapDataset2Assay2Sample.get(ds));
		}
		return m;
	}

	@Override
	public Map<String, BigDecimal> getTraits(Set<String> dataset, boolean legacySelected) {
		if (dataset.size() == 1) {

			return getTraitItem(dataset.iterator().next(), legacySelected);
		}

		Map m = new HashMap();
		Iterator it = dataset.iterator();
		while (it.hasNext()) {
			Map mds = getTraitItem((String) it.next(), legacySelected);
			if (mds != null)
				m.putAll(mds);
		}
		return m;

	}

	private Map<String, BigDecimal> getTraitItem(String dataset, boolean legacySelected) {
		Map phenotypeDefinitions = null;
		if (legacySelected)
			phenotypeDefinitions = this.mapDataset2phenotypeLegacyDefinitions.get(dataset);
		else
			phenotypeDefinitions = this.mapDataset2phenotypeCOTermDefinitions.get(dataset);

		if (phenotypeDefinitions == null)
			initMoreConstraints();

		Map m = null;
		if (legacySelected)
			m = mapDataset2phenotypeLegacyDefinitions.get(dataset);
		else
			m = mapDataset2phenotypeCOTermDefinitions.get(dataset);
		if (m == null)
			return new HashMap();
		else
			return m;
	}

	@Override
	public Map<BigDecimal, String> getAllTraits(Set<String> dataset, boolean legacyPhenotype) {
		return null;
	}
}