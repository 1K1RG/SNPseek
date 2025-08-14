package org.irri.iric.portal.genomics.zkui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.irri.iric.ds.chado.domain.LocalAlignment;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.LocusPromoter;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.irri.iric.ds.chado.domain.impl.LocalAlignmentImpl;
import org.irri.iric.ds.chado.domain.model.VLocusNotes;
import org.irri.iric.ds.chado.domain.object.WebsiteQuery;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genomics.service.GenomicsFacadeImpl;
//import org.irri.iric.portal.webclient.SnpseekWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zhtml.Form;
import org.zkoss.zhtml.Input;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Separator;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

@Controller
@Scope(value = "session")
// @EnableAsync
public class LocusQueryController extends SelectorComposer<Window> {

	@Autowired
	private GenomicsFacade genomics;

	// @Autowired
	// private WebsiteDAO websiteService;

	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemsDAO;

	@Autowired
	private WorkspaceFacade workspace;

	@Wire
	private Div div_info;
	@Wire
	private Listheader headerLogp;
	@Wire
	private Hbox hboxGenesetAnalysis;
	
	@Wire
	private Panel variety_panelPassportResult;
	
	@Wire
	private Button buttonRicenetV1;
	@Wire
	private Button buttonRicenetV2DirectNeighbors;
	@Wire
	private Button buttonRicenetV2ContextHubs;
	@Wire
	private Button buttonPantherOverrep;

	private Map mapParamsRicenetV1 = new HashMap();
	private Map mapParamsRicenetV2DirectNeighbors = new HashMap();
	private Map mapParamsRicenetV2ContextHubs = new HashMap();
	private Map mapParamsPantherOverrep = new HashMap();
	private Map mapRaramsPRIN = new HashMap();

	@Wire
	private Hbox hboxDownload;
	@Wire
	private Listbox listboxAnnotation;

	@Wire
	private Div divAdvancedOptions;
	
	@Wire
	private Div resultContentDiv;
	
	@Wire
	private Textbox textboxMaxInteracting;

	@Wire
	private A aMSU7;
	@Wire
	private A aRAP;
	@Wire
	private A aKegg;
	@Wire
	private A aOryzabase;
	@Wire
	private A aPhytozome;
	@Wire
	private A aGramene;
	@Wire
	private A aUniprot;
	@Wire
	private A aNCBI;
	@Wire
	private A aIC4RLit;

	@Wire
	private Grid gridLocusInfo;
	@Wire
	private Separator separator;
	@Wire
	private Splitter splitter;
	@Wire
	private Textbox textboxLocusname;
	@Wire
	private Textbox textboxPosition;
	@Wire
	private Textbox textboxFunction;
	@Wire
	private Listbox listboxOverlappinggenes;
	@Wire
	private Iframe iframeJbrowse;

	@Wire
	private Div divGeneModel;

	@Wire
	Label labelChrExample;

	@Wire
	private Textbox msgbox;
	// @Wire
	// private Grid gridMarker;
	// private Listbox listboxMarker;

	@Wire
	private Listbox listboxMarkerVar;
	@Wire
	private Borderlayout borderMarkerVar;

	@Wire
	// private Grid listboxLocus;
	private Listbox listboxLocus;
	// private Listbox listboxLocus;

	@Wire
	// private Grid gridAlignment;
	private Listbox listboxAlignment;
	@Wire
	private Checkbox checkboxTopSequencehit;

	@Wire
	private Div myModal;
	// @Wire
	// private Button searchButton;
	@Wire
	private Textbox textboxGenefunction;
	@Wire
	private Textbox textboxGenename;
	@Wire
	private Listbox listboxGenenameMatch;
	@Wire
	private Listbox listboxGenefunctionMatch;

	@Wire
	private Button searchRegionButton;

	@Wire
	private Button buttonCountGOTermLoci;
	@Wire
	private Grid gridGOTerms;
	@Wire
	private Row rowCountGOTermLoci;

	// @Wire
	// private Textbox textboxGOTerm;
	@Wire
	private Listbox listboxOrganism;

	@Wire
	private Listbox listboxGOTerm;
	@Wire
	private Listbox listboxPatoTerm;

	@Wire
	private Listbox listboxCV;
	@Wire
	private Listbox listboxCVPato;

	@Wire
	private Listbox listboxGOAncestors;
	@Wire
	private Listbox listboxGODescendants;
	@Wire
	private Label labelGOAncestors;
	@Wire
	private Label labelGODescendants;

	@Wire
	private Listbox listboxPatoAncestors;
	@Wire
	private Listbox listboxPatoDescendants;
	@Wire
	private Label labelPatoAncestors;
	@Wire
	private Label labelPatoDescendants;

	@Wire
	private Div divIncludeSequence;
	@Wire
	private Checkbox checkboxIncludeSequence;

	@Wire
	private Listbox listboxMyLocusListOntology;
	@Wire
	private Intbox intboxPlusMinusBP;

	@Wire
	private Button searchGenenameButton;
	@Wire
	private Button searchGenefunctionButton;
	@Wire
	private Button searchGOButton;
	@Wire
	private Button searchPatoButton;

	@Wire
	private Hbox hboxAddtolist;
	@Wire
	private Button buttonDownloadCSV;
	@Wire
	private Button buttonDownloadTab;
	@Wire
	private Button buttonAddToList;
	@Wire
	private Textbox txtboxListname;

	private List<Locus> locusresult;
	private List<LocalAlignmentImpl> locusalignmentresult;
	private List<MarkerAnnotation> markerresult;

	private String uploadString;

	@Wire
	private Radio radioFunction;
	@Wire
	private Radio radioProcess;
	@Wire
	private Radio radioLocation;
	@Wire
	private Radio radioProtein;
	@Wire
	private Radio radioPathway;

	@Wire
	private Button searchEnrichment;

	@Wire
	private Button buttonUploadSequence;
	@Wire
	private Label labelUploadSequence;

	@Wire
	private Textbox textboxSequence;
	@Wire
	private Listbox listboxBlastType;
	@Wire
	private Listbox listboxBlastDBType;
	@Wire
	private Label labelBlastType;
	@Wire
	private Button searchByBlast;
	@Wire
	private Textbox textboxEvalue;

	@Wire
	private Combobox listboxContig;
	@Wire
	private Intbox intboxContigStart;
	@Wire
	private Intbox intboxContigEnd;

	@Wire
	private Listbox listboxSearchby;
	@Wire
	private Row rowGeneFunction;
	@Wire
	private Row rowGeneName;
	@Wire
	private Row rowGoTerm;
	@Wire
	private Row rowPatoTerm;
	@Wire
	private Row rowRegion;
	@Wire
	private Row rowSequence;
	@Wire
	private Row rowMySnpList;

	private Set setAnnotations;

	// @Wire
	// private Row rowNPBannotation;

	@Wire
	private Button searchMySnpListButton;
	@Wire
	private Listbox listboxMySNPList;

	@Wire
	private Radio radioGroupbyMarker;
	@Wire
	private Radio radioGroupbyGene;
	@Wire
	private Radio radioGroupbyQtl;

	@Wire
	private Button buttonMyLocusList;
	@Wire
	private Row rowGeneset;
	@Wire
	private Checkbox checkboxRicenetv2direct;
	@Wire
	private Checkbox checkboxRicenetv2context;
	@Wire
	private Checkbox checkboxStringDB;

	@Wire
	private Checkbox checkboxCarmoFunctional;

	@Wire
	private Listbox listboxMyLocusListGeneset;
	@Wire
	private Button buttonRicenetv2direct;
	@Wire
	private Button buttonRicenetv2context;
	@Wire
	private Button buttonStringDB;
	@Wire
	private Button buttonCarmoFunctional;

	@Wire
	private Button buttonPlantGSEA;
	@Wire
	private Button buttonAgriGO;
	@Wire
	private Button buttonIC4RExpr;
	@Wire
	private Button buttonIC4RCoexpr;

	@Wire
	private Checkbox checkboxPlantGSEA;
	@Wire
	private Checkbox checkboxAgriGO;
	@Wire
	private Checkbox checkboxIC4RExpr;
	@Wire
	private Checkbox checkboxIC4RCoexpr;

	@Wire
	private Checkbox checkboxListModel;
	@Wire
	private Checkbox checkboxListPromoter;
	@Wire
	private Checkbox checkboxListRicenet;
	@Wire
	private Checkbox checkboxListPrin;

	@Wire
	private Textbox textboxMsgWebclient;

	@Wire
	private Checkbox checkboxQueryinbackground;
	@Wire
	private Timer timerCheckUrls;
	private List<SubmitWebsiteFuture> listFuture = new ArrayList();
	private long maxQueryinbackgroundSeconds = 60 * 20;
	private long countQueryinbackgroundSeconds = 0;

	@Wire
	private Listheader listheaderOverlapping;

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

	private boolean searchMenu;

	
	@Override
	public void doAfterCompose(Window comp) throws Exception {

		super.doAfterCompose(comp);

		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
		genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
		List listOrgs = new ArrayList();
		listOrgs.addAll(genomics.getOrganisms());
		listOrgs.add("Minghui 63");
		listOrgs.add("Zeshan 97");
		listOrgs.add("N22");
		listOrgs.add("IR8");

		List listLocuslist = new ArrayList();
		listLocuslist.add("");
		listLocuslist.addAll(workspace.getLocuslistNames());
		listLocuslist.add("create new list...");

		List listMySnps = new ArrayList();
		listMySnps.add("");
		listMySnps.addAll(workspace.getSnpPositionListNames());
		listMySnps.add("create new list...");

		/*
		 * List listMyLoci = new ArrayList(); listMyLoci.add(""); listMyLoci.addAll(
		 * workspace.getLocusListNames());
		 */

		listOrgs.add("ALL");
		listboxOrganism.setModel(new SimpleListModel(listOrgs));
		listboxOrganism.setSelectedIndex(0);
		// listboxOrganism.setDisabled(true);

		listboxMySNPList.setModel(new SimpleListModel(listMySnps));
		listboxMySNPList.setSelectedIndex(0);

		// listboxMyLocusListGeneset.setModel(new SimpleListModel(listMyLoci));
		listboxMyLocusListGeneset.setModel(new SimpleListModel(listLocuslist));
		listboxMyLocusListGeneset.setSelectedIndex(0);

		// listboxMyLocusList2.setModel(new SimpleListModel(listLocuslist));
		// listboxMyLocusList2.setSelectedIndex(0);

		listboxMyLocusListOntology.setModel(new SimpleListModel(listLocuslist));
		listboxMyLocusListOntology.setSelectedIndex(0);

		// gridAlignment.pagingChild.mold = "os";
		// gridAlignment.pagingPosition = "both";
		// gridLocus.pagingChild.mold = "os";
		// gridLocus.pagingPosition = "both";

		// listitemSequence.setVisible( !AppContext.isUsingsharedData() );

		String from = Executions.getCurrent().getParameter("from");
		if (from != null) {
			if (from.equals("snplist")) {
				listboxMySNPList.setSelectedIndex(listboxMySNPList.getItemCount() - 2);
				this.listboxSearchby.setSelectedIndex(4);
				// this.listboxSearchby.setSelectedItem( listboxSearchby.getItemAtIndex(4));
				this.rowMySnpList.setVisible(true);
			}
		}

		String locusname = Executions.getCurrent().getParameter("name");
		if (locusname == null || locusname.isEmpty()) {
		} else {
			listboxSearchby.setSelectedIndex(1);
			rowGeneName.setVisible(true);
			textboxGenename.setValue(locusname);
			listboxGenenameMatch.setSelectedIndex(2);
			Events.postEvent("onClick", searchGenenameButton, null);
		}

		if (AppContext.isIRRILAN())
			divAdvancedOptions.setVisible(true);

		String jbrowsestart = AppContext.getJbrowseDir();

		AppContext.debug("initdone");

		final Window self = this.getSelf();
		self.addEventListener("onClose", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {

				if (timerCheckUrls.isRunning() || listFuture.size() > 0) {
					if (Messagebox.show(
							"Background wesite submission are still running and will be canceled. Continue closing this page?",
							"WARNING", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
						if (timerCheckUrls.isRunning())
							timerCheckUrls.stop();
						Iterator<SubmitWebsiteFuture> itFutureIterator = listFuture.iterator();
						while (itFutureIterator.hasNext()) {
							itFutureIterator.next().getFuture().cancel(true);
						}
						self.onClose();
					}
				}
			}
		});
	}

	private void initResults() {
		listboxAlignment.setVisible(false);
		listboxLocus.setVisible(false);
		gridGOTerms.setVisible(false);
		msgbox.setValue("");
		hboxAddtolist.setVisible(false);
		// listboxMarker.setVisible(false);
		listboxMarkerVar.setVisible(false);
		borderMarkerVar.setVisible(false);
		this.gridLocusInfo.setVisible(false);
		this.splitter.setOpen(false);
		hboxDownload.setVisible(false);

		divIncludeSequence.setVisible(AppContext.isIRRILAN());

	}

