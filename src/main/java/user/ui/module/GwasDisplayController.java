package user.ui.module;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.LocusPromoter;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.VarietyPlusPlus;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImplAllelePvalue;
import org.irri.iric.ds.chado.domain.impl.PositionLogPvalueImpl;
import org.irri.iric.ds.chado.domain.impl.VarietyPlusPlusImpl;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.WebConstants;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.admin.WorkspaceLoadLocal;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genomics.service.GenomicsFacadeImpl;
import org.irri.iric.portal.genomics.zkui.MarkerAnnotationSorter;
import org.irri.iric.portal.genomics.zkui.MarkerVarListItemRenderer;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.gwas.GwasFacade;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.variety.zkui.VarietyListItemRenderer;
import org.irri.iric.portal.variety.zkui.VarietyPlusPlusComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.chart.AxisLabels;
import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.ChartsSelectionEvent;
import org.zkoss.chart.Exporting;
import org.zkoss.chart.ExportingButton;
import org.zkoss.chart.Marker;
import org.zkoss.chart.MenuItem;
import org.zkoss.chart.Series;
import org.zkoss.chart.Tooltip;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.chart.model.DefaultXYModel;
import org.zkoss.chart.plotOptions.ScatterPlotOptions;
import org.zkoss.chart.util.AnyVal;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import user.ui.module.util.constants.SessionConstants;

@Controller("GwasDisplayController")
@Scope(value = "session")
public class GwasDisplayController extends SelectorComposer<Window> {

	private static final String ON_MY_CUSTOM_ITEM = "onMyCustomItem";

	private int maxAnnotMarkers = 50;
	private int nLabels = 25;
	private int nMarkers = 100;
	private Map<String, Integer> mapChr2Offset = new HashMap();

	@Autowired
	@Qualifier("GwasFacade")
	GwasFacade gwas;
	@Autowired
	@Qualifier("GenomicsFacade")
	GenomicsFacade genomics;
	@Autowired
	@Qualifier("GenotypeFacade")
	GenotypeFacade genotype;
	@Autowired
	@Qualifier("VarietyFacade")
	VarietyFacade variety;

	@Autowired
	@Qualifier("WorkspaceFacade")
	WorkspaceFacade workspace;

	@Wire
	private Div divMultipositions;

	@Wire
	private Charts chartManhattanXY;

	@Wire
	private Label labelAnnotations;

	@Wire
	private Div divVarietylist;
	@Wire
	private Textbox txtboxListnameVar;
	@Wire
	private Button buttonAddToListVar;

	@Wire
	private Textbox txtboxListnameSnp;
	@Wire
	private Button buttonAddToListSnp;

	@Wire
	private Div resultContentDiv;

	@Wire
	private Hbox hboxAddtolist;
	@Wire
	private Hbox hboxDownload;
	@Wire
	private Button buttonDownloadCSV;
	@Wire
	private Button buttonDownloadTab;

	@Wire
	private Listheader listheaderPhenotype;

	@Wire
	private Listbox listboxVariety;
	@Wire
	private Label labelVarietyMsg;
	@Wire
	// private Combobox comboboxMinlogP;
	private Listbox comboboxMinlogP;

	@Wire
	private Label labelPositions;

	@Wire
	private Charts chartAlleleHist;

	@Wire
	private Charts chartSubpopHist;
	@Wire
	private Charts chartGenSubpopHist;
	@Wire
	private Charts chartGenotypeHist;
	@Wire
	private Checkbox checkboxNormalize;

	// @Wire
	// private Vbox vboxSubpopCheckbox;

	@Wire
	private Listbox listboxTrait;

	@Wire
	private Listbox listboxSnplist;

	@Wire
	private Listbox listboxPosition;

	@Wire
	private Listbox listboxMultiPosition;
	@Wire
	private Button buttonDisplayHist;

	@Wire
	private Listbox listboxSubpopulation;

	@Wire
	private Listbox listboxPhenotypeBins;
	/*
	 * @Wire private Button buttonResetzoom;
	 */

	@Wire
	private Label labelManhattan;

	@Wire
	private Tab tabRegion;
	@Wire
	private Tab tabPosition;
	@Wire
	private Tab tabPopulation;

	@Wire
	private Checkbox checkboxNormalizePopulation;

	@Wire
	// private Grid gridMarker;
	private Listbox listboxMarkerVar;
	@Wire
	private Borderlayout borderMarkerVar;

	// @Wire
	// private Charts chartManhattan;
	@Wire
	private Iframe iframeJbrowse;
	@Wire
	private Label msgJbrowse;

	@Wire
	private Checkbox checkboxJbrowse;

	@Wire
	private Listbox listboxVarietylist;

	@Wire
	private Image imageQQ;

	@Wire
	private Tab tabAnalysis;
	@Wire
	private Listbox listboxGenotype1;
	@Wire
	private Listbox listboxGenotype2;
	@Wire
	private Charts chartSmoothHist;
	@Wire
	private Intbox intboxBins;

	@Wire
	private Button buttonMinOverlap;

	@Wire
	private Intbox intboxMaxFeatures;
	@Wire
	private Intbox intboxMinCountPercent;
	@Wire
	private Charts chartSmoothPhen;
	@Wire
	private Listbox listboxBest;
	@Wire
	private Charts chartsTopScores;
	@Wire
	private Listbox listboxMLIteration;
	private List[] listMapalleleScores;

	@Wire
	private Radio radioGroupbyMarker;
	@Wire
	private Radio radioGroupbyGene;
	@Wire
	private Radio radioGroupbyQtl;

	@Wire
	private Listheader headerLogp;

	// copied from locus to create gene list

	@Wire
	private Hbox hboxAddtolistGenes;
	@Wire
	private Textbox txtboxListnameGene;
	@Wire
	private Button buttonAddToListGene;
	@Wire
	private Checkbox checkboxListModel;
	@Wire
	private Checkbox checkboxListPromoter;
	@Wire
	private Checkbox checkboxListRicenet;
	@Wire
	private Checkbox checkboxListPrin;

	@Wire
	private Checkbox checkboxIncludeInteractions;
	@Wire
	private Checkbox checkboxIncludePromoters;
	@Wire
	private Checkbox checkboxIncludeGO;
	@Wire
	private Checkbox checkboxIncludePOTO;
	@Wire
	private Checkbox checkboxIncludeQTL;
	@Wire
	private Button buttonUpdateAnnotations;
	@Wire
	private Div div_info;

//	@Wire
//	private Hlayout layoutResultId;
	@Wire
	private Div myModal;
	@Wire
	private Listbox listboxChromosome;
	@Wire
	private Radio radioLegacyTrait;
	@Wire
	private Radio radioCoTrait;

	private Map<String, Double> mapPos2Values;
	private Map<String, Integer> mapPos2Index;
	private Map<Integer, String> mapIndex2Pos;
	private Map<String, Integer> mapViewPos2Index;
	private Map<Integer, String> mapViewIndex2Pos;
	private Map<BigDecimal, Object> mapVarid2Phenotype;
	private List<Variety> listVarieties;

	private Map<Object, Map<Object, Collection>> mapSubpopGen2Phenotype2Count;
	private Map<Object, Map<Object, Collection>> mapSubpop2Phenotype2Count;
	private Map<Object, Map<Object, Collection>> mapGenotype2Phenotype2Count;
	private Map<Object, Map<Object, Integer>> mapAllele2Phenotype2Count;
	private Map<Object, Map<Object, Collection>> mapAllele2Phenotype2Set;

	private Map<Object, Map<Object, Collection>> mapGenotype2Phenotype2Frequency;

	private Map<Long, Object> mapVarid2Genotype;

	private List markerresult;
	private Set setAnnotations;
	private int prevMinX = -1;
	private int prevMaxX = -1;
	private String urljbrowse;

	private String dataset = VarietyFacade.DATASET_SNPINDELV2_IUPAC;

	private StringBuffer sb;

	private Session sess;

	private User user;

	private boolean searchMenu;

	private ListItemsDAO listitemsDAO;

	private Organism organism;

	public GwasDisplayController() {
		super();
		// TODO Auto-generated constructor stub
		// chartManhattan.getChart().getResetZoomButton().

		mapChr2Offset.put("1", 0);
		mapChr2Offset.put("2", 43270923);
		mapChr2Offset.put("3", 79208173);
		mapChr2Offset.put("4", 115621992);
		mapChr2Offset.put("5", 151124686);
		mapChr2Offset.put("6", 181083120);
		mapChr2Offset.put("7", 212331907);
		mapChr2Offset.put("8", 242029528);
		mapChr2Offset.put("9", 270472550);
		mapChr2Offset.put("10", 293485270);
		mapChr2Offset.put("11", 316692557);
		mapChr2Offset.put("12", 345713663);

		AppContext.debug(this.getClass() + " started");
		sb = new StringBuffer();

	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		AppContext.debug("doAfterCompose started");
		sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		searchMenu = true;

		if (user == null) {
//			Execution exec = Executions.getCurrent();
//		    HttpServletResponse response = (HttpServletResponse)exec.getNativeResponse();
//		    response.sendRedirect(response.encodeRedirectURL("/index.zul")); //assume there is /login
//		    exec.setVoided(true);
			Executions.sendRedirect("index.zul");
		}
		super.doAfterCompose(comp);
		try {
			AppContext.debug("doAfterCompose starting...");

			gwas = (GwasFacade) AppContext.checkBean(gwas, "GwasFacade");
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
			listitemsDAO = (ListItemsDAO) AppContext.checkBean(listitemsDAO, "ListItems");

			organism = listitemsDAO.getOrganismById(9);

			AppContext.debug("init subpoptrait");
			listboxSubpopulation.setModel(new SimpleListModel(
					AppContext.createUniqueUpperLowerStrings(gwas.getSubpopulations(), false, true)));
			listboxTrait.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(
					gwas.getTraits("all varieties", radioCoTrait.isSelected()), false, true)));

			AppContext.debug("set selected");
			listboxSubpopulation.setSelectedIndex(3);
			listboxTrait.setSelectedIndex(0);
			AppContext.debug("init snplistt");
			listboxSnplist.setModel(new SimpleListModel(
					AppContext.createUniqueUpperLowerStrings(workspace.getSnpPositionPvalueListNames(), false, true)));
			AppContext.debug("init selected");
			listboxSnplist.setSelectedIndex(0);