	@Listen("onSelect =#listboxSearchby")
	public void onselectSearchby() {

		rowGeneFunction.setVisible(false);
		rowGeneName.setVisible(false);
		rowGoTerm.setVisible(false);
		rowRegion.setVisible(false);
		rowSequence.setVisible(false);
		rowMySnpList.setVisible(false);
		rowCountGOTermLoci.setVisible(false);
		rowPatoTerm.setVisible(false);
		rowGeneset.setVisible(false);

		String searchby = listboxSearchby.getSelectedItem().getLabel();

		if (searchby.isEmpty())
			return;
		// <listitem label="Annotation (by source)"/>
		// <listitem selected="true" label="Gene name/symbol/function"/>

		try {
			if (searchby.startsWith("Annotation")) {
				textboxGenefunction.setValue("");
				rowGeneFunction.setVisible(true);
				this.listboxGenefunctionMatch.setSelectedIndex(0);
			} else if (searchby.startsWith("Gene name/symbol")) {
				textboxGenename.setValue("");
				rowGeneName.setVisible(true);
				this.listboxGenenameMatch.setSelectedIndex(0);
			} else if (searchby.equals("Gene Ontology term")) {

				genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

				listboxCV.setSelectedIndex(0);

				List listGoterms = new ArrayList();
				listGoterms.add("");
				// listGoterms.addAll(genomics.getGotermsByOrganism("biological_process",AppContext.getDefaultOrganism())
				// );
				listGoterms.addAll(genomics.getGotermsByOrganism("biological_process",
						this.listboxOrganism.getSelectedItem().getLabel())); // AppContext.getDefaultOrganism()) );

				listboxGOTerm.setModel(new SimpleListModel(listGoterms));
				listboxGOTerm.setSelectedIndex(0);
				rowGoTerm.setVisible(true);

				workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
				List listNew = new ArrayList();
				listNew.add("");
				listNew.addAll(workspace.getLocuslistNames());
				listNew.add("create new list...");
				this.listboxMyLocusListOntology.setModel(new SimpleListModel(listNew));
				listboxMyLocusListOntology.setSelectedIndex(0);

				// rowCountGOTermLoci.setVisible(true);
			}
			/*
			 * else if(searchby.equals("Plant and Trait Ontology term")) {
			 * 
			 * genomics = (GenomicsFacade)AppContext.checkBean(genomics, "GenomicsFacade");
			 * 
			 * listboxCVPato.setSelectedIndex(1);
			 * 
			 * List listPatoterms = new ArrayList(); listPatoterms.add("");
			 * listPatoterms.addAll(genomics.getPatotermsByOrganism("plant_trait_ontology",
			 * AppContext.getDefaultOrganism()) );
			 * 
			 * listboxPatoTerm.setModel(new SimpleListModel(listPatoterms));
			 * listboxPatoTerm.setSelectedIndex(0); rowPatoTerm.setVisible(true);
			 * 
			 * }
			 */
			else if (searchby.equals("Trait")) {

				genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

				listboxCVPato.setSelectedIndex(0);

				List listPatoterms = new ArrayList();
				listPatoterms.add("");
				listPatoterms.addAll(genomics.getPatotermsByOrganism("qtaro_gene_traits",
						listboxOrganism.getSelectedItem().getLabel())); // AppContext.getDefaultOrganism()) );

				listboxPatoTerm.setModel(new SimpleListModel(listPatoterms));
				listboxPatoTerm.setSelectedIndex(0);
				rowPatoTerm.setVisible(true);

			} else if (searchby.equals("Region")) {

				genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

				intboxContigStart.setValue(null);
				intboxContigEnd.setValue(null);

				List conts = genomics.getContigsByOrganism(this.listboxOrganism.getSelectedItem().getLabel());
				// listboxContig.setPreloadSize(50);

				List listContigs = AppContext.createUniqueUpperLowerStrings(conts, true, true);
				listboxContig.setModel(new SimpleListModel(listContigs));
				// listboxContig.setSelectedIndex(0);
				listboxContig.setValue("");

				String selOrg = listboxOrganism.getSelectedItem().getLabel();
				if (selOrg.equals("93-11"))
					labelChrExample.setValue("(ex. 9311_chr01, scaffold01_4619) ");
				else if (selOrg.equals("Kasalath"))
					labelChrExample.setValue("(ex. chr01, um) ");
				else
					labelChrExample.setValue("(ex. " + listContigs.get(1).toString().toLowerCase() + ") ");
				// labelChrExample.setValue("(ex. scaffold...)" );

				rowRegion.setVisible(true);
			} else if (searchby.equals("SNP Annotator (My SNP List)")) {

				workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
				List listMySnps = new ArrayList();
				listMySnps.add("");
				listMySnps.addAll(workspace.getSnpPositionListNames());
				listMySnps.add("create new list...");
				this.listboxMySNPList.setModel(new SimpleListModel(listMySnps));
				this.rowMySnpList.setVisible(true);
			} else if (searchby.equals("Gene set/networks")) {
				workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
				List listMyLocus = new ArrayList();
				listMyLocus.add("");
				listMyLocus.addAll(workspace.getLocuslistNames());
				listMyLocus.add("create new list...");
				this.listboxMyLocusListGeneset.setModel(new SimpleListModel(listMyLocus));
				rowGeneset.setVisible(true);

			}

			else if (searchby.equals("Sequence")) {
				// this.listboxBlastType.setSelectedIndex(0);
				// this.listboxBlastDBType.setSelectedIndex(0);
				this.textboxEvalue.setValue("10");
				textboxSequence.setValue("");

				String org = listboxOrganism.getItems().get(listboxOrganism.getSelectedIndex()).getLabel();

				listboxBlastType.setSelectedIndex(0);

				List listDBtype = new ArrayList();
				if (org.equals(AppContext.getDefaultOrganism())) {
					listDBtype.add("dna");
					listDBtype.add("cdna");
					listDBtype.add("cds");
				} else {
					listDBtype.add("dna");
					listDBtype.add("cdna");
					listDBtype.add("cds");
					listDBtype.add("upstream3000");
				}

				this.listboxBlastDBType.setModel(new SimpleListModel(listDBtype));

				this.labelBlastType.setValue("Select Blast program and database sequence.");

				this.divIncludeSequence.setVisible(AppContext.isIRRILAN());

				rowSequence.setVisible(true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// @Async
	// private List<String> queryWebsiteAsync(WebsiteQuery query, boolean display,
	// Button but) throws Exception {
	// List<String> listURLs = submitWebsite(query,display);
	// /*
	// if(listURLs!=null && display) {
	// Iterator<String> itUrl=listURLs.iterator();
	// int sitei=0;
	// while(itUrl.hasNext()) {
	// String url=itUrl.next();
	// String windowname="_" + ((String)query.getSite().get(sitei)).replace("
	// ","_").toLowerCase();
	// Executions.getCurrent().sendRedirect(url, windowname);
	// sitei++;
	// }
	// }
	// if(listURLs!=null) {
	// but.setHref(listURLs.get(0));
	// but.setTarget("_blank");
	// but.setDisabled(false);
	// }
	// */
	//
	// return listURLs;
	// }
	//
	// private List<String> submitWebsiteAsync(WebsiteQuery query, boolean display)
	// throws Exception {
	//
	// websiteService = (WebsiteDAO)AppContext.checkBean(websiteService,
	// "WebsiteDAO");
	// Future<List<String>> listURLs= websiteService.getURLAsync(query);
	//
	// if(listURLs!=null && display) {
	// Iterator<String> itUrl=listURLs.iterator();
	// int sitei=0;
	// while(itUrl.hasNext()) {
	// String url=itUrl.next();
	// String windowname="_" + ((String)query.getSite().get(sitei)).replace("
	// ","_").toLowerCase();
	// AppContext.debug("display " + windowname + ":" + url);
	// Executions.getCurrent().sendRedirect(url, "_blank");
	// sitei++;
	// }
	// } else {
	// AppContext.debug("listURLs=null");
	// }
	// return listURLs;
	// }
	//
	// public List<String> submitWebsite(WebsiteQuery query, boolean display) throws
	// Exception {
	//
	// websiteService = (WebsiteDAO)AppContext.checkBean(websiteService,
	// "WebsiteDAO");
	// List<String> listURLs= websiteService.getURL(query);
	//
	// if(listURLs!=null && display) {
	// Iterator<String> itUrl=listURLs.iterator();
	// int sitei=0;
	// while(itUrl.hasNext()) {
	// String url=itUrl.next();
	// String windowname="_" + ((String)query.getSite().get(sitei)).replace("
	// ","_").toLowerCase();
	// AppContext.debug("display " + windowname + ":" + url);
	// Executions.getCurrent().sendRedirect(url, "_blank");
	// sitei++;
	// }
	// }
	// return listURLs;
	// }

	/*
	 * @Async public Future<List<String>> queryWebsiteAsync(WebsiteQuery query,
	 * boolean display, Component comp) throws Exception {
	 * AppContext.debug("async wesite query started...");
	 * genomics=(GenomicsFacade)AppContext.checkBean(genomics,"GenomicsFacade");
	 * 
	 * List<String> list=genomics.queryWebsite(query,display); if(list!=null) {
	 * if(comp!=null) { Button but=(Button)comp; but.setHref(list.get(0));
	 * but.setDisabled(false); } } AppContext.debug("async wesite query done.");
	 * return new AsyncResult<List<String>>(list); }
	 */

	class SubmitWebsiteFuture {
		private List<Button> listButtons;
		private List<String> sites;
		private Future<List<String>> future;

		public SubmitWebsiteFuture(List<Button> listButtons, List sites, Future<List<String>> future) {
			super();
			this.listButtons = listButtons;
			this.future = future;
			this.sites = sites;
		}

		public SubmitWebsiteFuture(Button button, String site, Future<List<String>> future) {
			super();
			this.listButtons = new ArrayList();
			listButtons.add(button);
			this.future = future;
			this.sites = new ArrayList();
			sites.add(site);
		}

		public List<Button> getListButtons() {
			return listButtons;
		}

		public Future<List<String>> getFuture() {
			return future;
		}

		public List getSites() {
			return this.sites;
		}

	}

	public Callable<SubmitWebsiteFuture> callableSubmitwebsite(final WebsiteQuery query, final boolean display,
			final Button but) {
		return new Callable<SubmitWebsiteFuture>() {
			@Override
			public SubmitWebsiteFuture call() throws Exception {

				genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
				Future<List<String>> future = genomics.queryWebsiteAsync(query, display);

				// if(!future.isDone()) future.get(5, TimeUnit.SECONDS );
				// if(future.isDone()) return future.get();
				return new SubmitWebsiteFuture(but, query.getSite().toString(), future);
			}
		};
	}

	@Listen("onSelect =#listboxMyLocusListGeneset")
	public void onselectLocuslistgeneset() {

		if (listboxMyLocusListGeneset.getSelectedItem().getLabel().isEmpty())
			return;
		if (listboxMyLocusListGeneset.getSelectedItem().getLabel().equals("create new list...")) {
			Executions.sendRedirect("_workspace.zul?from=locus&src=locus");
			return;
		}

		buttonRicenetv2direct.setDisabled(true);
		buttonRicenetv2context.setDisabled(true);
		buttonStringDB.setDisabled(true);
		buttonCarmoFunctional.setDisabled(true);

		checkboxRicenetv2direct.setChecked(false);
		checkboxRicenetv2context.setChecked(false);
		checkboxStringDB.setChecked(false);
		checkboxCarmoFunctional.setChecked(false);

		checkboxPlantGSEA.setChecked(false);
		checkboxAgriGO.setChecked(false);
		checkboxIC4RExpr.setChecked(false);
		checkboxIC4RCoexpr.setChecked(false);

		buttonPlantGSEA.setDisabled(true);
		buttonAgriGO.setDisabled(true);
		buttonIC4RExpr.setDisabled(true);
		buttonIC4RCoexpr.setDisabled(true);
	}

	@Listen("onTimer =#timerCheckUrls")
	public void ontimer() {

		Iterator<SubmitWebsiteFuture> itFut = listFuture.iterator();
		Set setDone = new HashSet();
		StringBuffer buff = new StringBuffer();
		while (itFut.hasNext()) {
			SubmitWebsiteFuture fut = itFut.next();
			if (fut.getFuture().isDone()) {
				try {
					List<String> result = fut.getFuture().get();
					for (int iurl = 0; iurl < result.size(); iurl++) {
						String url = result.get(iurl);
						Button but = fut.getListButtons().get(iurl);
						Executions.getCurrent().sendRedirect(url, "_" + fut.getSites().get(iurl).toString()
								.toLowerCase().replace(" ", "_").replace("[", "").replace("]", ""));
						but.setHref(url);
						but.setDisabled(false);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				buff.append(fut.getSites().toString().replace("[", "").replace("]", "") + " DONE\n");

				setDone.add(fut);
			} else {
			}

		}
		listFuture.removeAll(setDone);
		AppContext.debug(listFuture.size() + " webqueries running...");
		textboxMsgWebclient.setValue(buff.toString() + "..." + listFuture.size() + " webqueries running..");

		if ((countQueryinbackgroundSeconds > maxQueryinbackgroundSeconds || listFuture.isEmpty())
				&& timerCheckUrls.isRunning()) {
			timerCheckUrls.stop();
			textboxMsgWebclient.setValue("");
			countQueryinbackgroundSeconds = 0;
		} else {

		}
		countQueryinbackgroundSeconds += 5;

	}

	@Listen("onClose ")

	private void queryWebsites(List setSites, Set setLoci, Button but) {
		try {
			if (checkboxQueryinbackground.isChecked()) {
				Callable<SubmitWebsiteFuture> call = callableSubmitwebsite(
						new WebsiteQuery(setSites, setLoci, AppContext.getPhantomjsExe()), false, but);
				SubmitWebsiteFuture future = call.call();
				AppContext.debug("callable<listurl> :" + (future != null ? future.toString() : "null"));
				// but.setHref((String)listurl.get(0)); but.setDisabled(false);
				listFuture.add(future);
				if (!timerCheckUrls.isRunning())
					timerCheckUrls.start();

			} else {
				
				WebsiteQuery query = new WebsiteQuery(setSites, setLoci, AppContext.getPhantomjsExe());
				genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
				List listurl = genomics.queryWebsite(query, true);

				AppContext.debug("listurl:" + (listurl != null ? listurl.toString() : "null"));
				but.setHref((String) listurl.get(0));
				but.setDisabled(false);

				/*
				 * if(listurl!=null) { Iterator<String> itUrl=listurl.iterator(); int sitei=0;
				 * while(itUrl.hasNext()) { String url=itUrl.next(); String windowname="_" +
				 * ((String)query.getSite().get(sitei)).replace(" ","_").toLowerCase();
				 * Executions.getCurrent().sendRedirect(url, "_blank"); sitei++; } }
				 */
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage());
		}
	}

	@Listen("onClick =#buttonMyLocusList")
	public void onclickLocuslist() {

		// Messagebox.show("onclickLocuslist()");
		AppContext.debug("onclickLocuslist()");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		if (listboxMyLocusListGeneset.getSelectedItem() == null)
			return;
		String listname = this.listboxMyLocusListGeneset.getSelectedItem().getLabel();
		if (listname.isEmpty())
			return;
		if (listname.equals("create new list...")) {
			return;
		}
		Set setLoci = workspace.getLoci(listname);

		int jobcount = 0;
		if (this.checkboxRicenetv2direct.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.RicenetNetworkNGA);
			queryWebsites(setSites, setLoci, buttonRicenetv2direct);
			jobcount++;
		}

		if (this.checkboxRicenetv2context.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.RicenetContextHub);
			queryWebsites(setSites, setLoci, buttonRicenetv2context);
			jobcount++;
		}

		if (this.checkboxStringDB.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.StringDBPPI);
			queryWebsites(setSites, setLoci, buttonStringDB);
			jobcount++;
		}

		if (this.checkboxCarmoFunctional.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.CarmoAnnotation);
			queryWebsites(setSites, setLoci, buttonCarmoFunctional);
			jobcount++;
		}

		if (this.checkboxPlantGSEA.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.PlantGSEA);
			queryWebsites(setSites, setLoci, buttonPlantGSEA);
			jobcount++;
		}
		if (this.checkboxAgriGO.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.AgriGO);
			queryWebsites(setSites, setLoci, buttonAgriGO);
			jobcount++;
		}
		if (this.checkboxIC4RExpr.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.IC4RExpr_RAP);
			queryWebsites(setSites, setLoci, buttonIC4RExpr);
			jobcount++;
		}
		if (this.checkboxIC4RCoexpr.isChecked()) {
			List setSites = new ArrayList();
			setSites.add(WebsiteQuery.IC4RCoexpr_RAP);
			queryWebsites(setSites, setLoci, buttonIC4RCoexpr);
			jobcount++;
		}