			List listVarphen = new ArrayList();
			AppContext.debug("init varlistt");
			listVarphen.addAll(workspace.getVarietyQuantPhenotypelistNames());
			listVarphen.addAll(workspace.getVarietyCatPhenotypelistNames());
			listboxVarietylist
					.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(listVarphen, false, true)));
			AppContext.debug("init selected");
			listboxVarietylist.setSelectedIndex(0);

			// divMultipositions.setVisible(AppContext.isLocalhost());

			/*
			 * List listPvals=new ArrayList(); for(int i=50; i<9; i-=5) {
			 * listPvals.add(Integer.toString(i)); } for(int i=9; i<1; i--) {
			 * listPvals.add(Integer.toString(i)); } this.comboboxMinlogP.setModel(new
			 * SimpleListModel(listPvals));
			 */

			AppContext.debug("doAfterCompose done");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	@Listen("onClick =#radioLegacyTrait")
	public void onclickLegacy() {
		if (radioLegacyTrait.isSelected())
			listboxTrait.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(
					gwas.getTraits(listboxSubpopulation.getSelectedItem().getLabel(), radioCoTrait.isSelected()), false,
					true)));
		listboxTrait.setSelectedIndex(0);
	}

	@Listen("onClick =#radioCoTrait")
	public void onclickCOTerm() {
		if (radioCoTrait.isSelected())
			listboxTrait.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(
					gwas.getTraits(listboxSubpopulation.getSelectedItem().getLabel(), radioCoTrait.isSelected()), false,
					true)));
		listboxTrait.setSelectedIndex(0);
	}

	@Listen("onClick=#searchButton")
	public void onClickSearch() {
		onselectTrait();
	}

	public void onselectTrait() {
		if (listboxTrait.getSelectedIndex() == 0) {
			// this.initDispayManhattan();
			this.chartManhattanXY.setVisible(false);
			return;
		}
		comboboxMinlogP.setSelectedIndex(13);
		displayManhattanXY();

		Clients.evalJavaScript("myFunction();");
		resultContentDiv.setVisible(true);
		div_info.setVisible(false);

		flipSearchBar();

	}

	@Listen("onClick=#backQueryDiv")
	public void onClick$backQueryDiv() {
		Clients.evalJavaScript("myFunction();");
		flipSearchBar();
	}

	private void flipSearchBar() {

		// AppContext.setSidebar(false);

		if (searchMenu) {
			myModal.setClass("unmodal");
			searchMenu = false;
//			layoutResultId.setVisible(true);

		} else {
			myModal.setClass("modal");
			searchMenu = true;
//			layoutResultId.setVisible(false);
		}

	}

	@Listen("onClick=#closeDiv")
	public void closeDiv() {
		Clients.evalJavaScript("myFunction();");
		flipSearchBar();

	}

	@Listen("onSelect =#comboboxMinlogP")
	public void onselectMinlogp() {
		// if(comboboxMinlogP.getValue().isEmpty()) return;
		if (listboxSnplist.getSelectedIndex() > 0 && listboxVarietylist.getSelectedIndex() > 0)
			displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(),
					listboxVarietylist.getSelectedItem().getLabel());
		else if (listboxSnplist.getSelectedIndex() > 0)
			displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(), null);
		else
			displayManhattanXY();
	}

	@Listen("onChange =#comboboxMinlogP")
	public void onchangeMinlogp() {
		// if(comboboxMinlogP.getValue().isEmpty()) return;
		if (listboxSnplist.getSelectedIndex() > 0 && listboxVarietylist.getSelectedIndex() > 0)
			displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(),
					listboxVarietylist.getSelectedItem().getLabel());
		else if (listboxSnplist.getSelectedIndex() > 0)
			displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(), null);
		else
			displayManhattanXY();
	}

	@Listen("onSelect =#listboxSubpopulation")
	public void onselectSubpop() {
		List l = new ArrayList();
		l.add("");
		l.addAll(gwas.getTraits(listboxSubpopulation.getSelectedItem().getLabel(), radioCoTrait.isSelected()));
		listboxTrait.setModel(new SimpleListModel(l));
		listboxTrait.setSelectedIndex(0);
		this.chartManhattanXY.setVisible(false);
		return;

		// displayManhattanXY();
	}

	@Listen("onSelect =#listboxChromosome")
	public void onselectChr() {
		// if(comboboxMinlogP.getValue().isEmpty()) return;
		if (listboxSnplist.getSelectedIndex() > 0 && listboxVarietylist.getSelectedIndex() > 0)
			displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(),
					listboxVarietylist.getSelectedItem().getLabel());
		else
			displayManhattanXY();
	}

	@Listen("onSelect = #listboxSnplist")
	public void onselectSnpListbox() {

		if (listboxSnplist.getSelectedIndex() > 0) {

			if (listboxVarietylist.getSelectedIndex() > 0)
				displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(),
						listboxVarietylist.getSelectedItem().getLabel());
			// else Messagebox.show("Select a variety list with phenotypes");
			else
				displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(), null);

		}
	}

	@Listen("onSelect = #listboxVarietylist")
	public void onselectVarietylist() {

		if (listboxVarietylist.getSelectedIndex() > 0) {
			if (listboxSnplist.getSelectedIndex() > 0)
				displayManhattanXY(listboxSnplist.getSelectedItem().getLabel(),
						listboxVarietylist.getSelectedItem().getLabel());
			else
				Messagebox.show("Select a SNP list with -logP values");
		}

	}

	private void displayManhattanXY(String snplist, String varietylist) {

		this.listboxSubpopulation.setSelectedIndex(0);
		this.listboxTrait.setSelectedIndex(0);

		// if(snplist.isEmpty() || varietylist.isEmpty()) return;
		if (snplist.isEmpty())
			return;

		initDispayManhattan();

		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		// int minlogp=Integer.valueOf(comboboxMinlogP.getValue());
		int minlogp = Integer.valueOf(comboboxMinlogP.getSelectedItem().getLabel());
		Set setSnps = workspace.getSnpPositions(snplist.split(":")[0], snplist.split(":")[1]);

		Set setVars = null;
		Set s = new HashSet();
		s.add(dataset);
		if (varietylist != null)
			setVars = workspace.getVarieties(varietylist);
		else
			setVars = variety.getGermplasm(s);
		AppContext.debug("plotting " + setSnps.size() + " snps");

		mapPos2Values = new TreeMap();
		mapPos2Index = new TreeMap();
		mapIndex2Pos = new TreeMap();
		int idx = 0;
		Iterator itSnp = setSnps.iterator();
		while (itSnp.hasNext()) {
			MultiReferencePositionImplAllelePvalue pos = (MultiReferencePositionImplAllelePvalue) itSnp.next();
			// PositionLogPvalue pos= (PositionLogPvalue)itSnp.next();
			// AppContext.debug( pos.getClass() + " " + pos);
			// Byte chr, BigDecimal position, BigDecimal minp
			// sortPos.add(new ManhattanPlotImpl(Byte.valueOf(pos.getChr().toString()),
			// pos.getPosition() ,BigDecimal.valueOf(pos.getPvalue())));
			String posstr = pos.getChr() + "-" + pos.getPosition();

			// if(pos.getLogPvalue()<minlogp) break;
			if (pos.getMinusLogPvalue() != null)
				if (pos.getMinusLogPvalue() >= minlogp) {
					mapPos2Values.put(posstr, pos.getMinusLogPvalue());
					mapPos2Index.put(posstr, idx);
					mapIndex2Pos.put(idx, posstr);
					idx++;
				}
		}
		displayManhattanXY(mapPos2Values);

		ArrayList listPos = new ArrayList();
		listPos.addAll(mapPos2Values.keySet());
		SimpleListModel lmodel = new SimpleListModel(listPos);
		lmodel.setMultiple(true);
		this.listboxMultiPosition.setModel(lmodel);
		this.tabPosition.setDisabled(false);

	}

	@Listen("onClick =#radioGroupbyMarker")
	public void groupbyMarker() {
		listboxMarkerVar.setModel(new SimpleListModel(markerresult));
	}

	@Listen("onClick =#radioGroupbyGene")
	public void groupbyGene() {

		listboxMarkerVar.setModel(new SimpleListModel(genomics.getMarkerAnnotsByGene(markerresult, 0)));

	}

	@Listen("onClick =#radioGroupbyQtl")
	public void groupbyQtl() {
		listboxMarkerVar.setModel(new SimpleListModel(genomics.getMarkerAnnotsByQTL(markerresult, 0)));

	}

	public void searchbyMySnpListQtl() {
		// initResults();

		try {
			if (markerresult.size() > 0) {
				List<Component> listheadersmarker = listboxMarkerVar.getListhead().getChildren();
				listheadersmarker.clear();
				Listheader header = new Listheader();
				header.setLabel("CONTIG,POS,-LOGP");
				header.setWidth("150px");
				header.setSortAscending(new MarkerAnnotationSorter(true, 0));
				header.setSortDescending(new MarkerAnnotationSorter(false, 0));
				listheadersmarker.add(header);

				boolean hasPvalue = true;
				if (hasPvalue) {
					headerLogp = new Listheader();
					headerLogp.setLabel("-LOGP");
					headerLogp.setWidth("100px");
					headerLogp.setSortAscending(new MarkerAnnotationSorter(true, 1));
					headerLogp.setSortDescending(new MarkerAnnotationSorter(false, 1));
					listheadersmarker.add(headerLogp);
					AppContext.debug("headerLogp created");
				} else
					AppContext.debug("headerLogp NOT created");

				/*
				 * header=new Listheader(); header.setLabel("POSITION");
				 * header.setWidth("100px"); header.setSortAscending(new
				 * MarkerAnnotationSorter(true,1)); header.setSortDescending(new
				 * MarkerAnnotationSorter(false,1)); listheadersmarker.add(header);
				 */
				header = new Listheader();
				header.setLabel("GENE MODELS");
				header.setWidth("300px");
				header.setSortAscending(new MarkerAnnotationSorter(true, "GENE MODELS"));
				header.setSortDescending(new MarkerAnnotationSorter(false, "GENE MODELS"));
				listheadersmarker.add(header);

				// Iterator itAnnotnames = markerresult.get(0).getAnnotations().iterator();
				Iterator itAnnotnames = setAnnotations.iterator();
				while (itAnnotnames.hasNext()) {
					String annotname = (String) itAnnotnames.next();
					if (annotname.equals("GENE MODELS"))
						continue;
					header = new Listheader();
					header.setLabel(annotname.toUpperCase());
					if (setAnnotations.size() > 3)
						header.setWidth("400px");
					else
						header.setWidth("700px");
					header.setSortAscending(new MarkerAnnotationSorter(true, annotname));
					header.setSortDescending(new MarkerAnnotationSorter(false, annotname));
					listheadersmarker.add(header);
				}
			}
			;

			listboxMarkerVar.setItemRenderer(new MarkerVarListItemRenderer(setAnnotations));
			listboxMarkerVar.setModel(new SimpleListModel(markerresult));

			hboxAddtolist.setVisible(markerresult.size() > 0);
			hboxAddtolistGenes.setVisible(markerresult.size() > 0);

			// this.msgbox.setValue("Search by SNP List positions: Contig " +
			// contigname[0].trim() + ", SNP List " +
			// listboxMySNPList.getSelectedItem().getLabel() + " ... RESULT:" +
			// markerresult.size() + " markers");

			// listboxMarker.setVisible(true);
			listboxMarkerVar.setVisible(true);
			borderMarkerVar.setVisible(true);
			hboxDownload.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onlclickSearchFunction Exception", Messagebox.OK, Messagebox.ERROR);
		}

		// updateGenesetLinks();

	}

	private void searchbyMySnpListQtlOld() {

		try {
			if (markerresult.size() > 0) {
				List<Component> listheadersmarker = listboxMarkerVar.getListhead().getChildren();
				listheadersmarker.clear();
				Listheader header = new Listheader();
				header.setLabel("CONTIG,POS,-LOGP");
				header.setWidth("150px");
				header.setSortAscending(new MarkerAnnotationSorter(true, 0));
				header.setSortDescending(new MarkerAnnotationSorter(false, 0));
				listheadersmarker.add(header);
				/*
				 * header=new Listheader(); header.setLabel("POSITION");
				 * header.setWidth("100px"); header.setSortAscending(new
				 * MarkerAnnotationSorter(true,1)); header.setSortDescending(new
				 * MarkerAnnotationSorter(false,1)); listheadersmarker.add(header);
				 */
				header = new Listheader();
				header.setLabel("GENE MODELS");
				header.setWidth("300px");
				header.setSortAscending(new MarkerAnnotationSorter(true, "GENE MODELS"));
				header.setSortDescending(new MarkerAnnotationSorter(false, "GENE MODELS"));
				listheadersmarker.add(header);
				// Iterator itAnnotnames = markerresult.get(0).getAnnotations().iterator();
				Iterator itAnnotnames = setAnnotations.iterator();
				while (itAnnotnames.hasNext()) {
					String annotname = (String) itAnnotnames.next();
					if (annotname.equals("GENE MODELS"))
						continue;
					header = new Listheader();
					header.setLabel(annotname.toUpperCase());
					if (setAnnotations.size() > 3)
						header.setWidth("400px");
					else
						header.setWidth("700px");
					header.setSortAscending(new MarkerAnnotationSorter(true, annotname));
					header.setSortDescending(new MarkerAnnotationSorter(false, annotname));
					listheadersmarker.add(header);
				}
			}
			;

			listboxMarkerVar.setItemRenderer(new MarkerVarListItemRenderer(setAnnotations));
			listboxMarkerVar.setModel(new SimpleListModel(markerresult));

			if (markerresult.size() > 0)
				hboxAddtolist.setVisible(true);
			else
				hboxAddtolist.setVisible(false);
			// this.msgbox.setValue("Search by SNP List positions: Contig " +
			// contigname[0].trim() + ", SNP List " +
			// listboxMySNPList.getSelectedItem().getLabel() + " ... RESULT:" +
			// markerresult.size() + " markers");
			listboxMarkerVar.setVisible(true);
			borderMarkerVar.setVisible(true);

			hboxDownload.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onlclickSearchFunction Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick =#buttonDownloadCSV")
	public void downloadtableCSV() {
		downloadtable(",", ";");
	}

	@Listen("onClick =#buttonDownloadTab")
	public void downloadtableTab() {
		downloadtable("\t", ";");
	}

	public void downloadtable(String delimiter, String celldelimiter) {

		int plusminus = 0;

		AppContext.debug("executing downloadtable...");
		if (this.listboxMarkerVar.isVisible()) {

			StringBuffer buff = new StringBuffer();
			// buff.append("CONTIG-POSITION").append(delimiter).append("POSITION").append(delimiter).append("GENE
			// MODELS");
			buff.append("\"CONTIG,POS,-LOGP\"").append(delimiter).append("\"GENE MODELS\"");

			Set tempset = new LinkedHashSet(setAnnotations);
			// tempset.remove("GENE MODELS");

			Iterator<String> itAnnots = tempset.iterator();
			if (itAnnots.hasNext())
				buff.append(delimiter);
			while (itAnnots.hasNext()) {
				buff.append("\"").append(itAnnots.next()).append("\"");
				if (itAnnots.hasNext())
					buff.append(delimiter);
			}
			buff.append("\n");

			List displaylist = markerresult;
			if (radioGroupbyGene.isSelected())
				displaylist = genomics.getMarkerAnnotsByGene(markerresult, plusminus);
			else if (radioGroupbyQtl.isSelected())
				displaylist = genomics.getMarkerAnnotsByQTL(markerresult, plusminus);

			Iterator<MarkerAnnotation> itMarker = displaylist.iterator();
			while (itMarker.hasNext()) {
				MarkerAnnotation annot = itMarker.next();
				String rows[] = GenomicsFacadeImpl.MarkerAnnotationTable(annot, tempset, celldelimiter);
				for (int col = 0; col < rows.length; col++) {
					if (rows[col] != null)
						buff.append("\"").append(rows[col]).append("\"");

					if (col < rows.length - 1)
						buff.append(delimiter);
				}
				buff.append("\n");
			}

			String filetype = "text/plain";
			String fileext = ".txt";
			if (delimiter.equals(",")) {
				filetype = "text/csv";
				fileext = ".csv";
			}

			try {
				String filename = "markers-" + AppContext.createTempFilename() + fileext;
				AppContext.debug("downloading... " + filename);
				Filedownload.save(buff.toString(), filetype, filename);
				// AppContext.debug("File download complete! Saved to: "+filename);
				org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
				AppContext.debug("markers download complete!" + filename + " " + markerresult.size()
						+ " MarkerAnnots,  Downloaded to:" + zksession.getRemoteHost() + "  "
						+ zksession.getRemoteAddr());
			} catch (Exception ex) {
				ex.printStackTrace();
				Messagebox.show(ex.getMessage());
			}

		}
	}

	@Listen("onClick =#buttonAddToListSnp")
	public void onclickAddtolistSnp() {
		// add to locus list

		try {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			List addSnplist = null;

			if (this.listboxMarkerVar.isVisible()) {
				addSnplist = new ArrayList();
				Iterator<MarkerAnnotation> itMarker = markerresult.iterator();
				while (itMarker.hasNext()) {
					MarkerAnnotation m = itMarker.next();
					addSnplist.add(new PositionLogPvalueImpl(m.getContig(), m.getPosition(), m.getChr().intValue(),
							m.getMinusLogPvalue()));
				}
				// addSnplist= markerresult;
			} else if (mapPos2Values != null && !mapPos2Values.isEmpty()) {
				addSnplist = new ArrayList();
				Iterator<String> itPos = mapPos2Values.keySet().iterator();
				while (itPos.hasNext()) {
					String posstr = itPos.next();
					String pos[] = posstr.split("\\-");
					addSnplist.add(new PositionLogPvalueImpl(pos[0], BigDecimal.valueOf(Long.valueOf(pos[1])),
							Integer.valueOf(AppContext.guessChrFromString(pos[0])), mapPos2Values.get(posstr)));
				}
			}

			if (addSnplist != null && addSnplist.size() == 0) {
				Messagebox.show("EMPTY SNP LIST");
				return;
			}
			if (txtboxListnameSnp.getValue().isEmpty()) {
				Messagebox.show("PROVIDE LIST NAME");
				return;
			}

			workspace.addSnpPositionList("ANY", txtboxListnameSnp.getValue(), new LinkedHashSet(addSnplist), false,
					true);
			txtboxListnameSnp.setValue("");

			/*
			 * List listNew = new ArrayList(); listNew.add(""); listNew.addAll(
			 * workspace.getSnpPositionPvalueListNames());
			 * listNew.add("create new list..."); this.listboxMyLocusList.setModel(new
			 * SimpleListModel(listNew ));
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Addtolist Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

	public void onclickAddtolistSnpOld() {
		// add to locus list

		try {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			List addSnplist = null;

			if (this.listboxMarkerVar.isVisible()) {
				addSnplist = markerresult;
			}

			if (addSnplist != null && addSnplist.size() == 0) {
				Messagebox.show("EMPTY LOCUS LIST");
				return;
			}
			if (txtboxListnameSnp.getValue().isEmpty()) {
				Messagebox.show("PROVIDE LIST NAME");
				return;
			}

			workspace.addSnpPositionList("ANY", txtboxListnameSnp.getValue(), new LinkedHashSet(addSnplist), false,
					true);
			txtboxListnameSnp.setValue("");

			/*
			 * List listNew = new ArrayList(); listNew.add(""); listNew.addAll(
			 * workspace.getSnpPositionPvalueListNames());
			 * listNew.add("create new list..."); this.listboxMyLocusList.setModel(new
			 * SimpleListModel(listNew ));
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Addtolist Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick =#buttonAddToListGene")
	public void onclickAddtolistGene() {
		// add to locus list

		try {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			List addLocuslist = null;

			if (this.listboxMarkerVar.isVisible()) {
				Set[] setLociName = collectLoci();
				AppContext.debug("adding genes " + setLociName[0] + setLociName[1]);
				Set setLoci = new LinkedHashSet();
				if (!setLociName[0].isEmpty())
					setLoci.addAll(genomics.getLocusByName(setLociName[0]));
				if (!setLociName[1].isEmpty())
					setLoci.addAll(genomics.getLocusByName(setLociName[1]));
				addLocuslist = new ArrayList();
				addLocuslist.addAll(setLoci);
			}
			if (addLocuslist != null && addLocuslist.size() == 0) {

				Messagebox.show("EMPTY LOCUS LIST");
				return;
			}
			if (txtboxListnameGene.getValue().isEmpty()) {
				Messagebox.show("PROVIDE LIST NAME");
				return;
			}

			if (user != null)
				WorkspaceLoadLocal.writeListToUserList(txtboxListnameGene.getValue(),
						WebConstants.LOCUS_DIR + File.separator, new LinkedHashSet(addLocuslist), user.getEmail());

			workspace.addLocusList(txtboxListnameGene.getValue().trim(), new LinkedHashSet(addLocuslist));
			txtboxListnameGene.setValue("");

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Addtolist Exception", Messagebox.OK, Messagebox.ERROR);
		}

	}

	private Set[] collectLoci() {
		Set setMSU = new HashSet();
		Set setRAP = new HashSet();
		if (this.listboxMarkerVar.isVisible()) {
			/*
			 * boolean includeRicenet= checkboxListRicenet.isChecked(); boolean
			 * includeGenemodel= checkboxListModel.isChecked(); boolean includePrin=
			 * checkboxListPrin.isChecked(); boolean includePromoter=
			 * checkboxListPromoter.isChecked();
			 */
			boolean includeRicenet = checkboxIncludeInteractions.isChecked();
			boolean includePrin = includeRicenet;
			boolean includePromoter = checkboxIncludePromoters.isChecked();
			boolean includeGenemodel = true;
			/*
			 * <checkbox id="checkboxIncludeInteractions"
			 * label="Interactions (RiceNetv2,PRIN)"/> <checkbox
			 * id="checkboxIncludePromoters" label="Promoters (FGenesh++,PlantPromDB)"/>
			 * <checkbox id="checkboxIncludeGO" label="Gene Ontology"/> <checkbox
			 * id="checkboxIncludePOTO" label="PO, TO, OGRO"/> <checkbox
			 * id="checkboxIncludeQTL" label="Q-TARO QTLs"/>
			 */
			Iterator<MarkerAnnotation> itMarker = markerresult.iterator();
			while (itMarker.hasNext()) {
				MarkerAnnotation annot = itMarker.next();
				Set allgenes = new HashSet();

				if (includeGenemodel) {
					if (annot.getAnnotation(MarkerAnnotation.GENE_MODEL) != null
							&& !annot.getAnnotation(MarkerAnnotation.GENE_MODEL).isEmpty()) {
						allgenes.addAll(annot.getAnnotation(MarkerAnnotation.GENE_MODEL));
					}
				}

				if (includeRicenet) {
					if (annot.getAnnotation(MarkerAnnotation.GENE_INT_RICENETV2) != null
							&& !annot.getAnnotation(MarkerAnnotation.GENE_INT_RICENETV2).isEmpty()) {
						allgenes.addAll(annot.getAnnotation(MarkerAnnotation.GENE_INT_RICENETV2));
					}
				}
				if (includePrin) {
					if (annot.getAnnotation(MarkerAnnotation.GENE_INT_PRINEXPT) != null
							&& !annot.getAnnotation(MarkerAnnotation.GENE_INT_PRINEXPT).isEmpty()) {
						allgenes.addAll(annot.getAnnotation(MarkerAnnotation.GENE_INT_PRINEXPT));
					}
					if (annot.getAnnotation(MarkerAnnotation.GENE_INT_PRINPRED) != null
							&& !annot.getAnnotation(MarkerAnnotation.GENE_INT_PRINPRED).isEmpty()) {
						allgenes.addAll(annot.getAnnotation(MarkerAnnotation.GENE_INT_PRINPRED));
					}
				}
				if (includePromoter) {
					if (annot.getAnnotation(MarkerAnnotation.PROM_FGENESH1K) != null
							&& !annot.getAnnotation(MarkerAnnotation.PROM_FGENESH1K).isEmpty()) {
						allgenes.addAll(annot.getAnnotation(MarkerAnnotation.PROM_FGENESH1K));
					}

					if (annot.getAnnotation(MarkerAnnotation.PROM_PLANTPROMDB) != null
							&& !annot.getAnnotation(MarkerAnnotation.PROM_PLANTPROMDB).isEmpty()) {
						allgenes.addAll(annot.getAnnotation(MarkerAnnotation.PROM_PLANTPROMDB));
					}
				}

				Iterator<Locus> itLocus = allgenes.iterator();
				while (itLocus.hasNext()) {
					Locus loc = itLocus.next();
					if (loc.getUniquename().toUpperCase().startsWith("LOC_"))
						setMSU.add(loc.getUniquename());
					else if (loc.getUniquename().toUpperCase().startsWith("OS0")
							|| loc.getUniquename().toUpperCase().startsWith("OS1"))
						setRAP.add(loc.getUniquename());
					if (loc instanceof MergedLoci) {
						MergedLoci ml = (MergedLoci) loc;
						StringBuffer loci = new StringBuffer();
						if (ml.getMSU7Name() != null) {
							String[] locnames = ml.getMSU7Name().split(",");
							for (int iloc = 0; iloc < locnames.length; iloc++)
								setMSU.add(locnames[iloc]);
						}
						if (ml.getRAPRepName() != null) {
							String[] locnames = ml.getRAPRepName().split(",");
							for (int iloc = 0; iloc < locnames.length; iloc++)
								setRAP.add(locnames[iloc]);
						}
						if (ml.getRAPPredName() != null) {
							String[] locnames = ml.getRAPPredName().split(",");
							for (int iloc = 0; iloc < locnames.length; iloc++)
								setRAP.add(locnames[iloc]);
						}
					}

					if (loc instanceof LocusPromoter) {
						Set proms = ((LocusPromoter) loc).getOverlappingGenes();
						Iterator<String> itProms = proms.iterator();
						while (itProms.hasNext()) {
							String promname = itProms.next();
							if (promname.toUpperCase().startsWith("LOC_"))
								setMSU.add(promname);
							else if (promname.toUpperCase().startsWith("OS0")
									|| promname.toUpperCase().startsWith("OS1"))
								setRAP.add(promname);
						}
					}

					if (loc.getUniquename().startsWith("OsNippo") && !(loc instanceof MergedLoci)
							&& !(loc instanceof LocusPromoter)) {
						AppContext.debug(
								"loc.getUniquename().startsWith(OsNippo) && !(loc instanceof MergedLoci) && !(loc instanceof LocusPromoter) for "
										+ loc.getUniquename());
					}

				}

				/*
				 * if(annot.getAnnotation(MarkerAnnotation.GENE_MODEL)!=null &&
				 * !annot.getAnnotation(MarkerAnnotation.GENE_MODEL).isEmpty()) {
				 * Iterator<Locus>
				 * itLocus=annot.getAnnotation(MarkerAnnotation.GENE_MODEL).iterator();
				 * while(itLocus.hasNext()) { Locus loc= itLocus.next();
				 * if(loc.getUniquename().toUpperCase().startsWith("LOC_"))
				 * setMSU.add(loc.getUniquename()); else
				 * if(loc.getUniquename().toUpperCase().startsWith("OS0") ||
				 * loc.getUniquename().toUpperCase().startsWith("OS1"))
				 * setRAP.add(loc.getUniquename()); if(loc instanceof MergedLoci) { MergedLoci
				 * ml=(MergedLoci)loc; StringBuffer loci=new StringBuffer();
				 * if(ml.getMSU7Name()!=null) setMSU.add(ml.getMSU7Name());
				 * if(ml.getRAPRepName()!=null) setRAP.add( ml.getRAPRepName());
				 * if(ml.getRAPPredName()!=null) setRAP.add( ml.getRAPPredName()); } } }
				 */

			}
		}

		return new Set[] { setMSU, setRAP };
	}

	private void initDispayManhattan() {
		// this.chartManhattanXY.setModel(new DefaultCategoryModel());
		msgJbrowse.setVisible(false);
		iframeJbrowse.setVisible(false);
		// gridMarker.setVisible(false);
		borderMarkerVar.setVisible(false);
		listboxMarkerVar.setVisible(false);
		labelAnnotations.setVisible(false);
		this.labelManhattan.setValue("");
	}

	private void displayManhattanXY() {

		// listboxSnplist.setSelectedIndex(0);
		Set snplist = null;
		if (listboxSnplist.getSelectedIndex() > 0) {
			String listname[] = listboxSnplist.getSelectedItem().getLabel().split(":");
			snplist = workspace.getSnpPositions(listname[0], listname[1]);
		}
		this.listboxVarietylist.setSelectedIndex(0);

		String trait = listboxTrait.getSelectedItem().getLabel();
		String subpop = this.listboxSubpopulation.getSelectedItem().getLabel();

		initDispayManhattan();
		if (trait == null || trait.isEmpty() || subpop == null || subpop.isEmpty()) {
			if (listboxSnplist.getSelectedIndex() == 0)
				return;
		}

		// double minlogp= Double.valueOf(comboboxMinlogP.getValue());
		double minlogp = Double.valueOf(comboboxMinlogP.getSelectedItem().getLabel());

		AxisLabels xlabels = chartManhattanXY.getXAxis().getLabels();
		xlabels.setRotation(-80);
		xlabels.setAlign("right");
		// chartManhattanXY.getXAxis().setMinorTickInterval((Number)null);
		chartManhattanXY.getYAxis().setTitle("-logP");
		chartManhattanXY.setTitle("Manhattan plot");

		readManhattanXY(trait, subpop, minlogp, snplist, listboxChromosome.getSelectedItem().getLabel());

		List listpos = new ArrayList();
		listpos.addAll(mapPos2Index.keySet());
		this.listboxPosition.setModel(new SimpleListModel(listpos));
		SimpleListModel lmodel = new SimpleListModel(listpos);
		lmodel.setMultiple(true);
		this.listboxMultiPosition.setModel(lmodel);

		if (mapPos2Index.size() == 0)
			labelManhattan.setValue(this.mapPos2Index.size() + " markers, decrease Minimum -logP.");
		else
			labelManhattan.setValue(this.mapPos2Index.size()
					+ " markers, select a narrow region to zoom, show JBrowse tracks and marker annotations. Click a marker to display allele distributions and varieties.");

		this.tabRegion.setSelected(true);

		if (this.listboxSnplist.getSelectedIndex() > 0)
			this.tabPosition.setDisabled(false);
		else
			this.tabPosition.setDisabled(true);

		if (mapPos2Index.size() < 100) {
			tabPosition.setDisabled(false);
		}

		getExportingButtons();

	}

	private void getExportingButtons() {
		Exporting exporting = chartManhattanXY.getExporting();
		ExportingButton buttons = exporting.getButtons();
		List<MenuItem> menuItems = new ArrayList<>();

		// optional rebuild the default menu items, otherwise they are replaced
		menuItems.add(defaultMenuItem("printChart", "this.print();"));
		menuItems.add(separator());
		menuItems.add(defaultMenuItem("downloadPNG", "this.exportChart();"));
		menuItems.add(defaultMenuItem("downloadJPEG", "this.exportChart({type: \"image/jpeg\"});"));
		menuItems.add(defaultMenuItem("downloadPDF", "this.exportChart({type: \"application/pdf\"});"));
		menuItems.add(defaultMenuItem("downloadSVG", "this.exportChart({type: \"image/svg+xml\"});"));
		menuItems.add(separator());
		// menuItems.add(customMenuItem("Download CSV", "this.exportChart({type:
		// \"image/svg+xml\"});"));
		menuItems.add(customMenuItem("Download CSV File", fireServerEventScript(ON_MY_CUSTOM_ITEM)));

		buttons.setMenuItems(menuItems);

	}

	private String fireServerEventScript(String eventName) {
		return "var chartsWidget = zk(evt.target).$(); "

				+ "chartsWidget.fire('" + eventName + "', null, {toServer: true});";
	}

	@Listen(ON_MY_CUSTOM_ITEM + " = #chartManhattanXY")
	public void handleMyCustomItem() {
		Filedownload.save(sb.toString().getBytes(), "text/csv", "dataset.csv");
	}

	private MenuItem customMenuItem(String text, String onclickJS) {
		MenuItem menuItem = new MenuItem();
		menuItem.setText(text);
		menuItem.setOnclick(new JavaScriptValue("function(evt) {" + onclickJS + "}"));
		return menuItem;
	}

	private MenuItem separator() {
		MenuItem menuItem = new MenuItem();
		menuItem.addExtraAttr("separator", new AnyVal<Boolean>(true));
		return menuItem;
	}

	private MenuItem defaultMenuItem(String textKey, String onclickJS) {
		MenuItem menuItem = new MenuItem();
		menuItem.addExtraAttr("textKey", new AnyVal<String>(textKey));
		menuItem.setOnclick(new JavaScriptValue("function(evt) {" + onclickJS + "}"));
		return menuItem;
	}

	private Integer[] convertX2ChrPos(Integer val) {

		Integer chr = 0;
		Integer off = 0;
		if (val > 345713663) {
			chr = 12;
			off = 345713663;
		} else if (val > 316692557) {
			chr = 11;
			off = 316692557;
		} else if (val > 293485270) {
			chr = 10;
			off = 293485270;
		} else if (val > 270472550) {
			chr = 9;
			off = 270472550;
		} else if (val > 242029528) {
			chr = 8;
			off = 242029528;
		} else if (val > 212331907) {
			chr = 7;
			off = 212331907;
		} else if (val > 181083120) {
			chr = 6;
			off = 181083120;
		} else if (val > 151124686) {
			chr = 5;
			off = 151124686;
		} else if (val > 115621992) {
			chr = 4;
			off = 115621992;
		} else if (val > 79208173) {
			chr = 3;
			off = 79208173;
		} else if (val > 43270923) {
			chr = 2;
			off = 43270923;
		} else {
			chr = 1;
			off = 0;
		}
		;
		return new Integer[] { chr, val - off };
	}

	@Listen("onSelection = #chartManhattanXY")
	public void doSelectionXY(ChartsSelectionEvent event) {
		// doing the zooming in function

		int min = event.getXAxisMin().intValue();
		int max = event.getXAxisMax().intValue();
		prevMinX = min;
		prevMaxX = max;
		AppContext.debug("event.getName()=" + event.getName() + "  min=" + min + " max=" + max); // + " interval=" +
																									// interval);
		doSelectionXY(min, max);
		checkboxIncludeInteractions.setChecked(false);
		checkboxIncludePromoters.setChecked(false);
		checkboxIncludeGO.setChecked(false);
		checkboxIncludePOTO.setChecked(false);
		checkboxIncludeQTL.setChecked(false);

	}

	public void doSelectionXY(int min, int max) {
		// doing the zooming in function
		try {

			msgJbrowse.setVisible(false);
			iframeJbrowse.setVisible(false);
			borderMarkerVar.setVisible(false);
			listboxMarkerVar.setVisible(false);
			this.labelAnnotations.setVisible(false);

			Integer minpos[] = this.convertX2ChrPos(min);
			Integer maxpos[] = this.convertX2ChrPos(max);

			// long interval = Math.max(1, Double.valueOf((max-min)/nLabels).longValue());
			AppContext.debug("left=" + minpos[0] + "-" + minpos[1] + ", right=" + maxpos[0] + "-" + maxpos[1]);
			// chartManhattan.getXAxis().getLabels().setStep( interval );

			/*
			 * if(mapIndex2Pos.get(min)==null) {
			 * AppContext.debug("mapIndex2Pos.get(min)==null" + min); return; }
			 * if(mapIndex2Pos.get(max)==null) {
			 * AppContext.debug("mapIndex2Pos.get(max)==null" + max); return; }
			 */

			int globalidxmin = minpos[1];
			int globalidxmax = maxpos[1];

			if (minpos[0].equals(maxpos[0])) {
				String chr = minpos[0].toString();
				String chrstr = minpos[0].toString();
				if (chr.length() == 1)
					chr = "chr0" + chr;
				else
					chr = "chr" + chr;

				List listPosstr = new ArrayList();
				List listPos = new ArrayList();
				for (int idx = globalidxmin; idx <= globalidxmax; idx++) {
					String chrpos = chrstr + "-" + idx;
					if (this.mapPos2Index.containsKey(chrpos)) {
						// listPos.add( BigDecimal.valueOf( Long.valueOf( idx )));

						// mapPos2Values

						// listPos.add( new PositionImpl(chr, BigDecimal.valueOf( Long.valueOf( idx
						// ))));
						// String contig, BigDecimal position, Integer chr, Double minuslogp
						listPos.add(new PositionLogPvalueImpl(chr, BigDecimal.valueOf(Long.valueOf(idx)),
								Integer.valueOf(AppContext.guessChrFromString(chr)), mapPos2Values.get(chrpos)));
						listPosstr.add(chrpos);
					}
					// if(mapIndex2Pos.containsKey(idx)) listPos.add( BigDecimal.valueOf(
					// Long.valueOf( mapIndex2Pos.get(idx).split("\\-")[1] )));
				}

				labelAnnotations.setVisible(false);
				if (listPos.size() > this.maxAnnotMarkers) {
					labelAnnotations.setValue(
							"Too many markers to annotate. Limit selection to <" + maxAnnotMarkers + " markers.");
					labelAnnotations.setVisible(true);
					this.tabPosition.setDisabled(true);
					listboxPosition.setModel(new SimpleListModel(new ArrayList()));
					listboxMultiPosition.setModel(new SimpleListModel(new ArrayList()));
				} else {
					this.tabPosition.setDisabled(false);
					updateMarkerAnnotations(chr, listPos);
					this.listboxPosition.setModel(new SimpleListModel(listPosstr));
					SimpleListModel lmodel = new SimpleListModel(listPosstr);
					lmodel.setMultiple(true);
					this.listboxMultiPosition.setModel(lmodel);
				}

				if (listPos.size() > 0)
					updateJBrowse(chr, Integer.toString(globalidxmin), Integer.toString(globalidxmax), "");
				labelManhattan.setValue(listPos.size()
						+ " markers, select a narrow region to zoom, show JBrowse tracks and marker annotations. Click a marker to display allele distributions and varieties.");

			} else {

				msgJbrowse.setValue(
						"Cannot display multiple contigs/chromosomes in JBrowse. Narrow selected region to a single contig/chromosome");
				msgJbrowse.setVisible(true);

				String chr = minpos[0].toString();
				String chrstr = minpos[0].toString();
				if (chr.length() == 1)
					chr = "chr0" + chr;
				else
					chr = "chr" + chr;
				List listPosstr = new ArrayList();
				List listPos = new ArrayList();
				for (int idx = globalidxmin; idx <= globalidxmax; idx++) {
					String chrpos = chrstr + "-" + idx;
					if (this.mapPos2Index.containsKey(chrpos)) {
						listPos.add(BigDecimal.valueOf(Long.valueOf(idx)));
						listPosstr.add(chrpos);
					}
					// if(mapIndex2Pos.containsKey(idx)) listPos.add( BigDecimal.valueOf(
					// Long.valueOf( mapIndex2Pos.get(idx).split("\\-")[1] )));
				}

				labelAnnotations.setVisible(false);
				if (listPos.size() > this.maxAnnotMarkers) {
					labelAnnotations.setValue(
							"Too many markers to annotate. Limit selection to <" + maxAnnotMarkers + " markers.");
					labelAnnotations.setVisible(true);
					this.tabPosition.setDisabled(true);
					listboxPosition.setModel(new SimpleListModel(new ArrayList()));
					listboxMultiPosition.setModel(new SimpleListModel(new ArrayList()));
				} else {
					this.tabPosition.setDisabled(false);
					updateMarkerAnnotations(chr, listPos);
					this.listboxPosition.setModel(new SimpleListModel(listPosstr));
					SimpleListModel lmodel = new SimpleListModel(listPosstr);
					lmodel.setMultiple(true);
					this.listboxMultiPosition.setModel(lmodel);

				}

			}

			AppContext.debug(listboxPosition.getRows() + " positions in listboxPosition");

			/*
			 * Map<String, Double> mapZoomManhattan=new LinkedHashMap(); for(int
			 * idx=globalidxmin; idx<=globalidxmax; idx++) { String zoompos =
			 * mapIndex2Pos.get(idx); mapZoomManhattan.put(zoompos ,
			 * mapPos2Values.get(zoompos)); } chartManhattan.setModel(
			 * displayManhattan(mapZoomManhattan) );
			 * chartManhattan.getPlotOptions().getSeries().setLineWidth(0);
			 * chartManhattan.getPlotOptions().getSeries().getMarker().setWidth(3);
			 */

			this.tabRegion.setSelected(true);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onPlotClick = #chartManhattanXY")
	public void showMessageXY(ChartsEvent event) {
		// Open an invisible popup at where the point clicked.
		List listpos = new ArrayList();
		// event.getPoint().getDataLabels()

		int pointindex = event.getPointIndex();
		String clicked = this.mapIndex2Pos.get(pointindex);

		long off = 0;
		int chr = Integer.valueOf(event.getSeries().getName().replace("chr0", "").replace("chr", ""));
		long val = event.getPoint().getX().longValue();
		if (val > 345713663) {
			chr = 12;
			off = 345713663;
		} else if (val > 316692557) {
			chr = 11;
			off = 316692557;
		} else if (val > 293485270) {
			chr = 10;
			off = 293485270;
		} else if (val > 270472550) {
			chr = 9;
			off = 270472550;
		} else if (val > 242029528) {
			chr = 8;
			off = 242029528;
		} else if (val > 212331907) {
			chr = 7;
			off = 212331907;
		} else if (val > 181083120) {
			chr = 6;
			off = 181083120;
		} else if (val > 151124686) {
			chr = 5;
			off = 151124686;
		} else if (val > 115621992) {
			chr = 4;
			off = 115621992;
		} else if (val > 79208173) {
			chr = 3;
			off = 79208173;
		} else if (val > 43270923) {
			chr = 2;
			off = 43270923;
		} else {
			chr = 1;
			off = 0;
		}
		;

		// return 'chr ' + chr + '-' + (val-off) + ', -logP=' + this.y.toFixed(2);}
		clicked = chr + "-" + (val - off);

		AppContext.debug("clicked=" + clicked);

		listpos.add(clicked);
		this.tabPosition.setSelected(true);
		listboxPosition.setModel(new SimpleListModel(listpos));
		listboxPosition.setSelectedIndex(0);
		SimpleListModel lmodel = new SimpleListModel(listpos);
		lmodel.setMultiple(true);
		this.listboxMultiPosition.setModel(lmodel);
		listboxMultiPosition.setSelectedIndex(0);
		onselectListboxPositions(listpos);

		AppContext.debug(listpos + " in listboxPosition");

		/*
		 * anchor.open(chart, "at_pointer"); Point point = event.getPoint();
		 * msgBox.setTitle(event.getSeries().getName()); // Locate the window's position
		 * by popup. msgBox.setTop(anchor.getTop()); msgBox.setLeft(anchor.getLeft());
		 * Label msg = (Label) msgBox.getFellow("msg"); String formattedDate =
		 * TimeUtil.getFormattedTime((Long) event.getCategory(),
		 * "EEEEEEEEE, MMM dd, yyyy"); msg.setValue(formattedDate + ":\n" + point.getY()
		 * + " visits"); msgBox.setVisible(true);
		 */
	}

	// mapGenotype2Phenotype2Count,chartGenotypeHist, "Genotype count", sPhenotype);
	// plotXYHistogram(mapSubpop2Phenotype2Count,chartSubpopHist, "Variety count",
	// sPhenotype);

	@Listen("onSelection = #chartGenotypeHist")
	public void doSelectionGenotypeHist(ChartsSelectionEvent event) {
		// doing the zooming in function
		String str = " count";
		if (checkboxNormalize.isChecked())
			str = " frequency";

		List listVarieties = createVarlistFromSelection(event, mapGenotype2Phenotype2Count,
				this.listboxTrait.getSelectedItem().getLabel(), "Genotype" + str);
		listboxVariety.setModel(new SimpleListModel(listVarieties));
		listboxVariety.setItemRenderer(new VarietyListItemRenderer(false));
		divVarietylist.setVisible(true);

	}

	@Listen("onSelection = #chartAlleleHist")
	public void doSelectionAlleleHist(ChartsSelectionEvent event) {
		// doing the zooming in function
		String str = " count";
		if (checkboxNormalize.isChecked())
			str = " frequency";

		List listVarieties = createVarlistFromSelection(event, mapAllele2Phenotype2Set, mapAllele2Phenotype2Count,
				this.listboxTrait.getSelectedItem().getLabel(), "Allele" + str);
		listboxVariety.setModel(new SimpleListModel(listVarieties));
		listboxVariety.setItemRenderer(new VarietyListItemRenderer(false));
		divVarietylist.setVisible(true);

	}

	@Listen("onSelection = #chartSubpopHist")
	public void doSelectionSubpopHist(ChartsSelectionEvent event) {
		// doing the zooming in function
		String str = " count";
		if (checkboxNormalize.isChecked())
			str = " frequency";
		List listVarieties = createVarlistFromSelection(event, mapSubpop2Phenotype2Count,
				this.listboxTrait.getSelectedItem().getLabel(), "Variety" + str);
		listboxVariety.setModel(new SimpleListModel(listVarieties));
		listboxVariety.setItemRenderer(new VarietyListItemRenderer(false));
		divVarietylist.setVisible(true);

	}

	@Listen("onSelection = #chartGenSubpopHist")
	public void doSelectionSubpopgenHist(ChartsSelectionEvent event) {
		// doing the zooming in function
		String str = " count";
		if (checkboxNormalize.isChecked())
			str = " frequency";
		List listVarieties = createVarlistFromSelection(event, mapSubpopGen2Phenotype2Count,
				this.listboxTrait.getSelectedItem().getLabel(), "Variety" + str);
		listboxVariety.setModel(new SimpleListModel(listVarieties));
		listboxVariety.setItemRenderer(new VarietyListItemRenderer(false));
		divVarietylist.setVisible(true);

	}

	private List createVarlistFromSelection(ChartsSelectionEvent event, Map mapGroup2X2Y, String xlabel,
			String ylabel) {
		return createVarlistFromSelection(event, mapGroup2X2Y, null, xlabel, ylabel);
	}

	private int collectionTotal(Map<Object, Collection> mapPhen2Group) {
		int total = 0;
		Iterator<Collection> itCol = mapPhen2Group.values().iterator();
		while (itCol.hasNext())
			total += itCol.next().size();
		return total;
	}

	private List createVarlistFromSelection(ChartsSelectionEvent event, Map mapGroup2X2Y, Map mapGroup2X2YCount,
			String xlabel, String ylabel) {
		Double xmin = (Double) event.getXAxisMin();
		Double xmax = (Double) event.getXAxisMax();
		/*
		 * Integer ymin= event.getYAxisMin().intValue()-1; Integer ymax =
		 * event.getYAxisMax().intValue()+1;
		 */

		Number ymin = null;
		Number ymax = null;

		boolean isNormalized = false;
		if (this.tabPosition.isSelected()) {
			if (this.checkboxNormalize.isChecked()) {
				ymin = event.getYAxisMin().doubleValue() - 0.01;
				ymax = event.getYAxisMax().doubleValue() + 0.01;
				isNormalized = true;
			} else {
				ymin = event.getYAxisMin().intValue();
				ymax = event.getYAxisMax().intValue() + 1;
			}
		} else if (this.tabPopulation.isSelected()) {
			if (this.checkboxNormalizePopulation.isChecked()) {
				ymin = event.getYAxisMin().doubleValue() - 0.01;
				ymax = event.getYAxisMax().doubleValue() + 0.01;
				isNormalized = true;
			} else {
				ymin = event.getYAxisMin().intValue();
				ymax = event.getYAxisMax().intValue() + 1;
			}
		}

		// Integer ymin=yminnum.intValue();
		// Integer ymax=ymaxnum.intValue();

		String strait = this.listboxTrait.getSelectedItem().getLabel();

		listVarieties = new ArrayList();
		Map<BigDecimal, Variety> mapVarid2Var = variety.getMapId2Variety(dataset);
		// Iterator<Map<Object,Collection>> itMapPhen2Col =
		// mapGroup2X2Y.values().iterator();
		Iterator itMapGroups = mapGroup2X2Y.keySet().iterator();
		while (itMapGroups.hasNext()) {
			Object groups = itMapGroups.next();
			Map<Object, Collection> mapPhen2Col = (Map<Object, Collection>) mapGroup2X2Y.get(groups);
			int total = 1;
			if (isNormalized)
				total = collectionTotal(mapPhen2Col);

			if (mapGroup2X2YCount != null) {

				Map<Object, Integer> mapPhen2Count = (Map<Object, Integer>) mapGroup2X2YCount.get(groups);
				Iterator itPhen = mapPhen2Col.keySet().iterator();
				while (itPhen.hasNext()) {
					Comparable x = (Comparable) itPhen.next();
					Integer phenvarcount = mapPhen2Count.get(x);
					Double dx = ((BigDecimal) x).doubleValue();
					if (dx.compareTo(xmin) >= 0 && dx.compareTo(xmax) <= 0) {
						Collection cols = mapPhen2Col.get(x);
						// if(cols.size()>=ymin.doubleValue()*total &&
						// cols.size()<=ymax.doubleValue()*total) {
						// if(phenvarcount>=ymin && cols.size()<=phenvarcount) {
						if (phenvarcount >= ymin.doubleValue() * total
								&& cols.size() <= phenvarcount.doubleValue() * total) {
							Iterator<BigDecimal> itVars = cols.iterator();
							while (itVars.hasNext()) {
								Variety thisvar = mapVarid2Var.get(itVars.next());
								VarietyPlusPlus phevar = new VarietyPlusPlusImpl(thisvar, strait,
										mapVarid2Phenotype.get(thisvar.getVarietyId()));
								// listVarieties.add( mapVarid2Var.get(itVars.next()));
								phevar.addValue("Genotype",
										this.mapVarid2Genotype.get(thisvar.getVarietyId().longValue()));
								listVarieties.add(phevar);
							}
						}
					}
				}
			} else {
				Iterator itPhen = mapPhen2Col.keySet().iterator();
				while (itPhen.hasNext()) {
					Comparable x = (Comparable) itPhen.next();
					Double dx = ((BigDecimal) x).doubleValue();
					if (dx.compareTo(xmin) >= 0 && dx.compareTo(xmax) <= 0) {
						Collection cols = mapPhen2Col.get(x);
						// if(cols.size()>=ymin && cols.size()<=ymax) {
						if (cols.size() >= ymin.doubleValue() * total && cols.size() <= ymax.doubleValue() * total) {
							Iterator<BigDecimal> itVars = cols.iterator();
							while (itVars.hasNext()) {
								// listVarieties.add( mapVarid2Var.get(itVars.next()));
								Variety thisvar = mapVarid2Var.get(itVars.next());
								VarietyPlusPlus phevar = new VarietyPlusPlusImpl(thisvar, strait,
										mapVarid2Phenotype.get(thisvar.getVarietyId()));
								// listVarieties.add( mapVarid2Var.get(itVars.next()));
								phevar.addValue("Genotype",
										this.mapVarid2Genotype.get(thisvar.getVarietyId().longValue()));
								listVarieties.add(phevar);

							}
						}
					}
				}
			}
		}

		NumberFormat formatter = new DecimalFormat("#0.00");
		this.labelVarietyMsg.setValue(xlabel + " between " + formatter.format(xmin) + " and " + formatter.format(xmax)
				+ ", " + ylabel + " between " + formatter.format(ymin) + " and " + formatter.format(ymax));
		labelVarietyMsg.setVisible(true);
		listheaderPhenotype.setLabel(xlabel);

		// update table header
		Map<String, String> listheaders = new LinkedHashMap();
		listheaders.put("NAME", "name");
		listheaders.put("IRIS ID", "irisId");
		listheaders.put("ACCESSION", "accession");
		listheaders.put("SUBPOPULATION", "subpopulation");
		listheaders.put("COUNTRY", "country");
		listheaders.put(strait, "");
		listheaders.put("Genotype", "");
		Listhead lhd = this.listboxVariety.getListhead();
		lhd.getChildren().clear();
		Iterator<String> itHeader = listheaders.keySet().iterator();
		while (itHeader.hasNext()) {
			Listheader lh = new Listheader();
			String headlabel = itHeader.next();
			lh.setLabel(headlabel);

			String field = listheaders.get(headlabel);
			if (!field.isEmpty())
				lh.setSort("auto(" + field + ")");
			else {
				VarietyPlusPlusComparator compAsc = new VarietyPlusPlusComparator(true, headlabel);
				VarietyPlusPlusComparator compDesc = new VarietyPlusPlusComparator(false, headlabel);
				lh.setSortAscending(compAsc);
				lh.setSortDescending(compDesc);
			}
			lh.setParent(lhd);
		}

		return listVarieties;
	}

	private DefaultCategoryModel readManhattan(String trait, String subpop, Double minlogP, String region) {
		gwas = (GwasFacade) AppContext.checkBean(gwas, "GwasFacade");
		AppContext.resetTimer("querying p values");
		mapPos2Values = gwas.getPosstr2MinusLogP(trait, subpop, minlogP, region);
		if (mapPos2Values == null) {
			Messagebox.show("No GWAS run for trait: " + trait + ", population: " + subpop);
			return null;
		}
		AppContext.resetTimer("displaying p values");
		mapPos2Index = new TreeMap();
		mapIndex2Pos = new TreeMap();
		int idx = 0;
		Iterator<String> itPos = mapPos2Values.keySet().iterator();
		while (itPos.hasNext()) {
			String pos = itPos.next();
			mapPos2Index.put(pos, idx);
			mapIndex2Pos.put(idx, pos);
			idx++;
		}
		// displayManhattanXY(mapPos2Values);
		return displayManhattan(mapPos2Values);
	}

	private void readManhattanXY(String trait, String subpop, Double minlogP, Set snplist, String region) {
		gwas = (GwasFacade) AppContext.checkBean(gwas, "GwasFacade");

		AppContext.resetTimer("querying p values");
		Set strsnplist = null;
		mapPos2Index = new TreeMap();
		mapIndex2Pos = new TreeMap();
		mapPos2Values = new TreeMap();
		if (trait == null || trait.isEmpty() || subpop == null || subpop.isEmpty()) {
			if (snplist != null) {
				int idx = 0;
				mapPos2Values = new TreeMap();
				Iterator<Position> itPos = snplist.iterator();
				while (itPos.hasNext()) {
					PositionLogPvalueImpl spos = (PositionLogPvalueImpl) itPos.next();
					String pos = spos.getChr() + "-" + spos.getPosition();
					mapPos2Values.put(pos, spos.getMinusLogPvalue());
					mapPos2Index.put(pos, idx);
					mapIndex2Pos.put(idx, pos);
					idx++;
				}
			} else
				return;
		} else {
			mapPos2Values = gwas.getPosstr2MinusLogP(trait, subpop, minlogP, region);
			if (mapPos2Values == null) {
				Messagebox.show("No GWAS run for trait: " + trait + ", population: " + subpop);
				return;
			}
			strsnplist = new HashSet();
			if (snplist != null) {
				Iterator<Position> itPos = snplist.iterator();
				while (itPos.hasNext()) {
					Position spos = itPos.next();
					strsnplist.add(spos.getChr() + "-" + spos.getPosition());
				}
			}
			int idx = 0;
			Iterator<String> itPos = mapPos2Values.keySet().iterator();
			while (itPos.hasNext()) {
				String pos = itPos.next();
				if (snplist != null && !strsnplist.contains(pos))
					continue;
				mapPos2Index.put(pos, idx);
				mapIndex2Pos.put(idx, pos);
				idx++;
			}
		}

		AppContext.resetTimer("displaying p values");

		displayManhattanXY(mapPos2Values);

		String qqfilename = gwas.getQQPlotFile(trait, subpop);
		if (qqfilename != null && !qqfilename.isEmpty()) {
			AppContext.debug("displaying " + qqfilename);
			imageQQ.setSrc(qqfilename);
			imageQQ.invalidate();
			imageQQ.setVisible(true);
		} else {
			imageQQ.setSrc(null);
			imageQQ.setVisible(false);
		}

		AppContext.resetTimer("displaying p values.. done");
	}

	private void displayManhattanXY(Map<String, Double> mapMyPos2Values) {

		chartManhattanXY.setVisible(false);
		Iterator<String> itPos = mapMyPos2Values.keySet().iterator();
		String prevchr = "";
		int seriescnt = 0;
		Series series = null;
		chartManhattanXY.setModel(new DefaultXYModel());
		chartManhattanXY.getPlotOptions().getSeries().getMarker().setWidth(3);

		StringBuffer buffjs = new StringBuffer();
		buffjs.append("function() { var chr=0; var off=0; var val=this.value; if(val>345713663) ");
		buffjs.append(
				" { chr=12; off=345713663;} else if(val>316692557){chr=11; off=316692557;}  else if(val>293485270){chr=10; off=293485270;}  else if(val>270472550){chr=9; off=270472550;}  else if(val>242029528){chr=8; off=242029528;} ");
		buffjs.append(
				" else if(val>212331907){chr=7; off=212331907;}  else if(val>181083120){chr=6; off=181083120;}    else if(val>151124686){chr=5; off=151124686;}   else if(val>115621992){chr=4; off=115621992;} ");
		buffjs.append(
				"   else if(val>79208173){chr=3; off=79208173;}   else if(val>43270923){chr=2; off=43270923;}   else {chr=1; off=0; }; var str1=\"\"; return str1.concat(chr).concat(\"-\").concat( val-off);}");
		chartManhattanXY.getXAxis().getLabels().setFormatter(new JavaScriptValue(buffjs.toString()));

		ScatterPlotOptions plotOptions = chartManhattanXY.getPlotOptions().getScatter();
		Marker marker = plotOptions.getMarker();
		// marker.
		// marker.setRadius(5);
		marker.getStates().getHover().setEnabled(true);
		// marker.getStates().getHover().setLineColor("rgb(100,100,100)");
		plotOptions.getStates().getHover().getMarker().setEnabled(false);
		// plotOptions.getTooltip().setHeaderFormat("<b>{series.name}</b><br>");
		// plotOptions.getTooltip().setPointFormat("{point.x} cm, {point.y} kg");
		// plotOptions.getTooltip().setPointFormat("{point.x}, {point.y}");
		// plotOptions.setTooltip(tooltip);

		// chartManhattanXY.setTooltip(tooltip);

		// marker.

		// TooltipPlotOptions tooltip = new TooltipPlotOptions();
		// chartManhattanXY.getPlotOptions().getScatter().setTooltip(tooltip); .sett
		// .setTooltip(tooltip);

		// chartManhattanXY.getXAxis().getLabels().setFormatter(new
		// JavaScriptValue("function() { var str1=\"Hello \"; return
		// str1.concat(this.value); }"));
		// chartManhattanXY.getXAxis().getLabels().setFormatter(new JavaScriptValue("{
		// return this.value); }"));
		sb.delete(0, sb.length());
		sb.append("Chr,Position,Value");
		while (itPos.hasNext()) {
			String pos = itPos.next();
			Double y = mapMyPos2Values.get(pos);
			String[] chrpos = pos.split("\\-");
			if (!prevchr.equals(chrpos[0])) {
				if (seriescnt == 0)
					series = this.chartManhattanXY.getSeries();
				else
					series = this.chartManhattanXY.getSeries(seriescnt);
				if (chrpos[0].length() == 1)
					series.setName("chr0" + chrpos[0]);
				else
					series.setName("chr" + chrpos[0]);
				AppContext.debug("creating plotxy  chr" + chrpos[0]);
				prevchr = chrpos[0];

				// Tooltip tt=new Tooltip();
				mapChr2Offset.get(chrpos[0]);
				// tt.setFormatter(new JavaScriptValue("function() { return point.x; }"));
				JavaScriptValue formatter = new JavaScriptValue("function() {" + " var xyArr=[]; "
						+ " $.each(this.points,function(){ "
						+ "    xyArr.push('chr: ' + this.series.name + ', ' +'bp: ' + this.x + ', -logP: ' + this.y);"
						+ " }); " + " return xyArr.join('<br/>'); }");
				series.getTooltip().setFormatter(formatter);

				// AppContext.debug(" series.getTooltip().toJSONString()=" +
				// series.getTooltip().toJSONString());
				seriescnt++;
			}
			sb.append("\n");
			sb.append(chrpos[0]);
			sb.append(",");
			sb.append(chrpos[1]);
			sb.append(",");
			sb.append(y);
			series.addPoint(Double.valueOf(mapChr2Offset.get(chrpos[0]) + Integer.valueOf(chrpos[1])), y);
		}

		/*
		 * JavaScriptValue formatterchart = new JavaScriptValue("function() {" +
		 * " var xyArr=[]; " + " $.each(this.points,function(){ " +
		 * "    xyArr.push('chr: ' + this.series.name + ', ' +'bp: ' + this.x + ', -logP: ' + this.y);"
		 * + " }); " + " return xyArr.join('<br/>'); }");
		 * 
		 * //chartManhattanXY.getTooltip().setFormatter(formatterchart);
		 * 
		 * //chartManhattanXY.getPlotData().getTooltip().setFormatter(formatterchart);
		 * chartManhattanXY.getPlotOptions().getScatter().getTooltip().
		 * setFormat("chr: this.series.name  bp: this.x -logP: this.y");
		 */

		/*
		 * Tooltip ttchart=new Tooltip(); ttchart.setFormatter(new
		 * JavaScriptValue("function() {" + " var xyArr=[]; " +
		 * " $.each(this.points,function(){ " +
		 * "    xyArr.push('chr: ' + this.series.name + ', ' +'pos: ' + this.x + ', -logP: ' +this.y);"
		 * + " }); " + " return xyArr.join('<br/>'); }"));
		 * chartManhattanXY.setTooltip(ttchart);
		 * //chartManhattanXY.getPlotData().setTooltip(ttchart);
		 */

		StringBuffer buffjsmarker = new StringBuffer();
		buffjsmarker.append("function() { var chr=0; var off=0; var val=this.x; if(val>345713663) ");
		buffjsmarker.append(
				" { chr=12; off=345713663;} else if(val>316692557){chr=11; off=316692557;}  else if(val>293485270){chr=10; off=293485270;}  else if(val>270472550){chr=9; off=270472550;}  else if(val>242029528){chr=8; off=242029528;} ");
		buffjsmarker.append(
				" else if(val>212331907){chr=7; off=212331907;}  else if(val>181083120){chr=6; off=181083120;}    else if(val>151124686){chr=5; off=151124686;}   else if(val>115621992){chr=4; off=115621992;} ");
		buffjsmarker.append(
				"   else if(val>79208173){chr=3; off=79208173;}   else if(val>43270923){chr=2; off=43270923;}   else {chr=1; off=0; };  return 'chr ' + chr + '-' +  (val-off) + ',  -logP=' + this.y.toFixed(2);}");

		/*
		 * chartManhattanXY .options.formatter = function() { var xyArr=[];
		 * $.each(this.points,function(){ xyArr.push('Series: ' + this.series.name + ',
		 * ' +'X: ' + this.x + ', Y: ' +this.y); }); return xyArr.join('<br/>'); }
		 */

		Tooltip ttc = new Tooltip();
		// ttc.setFormatter(new JavaScriptValue("function() { return this.x + ',' +
		// this.y; }"));
		ttc.setFormatter(new JavaScriptValue(buffjsmarker.toString()));
		chartManhattanXY.setTooltip(ttc);

		/*
		 * 
		 * AppContext.debug(" chartManhattanXY.getTooltip().toJSONString()=" +
		 * chartManhattanXY.getTooltip().toJSONString()); AppContext.
		 * debug(" chartManhattanXY.getPlotOptions().getScatter().getTooltip().getFormat()="
		 * + chartManhattanXY.getPlotOptions().getScatter().getTooltip().getFormat());
		 * 
		 * 
		 * AppContext.debug("getFormat=" +
		 * chartManhattanXY.getXAxis().getLabels().getFormat());
		 * AppContext.debug("getFormatter JSON=" +
		 * chartManhattanXY.getXAxis().getLabels().getFormatter().toJSONString());
		 * AppContext.debug("getFormatter String=" +
		 * chartManhattanXY.getXAxis().getLabels().getFormatter().toString());
		 */

		if (mapPos2Index.size() == 0)
			labelManhattan.setValue(this.mapPos2Index.size() + " markers, decrease Minimum -logP.");
		else
			labelManhattan.setValue(this.mapPos2Index.size()
					+ " markers, select a narrow region to zoom, show JBrowse tracks and marker annotations. Click a marker to display allele distributions and varieties.");

		chartManhattanXY.setVisible(true);

	}

	private DefaultCategoryModel displayManhattan(Map<String, Double> mapMyPos2Values) {

		DefaultCategoryModel model = new DefaultCategoryModel();
		int skippoints = 1;
		if (mapMyPos2Values.size() > nMarkers) {
			skippoints = mapMyPos2Values.size() / nMarkers + 1;
		}

		Iterator<String> itPos = mapMyPos2Values.keySet().iterator();

		mapViewPos2Index = new HashMap();
		mapViewIndex2Pos = new HashMap();

		int idx = 0;
		int idxview = 0;
		double idxmax = 0;
		String posmax = null;
		while (itPos.hasNext()) {

			String pos = itPos.next();

			double curval = mapMyPos2Values.get(pos).doubleValue();
			if (idxmax < curval) {
				idxmax = curval;
				posmax = pos;
			}

			if ((idx % skippoints) == 0) {
				mapViewPos2Index.put(posmax, idxview);
				mapViewIndex2Pos.put(idxview, posmax);

				int chr = Integer.parseInt(posmax.split("\\-")[0]);
				String chrstr = "chr0" + chr;
				if (chr > 9)
					chrstr = "chr" + chr;
				model.setValue(chrstr, posmax, idxmax);
				idxview++;

				posmax = null;
				idxmax = 0;
			}

			idx++;

		}
		AppContext.debug(mapMyPos2Values.size() + " pvalues,  " + mapViewPos2Index.size() + " displayed"
				+ " , skippoints=" + skippoints);

		List listPos = new ArrayList();
		listPos.add("");
		listPos.addAll(new TreeSet(mapMyPos2Values.keySet()));
		listboxPosition.setModel(new SimpleListModel(listPos));

		SimpleListModel listmodel = new SimpleListModel(listPos);
		listmodel.setMultiple(true);
		listboxMultiPosition.setModel(listmodel);

		return model;

	}

	@Listen("onClick =#buttonUpdateAnnotations")
	public void onclickupdateannots() {
		try {
			int min = prevMinX; // chartManhattanXY.getXAxis().getMin().intValue();
			int max = prevMaxX; // chartManhattanXY.getXAxis().getMax().intValue();
			AppContext.debug("update annots, min=" + min + ", max=" + max);
			doSelectionXY(min, max);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void updateMarkerAnnotations(String chr, Collection colPos) {

		genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
		// Set annotations=new LinkedHashSet();
		setAnnotations = new LinkedHashSet();

		Set excludeAnnotation = new HashSet();
		if (!checkboxIncludeInteractions.isChecked()) {
			excludeAnnotation.add(MarkerAnnotation.GENE_INT_PRINEXPT);
			excludeAnnotation.add(MarkerAnnotation.GENE_INT_PRINPRED);
			excludeAnnotation.add(MarkerAnnotation.GENE_INT_RICENETV1);
			excludeAnnotation.add(MarkerAnnotation.GENE_INT_RICENETV2);
		}

		if (!checkboxIncludePromoters.isChecked()) {
			excludeAnnotation.add(MarkerAnnotation.PROM_FGENESH1K);
			excludeAnnotation.add(MarkerAnnotation.PROM_PLANTPROMDB);
		}

		if (!checkboxIncludeGO.isChecked()) {
			excludeAnnotation.add(MarkerAnnotation.GENE_GO_BP);
			excludeAnnotation.add(MarkerAnnotation.GENE_GO_MF);
			excludeAnnotation.add(MarkerAnnotation.GENE_GO_CC);
		}

		if (!checkboxIncludePOTO.isChecked()) {
			excludeAnnotation.add(MarkerAnnotation.GENE_PO);
			excludeAnnotation.add(MarkerAnnotation.GENE_PO_DEVT);
			excludeAnnotation.add(MarkerAnnotation.GENE_TO);
			excludeAnnotation.add(MarkerAnnotation.GENE_QTARO);
		}

		if (!checkboxIncludeQTL.isChecked()) {
			excludeAnnotation.add(MarkerAnnotation.QTL_GRAMENE);
			excludeAnnotation.add(MarkerAnnotation.QTL_QTARO);
		}

		/*
		 * markerresult = genomics.getMarkerAnnotsByContigPositions(contigname[0].trim()
		 * , colPos, this.listboxOrganism.getSelectedItem().getLabel(),
		 * intboxPlusMinusBP.getValue(), annot , setAnnotations, maxint,
		 * excludeAnnotation);
		 */

		markerresult = genomics.getMarkerAnnotsByContigPositions(chr, colPos, AppContext.REFERENCE_NIPPONBARE(), 0,
				GenomicsFacade.GENEMODEL_IRIC, setAnnotations, 10, excludeAnnotation);

		this.searchbyMySnpListQtl();

		AppContext.debug("setAnnotations=" + setAnnotations);
		AppContext.debug("excludeAnnotation=" + excludeAnnotation);
		/*
		 * gridMarker.setRowRenderer(new MarkerGridRenderer()); gridMarker.setModel( new
		 * SimpleListModel( markerresult )); gridMarker.setVisible(true);
		 */

	}

	@Listen("onClick = #checkboxJbrowse")
	public void onclickJbrowse() {
		if (checkboxJbrowse.isChecked() && urljbrowse != null) {
			if (!urljbrowse.contains(AppContext.getJbrowseDir()))
				return;
			AppContext.debug("displaying " + urljbrowse);
			iframeJbrowse.setSrc(urljbrowse);
			msgJbrowse.setVisible(true);
			iframeJbrowse.setVisible(true);
			this.tabRegion.setSelected(true);
		} else {
			msgJbrowse.setVisible(false);
			iframeJbrowse.setVisible(false);
		}
	}

	/**
	 * Prepare JBRowse URL. JBrowse is not fetched/rendered until the tab is
	 * clicked, but the URL is already formulated
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 * @param locus
	 */
	private void updateJBrowse(String chr, String start, String end, String locus) {
		// return;

		chr = chr.trim();
		String chrpad = chr;
		if (chrpad.length() == 1)
			chrpad = "0" + chr;

		String org = AppContext.REFERENCE_NIPPONBARE();

		if (org.equals(AppContext.getDefaultOrganism()))
			chrpad = "loc=" + chr; // + AppContext.getJbrowseContigSuffix(); //"|msu7";
		else if (org.equals("IR64-21"))
			chrpad = "data=ir6421v1&loc=" + chr + "|ir6421v1";
		else if (org.equals("93-11"))
			chrpad = "data=9311v1&loc=" + chr + "|9311v1";
		else if (org.equals("DJ123"))
			chrpad = "data=dj123v1&loc=" + chr + "|dj123v1";
		else if (org.equals("Kasalath"))
			chrpad = "data=kasv1&loc=" + chr + "|kasv1";

		String displaymode = "%22displayMode%22:%22compact%22,%22maxHeight%22:%222000%22";

		String rendertype = "";

		if (locus.isEmpty())
			msgJbrowse.setValue("Chromosome " + chr + " [" + start + ".." + end + "]");
		else
			msgJbrowse.setValue(locus + "  Chromosome " + chr + " [" + start + ".." + end + "]");

		iframeJbrowse.setScrolling("yes");
		iframeJbrowse.setAlign("left");

		// String urltemplate = "..%2F..%2F" + AppContext.getHostDirectory() +
		// "%2Ftmp%2F" + "GFF_FILE";
		String urltemplate = "..%2F..%2Ftemp%2F" + "GFF_FILE";

		String snp3kcore = "";
		/*
		 * if(checkboxSNP.isChecked()) { if(checkboxCoreSNP.isChecked())
		 * snp3kcore="msu7coresnpsv2%2C"; } if(checkboxIndel.isChecked()) snp3kcore +=
		 * "msu7indelsv2%2C";
		 */

		// for all varieties
		rendertype = "%22JBrowse%2FView%2FTrack%2FAlignments2VarietyMismatchSynIndels%22";

		String showTracks = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset()); // + "%2C" + snp3kcore;

		if (org.equals(AppContext.getDefaultOrganism())) {
		}
		/*
		 * else if(org.equals("IR64-21")) showTracks =
		 * "IR64-21%20DNA,ir6421v1rengff,alignir6421v1vsmsu7%2C"; else
		 * if(org.equals("93-11")) showTracks = "9311%20DNA%2C9311v1rengff,"; else
		 * if(org.equals("DJ123")) showTracks =
		 * "DJ123%20DNA%2C,dj123v1rengff,aligndj123v1vsmsu7%2C"; else
		 * if(org.equals("Kasalath")) showTracks = "Kasalath%20DNA%2Ckasrapv1rengff,";
		 */
		else if (org.equals("IR64-21"))
			showTracks = "DNA,ir6421v1gffv2,";
		else if (org.equals("93-11"))
			showTracks = "DNA%2C9311v1gffv2,";
		else if (org.equals("DJ123"))
			showTracks = "DNA%2C,dj123v1gffv2,";
		else if (org.equals("Kasalath"))
			showTracks = "DNA%2Ckasv1gffrapv2,";

		Map<String, String> mapTrack = new HashMap();
		mapTrack.put("Culm diameter (mm) of basal internode at repro.", "cudi_repro");
		mapTrack.put("Culm length (cm) at reproductive", "cult_repro");
		mapTrack.put("Culm number (count) at reproductive - cultivated", "cuno_repro");
		mapTrack.put("Grain length (mm)", "grlt");
		mapTrack.put("Grain Width(mm)", "grwd");
		mapTrack.put("100-grain weight (gm) - cultivated", "grwt100");
		mapTrack.put("Heading (days to 80% fully headed) - cultivated", "hdg_80head");
		mapTrack.put("Ligule length (mm) - cultivated", "liglt");
		mapTrack.put("Leaf length (cm) - cultivated", "llt");
		mapTrack.put("Leaf width (cm) - cultivated", "lwd");
		mapTrack.put("Panicle length (cm) at post-harvest", "plt_post");
		mapTrack.put("Seedling height (cm)", "sdht");

		String strait = listboxTrait.getSelectedItem().getLabel().trim();
		if (strait.startsWith("CO")) {
			strait = gwas.getLegacyTraitname(strait);
		}
		if (!strait.isEmpty()) {
			showTracks += ",gwas3k_" + mapTrack.get(strait) + "_emmax_all_density,gwas3k_" + mapTrack.get(strait)
					+ "_emmax_all_xy";
		}

		showTracks = showTracks.replace(",msu7indelsv2", "");

		// String urljbrowse= "172.29.4.215:8080" + /* AppContext.getHostname() + */ "/"
		// + AppContext.getJbrowseDir() + "/?" + chrpad + ":" + start + ".." + end +
		// "&tracks=" + AppContext.getJBrowseDefaulttracks();
		urljbrowse = AppContext.getJbrowseDir() + "/?" + chrpad + ":" + start + ".." + end + "&tracks=" + showTracks; // AppContext.getJBrowseDefaulttracks();

		if (checkboxJbrowse.isChecked()) {
			AppContext.debug("displaying " + urljbrowse);
			iframeJbrowse.setSrc(urljbrowse);
			msgJbrowse.setVisible(true);
			iframeJbrowse.setVisible(true);
		} else {
			msgJbrowse.setVisible(false);
			iframeJbrowse.setVisible(false);
		}

	}

	@Listen("onClick =#tabPosition")
	public void onclickTabposition() {
		// tabPosition
		/*
		 * 
		 * int min= chartManhattanXY.getXAxis(0).getMin().intValue(); int max =
		 * chartManhattanXY.getXAxis(0).getMax().intValue();
		 * 
		 * 
		 * 
		 * AppContext.debug( "min=" + min + "; max=" + max);
		 * 
		 * List listPos=new ArrayList(); for(int idx=min; idx<=max; idx++) {
		 * listPos.add( mapIndex2Pos.get(idx) ); } listboxPosition.setModel( new
		 * SimpleListModel(listPos) );
		 */

	}

	@Listen("onClick =#tabPopulation")
	public void onclickTabpopulation() {
		Collection varlist = null;

		if (listboxVarietylist.getSelectedIndex() > 0 || this.listboxTrait.getSelectedIndex() > 0) {
		} else
			return;

		String sPhenotype = listboxTrait.getSelectedItem().getLabel();
		if (listboxVarietylist.getSelectedIndex() > 0) {
			sPhenotype = listboxVarietylist.getSelectedItem().getLabel();
			varlist = workspace.getVarieties(sPhenotype);
		}

		mapSubpopGen2Phenotype2Count = null;
		mapSubpop2Phenotype2Count = null;
		mapVarid2Phenotype = null;

		Map[] mapPopHists = gwas.createSubpopDistributions(sPhenotype, varlist,
				checkboxNormalizePopulation.isChecked());

		mapSubpopGen2Phenotype2Count = mapPopHists[1];
		mapSubpop2Phenotype2Count = mapPopHists[0];
		mapVarid2Phenotype = mapPopHists[4];

		if (checkboxNormalizePopulation.isChecked()) {
			plotXYHistogram(mapPopHists[2], this.chartSubpopHist, "Subpopulation frequency", sPhenotype, false);
			plotXYHistogram(mapPopHists[3], this.chartGenSubpopHist, "Variety group frequency", sPhenotype, false);

		} else {
			plotXYHistogram(mapPopHists[0], chartSubpopHist, "Subpopulation count", sPhenotype, false);
			plotXYHistogram(mapPopHists[1], chartGenSubpopHist, "Variety group count", sPhenotype, false);
		}
	}

	@Listen("onSelect =#listboxPosition")
	public void onselectListboxPosition() {
		String pos = listboxPosition.getSelectedItem().getLabel();
		List listpos = new ArrayList();
		listpos.add(pos);
		onselectListboxPositions(listpos);
	}

	@Listen("onClick =#buttonDisplayHist")
	public void onclickHist() {
		Iterator itsetsel = listboxMultiPosition.getSelectedItems().iterator();
		List listpos = new ArrayList();
		while (itsetsel.hasNext()) {
			Listitem item = (Listitem) itsetsel.next();
			listpos.add(item.getLabel());
		}
		onselectListboxPositions(listpos);
	}

	public void onselectListboxPositions(List listpos) {

		if ((listboxTrait.getSelectedIndex() > 0 && listboxSubpopulation.getSelectedIndex() > 0)
				|| this.listboxVarietylist.getSelectedIndex() > 0) {
		} else {
			Messagebox.show(
					"No variety phenotype data. Select a trait and subpopulation from stored results, OR a variety list with phenotype value");
			return;
		}

		this.tabPosition.setDisabled(false);

		divVarietylist.setVisible(false);

		/*
		 * mapSubpop2Phenotype2Count=new HashMap(); mapSubpopGen2Phenotype2Count=new
		 * HashMap(); mapGenotype2Phenotype2Count=new HashMap();
		 * mapAllele2Phenotype2Count=new HashMap(); mapAllele2Phenotype2Set=new
		 * HashMap(); listVarieties=null;
		 */

		Set sortPos = new TreeSet(listpos);
		listpos = new ArrayList();
		listpos.addAll(sortPos);

		String sSubpopulation = this.listboxSubpopulation.getSelectedItem().getLabel();

		String sChr = null;
		boolean isMulticontig = false;
		List poslist = new ArrayList();
		List ckeckedposlist = new ArrayList();
		List multicontlist = new ArrayList();
		Iterator itPos = listpos.iterator();
		while (itPos.hasNext()) {

			String pos = (String) itPos.next();
			if (pos.isEmpty())
				continue;
			if (sChr != null && sChr.equals(pos.split("\\-")[0]))
				isMulticontig = true;
			sChr = pos.split("\\-")[0];
			if (sChr.length() == 1)
				sChr = "chr0" + sChr;
			else
				sChr = "chr" + sChr;

			poslist.add(BigDecimal.valueOf(Double.valueOf(pos.split("\\-")[1])));
			multicontlist.add(new MultiReferencePositionImpl(AppContext.REFERENCE_NIPPONBARE(), sChr,
					BigDecimal.valueOf(Double.valueOf(pos.split("\\-")[1]))));

		}

		if (isMulticontig) {
			sChr = "any";
			poslist = multicontlist;
		} else {
			// Iterator<SnpsAllvarsPos> itSnpsDB = genotype.checkSNPInChromosome(sChr, new
			// HashSet(poslist), SnpsAllvarsPosDAO.TYPE_3KALLSNP ).iterator();

			Set s = new HashSet();
			s.add("3kall");
			Iterator<SnpsAllvarsPos> itSnpsDB = genotype
					.checkSNPInChromosome(organism.getOrganismId().intValue(), sChr, new HashSet(poslist), s)
					.iterator();
			while (itSnpsDB.hasNext()) {
				ckeckedposlist.add(itSnpsDB.next().getPosition());
			}
			if (ckeckedposlist.size() == 0) {
				Messagebox.show("Cannot find variant " + sChr + " " + poslist);
				return;
			}
			poslist = ckeckedposlist;
		}

		String sPhenotype = listboxTrait.getSelectedItem().getLabel();
		if (poslist.size() == 0) {
			plotXYHistogramCount(new HashMap(), chartAlleleHist, "Allele count", sPhenotype,
					checkboxNormalize.isChecked());
			plotXYHistogram(new HashMap(), chartGenotypeHist, "Genotype count", sPhenotype,
					checkboxNormalize.isChecked());
			// plotXYHistogram(new HashMap(),chartSubpopHist, "Variety count", sPhenotype,
			// checkboxNormalize.isChecked(), true );
			// plotXYHistogram(new HashMap(),chartGenSubpopHist, "Variety count",
			// sPhenotype, checkboxNormalize.isChecked());
			return;
		}

		Collection varlist = null;
		if (listboxVarietylist.getSelectedIndex() > 0) {
			varlist = workspace.getVarieties(listboxVarietylist.getSelectedItem().getLabel());
		}

		mapGenotype2Phenotype2Count = null;
		mapGenotype2Phenotype2Frequency = null;

		mapAllele2Phenotype2Count = null;
		mapVarid2Phenotype = null;
		mapVarid2Genotype = null;
		mapAllele2Phenotype2Set = null;

		Map[] mapAlleleHists = gwas.createAlleleDistributions(sChr, poslist, sPhenotype, varlist,
				checkboxNormalize.isChecked());

		mapGenotype2Phenotype2Count = mapAlleleHists[1];
		mapAllele2Phenotype2Count = mapAlleleHists[0];
		mapVarid2Phenotype = mapAlleleHists[4];
		mapVarid2Genotype = mapAlleleHists[5];
		mapAllele2Phenotype2Set = mapAlleleHists[6];
		mapGenotype2Phenotype2Frequency = mapAlleleHists[3];

		if (checkboxNormalize.isChecked()) {
			plotXYHistogramCount(mapAlleleHists[2], chartAlleleHist, "Allele frequency", sPhenotype, false);
			plotXYHistogram(mapAlleleHists[3], chartGenotypeHist, "Genotype frequency", sPhenotype, false);

		} else {
			plotXYHistogramCount(mapAlleleHists[0], chartAlleleHist, "Allele count", sPhenotype, false);
			plotXYHistogram(mapAlleleHists[1], chartGenotypeHist, "Genotype count", sPhenotype, false);
		}

		List listGenotypes = new ArrayList();
		listGenotypes.add("");
		listGenotypes.addAll(mapGenotype2Phenotype2Count.keySet());
		listboxGenotype1.setModel(new SimpleListModel(listGenotypes));
		listboxGenotype2.setModel(new SimpleListModel(listGenotypes));
		listboxGenotype1.setSelectedIndex(0);
		listboxGenotype2.setSelectedIndex(0);
		// plotXYHistogram(mapGenotype2Phenotype2Count, chartSmoothHist, "Genotype
		// frequency" , sPhenotype, false);

		// plotXYHistogramCount(mapAllele2Phenotype2Count,chartAlleleHist, "Allele "
		// +slabel, sPhenotype, checkboxNormalize.isChecked());
		// plotXYHistogram(mapGenotype2Phenotype2Count,chartGenotypeHist, "Genotype " +
		// slabel, sPhenotype, checkboxNormalize.isChecked());
		// plotXYHistogram(mapSubpop2Phenotype2Count,chartSubpopHist, "Variety count",
		// sPhenotype, checkboxNormalize.isChecked(), true );
		// plotXYHistogram(mapSubpopGen2Phenotype2Count,chartGenSubpopHist, "Variety
		// count", sPhenotype, checkboxNormalize.isChecked());

		labelPositions.setMaxlength(70);
		labelPositions.setMultiline(true);
		labelPositions.setValue("Histograms for positions " + listpos.toString());

	}

	@Listen("onClick = #checkboxNormalize")
	public void onclickNormalize() {
		String sPhenotype = listboxTrait.getSelectedItem().getLabel();

		String y = " count";
		if (checkboxNormalize.isChecked())
			y = " frequency";

		if (mapAllele2Phenotype2Count != null)
			plotXYHistogramCount(mapAllele2Phenotype2Count, chartAlleleHist, "Allele" + y, sPhenotype,
					checkboxNormalize.isChecked());
		if (mapGenotype2Phenotype2Count != null)
			plotXYHistogram(mapGenotype2Phenotype2Count, chartGenotypeHist, "Genotype" + y, sPhenotype,
					checkboxNormalize.isChecked());

		// plotXYHistogram(mapSubpop2Phenotype2Count,chartSubpopHist, "Variety" + y,
		// sPhenotype, checkboxNormalize.isChecked(), true);
		// plotXYHistogram(mapSubpopGen2Phenotype2Count,chartGenSubpopHist, "Variety" +
		// y, sPhenotype, checkboxNormalize.isChecked());
	}

	@Listen("onClick = #checkboxNormalizePopulation")
	public void onclickNormalizePop() {
		String sPhenotype = listboxTrait.getSelectedItem().getLabel();

		String y = " count";
		if (checkboxNormalizePopulation.isChecked())
			y = " frequency";

		if (mapSubpop2Phenotype2Count != null)
			plotXYHistogram(mapSubpop2Phenotype2Count, chartSubpopHist, "Variety" + y, sPhenotype,
					checkboxNormalizePopulation.isChecked());
		if (mapSubpopGen2Phenotype2Count != null)
			plotXYHistogram(mapSubpopGen2Phenotype2Count, chartGenSubpopHist, "Variety" + y, sPhenotype,
					checkboxNormalizePopulation.isChecked());

		// plotXYHistogram(mapSubpop2Phenotype2Count,chartSubpopHist, "Variety" + y,
		// sPhenotype, checkboxNormalize.isChecked(), true);
		// plotXYHistogram(mapSubpopGen2Phenotype2Count,chartGenSubpopHist, "Variety" +
		// y, sPhenotype, checkboxNormalize.isChecked());
	}

	private void checkSeries(Charts chart, Vbox vboxSeries) {
		vboxSeries.getChildren().clear();

	}

	private void plotXYHistogram(Map<Object, Map<Object, Collection>> mapData, Charts histchart, String yaxis,
			String xaxis, boolean normalize) {
		plotXYHistogram(mapData, histchart, yaxis, xaxis, normalize, false);
	}

	private void plotLine(Map<String, List> mapData, Charts linechart, String yaxis, String xaxis) {
		CategoryModel model = new DefaultCategoryModel();
		Iterator<String> itName = mapData.keySet().iterator();
		while (itName.hasNext()) {
			String name = itName.next();
			int i = 0;
			List tops = mapData.get(name);
			Iterator itScores = tops.iterator();
			while (itScores.hasNext()) {
				model.setValue(name, i, (Number) itScores.next());
				i++;
			}
		}

		linechart.setModel(model);

		linechart.getYAxis().setTitle(yaxis);
		linechart.getXAxis().setTitle(xaxis);

	}

	private void plotXYHistogram(Map<Object, Map<Object, Collection>> mapData, Charts histchart, String yaxis,
			String xaxis, boolean normalize, boolean hideSeries) {

		// Map< Series
		Vbox vboxSeries = null;
		List<Component> listchecks = null;
		if (vboxSeries != null) {
			listchecks = vboxSeries.getChildren();
			listchecks.clear();
		}

		histchart.setModel(new DefaultXYModel());
		Iterator itSeries = mapData.keySet().iterator();
		int seriescount = 0;
		while (itSeries.hasNext()) {
			Object seriesobj = itSeries.next();
			Series series = null;
			if (seriescount == 0)
				series = histchart.getSeries();
			else
				series = histchart.getSeries(seriescount);

			if (seriesobj == null || seriesobj.toString().isEmpty())
				series.setName("?");
			else
				series.setName(seriesobj.toString());

			if (hideSeries) {
				if (seriescount < 2)
					series.setVisible(true);
				else
					series.setVisible(false);
			} else
				series.setVisible(true);

			series.setLineWidth(1);

			if (vboxSeries != null) {
				final Series fseries = series;
				Checkbox newcheck = new Checkbox();
				newcheck.setLabel(seriesobj.toString());
				if (seriescount < 2)
					newcheck.setChecked(true);
				newcheck.addEventListener(Events.ON_CHECK, new EventListener() {
					public void onEvent(Event ev) throws Exception {
						CheckEvent checkev = (CheckEvent) ev;
						fseries.setVisible(checkev.isChecked());
					}
				});
				listchecks.add(newcheck);
			}

			for (Double[] value : createXY(mapData.get(seriesobj), normalize)) {
				series.addPoint(value[0], value[1]);
			}

			seriescount++;
		}

		histchart.getXAxis().setTitle(xaxis);
		histchart.getYAxis().setTitle(yaxis);
	}

	private Double[][] createXY(Map mapX2Y, boolean normalize) {
		double total = 0.0;
		List<Double[]> listdouble = new ArrayList();
		Iterator itX = new TreeSet(mapX2Y.keySet()).iterator();
		while (itX.hasNext()) {
			Object x = itX.next();
			String xstr = x.toString();
			try {
				Object y = mapX2Y.get(x);
				Double cnt = null;

				if (y == null)
					cnt = Double.valueOf(0);
				else if (y instanceof Number)
					cnt = ((Number) y).doubleValue();
				else if (y instanceof Collection)
					cnt = Double.valueOf(((Collection) y).size());

				// else cnt= Double.valueOf(((Collection)y).size());
				total += cnt;
				Double idouble[] = new Double[] { Double.valueOf(xstr), cnt };
				listdouble.add(idouble);
			} catch (Exception ex) {
				AppContext.debug("x=" + x);
				ex.printStackTrace();
			}
		}
		Iterator<Double[]> itXY = listdouble.iterator();
		Double[][] xy = new Double[listdouble.size()][2];
		int ix = 0;
		while (itXY.hasNext()) {
			Double[] ixy = itXY.next();
			if (normalize)
				xy[ix] = new Double[] { ixy[0], ixy[1] / total };
			else
				xy[ix] = ixy;
			ix++;
		}
		return xy;
	}

	private void plotXYHistogramCount(Map<Object, Map<Object, Integer>> mapData, Charts histchart, String yaxis,
			String xaxis, boolean normalize) {
		histchart.setModel(new DefaultXYModel());
		Iterator itSeries = mapData.keySet().iterator();
		int seriescount = 0;
		while (itSeries.hasNext()) {
			Object seriesobj = itSeries.next();
			Series series = null;
			if (seriescount == 0)
				series = histchart.getSeries();
			else
				series = histchart.getSeries(seriescount);

			if (seriesobj == null || seriesobj.toString().isEmpty())
				series.setName("?");
			else
				series.setName(seriesobj.toString());
			series.setLineWidth(1);

			for (Double[] value : createXYCount(mapData.get(seriesobj), normalize)) {
				series.addPoint(value[0], value[1]);
			}
			seriescount++;
		}

		histchart.getXAxis().setTitle(xaxis);
		histchart.getYAxis().setTitle(yaxis);
	}

	private Double[][] createXYCount(Map mapX2Y, boolean normalize) {
		double total = 0;
		List<Double[]> listdouble = new ArrayList();
		Iterator itX = new TreeSet(mapX2Y.keySet()).iterator();
		while (itX.hasNext()) {
			Object x = itX.next();
			String xstr = x.toString();
			try {
				Object y = mapX2Y.get(x);

				Double cnt = null;
				if (y == null)
					cnt = Double.valueOf(0);
				else if (y instanceof Number)
					cnt = ((Number) y).doubleValue();
				else if (y instanceof Collection)
					cnt = Double.valueOf(((Collection) y).size());

				// if(y==null) y=Double.valueOf(0);
				// Double cnt=Double.valueOf(y.toString());

				total += cnt;
				Double idouble[] = new Double[] { Double.valueOf(xstr), cnt };
				listdouble.add(idouble);
			} catch (Exception ex) {
				AppContext.debug("x=" + x);
				ex.printStackTrace();
			}
		}
		Iterator<Double[]> itXY = listdouble.iterator();
		Double[][] xy = new Double[listdouble.size()][2];
		int ix = 0;
		while (itXY.hasNext()) {
			Double[] ixy = itXY.next();
			if (normalize)
				xy[ix] = new Double[] { ixy[0], ixy[1] / total };
			else
				xy[ix] = ixy;
			ix++;
		}
		return xy;
	}

}