		if (checkboxQueryinbackground.isChecked())
			textboxMsgWebclient.setValue(jobcount + " webqueries running in background...");
		else
			textboxMsgWebclient.setValue("");

	}

	@Listen("onSelect= #listboxCV")
	public void onselectCV() {
		List listGoterms = new ArrayList();
		listGoterms.add("");
		// listGoterms.addAll(genomics.getGotermsByOrganism((String)listboxCV.getSelectedItem().getValue(),AppContext.getDefaultOrganism())
		// );
		listGoterms.addAll(genomics.getGotermsByOrganism((String) listboxCV.getSelectedItem().getValue(),
				this.listboxOrganism.getSelectedItem().getLabel()));

		listboxGOTerm.setModel(new SimpleListModel(listGoterms));
		listboxGOTerm.setSelectedIndex(0);
		listboxGODescendants.setModel(new SimpleListModel(new ArrayList()));
		listboxGOAncestors.setModel(new SimpleListModel(new ArrayList()));
		labelGOAncestors.setValue("");
		labelGODescendants.setValue("");
	}

	@Listen("onSelect= #listboxGOTerm")
	public void onselectGOterm() {

		return;

		/*
		 * try { List listGotermsA = new ArrayList();
		 * listGotermsA.addAll(genomics.getCVtermAncestors((String)listboxCV.
		 * getSelectedItem().getValue(), listboxGOTerm.getSelectedItem().getLabel() ));
		 * listboxGOAncestors.setModel(new SimpleListModel(listGotermsA));
		 * labelGOAncestors.setValue(listGotermsA.size() + " ancestors"); List
		 * listGotermsD = new ArrayList();
		 * listGotermsD.addAll(genomics.getCVtermDescendants((String)listboxCV.
		 * getSelectedItem().getValue(), listboxGOTerm.getSelectedItem().getLabel() ));
		 * listboxGODescendants.setModel(new SimpleListModel(listGotermsD));
		 * labelGODescendants.setValue(listGotermsD.size() + " descendants");
		 * 
		 * } catch(Exception ex) { ex.printStackTrace(); }
		 */

	}

	@Listen("onSelect= #listboxCVPato")
	public void onselectCVPato() {

		String cv = (String) listboxCVPato.getSelectedItem().getValue();

		List listPatoterms = new ArrayList();
		listPatoterms.add("");
		// listGoterms.addAll(genomics.getGotermsByOrganism((String)listboxCV.getSelectedItem().getValue(),AppContext.getDefaultOrganism())
		// );
		listPatoterms.addAll(genomics.getPatotermsByOrganism(cv, this.listboxOrganism.getSelectedItem().getLabel()));

		listboxPatoTerm.setModel(new SimpleListModel(listPatoterms));
		listboxPatoTerm.setSelectedIndex(0);
		listboxPatoDescendants.setModel(new SimpleListModel(new ArrayList()));
		listboxPatoAncestors.setModel(new SimpleListModel(new ArrayList()));
		labelPatoAncestors.setValue("");
		labelPatoDescendants.setValue("");

		if (cv.startsWith("qtaro")) {
			listboxAnnotation.setSelectedIndex(0);
		} else
			listboxAnnotation.setSelectedIndex(2);
	}

	@Listen("onSelect= #listboxPatoTerm")
	public void onselectPatoterm() {

		/*
		 * try { List listGotermsA = new ArrayList();
		 * listGotermsA.addAll(genomics.getCVtermAncestors((String)listboxCVPato.
		 * getSelectedItem().getValue(), listboxPatoTerm.getSelectedItem().getLabel()
		 * )); listboxPatoAncestors.setModel(new SimpleListModel(listGotermsA));
		 * labelPatoAncestors.setValue(listGotermsA.size() + " ancestors"); List
		 * listGotermsD = new ArrayList();
		 * listGotermsD.addAll(genomics.getCVtermDescendants((String)listboxCVPato.
		 * getSelectedItem().getValue(), listboxPatoTerm.getSelectedItem().getLabel()
		 * )); listboxPatoDescendants.setModel(new SimpleListModel(listGotermsD));
		 * labelPatoDescendants.setValue(listGotermsD.size() + " descendants");
		 * 
		 * } catch(Exception ex) { ex.printStackTrace(); }
		 * 
		 */

	}

	@Listen("onSelect = #listboxOrganism")
	public void onselectOrganism() {

		rowGoTerm.setVisible(false);
		rowRegion.setVisible(false);
		rowMySnpList.setVisible(false);
		rowGeneFunction.setVisible(false);
		rowSequence.setVisible(false);
		// rowNPBannotation.setVisible(false);
		rowPatoTerm.setVisible(false);
		rowGeneName.setVisible(false);

		// <listitem label="Annotation (by source)"/>
		// <listitem selected="true" label="Gene name/symbol/function"/>

		String selorg = listboxOrganism.getSelectedItem().getLabel();
		if (selorg.equalsIgnoreCase("ALL")) {
			List listSearch = new ArrayList();
			// listSearch.add("");
			// listSearch.add("Gene function");
			listSearch.add("Gene name/symbol/function");
			// listSearch.add("Gene Ontology term");
			// listSearch.add("Region");
			listSearch.add("Sequence");
			// listSearch.add("My SNP List");
			listboxSearchby.setModel(new SimpleListModel(listSearch));
			listboxSearchby.setSelectedIndex(1);
			this.rowSequence.setVisible(true);
			this.searchGenefunctionButton.setDisabled(false);
			this.searchGenenameButton.setDisabled(false);

			labelChrExample.setValue("");
			this.listboxAnnotation.setVisible(false);
			divGeneModel.setVisible(false);
		} else if (selorg.equals(AppContext.getDefaultOrganism())) {

			List listSearch = new ArrayList();
			// listSearch.add("");
			listSearch.add("Gene function");
			listSearch.add("Gene name/symbol/function");
			listSearch.add("Gene Ontology term");
			// listSearch.add("Plant and Trait Ontology term");
			listSearch.add("Trait");
			listSearch.add("Region");
			listSearch.add("Sequence");
			listSearch.add("SNP Annotator (My SNP List)");
			listboxSearchby.setModel(new SimpleListModel(listSearch));
			listboxSearchby.setSelectedIndex(1);
			rowGeneName.setVisible(true);
			this.searchGenefunctionButton.setDisabled(false);
			this.searchGenenameButton.setDisabled(false);
			// onselectSearchby();

			labelChrExample.setValue("(ex. chr01)");

			// rowNPBannotation.setVisible(true);
			// this.listboxAnnotation.setVisible(true);
			divGeneModel.setVisible(true);

		} else if (selorg.toLowerCase().equals("minghui 63") || selorg.toLowerCase().equals("zeshan 97")
				|| selorg.toLowerCase().equals("n22") || selorg.toLowerCase().equals("ir8")) {
			List listSearch = new ArrayList();
			listSearch.add("Sequence");
			// listSearch.add("My SNP List");
			listboxSearchby.setModel(new SimpleListModel(listSearch));
			listboxSearchby.setSelectedIndex(0);
			this.rowSequence.setVisible(true);
			divGeneModel.setVisible(false);
		} else {
			List listSearch = new ArrayList();
			// listSearch.add("");
			listSearch.add("Annotation (by source)");
			listSearch.add("Gene name/symbol/function");

			this.searchGenefunctionButton.setDisabled(true);
			// listSearch.add("Plant and Trait Ontology term");
			listSearch.add("Gene Ontology term");
			listSearch.add("Region");
			listSearch.add("Sequence");
			// listSearch.add("My SNP List");
			listboxSearchby.setModel(new SimpleListModel(listSearch));
			listboxSearchby.setSelectedIndex(1);
			// this.rowGoTerm.setVisible(true);
			rowGeneName.setVisible(true);

			String selOrg = listboxOrganism.getSelectedItem().getLabel();
			if (selOrg.equals("93-11"))
				labelChrExample.setValue("(ex. 9311_chr01, scaffold01_4619) ");
			else if (selOrg.equals("Kasalath"))
				labelChrExample.setValue("(ex. chr01, um) ");
			else
				// labelChrExample.setValue("(ex. " +
				// listContigs.get(1).toString().toLowerCase() + ") " );
				labelChrExample.setValue("(ex. scaffold...)");

			// this.listboxAnnotation.setVisible(false);
			divGeneModel.setVisible(false);
		}

	}

	@Listen("onChanging =#textboxEvalue")
	public void onchangingEvalue() {

		if (textboxEvalue.getValue() != null && !textboxEvalue.getValue().isEmpty()) {
			try {
				Double.valueOf(textboxEvalue.getValue());
			} catch (Exception ex) {
				Messagebox.show(ex.getMessage(), "Exception", Messagebox.OK, Messagebox.EXCLAMATION);
				textboxEvalue.setValue("");
			}
		}
	}

	@Listen("onClick =#searchRegionButton")
	public void searchByRegion() {

		AppContext.debug("searching region in " + listboxContig.getValue());

		// if(this.listboxContig.getSelectedIndex()==0 ||
		// this.intboxContigStart.getValue()==null ||
		// this.intboxContigEnd.getValue()==null ) {
		if (this.listboxContig.getValue() == null || this.listboxContig.getValue().trim().isEmpty()
				|| this.intboxContigStart.getValue() == null || this.intboxContigEnd.getValue() == null) {
			Messagebox.show("Required contig, start and end positions", "Incomplete info", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}
		if (this.intboxContigStart.getValue().intValue() > this.intboxContigEnd.getValue().intValue()) {
			Messagebox.show("End should be greater than or equal to start", "Invalid values", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}
		if (this.intboxContigStart.getValue().intValue() < 1 || this.intboxContigEnd.getValue().intValue() < 1) {
			Messagebox.show("Start and End should be greater than zero", "Invalid values", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}

		initResults();
		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

			// locusresult=new ArrayList();
			locusresult = genomics.getLociByRegion(listboxContig.getValue().trim(),
					Long.valueOf(intboxContigStart.getValue()), Long.valueOf(intboxContigEnd.getValue()),
					this.listboxOrganism.getSelectedItem().getLabel(),
					(String) this.listboxAnnotation.getSelectedItem().getValue());
			// listboxLocus.setRowRenderer(new LocusGridRenderer());
			listboxLocus.setItemRenderer(new LocusListItemRenderer());
			listboxLocus.setModel(new SimpleListModel(locusresult));

			if (locusresult.size() > 0) {
				listboxLocus.setVisible(true);
				hboxAddtolist.setVisible(true);
				hboxDownload.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
			}

			this.msgbox.setValue("Search by region " + listboxContig.getValue().trim() + " ["
					+ intboxContigStart.getValue().intValue() + "-" + intboxContigEnd.getValue().intValue()
					+ "] ... RESULT:" + locusresult.size() + " loci");

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "searchByRegion Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onSelect =#listboxMySNPList")
	public void onselectMySnpList() {

		if (listboxMySNPList.getSelectedIndex() > 0) {

			if (listboxMySNPList.getSelectedItem().getLabel().equals("create new list...")) {
				Executions.sendRedirect("_workspace.zul?from=snp&src=locus");
			}
		}
	}

	// @Listen("onClick =#searchMySnpListButton")
	public void searchbyMySnpList() {
		initResults();

		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			String mylist = this.listboxMySNPList.getSelectedItem().getLabel();

			String contigname[] = mylist.trim().split(":");

			Collection colPos = workspace.getSnpPositions(contigname[0].trim(), contigname[1].trim());

			String annot = (String) this.listboxAnnotation.getSelectedItem().getValue();
			annot = annot.replace("only", "");

			locusresult = genomics.getLociByContigPositions(contigname[0].trim(), colPos,
					this.listboxOrganism.getSelectedItem().getLabel(), intboxPlusMinusBP.getValue(), annot);
			// listboxLocus.setRowRenderer(new LocusGridRenderer());
			listboxLocus.setItemRenderer(new LocusListItemRenderer());
			listboxLocus.setModel(new SimpleListModel(locusresult));

			if (locusresult.size() > 0)
				hboxAddtolist.setVisible(true);
			else
				hboxAddtolist.setVisible(false);

			this.msgbox.setValue("Search by SNP List positions: Contig " + contigname[0].trim() + ", SNP List "
					+ listboxMySNPList.getSelectedItem().getLabel() + " ... RESULT:" + locusresult.size() + " loci");

			listboxLocus.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onlclickSearchFunction Exception", Messagebox.OK, Messagebox.ERROR);
		}

		updateGenesetLinks();

	}

	// //@Listen("onClick =#searchMySnpListButtonWithqtl")
	// @Listen("onClick =#searchMySnpListButton")
	// public void searchbyMySnpListQtl() {
	// initResults();
	//
	//
	//
	// try {
	// genomics = (GenomicsFacade)AppContext.checkBean(genomics, "GenomicsFacade");
	// workspace =(WorkspaceFacade)AppContext.checkBean(workspace,
	// "WorkspaceFacade");
	//
	// if(listboxMySNPList.getSelectedItem()==null) return;
	//
	// String mylist = this.listboxMySNPList.getSelectedItem().getLabel();
	//
	// String contigname[] = mylist.trim().split(":");
	//
	// Collection colPos = workspace.getSnpPositions(contigname[0].trim(),
	// contigname[1].trim());
	// boolean hasPvalue= workspace.getSnpPositionPvalueListNames().contains(
	// mylist);
	//
	// AppContext.debug("mylist=" + mylist + ", hasPvalue=" + hasPvalue + "
	// getSnpPositionPvalueListNames=" + workspace.getSnpPositionPvalueListNames());
	//
	// //setAnnotations=new LinkedHashSet();
	// setAnnotations=new TreeSet();
	// Integer maxint = null;
	// try {
	// maxint = Integer.valueOf(textboxMaxInteracting.getValue());
	// } catch(Exception ex) {};
	//
	// String annot=(String)this.listboxAnnotation.getSelectedItem().getValue();
	// annot=annot.replace("only", "");
	//
	//
	// markerresult = genomics.getMarkerAnnotsByContigPositions(contigname[0].trim()
	// ,
	// colPos,
	// this.listboxOrganism.getSelectedItem().getLabel(),
	// intboxPlusMinusBP.getValue(),
	// annot , setAnnotations, maxint);
	// //listboxMarker.setItemRenderer(new MarkerListItemRenderer());
	// //listboxMarker.setModel( new SimpleListModel( markerresult ));
	// Map<Integer,String> idx2Column=new HashMap();
	//
	// if(markerresult.size()>0) {
	// List<Component> listheadersmarker =
	// listboxMarkerVar.getListhead().getChildren();
	// listheadersmarker.clear();
	// Listheader header=new Listheader();
	// header.setLabel("CONTIG,POS,-LOGP"); header.setWidth("150px");
	// header.setSortAscending(new MarkerAnnotationSorter(true,0));
	// header.setSortDescending(new MarkerAnnotationSorter(false,0));
	// listheadersmarker.add(header);
	//
	// //if(hasPvalue) {
	// if(true) {
	// headerLogp=new Listheader();
	// headerLogp.setLabel("-LOGP"); headerLogp.setWidth("100px");
	// headerLogp.setSortAscending(new MarkerAnnotationSorter(true,1));
	// headerLogp.setSortDescending(new MarkerAnnotationSorter(false,1));
	// listheadersmarker.add(headerLogp);
	// AppContext.debug("headerLogp created");
	// } else AppContext.debug("headerLogp NOT created");
	//
	// /*
	// header=new Listheader();
	// header.setLabel("POSITION"); header.setWidth("100px");
	// header.setSortAscending(new MarkerAnnotationSorter(true,1));
	// header.setSortDescending(new MarkerAnnotationSorter(false,1));
	// listheadersmarker.add(header);
	// */
	// header=new Listheader();
	// header.setLabel("GENE MODELS"); header.setWidth("300px");
	// header.setSortAscending(new MarkerAnnotationSorter(true,"GENE MODELS"));
	// header.setSortDescending(new MarkerAnnotationSorter(false,"GENE MODELS"));
	// listheadersmarker.add(header);
	//
	// //Iterator itAnnotnames = markerresult.get(0).getAnnotations().iterator();
	// Iterator itAnnotnames = setAnnotations.iterator();
	// while(itAnnotnames.hasNext()) {
	// String annotname=(String)itAnnotnames.next();
	// if(annotname.equals("GENE MODELS")) continue;
	// header=new Listheader();
	// header.setLabel(annotname.toUpperCase());
	// if(setAnnotations.size()>3)
	// header.setWidth("400px");
	// else header.setWidth("700px");
	// header.setSortAscending(new MarkerAnnotationSorter(true,annotname));
	// header.setSortDescending(new MarkerAnnotationSorter(false,annotname));
	// listheadersmarker.add(header);
	// }
	// };
	//
	// listboxMarkerVar.setItemRenderer(new
	// MarkerVarListItemRenderer(setAnnotations));
	// listboxMarkerVar.setModel( new SimpleListModel( markerresult ));
	//
	// if(markerresult.size()>0){
	// hboxAddtolist.setVisible(true);
	// }
	// else {
	// hboxAddtolist.setVisible(false);
	// }
	//
	//
	// this.msgbox.setValue("Search by SNP List positions: Contig " +
	// contigname[0].trim() + ", SNP List " +
	// listboxMySNPList.getSelectedItem().getLabel() + " ... RESULT:" +
	// markerresult.size() + " markers");
	//
	// //listboxMarker.setVisible(true);
	// listboxMarkerVar.setVisible(true);
	// borderMarkerVar.setVisible(true);
	// hboxDownload.setVisible(true);
	//
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// Messagebox.show( ex.getMessage(), "onlclickSearchFunction Exception",
	// Messagebox.OK, Messagebox.ERROR );
	// }
	//
	// updateGenesetLinks();
	//
	// }
	//

	@Listen("onClick =#searchMySnpListButton")
	public void searchbyMySnpListQtl() {
		initResults();

		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			if (listboxMySNPList.getSelectedItem() == null)
				return;

			String mylist = this.listboxMySNPList.getSelectedItem().getLabel();

			String contigname[] = mylist.trim().split(":");

			Collection colPos = workspace.getSnpPositions(contigname[0].trim(), contigname[1].trim());
			boolean hasPvalue = workspace.getSnpPositionPvalueListNames().contains(mylist);

			AppContext.debug("mylist=" + mylist + ", hasPvalue=" + hasPvalue + "  getSnpPositionPvalueListNames="
					+ workspace.getSnpPositionPvalueListNames());

			// setAnnotations=new LinkedHashSet();
			setAnnotations = new TreeSet();
			Integer maxint = null;
			try {
				maxint = Integer.valueOf(textboxMaxInteracting.getValue());
			} catch (Exception ex) {
			}
			;

			String annot = (String) this.listboxAnnotation.getSelectedItem().getValue();
			annot = annot.replace("only", "");

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

			markerresult = genomics.getMarkerAnnotsByContigPositions(contigname[0].trim(), colPos,
					this.listboxOrganism.getSelectedItem().getLabel(), intboxPlusMinusBP.getValue(), annot,
					setAnnotations, maxint, excludeAnnotation);
			// listboxMarker.setItemRenderer(new MarkerListItemRenderer());
			// listboxMarker.setModel( new SimpleListModel( markerresult ));
			Map<Integer, String> idx2Column = new HashMap();

			if (markerresult.size() > 0) {
				List<Component> listheadersmarker = listboxMarkerVar.getListhead().getChildren();
				listheadersmarker.clear();
				Listheader header = new Listheader();
				header.setLabel("CONTIG,POS,-LOGP");
				header.setWidth("150px");
				header.setSortAscending(new MarkerAnnotationSorter(true, 0));
				header.setSortDescending(new MarkerAnnotationSorter(false, 0));
				listheadersmarker.add(header);

				// if(hasPvalue) {
				if (true) {
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

			if (markerresult.size() > 0) {
				hboxAddtolist.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
			}

			this.msgbox.setValue("Search by SNP List positions: Contig " + contigname[0].trim() + ", SNP List "
					+ listboxMySNPList.getSelectedItem().getLabel() + " ... RESULT:" + markerresult.size()
					+ " markers");

			// listboxMarker.setVisible(true);
			listboxMarkerVar.setVisible(true);
			borderMarkerVar.setVisible(true);
			hboxDownload.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onlclickSearchFunction Exception", Messagebox.OK, Messagebox.ERROR);
		}

		updateGenesetLinks();

	}

	@Listen("onClick =#searchByBlast")
	public void searchByBlast() {

		

		Clients.evalJavaScript("myFunction();");

		initResults();
		
		searchMenu = false;
		flipSearchBar();
		
		AppContext.debug(textboxSequence.getMaxlength() + " seq maxlength");
		textboxSequence.setMaxlength(500000000);
		if (textboxSequence.getValue().isEmpty() && uploadString == null) {
			Messagebox.show("Empty sequence. Paste sequence or upload FASTA file", "Incomplete info", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}

		if (!textboxSequence.getValue().isEmpty() && uploadString != null) {
			Messagebox.show("Both sequence text and file uploads are filled. Use only one method", "Incomplete info",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		if (this.listboxBlastDBType.getSelectedIndex() == 0) {
			Messagebox.show("Select database sequence type", "Incomplete info", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		if (this.listboxBlastType.getSelectedIndex() == 0) {
			Messagebox.show("Select BLAST program to use", "Incomplete info", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
			String seq = null;
			if (!textboxSequence.getValue().isEmpty())
				seq = textboxSequence.getValue();
			else if (uploadString != null && !uploadString.isEmpty())
				seq = uploadString;

			locusalignmentresult = genomics.getLociBySequence(seq, this.listboxOrganism.getSelectedItem().getLabel(),
					listboxBlastType.getSelectedItem().getLabel(), listboxBlastDBType.getSelectedItem().getLabel(),
					Double.valueOf(textboxEvalue.getValue()), this.checkboxTopSequencehit.isChecked(),
					checkboxIncludeSequence.isChecked());

			// gridAlignment.setRowRenderer(new LocusBlastresultGridRenderer());
			// gridAlignment.setModel( new SimpleListModel( locusalignmentresult ));
			// gridAlignment.setVisible(true);

			
			// listboxAlignment.setItemRenderer(new LocusBlastresultGridRenderer());
			listboxAlignment.setItemRenderer(new BlastResultListitemRenderer());
			listboxAlignment.setModel(new SimpleListModel(locusalignmentresult));
			listboxAlignment.setVisible(true);

			String dbtype = this.listboxBlastDBType.getSelectedItem().getLabel();
			if ((dbtype.equals("cdna") || dbtype.equals("cds") || dbtype.equals("pep"))
					&& locusalignmentresult.size() > 0) {
				hboxAddtolist.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
			}

			hboxDownload.setVisible(true);

			this.msgbox.setValue("Search by sequence ... RESULT:" + locusalignmentresult.size() + " loci");

			resultContentDiv.setVisible(true);
			variety_panelPassportResult.setVisible(true);
			div_info.setVisible(false);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "searchByBlast Exception", Messagebox.OK, Messagebox.ERROR);
		}

		updateGenesetLinks();

	}

	private void flipSearchBar() {
		if (searchMenu) {
			myModal.setClass("modal");
			searchMenu = false;
			// layoutResultId.setVisible(false);
		} else {
			myModal.setClass("unmodal");
			searchMenu = true;
			// layoutResultId.setVisible(true);
		}

	}

	private void setListboxCurrent(Listbox listbox, Listitem item) {

		// listbox.setc
	}

	private void createBlastHint() {

		String blastdbtype = listboxBlastDBType.getSelectedItem().getLabel();
		String blastDBMsg = listboxOrganism.getSelectedItem().getLabel() + " ";
		String blasttype = listboxBlastType.getSelectedItem().getLabel();

		// listboxBlastType.invalidate();listboxBlastDBType.invalidate();listboxBlastDBType.invalidate();

		// String blastdbtype =
		// listboxBlastDBType.getItemAtIndex(listboxBlastDBType.getSelectedIndex()).getLabel();
		// String blastDBMsg =
		// listboxOrganism.getItemAtIndex(listboxOrganism.getSelectedIndex()).getLabel()
		// + " ";
		// String blasttype =
		// listboxBlastType.getItemAtIndex(listboxBlastType.getSelectedIndex()).getLabel();

		if (blasttype.isEmpty()) {
			blasttype = "blastn";
			blastdbtype = "dna";
		}

		// if(listboxBlastDBType.getItems().size()==1) {
		// blastdbtype = listboxBlastDBType.getItems().get(0).getLabel();
		// }

		if (blastdbtype.isEmpty()) {
			labelBlastType.setValue("Select database sequence type");
			return;
			// blastdbtype = "dna";
		}

		/*
		 * if(blastdbtype.isEmpty()) { if(blasttype.equals("blastp") ||
		 * blasttype.equals("blastx")) blastdbtype = "pep"; else blastdbtype = "dna"; }
		 */

		AppContext.debug("blastdbtype=" + blastdbtype);

		if (blastdbtype.equals("dna"))
			blastDBMsg += "genome";
		else if (blastdbtype.equals("cds"))
			blastDBMsg += "coding sequences";
		else if (blastdbtype.equals("cdna"))
			blastDBMsg += "locus regions";
		else if (blastdbtype.equals("upstream3000"))
			blastDBMsg += "3000bp upstream regions";
		else if (blastdbtype.equals("pep"))
			blastDBMsg += "protein sequences";

		String blastMsg = "";

		if (blasttype.equals("blastn"))
			blastMsg = "Search " + blastDBMsg + " using a nucleotide query";
		else if (blasttype.equals("blastp"))
			blastMsg = "Search " + blastDBMsg + " using a protein query";
		else if (blasttype.equals("blastx"))
			blastMsg = "Search " + blastDBMsg + " using a translated nucleotide query";
		else if (blasttype.equals("tblastn"))
			blastMsg = "Search translated " + blastDBMsg + " using a protein query";
		else if (blasttype.equals("tblastx"))
			blastMsg = "Search translated " + blastDBMsg + " using a translated nucleotide query";

		AppContext.debug("blasttype=" + blasttype);
		AppContext.debug("blastMsg=" + blastMsg);

		labelBlastType.setValue(blastMsg);

	}

	@Listen("onSelect = #listboxBlastType")
	public void onselectBlastType() {
		// listboxBlastType.invalidate();
		// String blasttype =
		// listboxBlastType.getItems().get(listboxBlastType.getSelectedIndex()).getLabel();
		String blasttype = listboxBlastType.getSelectedItem().getLabel();

		if (blasttype.isEmpty())
			blasttype = "blastn";

		List listDBType = new ArrayList();

		listDBType.add("");
		if (listboxOrganism.getSelectedItem().getLabel().equals(AppContext.getDefaultOrganism())) {

			if (blasttype.equals("blastn") || blasttype.equals("tblastn") || blasttype.equals("tblastx")) {
				listDBType.add("dna");
				listDBType.add("cds");
				listDBType.add("cdna");
			} else {
				listDBType.add("pep");
			}

		} else {
			if (blasttype.equals("blastn") || blasttype.equals("tblastn") || blasttype.equals("tblastx")) {
				listDBType.add("dna");
				listDBType.add("cds");
				listDBType.add("cdna");
				listDBType.add("upstream3000");
			} else {
				listDBType.add("pep");
			}
		}

		listboxBlastDBType.setModel(new SimpleListModel(listDBType));

		// Events.sendEvent( "onSelect", listboxBlastDBType, 0);
		listboxBlastDBType.setSelectedIndex(0);
		createBlastHint();

	}

	@Listen("onSelect = #listboxBlastDBType")
	public void onselectBlastDBType() {
		createBlastHint();

	}

	@Listen("onSelect = #listboxMyLocusListOntology")
	public void onselectMyVarieties() {
		if (listboxMyLocusListOntology.getSelectedItem().getLabel().equals("create new list...")) {
			Executions.sendRedirect("_workspace.zul?from=locus");
		}
	}

	@Listen("onClick =#buttonDownloadCSV")
	public void downloadtableCSV() {
		downloadtable(",", "; ");
	}

	@Listen("onClick =#buttonDownloadTab")
	public void downloadtableTab() {
		downloadtable("\t", "; ");
	}

	private void submitForm(String target, Map params) {
		Form form = new Form();
		form.setDynamicProperty("action", params.get("action"));
		form.setDynamicProperty("method", "post");
		form.setDynamicProperty("target", target); // 'name' of iframe form.setPage(super.getPage()); // need Page
													// object

		Input input1 = new Input();
		input1.setParent(form);

		Iterator itParams = params.keySet().iterator();
		while (itParams.hasNext()) {
			Object key = itParams.next();
			input1.setDynamicProperty(key.toString(), params.get(key).toString());
		}

		// input1.setDynamicProperty("type", "hidden");
		// input1.setDynamicProperty("name", "CMD"); input1.setValue("This is a
		// POST!!!");

		Clients.submitForm(form);

	}

	void updateGenesetLinks() {
		return;
		//
		// Set[] loci = collectLoci();
		// StringBuffer buff=new StringBuffer();
		// if(!loci[0].isEmpty()) buff.append(
		// loci[0].toString().replace("[","").replace("]","").trim());
		// if(!loci[1].isEmpty()) {
		// if(buff.length()>0) buff.append(",");
		// buff.append( loci[1].toString().replace("[","").replace("]","").trim());
		// }
		//
		// mapParamsRicenetV1.clear();
		// mapParamsRicenetV2DirectNeighbors.clear();
		// mapParamsRicenetV2ContextHubs.clear();
		// mapParamsPantherOverrep.clear();
		//
		// if(!loci[0].isEmpty()) {
		// buttonRicenetV1.setTarget("_geneset");
		// mapParamsRicenetV1.put("action","http://www.functionalnet.org/ricenet/cgi-perl/RiceNet.v1_nga_form.cgi");
		// mapParamsRicenetV1.put("nga_seed",
		// loci[0].toString().replace("[","").replace("]","").trim());
		// buttonRicenetV1.setVisible(true);
		// } else {
		// buttonRicenetV1.setVisible(false);
		// }
		//
		// if(buff.length()>0) {
		//
		// mapParamsRicenetV2ContextHubs.put("action","http://www.inetbio.org/ricenet/context_associated_hub.php");
		// mapParamsRicenetV2ContextHubs.put("type","4");
		// mapParamsRicenetV2ContextHubs.put("genes", buff.toString());
		//
		//
		// mapParamsRicenetV2DirectNeighbors.put("action","http://www.inetbio.org/ricenet/Network_nga_form_conv.php");
		// mapParamsRicenetV2DirectNeighbors.put("type","1");
		// mapParamsRicenetV2DirectNeighbors.put("genes",buff.toString());
		//
		// mapParamsPantherOverrep.put("action","http://pantherdb.org/geneListAnalysis.do");

		// mapParamsPRINPPI.put("action","http://bis.zju.edu.cn/prin/search.do");
		// mapParamsPRINPPI.put("ids", buff.toString());

		// mapParamsPRINNeighbors.put("action","http://bis.zju.edu.cn/prin/protein.do");
		// mapParamsPRINNeighbors.put("ac", buff.toString());

		// mapParamsPantherOverrep.put("idField", buff.toString());
		// mapParamsPantherOverrep.put("fileType", "10");
		// mapParamsPantherOverrep.put("organism","Oryza sativa");
		// mapParamsPantherOverrep.put("ataset","Oryza sativa");
		// mapParamsPantherOverrep.put("resultType","3");
		// mapParamsPantherOverrep.put("defaultSettingsOver","on");
		//
		// hboxGenesetAnalysis.setVisible(true);
		//
		// } else {
		// //buttonRicenetV2ContextHubs.setVisible(false);
		// //buttonRicenetV2DirectNeighbors.setVisible(false);
		// //buttonPantherOverrep.setVisible(false);
		// hboxGenesetAnalysis.setVisible(false);
		// }
		//
		//
		//
		// if(!loci[0].isEmpty()) {
		// buttonRicenetV1.setTarget("_geneset");
		// buttonRicenetV1.setHref(
		// "http://www.functionalnet.org/ricenet/cgi-perl/RiceNet.v1_nga_form.cgi?" +
		// loci[0].toString().replace("[","").replace("]","").trim());
		// buttonRicenetV1.setVisible(false);
		// } else {
		// buttonRicenetV1.setHref(null);
		// buttonRicenetV1.setVisible(false);
		// }
		//
		// if(buff.length()>0) {
		// buttonRicenetV2ContextHubs.setTarget("_geneset");
		// buttonRicenetV2ContextHubs.setHref("http://www.inetbio.org/ricenet/convertid_writequery.php?type=4&genes="
		// + buff.toString());
		// buttonRicenetV2DirectNeighbors.setTarget("_geneset");
		// buttonRicenetV2DirectNeighbors.setHref("http://www.inetbio.org/ricenet/convertid_writequery.php?type=1&genes="
		// + buff.toString());
		// buttonRicenetV2DirectNeighbors.setVisible(true);
		// buttonRicenetV2ContextHubs.setVisible(true);
		//
		//
		// //"http://pantherdb.org/webservices/garuda/enrichment.jsp?organism=Oryza
		// sativa&
		// //http://pantherdb.org/geneListAnalysis.do
		// buttonPantherOverrep.setTarget("_geneset");
		// buttonPantherOverrep.setHref("http://pantherdb.org/geneListAnalysis.do?idField="
		// + buff.toString() + "&fileType=10&organism=Oryza sativa&dataset=Oryza
		// sativa&resultType=3&defaultSettingsOver=on");
		// buttonPantherOverrep.setVisible(true);
		// hboxGenesetAnalysis.setVisible(true);
		//
		// } else {
		// //buttonRicenetV2ContextHubs.setVisible(false);
		// //buttonRicenetV2DirectNeighbors.setVisible(false);
		// //buttonPantherOverrep.setVisible(false);
		// hboxGenesetAnalysis.setVisible(false);
		// }
		//
		//

	}

	private Set[] collectLoci() {
		Set setMSU = new HashSet();
		Set setRAP = new HashSet();
		if (this.listboxMarkerVar.isVisible()) {
			boolean includeRicenet = checkboxListRicenet.isChecked();
			boolean includeGenemodel = checkboxListModel.isChecked();
			boolean includePrin = checkboxListPrin.isChecked();
			boolean includePromoter = checkboxListPromoter.isChecked();
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

		} else if (listboxAlignment.isVisible()) {

			int isize = listboxAlignment.getListModel().getSize();
			for (int i = 0; i < isize; i++) {
				LocalAlignment align = (LocalAlignment) listboxAlignment.getListModel().getElementAt(i);
				String subj = align.getSacc().replace("|msu7", "");
				if (subj.toUpperCase().startsWith("LOC_"))
					setMSU.add(subj);
				if (subj.toUpperCase().startsWith("OS0") || subj.startsWith("OS1"))
					setRAP.add(subj);
			}
		}

		return new Set[] { setMSU, setRAP };
	}

	public void downloadtable(String delimiter, String celldelimiter) {

		AppContext.debug("executing downloadtable...");
		String quote = "";
		if (delimiter.equals(","))
			quote = "\"";

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
				String an = itAnnots.next();
				if (an.equals("GENE MODELS"))
					continue;
				buff.append("\"").append(an).append("\"");
				if (itAnnots.hasNext())
					buff.append(delimiter);
			}
			buff.append("\n");

			List displaylist = markerresult;
			int pm = 0;
			Integer opm = intboxPlusMinusBP.getValue();
			if (opm != null)
				pm = opm.intValue();

			if (radioGroupbyGene.isSelected())
				displaylist = genomics.getMarkerAnnotsByGene(markerresult, pm);
			else if (radioGroupbyQtl.isSelected())
				displaylist = genomics.getMarkerAnnotsByQTL(markerresult, pm);

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

			// Iterator<MarkerAnnotation> itMarker=markerresult.iterator();
			// while(itMarker.hasNext()) {
			//
			// MarkerAnnotation annot = itMarker.next();
			// buff.append(annot.getContig()).append(delimiter).append(annot.getPosition()).append(delimiter);
			//
			// if(annot.getAnnotation("GENE MODELS")!=null && !annot.getAnnotation("GENE
			// MODELS").isEmpty()) {
			// //Iterator<Locus> itLocus=annot.getGenes().iterator();
			// Iterator<Locus> itLocus=annot.getAnnotation("GENE MODELS").iterator();
			//
			// //buff.append("\"");
			// buff.append(quote);
			// while(itLocus.hasNext()) {
			// Locus loc= itLocus.next();
			// buff.append( loc.getUniquename() + " " + loc.getContig() + " [" +
			// loc.getFmin() + "-" + loc.getFmax() + "]" );
			// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
			// buff.append(" -" + loc.getDescription() );
			// }
			// if(loc instanceof MergedLoci) {
			// MergedLoci ml=(MergedLoci)loc;
			// StringBuffer loci=new StringBuffer();
			// if(ml.getMSU7Name()!=null && !loc.getUniquename().startsWith("LOC_") )
			// loci.append(ml.getMSU7Name());
			// if(ml.getRAPRepName()!=null && ! (loc.getUniquename().startsWith("Os0") ||
			// loc.getUniquename().startsWith("Os1")) ) {
			// if(loci.length()>0) loci.append(",");
			// loci.append(ml.getRAPRepName());
			// }
			// if(ml.getRAPPredName()!=null && ! (loc.getUniquename().startsWith("Os0") ||
			// loc.getUniquename().startsWith("Os1")) ) {
			// if(loci.length()>0) loci.append(",");
			// loci.append(ml.getRAPPredName());
			// }
			// if(ml.getIRICName()!=null && ! loc.getUniquename().startsWith("OsNippo")) {
			// if(loci.length()>0) loci.append(",");
			// loci.append(ml.getIRICName());
			// }
			// if(loci.length()==0 && ml.getFGeneshName()!=null)
			// loci.append(ml.getFGeneshName());
			//
			// if(loci.length()>0) buff.append(" (" + loci + ")");
			// }
			//
			// if(itLocus.hasNext()) buff.append("; ");
			// }
			// //buff.append("\"");
			// buff.append(quote);
			//
			// };
			//
			// buff.append(delimiter);
			// //AppContext.debug(annot.getContig() + " " + annot.getPosition() + " " +
			// annot.getAnnotations());
			//
			// itAnnots =tempset.iterator();
			// while(itAnnots.hasNext()) {
			// String annotname=(String)itAnnots.next();
			// if(annot.getAnnotation(annotname)!=null) {
			// //buff.append("\"");
			// buff.append(quote);
			// Iterator<Locus> itLocus= annot.getAnnotation(annotname).iterator();
			// while(itLocus.hasNext()) {
			// Locus loc= itLocus.next();
			// buff.append( loc.getUniquename() + " " + loc.getContig() + "[" +
			// loc.getFmin() + "-" + loc.getFmax() + "]" );
			// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
			// buff.append(" - " + loc.getDescription() );
			// }
			// if(itLocus.hasNext()) buff.append("; ");
			// }
			// //buff.append("\"");
			// buff.append(quote);
			// }
			//
			// if(itAnnots.hasNext()) buff.append(delimiter);
			// }
			//
			// buff.append("\n");
			// }

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
		} else if (listboxAlignment.isVisible()) {

			StringBuffer buff = new StringBuffer();
			buff.append("QUERY").append(delimiter).append("Q_START").append(delimiter).append("Q_END").append(delimiter)
					.append("SUBJECT").append(delimiter).append("S_START").append(delimiter).append("S_END")
					.append(delimiter).append("S_STRAND").append(delimiter).append("ALIGNED_LENGTH").append(delimiter)
					.append("MATCHES").append(delimiter).append("MISMATCHES").append(delimiter).append("PERCENT_MATCH")
					.append(delimiter).append("E_VALUE").append(delimiter).append("RAW_SCORE");

			if (this.checkboxIncludeSequence.isChecked()) {
				buff.append(delimiter).append("Q_SEQUENCE").append(delimiter).append("S_SEQUENCE");
			}
			buff.append("\n");
			int isize = listboxAlignment.getListModel().getSize();
			for (int i = 0; i < isize; i++) {
				LocalAlignment align = (LocalAlignment) listboxAlignment.getListModel().getElementAt(i);
				buff.append(quote).append(align.getQacc()).append(quote).append(delimiter).append(align.getQstart())
						.append(delimiter).append(align.getQend()).append(delimiter).append(quote)
						.append(align.getSacc()).append(quote).append(delimiter).append(align.getSstart())
						.append(delimiter).append(align.getSend()).append(delimiter).append(align.getSstrand())
						.append(delimiter).append(align.getAlignmentLength()).append(delimiter)
						.append(align.getMatches()).append(delimiter).append(align.getMismatches()).append(delimiter)
						.append(align.getPercentMatches()).append(delimiter).append(align.getEvalue()).append(delimiter)
						.append(align.getRawScore());

				if (this.checkboxIncludeSequence.isChecked()) {
					buff.append(delimiter).append(align.getQSequence()).append(delimiter).append(align.getSSequence());
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
				String filename = "blast-" + AppContext.createTempFilename() + fileext;
				AppContext.debug("downloading... " + filename);
				Filedownload.save(buff.toString(), filetype, filename);
				// AppContext.debug("File download complete! Saved to: "+filename);
				org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
				AppContext.debug("alignments download complete!" + filename + " "
						+ listboxAlignment.getListModel().getSize() + " LocalAlignments,  Downloaded to:"
						+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());
			} catch (Exception ex) {
				ex.printStackTrace();
				Messagebox.show(ex.getMessage());
			}
		} else if (listboxLocus.isVisible()) {
			/*
			 * <listbox id="listboxLocus" fixedLayout="true" mold="paging" pageSize="100"
			 * visible="false" > <listhead> <listheader label="LOCUS" width="180px"
			 * sortAscending="${lasc0}" sortDescending="${ldsc0}" /> <listheader
			 * label="CONTIG" width="120px" sortAscending="${lasc1}"
			 * sortDescending="${ldsc1}" /> <listheader label="START" width="100px"
			 * sortAscending="${lasc2}" sortDescending="${ldsc2}" /> <listheader
			 * label="STOP" width="100px" sortAscending="${lasc3}" sortDescending="${ldsc3}"
			 * /> <listheader label="STRAND" width="60px" sortAscending="${lasc4}"
			 * sortDescending="${ldsc4}" /> <listheader label="OVERLAPPING GENE MODELS"
			 * width="300px" sort="auto" /> <listheader label="DESCRIPTION"
			 * sortAscending="${lasc6}" sortDescending="${ldsc6}" /> </listhead> </listbox>
			 */

			int isize = listboxLocus.getListModel().getSize();
			StringBuffer buff = new StringBuffer();
			if (isize > 0) {
				

				if (listboxLocus.getListModel().getElementAt(0) instanceof MergedLoci) {
					if (listheaderOverlapping.isVisible())
						buff.append("LOCUS").append(delimiter).append("CONTIG").append(delimiter).append("START")
								.append(delimiter).append("STOP").append(delimiter).append("STRAND").append(delimiter)
								.append("OVERLAPPING_GENE MODELS").append(delimiter).append("DESCRIPTION");
					else
						buff.append("LOCUS").append(delimiter).append("CONTIG").append(delimiter).append("START")
								.append(delimiter).append("STOP").append(delimiter).append("STRAND").append(delimiter)
								.append("DESCRIPTION");

					buff.append("\n");
					for (int i = 0; i < isize; i++) {
						MergedLoci align = (MergedLoci) listboxLocus.getListModel().getElementAt(i);

						if (listheaderOverlapping.isVisible())
							buff.append(quote).append(align.getUniquename()).append(quote).append(delimiter)
									.append(align.getContig()).append(delimiter).append(align.getFmin())
									.append(delimiter).append(align.getFmax()).append(delimiter)
									.append(align.getStrand()).append(delimiter).append(quote)
									.append(align.getOverlappingGenes()).append(quote).append(delimiter).append(quote)
									.append(align.getDescription()).append(quote);
						else
							buff.append(quote).append(align.getUniquename()).append(quote).append(delimiter)
									.append(align.getContig()).append(delimiter).append(align.getFmin())
									.append(delimiter).append(align.getFmax()).append(delimiter)
									.append(align.getStrand()).append(delimiter).append(quote)
									.append(align.getDescription()).append(quote);
						buff.append("\n");
					}
				}

				if (listboxLocus.getListModel().getElementAt(0) instanceof VLocusNotes) {
					buff.append("LOCUS").append(delimiter).append("CONTIG").append(delimiter).append("START")
							.append(delimiter).append("STOP").append(delimiter).append("STRAND").append(delimiter)
							.append("DESCRIPTION");

					buff.append("\n");
					for (int i = 0; i < isize; i++) {
						VLocusNotes align = (VLocusNotes) listboxLocus.getListModel().getElementAt(i);

						buff.append(quote).append(align.getUniquename()).append(quote).append(delimiter)
								.append(align.getContig()).append(delimiter).append(align.getFmin()).append(delimiter)
								.append(align.getFmax()).append(delimiter).append(align.getStrand()).append(delimiter)
								.append(quote).append(align.getDescription()).append(quote);
						buff.append("\n");
					}
				}

			}
			String filetype = "text/plain";
			String fileext = ".txt";
			if (delimiter.equals(",")) {
				filetype = "text/csv";
				fileext = ".csv";
			}

			try {
				String filename = "locus-" + AppContext.createTempFilename() + fileext;
				AppContext.debug("downloading... " + filename);
				Filedownload.save(buff.toString(), filetype, filename);
				// AppContext.debug("File download complete! Saved to: "+filename);
				org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
				AppContext.debug("loci download complete!" + filename + " " + listboxLocus.getListModel().getSize()
						+ " Loci,  Downloaded to:" + zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());
			} catch (Exception ex) {
				ex.printStackTrace();
				Messagebox.show(ex.getMessage());
			}
		}
	}

	@Listen("onClick =#buttonUploadSequence")
	public void onclickUploadsequence() {

		Fileupload.get(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {

				uploadString = null;
				labelUploadSequence.setValue("");
				try {
					org.zkoss.util.media.Media media = ((UploadEvent) event).getMedia();
					uploadString = media.getStringData();
					if (uploadString == null || uploadString.trim().isEmpty()) {
						Messagebox.show("EMPTY FILE");
						uploadString = null;
					} else {
						labelUploadSequence.setValue(media.getName() + " (" + uploadString.length() + " bytes)");
						AppContext.debug("upload fasta successful..");
					}

					/*
					 * if(media.isBinary()) { Messagebox.show("INVALID MEDIA FORMAT"); //return; }
					 * else { uploadString=media.getStringData(); if(uploadString==null ||
					 * uploadString.trim().isEmpty()) { Messagebox.show("EMPTY FILE");
					 * uploadString=null; } else { labelUploadSequence.setValue(media.getName() +
					 * " (" + uploadString.length() + " bytes)" ); } }
					 */

					/*
					 * } catch(InvocationTargetException ex) {
					 * AppContext.debug(ex.getCause().getMessage()); ex.getCause().getStackTrace();
					 */
				} catch (Exception ex) {
					AppContext.debug(ex.getMessage());
					ex.getStackTrace();
				}
			}
		});

	}

	@Listen("onClick =#buttonAddToList")
	public void onclickAddtolist() {
		// add to locus list

		try {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			List addLocuslist = null;

			if (this.listboxAlignment.isVisible()) {
				genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
				addLocuslist = genomics.getLociFromAlignment(locusalignmentresult);
			} else if (this.listboxLocus.isVisible()) {
				addLocuslist = locusresult;
			} else if (this.listboxMarkerVar.isVisible()) {
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
			if (txtboxListname.getValue().isEmpty()) {
				Messagebox.show("PROVIDE LIST NAME");
				return;
			}

			// Set setVarieties = new LinkedHashSet();

			// varietyresult.getModel()

			// Iterator<Listitem> itItems= varietyresult.getItems().iterator();

			/*
			 * Iterator<Variety> itItems= varsresult.iterator(); while(itItems.hasNext()) {
			 * setVarieties.add( (Variety)itItems.next().getValue()) ; }
			 */

			workspace.addLocusList(txtboxListname.getValue().trim(), new LinkedHashSet(addLocuslist));
			txtboxListname.setValue("");

			List listNew = new ArrayList();
			listNew.add("");
			listNew.addAll(workspace.getLocuslistNames());
			listNew.add("create new list...");
			this.listboxMyLocusListOntology.setModel(new SimpleListModel(listNew));
			this.listboxMyLocusListGeneset.setModel(new SimpleListModel(listNew));

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Addtolist Exception", Messagebox.OK, Messagebox.ERROR);
		}

	}

	@Listen("onClick =#searchGenefunctionButton")
	public void onclickSearchGenefunction() {
		initResults();
		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

			locusresult = new ArrayList();
			if (textboxGenefunction.getValue() != null && !textboxGenefunction.getValue().isEmpty()) {
				// locusresult = genomics.getLociByDescription( textboxGenefunction.getValue()
				// );
				TextSearchOptions searchoptions = new TextSearchOptions(textboxGenefunction.getValue());
				// boolean isRegex, boolean isWholeWord, boolean isExact)
				String matchoption = this.listboxGenefunctionMatch.getSelectedItem().getLabel();
				if (matchoption.equals("whole word only")) {
					searchoptions = new TextSearchOptions(textboxGenefunction.getValue(), false, true, false);
				} else if (matchoption.equals("substring")) {
					searchoptions = new TextSearchOptions(textboxGenefunction.getValue(), false, false, false);
				} else if (matchoption.equals("exact match")) {
					searchoptions = new TextSearchOptions(textboxGenefunction.getValue(), false, false, true);
				} else if (matchoption.equals("regular expression")) {
					searchoptions = new TextSearchOptions(textboxGenefunction.getValue(), true, true, false);
				}
				locusresult = genomics.getLociByDescription(searchoptions, listboxOrganism.getSelectedItem().getLabel(),
						(String) listboxAnnotation.getSelectedItem().getValue());
			}
			// listboxLocus.setRowRenderer(new LocusGridRenderer());
			boolean showoverlap = ((String) listboxAnnotation.getSelectedItem().getValue())
					.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)
					|| ((String) listboxAnnotation.getSelectedItem().getValue())
							.equals(GenomicsFacade.GENEMODEL_RAP_ONLY);
			showoverlap = !showoverlap;
			listheaderOverlapping.setVisible(showoverlap);
			listboxLocus.setItemRenderer(new LocusListItemRenderer());
			listboxLocus.setModel(new SimpleListModel(locusresult));

			if (locusresult.size() > 0) {
				hboxAddtolist.setVisible(true);
				hboxDownload.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
				hboxDownload.setVisible(false);
			}

			if (locusresult.size() == 1) {
				show_locus(locusresult.get(0));
			}

			this.msgbox.setValue("Search by function '" + textboxGenefunction.getValue() + "' ... RESULT:"
					+ locusresult.size() + " loci");

			listboxLocus.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onlclickSearchFunction Exception", Messagebox.OK, Messagebox.ERROR);
		}

	}

	@Listen("onClick =#searchGenenameButton")
	public void onclickSearchGenename() {
		initResults();
		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

			locusresult = new ArrayList();
			if (textboxGenename.getValue() != null && !textboxGenename.getValue().isEmpty()) {
				// locusresult = genomics.getLociByDescription( textboxGenefunction.getValue()
				// );
				TextSearchOptions searchoptions = new TextSearchOptions(textboxGenename.getValue());
				// boolean isRegex, boolean isWholeWord, boolean isExact)
				String matchoption = this.listboxGenenameMatch.getSelectedItem().getLabel();
				if (matchoption.equals("whole word only")) {
					searchoptions = new TextSearchOptions(textboxGenename.getValue(), false, true, false);
				} else if (matchoption.equals("substring")) {
					searchoptions = new TextSearchOptions(textboxGenename.getValue(), false, false, false);
				} else if (matchoption.equals("exact match")) {
					searchoptions = new TextSearchOptions(textboxGenename.getValue(), false, false, true);
				} else if (matchoption.equals("regular expression")) {
					searchoptions = new TextSearchOptions(textboxGenename.getValue(), true, true, false);
				}
				locusresult = genomics.getLociBySynonym(searchoptions, listboxOrganism.getSelectedItem().getLabel(),
						(String) listboxAnnotation.getSelectedItem().getValue());
			}
			// listboxLocus.setRowRenderer(new LocusGridRenderer());

			boolean showoverlap = ((String) listboxAnnotation.getSelectedItem().getValue())
					.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)
					|| ((String) listboxAnnotation.getSelectedItem().getValue())
							.equals(GenomicsFacade.GENEMODEL_RAP_ONLY);
			showoverlap = !showoverlap;
			listheaderOverlapping.setVisible(showoverlap);
			listboxLocus.setItemRenderer(new LocusListItemRenderer());

			listboxLocus.setModel(new SimpleListModel(locusresult));

			if (locusresult.size() > 0) {
				hboxAddtolist.setVisible(true);
				hboxDownload.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
				hboxDownload.setVisible(false);
			}

			this.msgbox.setValue(
					"Search by name '" + textboxGenename.getValue() + "' ... RESULT:" + locusresult.size() + " loci");

			if (locusresult.size() == 1) {
				show_locus(locusresult.get(0));
			}
			listboxLocus.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onlclickSearchName Exception", Messagebox.OK, Messagebox.ERROR);
		}

	}

	@Listen("onClick =#searchGOButton")
	public void onclickSearchGO() {
		initResults();
		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

			locusresult = new ArrayList();

			if (this.listboxGOTerm.getSelectedIndex() > 0) {
				locusresult = genomics.getLociByCvTerm(listboxGOTerm.getSelectedItem().getLabel(),
						this.listboxOrganism.getSelectedItem().getLabel(),
						(String) listboxCV.getSelectedItem().getValue(),
						(String) listboxAnnotation.getSelectedItem().getValue());
			}

			if (locusresult.size() > 0) {
				hboxAddtolist.setVisible(true);
				hboxDownload.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
				hboxDownload.setVisible(false);
			}

			// listboxLocus.setRowRenderer(new LocusGridRenderer());
			listboxLocus.setItemRenderer(new LocusListItemRenderer());
			listboxLocus.setModel(new SimpleListModel(locusresult));

			this.msgbox.setValue("Search by GO term '" + listboxGOTerm.getSelectedItem().getLabel() + "' ... RESULT:"
					+ locusresult.size() + " loci");

			listboxLocus.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onclickSearchGO Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick =#searchPatoButton")
	public void onclickSearchPato() {
		initResults();
		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");

			locusresult = new ArrayList();

			if (this.listboxPatoTerm.getSelectedIndex() > 0) {
				locusresult = genomics.getLociByCvTerm(listboxPatoTerm.getSelectedItem().getLabel(),
						this.listboxOrganism.getSelectedItem().getLabel(),
						(String) this.listboxCVPato.getSelectedItem().getValue(),
						(String) listboxAnnotation.getSelectedItem().getValue());
			}

			if (locusresult.size() > 0) {
				hboxAddtolist.setVisible(true);
				hboxDownload.setVisible(true);
			} else {
				hboxAddtolist.setVisible(false);
				hboxDownload.setVisible(false);
			}

			String prefixDesc = "";
			if (listboxCVPato.getSelectedItem().getLabel().startsWith("Q-TARO"))
				prefixDesc = null;
			/*
			 * if(listboxCVPato.getSelectedItem().getLabel().startsWith("TO"))
			 * prefixDesc="TO:"; else
			 * if(listboxCVPato.getSelectedItem().getLabel().startsWith("PO"))
			 * prefixDesc="PO:"; else
			 * if(listboxCVPato.getSelectedItem().getLabel().startsWith("CO"))
			 * prefixDesc="CO:"; else
			 * if(listboxCVPato.getSelectedItem().getLabel().startsWith("PATO"))
			 * prefixDesc="PATO:"; else
			 * if(listboxCVPato.getSelectedItem().getLabel().startsWith("Q-TARO"))
			 * prefixDesc="";
			 */

			// listboxLocus.setRowRenderer(new LocusGridRenderer(prefixDesc));
			listboxLocus.setItemRenderer(new LocusListItemRenderer(prefixDesc));
			listboxLocus.setModel(new SimpleListModel(locusresult));

			this.msgbox.setValue("Search by PATO term '" + listboxPatoTerm.getSelectedItem().getLabel()
					+ "' ... RESULT:" + locusresult.size() + " loci");

			listboxLocus.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onclickSearchPato Exception", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick =#buttonCountGOTermLoci")
	public void onclickCountGOTerm() {
		initResults();

		try {
			genomics = (GenomicsFacade) AppContext.checkBean(genomics, "GenomicsFacade");
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			String locuslistname = this.listboxMyLocusListOntology.getSelectedItem().getLabel();
			if (locuslistname.isEmpty() || locuslistname.startsWith("create new list")) {
				Messagebox.show("Select a Locus List", "Incomplete Info", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			// List listGOTermCount = genomics.getGOTermLociCounts(
			// this.listboxOrganism.getSelectedItem().getLabel(), workspace.getLoci(
			// locuslistname ), (String)this.listboxCV.getSelectedItem().getValue() );
			List listGOTermCount = genomics.getGOTermLociCounts(this.listboxOrganism.getSelectedItem().getLabel(),
					workspace.getLoci(locuslistname), null);
			this.gridGOTerms.setModel(new SimpleListModel(listGOTermCount));
			gridGOTerms.setRowRenderer(new CvTermLocusCountGridRenderer());
			gridGOTerms.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "onclickCountGOTerm Exception", Messagebox.OK, Messagebox.ERROR);
		}

	}

	// @Listen("onClick =#searchEnrichment")
	// public void enrichList() {
	// initResults();
	// try {
	// if(listboxMyLocusList2.getSelectedIndex()==0) {
	// Messagebox.show("Select a Locus list", "Incomplete info", Messagebox.OK ,
	// Messagebox.EXCLAMATION );
	// return;
	// }
	//
	// workspace = (WorkspaceFacade)AppContext.checkBean(workspace,
	// "WorkspaceFacade");
	// Set setLoci = workspace.getLoci(
	// listboxMyLocusList.getSelectedItem().getLabel() );
	//
	// genomics = (GenomicsFacade)AppContext.checkBean(genomics, "GenomicsFacade");
	//
	// String enrichmentType="";
	// if(this.radioFunction.isSelected() )
	// enrichmentType=GeneOntologyService.PANTHER_ENRICHMENTTYPE_FUNCTION;
	// else if(this.radioProcess.isSelected() )
	// enrichmentType=GeneOntologyService.PANTHER_ENRICHMENTTYPE_PROCESS;
	// else if(this.radioLocation.isSelected() )
	// enrichmentType=GeneOntologyService.PANTHER_ENRICHMENTTYPE_CELLULARLOCATION;
	// else if(this.radioProtein.isSelected() )
	// enrichmentType=GeneOntologyService.PANTHER_ENRICHMENTTYPE_PROTEINCLASS;
	// else if(this.radioPathway.isSelected() )
	// enrichmentType=GeneOntologyService.PANTHER_ENRICHMENTTYPE_PATHWAY;
	//
	// String result =
	// genomics.overRepresentationTest(listboxOrganism.getSelectedItem().getLabel(),
	// setLoci, enrichmentType);
	//
	// AppContext.debug(result);
	//
	//
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// Messagebox.show( ex.getMessage(), "enrichList Exception", Messagebox.OK,
	// Messagebox.ERROR );
	// }
	//
	// }
	//
	@Listen("onSelect = #listboxLocus")
	public void selectLocus() {

		Listitem selItem = listboxLocus.getSelectedItem();

		Listcell lc = (Listcell) selItem.getChildren().get(0);
		Label lb = (Label) lc.getChildren().get(0);
		String varname = lb.getValue();
		Locus selLoc = selItem.getValue();
		show_locus(selLoc);
	}

	@Listen("onClick =#radioGroupbyMarker")
	public void groupbyMarker() {
		if (headerLogp != null)
			headerLogp.setVisible(true);
		listboxMarkerVar.setModel(new SimpleListModel(markerresult));
	}

	@Listen("onClick =#radioGroupbyGene")
	public void groupbyGene() {
		// if(headerLogp!=null) headerLogp.setVisible(false);
		int pm = 0;
		Integer opm = intboxPlusMinusBP.getValue();
		if (opm != null)
			pm = opm.intValue();
		listboxMarkerVar.setModel(new SimpleListModel(genomics.getMarkerAnnotsByGene(markerresult, pm)));

	}

	@Listen("onClick =#radioGroupbyQtl")
	public void groupbyQtl() {
		// if(headerLogp!=null) headerLogp.setVisible(false);
		int pm = 0;
		Integer opm = intboxPlusMinusBP.getValue();
		if (opm != null)
			pm = opm.intValue();
		listboxMarkerVar.setModel(new SimpleListModel(genomics.getMarkerAnnotsByQTL(markerresult, pm)));

	}

	@Listen("onSelect = #listboxMarkerVar")
	public void selectMarkerAnnotation() {
		Listitem selItem = listboxMarkerVar.getSelectedItem();
		MarkerAnnotation annot = selItem.getValue();
		// if(annot.getGenes()!=null && !annot.getGenes().isEmpty()) {
		// show_locus( annot.getGenes().iterator().next());
		if (annot == null)
			return;
		if (annot.getAnnotation("GENE MODELS") != null && !annot.getAnnotation("GENE MODELS").isEmpty()) {
			show_locus((Locus) annot.getAnnotation("GENE MODELS").iterator().next());
		} else {
			show_jbrowse(annot.getContig(), Math.max(1, annot.getPosition().intValue() - 100),
					annot.getPosition().intValue() + 100);
		}
	}

	@Listen("onSelect = #listboxAlignment")
	public void selectLocusAlignment() {

		Listitem selItem = listboxAlignment.getSelectedItem();

		Listcell lc = (Listcell) selItem.getChildren().get(0);
		Label lb = (Label) lc.getChildren().get(0);
		String varname = lb.getValue();
		Locus selLoc = null;
		if (selItem.getValue() instanceof Locus) {
			selLoc = selItem.getValue();
		} else if (selItem.getValue() instanceof LocalAlignment) {
			LocalAlignment localall = (LocalAlignment) selItem.getValue();
			String subject = localall.getSacc();
			String dbtype = this.listboxBlastDBType.getSelectedItem().getLabel();
			if (dbtype.equals("cds") || dbtype.equals("cdna") || dbtype.equals("pep")) { // if(subject.toLowerCase().startsWith("loc_")
																							// ||
																							// subject.toLowerCase().startsWith("os"))
																							// {
				try {

					// listitemsDAO = (ListItemsDAO)AppContext.checkBean(listitemsDAO,
					// "ListItemsDAO");
					// selLoc = listitemsDAO.findGeneFromName(subject.split("\\.")[0],
					// this.listboxOrganism.getSelectedItem().getLabel() );
					TextSearchOptions options = new TextSearchOptions(subject.split("\\.")[0], false, false, true);

					// selLoc = genomics.getLocusByName( subject.split("\\.")[0] );
					List<Locus> loci = genomics.getLociBySynonym(options, listboxOrganism.getSelectedItem().getLabel(),
							(String) this.listboxAnnotation.getSelectedItem().getValue());
					if (loci.size() == 0) {
						Messagebox.show("Cannot find locus " + subject);
						return;
					}
					selLoc = loci.get(0);

				} catch (Exception ex) {
					ex.printStackTrace();
					Messagebox.show(ex.getMessage());
					return;
				}
				// if(selLoc==null) {
			} else if (dbtype.equals("dna")) {

				gridLocusInfo.setVisible(false);
				show_jbrowse(localall.getSacc(), localall.getSstart().toString(), localall.getSend().toString());
				return;
			}
		}
		show_locus(selLoc);
		gridLocusInfo.setVisible(true);

	}

	private void show_jbrowse(String contig, int start, int end) {
		show_jbrowse(contig, Integer.toString(start), Integer.toString(end));
	}

	private void show_jbrowse(String contig, String start, String end) {

		iframeJbrowse.setSrc(createJBrowseUrl(contig, start, end));
		splitter.setOpen(true);
		separator.setVisible(true);
		splitter.setVisible(true);

	}

	/**
	 * Show locus details for variety
	 * 
	 * @param locus
	 */
	private void show_locus(Locus locus) {

		this.textboxLocusname.setValue(locus.getUniquename());
		String locusname = locus.getUniquename().split("\\.")[0];
		if (locus instanceof LocalAlignment) {
			LocalAlignment alloc = (LocalAlignment) locus;
			locusname = alloc.getSacc();
		}
		;
		// locusname = ((LocalAlignment)).get

		String strand = "(+)";
		if (locus.getStrand() < 0)
			strand = "(-)";
		this.textboxPosition
				.setValue(locus.getContig() + " " + locus.getFmin() + ".." + locus.getFmax() + " " + strand);
		this.textboxFunction.setValue(locus.getDescription());
		if (locus instanceof MergedLoci) {
			MergedLoci mloci = (MergedLoci) locus;
			List listMerged = new ArrayList();
			if (mloci.getMSU7Name() != null && !mloci.getMSU7Name().isEmpty()) {
				listMerged.add(mloci.getMSU7Name());
				aMSU7.setHref(AppContext.getHrefMSU7(mloci.getMSU7Name()));
				aMSU7.setVisible(true);

				aPhytozome.setHref(AppContext.getHrefPhytozome(mloci.getMSU7Name()));
				aPhytozome.setVisible(true);

				aUniprot.setHref(AppContext.getHrefUniprot(mloci.getMSU7Name()));
				aUniprot.setVisible(true);

			} else {
				aMSU7.setVisible(false);
				aPhytozome.setVisible(false);
				aUniprot.setVisible(false);
			}

			if (mloci.getRAPPredName() != null && !mloci.getRAPPredName().isEmpty())
				listMerged.add(mloci.getRAPPredName());

			if (mloci.getRAPRepName() != null && !mloci.getRAPRepName().isEmpty()) {
				listMerged.add(mloci.getRAPRepName());
				aRAP.setHref(AppContext.getHrefRAP(mloci.getRAPRepName()));
				aRAP.setVisible(true);

				aKegg.setHref(AppContext.getHrefKEGG(mloci.getRAPRepName()));
				aKegg.setVisible(true);

				aGramene.setHref(AppContext.getHrefGramene(mloci.getRAPRepName()));
				aGramene.setVisible(true);

				aOryzabase.setHref(AppContext.getHrefOryzabase(mloci.getRAPRepName()));
				aOryzabase.setVisible(true);

				aUniprot.setHref(AppContext.getHrefUniprot(mloci.getRAPRepName()));
				aUniprot.setVisible(true);

				aNCBI.setHref(AppContext.getHrefNcbi(mloci.getRAPRepName()));
				aNCBI.setVisible(true);

				aIC4RLit.setHref(AppContext.getHrefIC4RLit(mloci.getRAPRepName()));
				aIC4RLit.setVisible(true);

			} else {
				if (mloci.getRAPPredName() != null && !mloci.getRAPPredName().isEmpty()) {
					aRAP.setHref(AppContext.getHrefRAP(mloci.getRAPPredName()));
					aRAP.setVisible(true);
					aKegg.setHref(AppContext.getHrefKEGG(mloci.getRAPPredName()));
					aKegg.setVisible(true);

					aGramene.setHref(AppContext.getHrefGramene(mloci.getRAPPredName()));
					aGramene.setVisible(true);

					aOryzabase.setHref(AppContext.getHrefOryzabase(mloci.getRAPPredName()));
					aOryzabase.setVisible(true);

					aUniprot.setHref(AppContext.getHrefUniprot(mloci.getRAPPredName()));
					aUniprot.setVisible(true);

					aNCBI.setHref(AppContext.getHrefNcbi(mloci.getRAPPredName()));
					aNCBI.setVisible(true);

					aIC4RLit.setHref(AppContext.getHrefIC4RLit(mloci.getRAPPredName()));
					aIC4RLit.setVisible(true);

				} else {
					aRAP.setVisible(false);
					aKegg.setVisible(false);
					aGramene.setVisible(false);
					aOryzabase.setVisible(false);
					aUniprot.setVisible(false);
					aNCBI.setVisible(false);
					aIC4RLit.setVisible(false);

				}
			}

			if (mloci.getFGeneshName() != null && !mloci.getFGeneshName().isEmpty())
				listMerged.add(mloci.getFGeneshName());
			if (mloci.getIRICName() != null && !mloci.getIRICName().isEmpty())
				listMerged.add(mloci.getIRICName());
			this.listboxOverlappinggenes.setModel(new SimpleListModel(listMerged));
		} else {
			if (locus.getUniquename().toLowerCase().startsWith("loc_")) {
				aMSU7.setHref(AppContext.getHrefMSU7(locus.getUniquename()));
				aMSU7.setVisible(true);

				aPhytozome.setHref(AppContext.getHrefPhytozome(locus.getUniquename()));
				aPhytozome.setVisible(true);

				aUniprot.setHref(AppContext.getHrefUniprot(locus.getUniquename()));
				aUniprot.setVisible(true);

			} else {
				aMSU7.setVisible(false);
				aPhytozome.setVisible(false);
				aUniprot.setVisible(false);

			}

			if (locus.getUniquename().toLowerCase().startsWith("os0")
					|| locus.getUniquename().toLowerCase().startsWith("os1")) {
				aRAP.setHref(AppContext.getHrefRAP(locus.getUniquename()));
				aRAP.setVisible(true);

				aKegg.setHref(AppContext.getHrefKEGG(locus.getUniquename()));
				aKegg.setVisible(true);

				aGramene.setHref(AppContext.getHrefGramene(locus.getUniquename()));
				aGramene.setVisible(true);

				aOryzabase.setHref(AppContext.getHrefOryzabase(locus.getUniquename()));
				aOryzabase.setVisible(true);

				aUniprot.setHref(AppContext.getHrefUniprot(locus.getUniquename()));
				aUniprot.setVisible(true);

				aNCBI.setHref(AppContext.getHrefNcbi(locus.getUniquename()));
				aNCBI.setVisible(true);

				aIC4RLit.setHref(AppContext.getHrefIC4RLit(locus.getUniquename()));
				aIC4RLit.setVisible(true);

			} else {
				aRAP.setVisible(false);
				aKegg.setVisible(false);
				aGramene.setVisible(false);
				aOryzabase.setVisible(false);
				aUniprot.setVisible(false);
				aNCBI.setVisible(false);
				aIC4RLit.setVisible(false);
			}
		}

		if (locus instanceof MarkerAnnotation) {

		}

		iframeJbrowse.setSrc(createJBrowseUrl(locus));
		this.gridLocusInfo.setVisible(true);
		splitter.setOpen(true);
		separator.setVisible(true);
		splitter.setVisible(true);

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
	private String createJBrowseUrl(String contig, String start, String end) {
		return createJBrowseUrl(null, contig, start, end);
	}

	private String createJBrowseUrl(Locus locus) {
		return createJBrowseUrl(locus, null, null, null);
	}

	private String createJBrowseUrl(Locus locus, String contig, String start, String end) {
		// return;

		String org = this.listboxOrganism.getSelectedItem().getLabel();
		String chr = contig;
		String chrpad = contig;

		if (locus != null) {
			chr = locus.getContig();
			chrpad = chr;
			start = locus.getFmin().toString();
			end = locus.getFmax().toString();

		}

		if (org.equals(AppContext.getDefaultOrganism()))
			chrpad = "loc=" + chr.split("\\|")[0]; // locus.getContig(); //chr; // +
													// AppContext.getJbrowseContigSuffix(); //"|msu7";
		else if (org.equals("IR64-21"))
			chrpad = "data=ir6421v1&loc=" + chr + "|ir6421v1";
		else if (org.equals("93-11"))
			chrpad = "data=9311v1&loc=" + chr + "|9311v1";
		else if (org.equals("DJ123"))
			chrpad = "data=dj123v1&loc=" + chr + "|dj123v1";
		else if (org.equals("Kasalath"))
			chrpad = "data=kasv1&loc=" + chr + "|kasv1";

		String displaymode = "%22displayMode%22:%22normal%22,%22maxHeight%22:%222000%22";

		iframeJbrowse.setStyle("width:1500px;height:800px;border:0px inset;");

		String rendertype = "";

		iframeJbrowse.setScrolling("yes");
		iframeJbrowse.setAlign("left");

		String urljbrowse = "";

		// String urltemplate = "..%2F..%2F" + AppContext.getHostDirectory() +
		// "%2Ftmp%2F" + "GFF_FILE";
		String urltemplate = "..%2F..%2Ftemp%2F" + "GFF_FILE";

		// for all varieties
		rendertype = "%22JBrowse%2FView%2FTrack%2FAlignments2VarietyMismatchSynIndels%22";

		String showTracks = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());

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

		// urljbrowse= AppContext.getHostname() + "/" + AppContext.getJbrowseDir() +
		// "/?loc=chr" + chrpad + "|msu7:" + start + ".." + end +
		// "&tracks=DNA%2Cmsu7gff%2Csnp3k%2C" + snp3kcore +
		// "SNP%20Genotyping&addStores={%22url%22%3A{%22type%22%3A%22JBrowse%2FStore%2FSeqFeature%2FGFF3Variety%22%2C%22urlTemplate%22%3A%22"
		// + urltemplate +

		// urljbrowse= AppContext.getHostname() + "/" + AppContext.getJbrowseDir() +
		// "/?loc=" + chrpad + ":" + start + ".." + end + "&tracks=DNA%2Cmsu7gff%2C" +
		// snp3kcore +
		// "SNP%20Genotyping&addStores={%22url%22%3A{%22type%22%3A%22JBrowse%2FStore%2FSeqFeature%2FGFF3Variety%22%2C%22urlTemplate%22%3A%22"
		// + urltemplate +
		urljbrowse = AppContext.getJbrowseDir() + "/?" + chrpad + ":" + start + ".." + end + "&tracks=" + showTracks
				+ "SNP%20Genotyping&addStores={%22url%22%3A{%22type%22%3A%22JBrowse%2FStore%2FSeqFeature%2FGFF3Variety%22%2C%22urlTemplate%22%3A%22"
				+ urltemplate + "%22}}&addTracks=[{%22label%22%3A%22SNP%20Genotyping%22%2C%22type%22%3A" + rendertype
				+ "%2C%22store%22%3A%22url%22%2C%20" + displaymode
				+ ",%22metadata%22:{%22Description%22%3A%20%22Varieties%20SNP%20Genotyping%20in%20the%20region.%20Each%20row%20is%20a%20variety.%20Red%20means%20there%20is%20variation%20with%20the%20reference%22}"
				+ ",%22fmtDetailValue_Name%22%3A%20%22function(name)%20%7B%20return%20%27%3Ca%20target%3D%5C%22variety%5C%22%20href%3D%5C%22/"
				+ AppContext.getHostDirectory()
				+ "/_variety.zul%3Fname%3D%27%2Bname%2B%27%5C%22%3E%27%2Bname%2B%27%3C%2Fa%3E%27%3B%20%7D%22%20"
				+ "%2C%20%22style%22%3A{%22showLabels%22%3A%22false%22%2C%22textFont%22%3A%22normal%208px%20Univers%2CHelvetica%2CArial%2Csans-serif%22}}]&highlight=";
		// "fmtDetailValue_Name": "function(name) { return '<a target=\"variety\"
		// href=\"_variety.zul?name='+name+'\">'+name+'</a>'; }"

		return urljbrowse = urljbrowse.replace("{", "%7B").replace("}", "%7D");
	}

}
