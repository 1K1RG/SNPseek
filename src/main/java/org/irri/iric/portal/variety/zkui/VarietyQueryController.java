package org.irri.iric.portal.variety.zkui;

import java.io.BufferedWriter;
import java.io.File;
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

import javax.servlet.http.HttpSession;

import org.irri.iric.ds.chado.domain.CvTermUniqueValues;
import org.irri.iric.ds.chado.domain.Passport;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.VarietyPlus;
import org.irri.iric.ds.chado.domain.VarietyPlusPlus;
import org.irri.iric.ds.chado.domain.impl.VarietyImpl;
import org.irri.iric.ds.chado.domain.impl.VarietyPlusPlusImpl;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.ds.chado.domain.model.VGenotypeRun;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.SimpleListModelExt;
import org.irri.iric.portal.WebConstants;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.admin.WorkspaceLoadLocal;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genotype.zkui.PopupBand;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.variety.service.Data;
import org.irri.iric.portal.zk.ListboxMessageBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.Marker;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.model.DefaultXYModel;
import org.zkoss.chart.plotOptions.ScatterPlotOptions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import user.ui.module.util.constants.SessionConstants;

/**
 * Controls Variety Query page
 * 
 * @author LMansueto
 *
 */
@Controller
@Scope("session")
public class VarietyQueryController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;

	// hold navigation state
	private java.util.List<Variety> varsresult;
	private boolean isdonePhylo = false;
	private boolean isdoneMDS = false;
	private boolean istreebrowser = true;

	private List<ListboxPhenotype> listPhenotypeListbox = new ArrayList();
	private int phenotypevalue_type = ListItemsDAO.PHENOTYPETYPE_NONE;

	// access APIs
	@Autowired
	@Qualifier("VarietyFacade")
	private VarietyFacade variety;

	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;

	// hold form components

	@Wire
	private Window winVariety;
	@Wire
	private Combobox comboVarname;
	@Wire
	private Combobox comboIrisId;
	@Wire
	private Combobox comboCountry;
	@Wire
	private Listbox listboxSubpopulation;
	@Wire
	private Textbox msgbox;
	@Wire
	private Button buttonReset;
	@Wire
	private Button buttonSearch;
	@Wire
	private Button showallButton;
	@Wire
	private Listbox listboxVarietyresult;
	@Wire
	private Tabbox tabboxDisplay;
	@Wire
	private Tab tabPhylo;
	@Wire
	private Tab tabMDS;
	@Wire
	private Hbox refBox;

	@Wire
	private Charts chartMDS;
	@Wire
	private Charts chartMDSNeighbors;
	@Wire
	private Tab tabTable;
	@Wire
	private Vbox hboxQuery;
	@Wire
	private Checkbox checkboxShowMDSLabel;
	@Wire
	private Grid gridGermplasm;
	@Wire
	private Textbox textboxGermDesignation;
	@Wire
	private Textbox textboxGermAccession;
	@Wire
	private Textbox textboxGermSubpopulation;
	@Wire
	private Textbox textboxGermCountry;
	@Wire
	private Listbox listboxGermPhenotypes;
	@Wire
	private Textbox textboxIRISId;
	@Wire
	private Label labelIRISId;
	@Wire
	private Splitter splitter;
	@Wire
	private Listbox listboxPassport;
	@Wire
	private Listbox listboxPassportValue;
	@Wire
	private Listbox listboxPhenotypes;
	@Wire
	private Listbox listboxPhenComparator;
	@Wire
	private Listbox listboxPhenValue;
	@Wire
	private Vbox vboxPhenConstraints;
	@Wire
	private Button buttonAddPhenConstraint;
	@Wire
	private Vbox vboxPassportConstraints;
	@Wire
	private Listbox listboxGermPassport;
	@Wire
	private Tab tabPassportGenesys;
	@Wire
	private Tab tabPassportIRGCIS;
	@Wire
	private Listbox listboxGermPassportGenesys;
	@Wire
	private Iframe iframePhylotree;
	@Wire
	private Button buttonCheckAll;
	@Wire
	private Button buttonUncheckAll;
	@Wire
	private Button buttonAddToList;
	@Wire
	private Textbox txtboxListname;
	@Wire
	private Hbox hboxAddtolist;
	@Wire
	private Listbox listboxMyVariety;
	@Wire
	private Button buttonDownloadCSV;
	@Wire
	private Button buttonDownloadTab;
	@Wire
	private Button buttonDownloadFastqc;
	@Wire
	private Button buttonDownloadFastq;
	@Wire
	private Button buttonDownloadBAM;
	@Wire
	private Button buttonDownloadVCF;

	@Wire
	private Listbox listboxPtocoterm;

	@Wire
	private Div backQueryDiv;

	@Wire
	private A aCO;
	@Wire
	private A aTO;

	// HDRA related

	@Wire
	private Listbox listboxDataset;
	@Wire
	private Combobox comboAccession;

	@Wire
	private Grid gridQuery;

	@Wire
	private Label labelExampleSubpop;
	@Wire
	private Label labelExampleVarname;
	@Wire
	private Label labelExampleAccession;

	@Wire
	private Label labelExampleIrisId;

	@Wire
	private Listbox listboxMDSPhenotype;
	@Wire
	private Label labelMDSPhenotype;
	@Wire
	private Listbox listboxHighlightVariety;
	@Wire
	private Listbox listboxHighlightVarietyList;
	@Wire
	private Button buttonHighlight;
	@Wire
	private Checkbox checkboxShowMDSNeighbors;
	@Wire
	private Div divMDSNeighbors;
	@Wire
	private Hbox hboxRawfiles;
	@Wire
	private Hbox hboxSubpopColor1;
	@Wire
	private Hbox hboxSubpopColor2;

	@Wire
	private Bandbox bandboxVarietyset;

	@Wire
	private Radio radioLegacyTrait;

	@Wire
	private Radio radioCoTrait;

	private User user;

	public VarietyQueryController() {
		super();
		// TODO Auto-generated constructor stub
		AppContext.debug("VarietyQueryController created");
	}

	@Listen("onClick=#backQueryDiv")
	public void onClick$backQueryDiv() {
		Clients.evalJavaScript("myFunction();");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		// String url = (String)desktop.getExecution().getArg().get("href");
		try {
			AppContext.debug("..initstart");

			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			Session sess = Sessions.getCurrent();
			user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

			Set sDataset = new HashSet();
			sDataset.add(VarietyFacade.DATASET_SNPINDELV2_IUPAC);
			java.util.List varnames = variety.getVarietyNames(sDataset);
			AppContext.debug("varnames=" + varnames.size());
			java.util.List varaccessions = variety.getVarietyAccessions(sDataset);
			AppContext.debug("varaccessions=" + varaccessions.size());
			java.util.List irisIds = variety.getIrisIds(sDataset);
			AppContext.debug("irisIds=" + irisIds.size());
			java.util.List countries = variety.getCountries(sDataset);
			AppContext.debug("countries=" + countries.size());
			java.util.List subpopulation = (List) variety.getSubpopulations(sDataset);
			AppContext.debug("subpopulation=" + subpopulation.size());

			AppContext.debug("..initmark 1");

			/*
			 * Set setPassport = new HashSet(); Set setPhenotype = new HashSet(); Set
			 * setPtoco= new HashSet();
			 */

			Set setPassport = variety.getPassportDefinitions(sDataset).keySet();
			AppContext.debug("setPassport=" + setPassport.size());
			Set setPhenotype = variety.getPhenotypeDefinitions(sDataset).keySet();
			AppContext.debug("setPhenotype=" + setPhenotype.size());
			Set setPtoco = variety.getPtocoDefinitions(sDataset).keySet();
			AppContext.debug("setPtoco=" + setPtoco.size());

			AppContext.debug("classes: " + (setPassport.size() > 0 ? setPassport.iterator().next().getClass() : "0")
					+ "  " + (setPhenotype.size() > 0 ? setPhenotype.iterator().next().getClass() : "0"));

			AppContext.debug("..initmark 2");

			String msg = (varnames == null ? "null" : varnames.size()) + " variety names, "
					+ (varaccessions == null ? "null" : varaccessions.size()) + " accessions, "
					+ (irisIds == null ? "null" : irisIds.size()) + " IRIS IDs, "
					+ (countries == null ? "null" : countries.size()) + " countries, "
					+ (subpopulation == null ? "null" : subpopulation.size()) + " subpopulations, "
					+ (setPassport == null ? "null" : setPassport.size()) + " passport fields, "
					+ (setPhenotype == null ? "null" : setPhenotype.size()) + " phenotypes, "
					+ (setPtoco == null ? "null" : setPtoco.size()) + " rice trait terms, for selection";
			msgbox.setValue(msg);
			AppContext.debug(msg);

			List<VGenotypeRun> result = variety.getGenotyperuns();

			Map<String, String> vrunMap = new TreeMap();

			for (VGenotypeRun vRUn : result) {
				vrunMap.put(vRUn.getDataset(), vRUn.getDsDescription());
			}

			List listDatasets = new ArrayList(vrunMap.keySet());
			// List listDatasets = variety.getDatasets();
			AppContext.debug("listDatasets=" + listDatasets.size());

			AppContext.debug("..initmark 3");

			AppContext.debug("classes: " + (setPassport.size() > 0 ? setPassport.iterator().next().getClass() : "0")
					+ "  " + (setPhenotype.size() > 0 ? setPhenotype.iterator().next().getClass() : "0"));

			List listPassport = new ArrayList();
			listPassport.add("");
			listPassport.addAll(setPassport);
			List listPhenotype = new ArrayList();
			listPhenotype.add("");
			listPhenotype.addAll(setPhenotype);

			List listPtocoterm = new ArrayList();
			listPtocoterm.add("");
			listPtocoterm.addAll(setPtoco);

			java.util.List listAlleleFilter = new ArrayList();
			listAlleleFilter.add("");
			listAlleleFilter.addAll(workspace.getSnpPositionAlleleListNames());

			subpopulation = AppContext.createUniqueUpperLowerStrings(subpopulation, false, true);
			countries = AppContext.createUniqueUpperLowerStrings(countries, true, false);
			// varnames = AppContext.createUniqueUpperLowerStrings( varnames, true, false);
			// varaccessions = AppContext.createUniqueUpperLowerStrings( varaccessions,
			// true, false);
			// irisIds = AppContext.createUniqueUpperLowerStrings( irisIds, true, false);

			java.util.List listMyVarieties = new ArrayList();
			listMyVarieties.add("");
			listMyVarieties.addAll(workspace.getVarietylistNames());

			AppContext.debug("..initdone");

			SimpleListModel m = new SimpleListModel(listDatasets);
			m.setMultiple(true);
			listboxDataset.setModel(m);
			listboxDataset.setSelectedIndex(0);

			int i = 0;
			for (Listitem item : listboxDataset.getItems()) {
				// POPUP temporary
				Popup pop = new Popup();

				Vlayout layout = PopupBand.setPopup(vrunMap.get(listboxDataset.getModel().getElementAt(i).toString()));

				layout.setParent(pop);

				pop.setParent(refBox);

				item.addEventListener(Events.ON_MOUSE_OVER, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						pop.open(item, "end_after");
					}

				});

				i++;
			}

			comboVarname.setModel(new SimpleListModelExt(varnames));
			comboAccession.setModel(new SimpleListModelExt(varaccessions));

			listboxSubpopulation.setModel(new SimpleListModel(subpopulation));
			if (subpopulation.size() > 0)
				listboxSubpopulation.setSelectedIndex(0);

			comboIrisId.setModel(new SimpleListModelExt(irisIds));
			comboCountry.setModel(new SimpleListModel(countries));
			// listboxAlleleFilter.setModel( new SimpleListModel(listAlleleFilter));
			listboxMyVariety.setModel(new SimpleListModel(listMyVarieties));

			// lb1.value="(If Variety name or IRIS Id is specified,\nthe other filters are
			// ignored)";

			listboxPassport.setModel(new SimpleListModel(listPassport));
			listboxPhenotypes.setModel(new SimpleListModel(listPhenotype));
			listboxPtocoterm.setModel(new SimpleListModel(listPtocoterm));

			List listQuanOperator = new ArrayList();
			listQuanOperator.add(VarietyFacade.COMPARATOR_EQUALS);
			listQuanOperator.add(VarietyFacade.COMPARATOR_LESSTHAN);
			listQuanOperator.add(VarietyFacade.COMPARATOR_GREATERTHAN);
			listboxPhenComparator.setModel(new SimpleListModel(listQuanOperator));
			listboxPhenComparator.setSelectedIndex(0);

			listboxGermPassport.setItemRenderer(new org.irri.iric.portal.variety.zkui.PassportListItemRenderer());
			listboxGermPassport.setModel(new SimpleListModel(new java.util.ArrayList()));
			listboxGermPassportGenesys
					.setItemRenderer(new org.irri.iric.portal.variety.zkui.PassportListItemRenderer());
			// listboxGermPassportGenesys.setModel(new SimpleListModel(new
			// java.util.ArrayList()));

			listboxGermPhenotypes.setItemRenderer(new org.irri.iric.portal.variety.zkui.PhenotypesListRenderer());
			listboxGermPhenotypes.setModel(new SimpleListModel(new java.util.ArrayList()));

			Set s = new HashSet();
			s.add(VarietyFacade.DATASET_SNPINDELV2_IUPAC);
			listboxHighlightVariety.setModel(new SimpleListModel(
					AppContext.createUniqueUpperLowerStrings(variety.getVarietyNames(s), false, true)));
			if (listboxHighlightVariety.getRows() > 0)
				listboxHighlightVariety.setSelectedIndex(0);
			listboxHighlightVarietyList.setModel(new SimpleListModel(listMyVarieties));
			if (listboxHighlightVarietyList.getRows() > 0)
				listboxHighlightVarietyList.setSelectedIndex(0);

			listboxMDSPhenotype.setModel(new SimpleListModel(listPhenotype));
			if (listboxMDSPhenotype.getRows() > 0)
				listboxMDSPhenotype.setSelectedIndex(0);

			listboxVarietyresult.getPagingChild().setMold("os"); // .pagingChild.mold = "os";
			listboxVarietyresult.setPagingPosition("both"); // .pagingPosition = "both";

			// ListModel snpListModel= new SimpleListModel(listSNPs);
			VarietyListItemRenderer rend = new VarietyListItemRenderer();
			rend.setBasic(false);
			listboxVarietyresult.setItemRenderer(rend);
			listboxVarietyresult.setModel(new SimpleListModel(new java.util.ArrayList()));

			AppContext.debug("AppContext.isIRRILAN()=" + AppContext.isIRRILAN() + "  AppContext.isAWS()="
					+ AppContext.isAWS() + "  AppContext.isLocalhost()=" + AppContext.isLocalhost()
					+ " AppContext.isPollux()=" + AppContext.isPollux());

			if (AppContext.isIRRILAN()) {
				hboxRawfiles.setVisible(true);
			}

			AppContext.debug(Executions.getCurrent().getParameterMap().toString());

			String varid = Executions.getCurrent().getParameter("irisid");
			if (varid == null || varid.isEmpty()) {
				String varname = Executions.getCurrent().getParameter("name");
				if (varname == null || varname.isEmpty()) {
				} else {
					varname = varname.toUpperCase().replace("_", " ");
					if (varname.startsWith("313"))
						varname = "IRIS " + varname;
					comboVarname.setValue(varname);
					Events.postEvent("onClick", buttonSearch, null);
				}
			} else {
				if (varid.equals("alltree") || varid.equals("alltree2")) {

					iframePhylotree.setStyle("height:1500px;width:1500px");
					iframePhylotree.setScrolling("yes");
					String url = "treeBrowser3023.html"; // "jsp/phylotreeGermplasms.jsp?scale=1&amp;varid=all" ; //
															// "treeBrowser3k.htm"; // //"treeBrowser1k.htm";
					if (varid.equals("alltree2")) {
						hboxSubpopColor1.setVisible(false);
						hboxSubpopColor2.setVisible(true);
						url = "treeBrowser3023color2.html";
					}

					AppContext.debug(url);
					iframePhylotree.setSrc(url);
					iframePhylotree.setVisible(true);

					tabPhylo.setSelected(true);
					tabTable.setVisible(false);
					tabMDS.setVisible(false);

					// listboxVarietyresult.setVisible(false);
					hboxQuery.setVisible(false);

				} else if (varid.equals("allmds")) {
					// showmds_allvars();

					Events.postEvent("onShowAllMDS", winVariety, null);
					tabPhylo.setSelected(false);
					tabTable.setVisible(false);
					hboxQuery.setVisible(false);
					tabMDS.setVisible(true);
					tabMDS.setSelected(true);
				} else if (varid.equals("allmdshdra")) {
					// showmds_allvars();

					Events.postEvent("onShowAllMDSHDRA", winVariety, null);
					tabPhylo.setSelected(false);
					tabTable.setVisible(false);
					hboxQuery.setVisible(false);
					tabMDS.setVisible(true);
					tabMDS.setSelected(true);
				}

				else {
					varid = varid.toUpperCase().replace("_", " ");
					if (varid.startsWith("313"))
						varid = "IRIS " + varid;
					comboIrisId.setValue(varid);
					Events.postEvent("onClick", buttonSearch, null);
				}
			}

			aTO.setHref(AppContext.getHrefPlantTraitOntology(null));
			aCO.setHref(AppContext.getHrefRiceCropOntology(null));

			AppContext.debug("tabMDS.isVisible=" + tabMDS.isVisible());
			AppContext.debug("tabMDS.isSelected=" + tabMDS.isSelected());
			AppContext.debug("tabTable.isVisible=" + tabTable.isVisible());

			bandboxVarietyset.setValue(AppContext.getDefaultDataset()); // "3k");
			// updateUI();
			/*
			 * if(listboxDataset.getRows()>0) { listboxDataset.setSelectedIndex(0); }
			 */

			AppContext.debug("..zulend");

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	@Listen("onSelect = #listboxDataset")
	public void onSelectcheckboxdroplistGenotyperun(Event e) throws InterruptedException {

		String str = "";

		for (Listitem li : listboxDataset.getItems()) {
			if (!li.isSelected()) {
				continue;
			}
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += li.getLabel();
		}
		// Bandbox bandbox = (Bandbox)comp.getFellow("bd");
		// bandbox.setValue(str);
		bandboxVarietyset.setValue(str);
	}

	@Listen("onOpen = #bandboxVarietyset")
	public void onOpencheckboxdroplistGenotyperun(OpenEvent e) throws InterruptedException {
		AppContext.debug("e.isOpen()=" + e.isOpen() + " text=" + bandboxVarietyset.getText() + "  value="
				+ bandboxVarietyset.getValue());
		if (e.isOpen()) {
			// setVarietyset(getVarietyset());
			setVarietyset(getDataset());
			return;
		} else {
			updateUI();
		}

	}

	private void setVarietyset(Set s) {
		String str = "";
		for (Object li : s) {
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += (String) li;
		}
		bandboxVarietyset.setValue(str);
		Set setsel = new HashSet();
		for (Listitem li : listboxDataset.getItems()) {
			if (s.contains(li.getLabel())) {
				setsel.add(li);
			}
		}
		listboxDataset.setSelectedItems(setsel);
	}

	private Set getDataset() {

		Set s = new LinkedHashSet();
		String[] ds = bandboxVarietyset.getText().split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;

		// return listboxDataset.getSelectedItem().getValue();
		/*
		 * Set s=new LinkedHashSet(); for(Listitem
		 * item:listboxDataset.getSelectedItems()) { s.add( item.getValue() ); } return
		 * s;
		 */

		/*
		 * if(this.listboxDataset.getSelectedItem().getValue().equals("3k")) return
		 * VarietyFacade.DATASET_SNPINDELV2_IUPAC; else
		 * if(this.listboxDataset.getSelectedItem().getValue().equals("hdra")) return
		 * VarietyFacade.DATASET_SNP_HDRA; else
		 * if(this.listboxDataset.getSelectedItem().getValue().equals("hdra")) return
		 * VarietyFacade.DATASET_SNP_HDRA; return null;
		 */
	}

	// @Listen("onSelect =#listboxDataset")
	// public void onselectListboxdataset() {

	private void updateUI() {

		try {
			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

			Set<Integer> setRows3k = new HashSet();
			Set<Integer> setRowsHdra = new HashSet();
			setRows3k.add(3);
			setRows3k.add(5);
			setRows3k.add(6);
			setRows3k.add(7);
			setRows3k.add(8);
			setRowsHdra.add(3);
			setRowsHdra.add(6);
			setRowsHdra.add(7);
			setRowsHdra.add(8);
			Set<Integer> setRowsGq92 = new HashSet(setRows3k);
			Set<Integer> setRowsWissuwa = new HashSet(setRows3k);

			Set dataset = getDataset();
			Set setPassport = new TreeSet(variety.getPassportDefinitions(dataset).keySet());
			Set setPhenotype = new TreeSet(variety.getPhenotypeDefinitions(dataset).keySet());
			Set setPtoco = new TreeSet(variety.getPtocoDefinitions(dataset).keySet());

			java.util.List varnames = variety.getVarietyNames(dataset);
			java.util.List varaccessions = variety.getVarietyAccessions(dataset);
			java.util.List irisIds = variety.getIrisIds(dataset);
			java.util.List countries = variety.getCountries(dataset);
			Collection subpopulation = variety.getSubpopulations(dataset);

			java.util.List listPassport = new java.util.ArrayList();
			listPassport.add("");
			listPassport.addAll(setPassport);
			java.util.List listPhenotype = new java.util.ArrayList();
			listPhenotype.add("");
			listPhenotype.addAll(setPhenotype);
			java.util.List listPTOCO = new java.util.ArrayList();
			listPTOCO.add("");
			listPTOCO.addAll(setPtoco);

			/*
			 * msgbox.setValue(varnames.size() + " varieties, " + irisIds.size()+
			 * (isHdraDataset()?" GSOR IDs, ":" IRIS IDs, ") + countries.size()
			 * +" countries, " + subpopulation.size()+ " subpopulations, " +
			 * setPassport.size() + " passport fields, " + setPhenotype.size() +
			 * " phenotypes, " + setPtoco.size() + " rice trait terms, for selection");
			 */
			msgbox.setValue(varnames.size() + " designations, " + varaccessions.size() + " accessions, "
					+ countries.size() + " countries, " + subpopulation.size() + " subpopulations, "
					+ setPassport.size() + " passport fields, " + setPhenotype.size() + " phenotypes, "
					+ setPtoco.size() + " rice trait terms, for selection");

			this.listboxPtocoterm.setModel(new SimpleListModel(listPTOCO));
			listboxPtocoterm.setSelectedIndex(0);
			this.listboxPhenotypes.setModel(new SimpleListModel(listPhenotype));
			listboxPhenotypes.setSelectedIndex(0);
			this.listboxPassport.setModel(new SimpleListModel(listPassport));
			listboxPassport.setSelectedIndex(0);
			this.listboxPassportValue.setModel(new SimpleListModel(new ArrayList()));
			this.listboxPhenValue.setModel(new SimpleListModel(new ArrayList()));

			comboAccession
					.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(varaccessions, true, true)));
			comboAccession.setValue("");
			comboIrisId.setValue("");
			comboIrisId.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(irisIds, true, true)));
			comboVarname.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(varnames, true, true)));
			this.listboxSubpopulation.setModel(
					(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(subpopulation, false, true))));

			if (is3kDataset()) {
				Iterator<Component> itRows = gridQuery.getRows().getChildren().iterator();
				int irow = 0;
				while (itRows.hasNext()) {
					Component c = itRows.next();
					if (setRowsHdra.contains(irow))
						c.setVisible(false);
					if (setRows3k.contains(irow))
						c.setVisible(true);
					irow++;
				}
				labelExampleSubpop.setValue("(ex. trop)");
				labelExampleVarname.setValue("(ex. loto::gervex 104-c1)");
				labelExampleAccession.setValue("ex. IRGC 122151");
				labelExampleIrisId.setValue("ex.  iris 313-10000");
				this.checkboxShowMDSNeighbors.setVisible(true);

			} else if (isGq92Dataset()) {
				Iterator<Component> itRows = gridQuery.getRows().getChildren().iterator();
				int irow = 0;
				while (itRows.hasNext()) {
					Component c = itRows.next();
					if (setRowsHdra.contains(irow))
						c.setVisible(false);
					if (setRows3k.contains(irow))
						c.setVisible(true);
					irow++;
				}
				labelExampleSubpop.setValue("");
				labelExampleVarname.setValue("(ex. IRRI 161)");
				labelExampleAccession.setValue("");
				labelExampleIrisId.setValue("");
				this.checkboxShowMDSNeighbors.setVisible(false);
			} else if (isHdraDataset()) {

				Iterator<Component> itRows = gridQuery.getRows().getChildren().iterator();
				int irow = 0;
				while (itRows.hasNext()) {
					Component c = itRows.next();
					if (setRows3k.contains(irow))
						c.setVisible(false);
					if (setRowsHdra.contains(irow))
						c.setVisible(true);
					irow++;
				}
				labelExampleSubpop.setValue("(ex. indica)");
				labelExampleVarname.setValue("(ex. malagkit puti::irgc19451-1)");
				labelExampleAccession.setValue("(ex. IRGC 121423, NSFTV100)");
				labelExampleIrisId.setValue("");

				this.checkboxShowMDSNeighbors.setVisible(true);
			} else if (isWissuwaDataset()) {

				Iterator<Component> itRows = gridQuery.getRows().getChildren().iterator();
				int irow = 0;
				while (itRows.hasNext()) {
					Component c = itRows.next();
					if (setRows3k.contains(irow))
						c.setVisible(false);
					if (setRowsWissuwa.contains(irow))
						c.setVisible(true);
					irow++;
				}
				labelExampleSubpop.setValue("");
				labelExampleVarname.setValue("(ex. wab104)");
				labelExampleAccession.setValue("");
				labelExampleIrisId.setValue("");
				this.checkboxShowMDSNeighbors.setVisible(false);
			}

			reset();

			this.tabboxDisplay.setVisible(false);
			this.gridGermplasm.setVisible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@Listen("onClick = #buttonDownloadCSV")
	public void clickDownloadCSV() {
		downloadVarieties("varieties-" + AppContext.createTempFilename() + ".csv", ",");
	}

	@Listen("onClick = #buttonDownloadTab")
	public void clickDownloadTab() {
		downloadVarieties("varieties-" + AppContext.createTempFilename() + ".txt", "\t");
	}

	/**
	 * Download variety list with filename and delimiter
	 * 
	 * @param filename
	 * @param delimiter
	 */
	private void downloadVarieties(String filename, String delimiter) {

		StringBuffer buff = new StringBuffer();

		Listhead lhd = listboxVarietyresult.getListhead();
		Iterator itHeader = lhd.getChildren().iterator();
		while (itHeader.hasNext()) {
			Listheader lh = (Listheader) itHeader.next();
			buff.append(lh.getLabel());
			if (itHeader.hasNext())
				buff.append(delimiter);
		}
		buff.append("\n");

		Iterator<Variety> itvar = varsresult.iterator();

		while (itvar.hasNext()) {
			buff.append(itvar.next().printFields(delimiter));
			if (itvar.hasNext())
				buff.append("\n");
		}

		try {
			String filetype = "text/plain";
			if (delimiter.equals(","))
				filetype = "text/csv";

			File file = new File(AppContext.getTempDir() + filename);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(buff.toString());
			bw.flush();
			bw.close();

			org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			AppContext.debug("Variety write complete!" + file.getAbsolutePath() + " Downloaded to:"
					+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());

			try {
				Filedownload.save(new File(AppContext.getTempDir() + filename), filetype);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	/**
	 * Create variety list from result
	 */
	@Listen("onClick = #buttonAddToList")
	public void addToList() {

		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		if (listboxVarietyresult.getItems().size() == 0) {
			Messagebox.show("EMPTY VARIETY LIST");
			return;
		}
		if (txtboxListname.getValue().isEmpty()) {
			Messagebox.show("PROVIDE LIST NAME");
			return;
		}
		Set setVarieties = new LinkedHashSet();

		if (user != null)
			WorkspaceLoadLocal.writeListToUserList(txtboxListname.getValue(),
					WebConstants.VARIETY_DIR + File.separator + getDataset(), setVarieties, user.getEmail());

		workspace.addVarietyList(txtboxListname.getValue().trim(), new LinkedHashSet(varsresult), getDataset());

		Messagebox.show("List " + txtboxListname.getValue().trim() + " was created");
		txtboxListname.setValue("");

	}

	@Listen("onSelect = #listboxPtocoterm")
	public void onselectPtocoterm() {
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		Set dataset = getDataset();
		String term = this.listboxPtocoterm.getSelectedItem().getLabel();
		String cv = "";
		String terms[] = null;
		List list = new ArrayList();
		if (term.isEmpty()) {
			// display all traits
			Set setPhenotype = variety.getPhenotypeDefinitions(dataset).keySet();
			list.add("");
			list.addAll(setPhenotype);
			aCO.setHref(AppContext.getHrefRiceCropOntology(null));
			aTO.setHref(AppContext.getHrefPlantTraitOntology(null));
			aCO.setDisabled(false);
			aTO.setDisabled(false);

		} else {
			// String terms[] = term.split(":");
			// String terms2[] = terms[0].split("\\(");

			terms = term.split("\\(");
			String ontoterms2 = terms[terms.length - 1];
			String ontoterms = ontoterms2.substring(0, ontoterms2.length() - 1);
			if (ontoterms.contains("TO")) {
				cv = "plant_trait_ontology";
				aTO.setHref(AppContext.getHrefPlantTraitOntology(ontoterms));
				aCO.setDisabled(true);
				aTO.setDisabled(false);
			} else if (ontoterms.contains("CO_")) {
				cv = "rice_trait";
				aCO.setHref(AppContext.getHrefRiceCropOntology(ontoterms));
				aTO.setDisabled(true);
				aCO.setDisabled(false);
			}
			term = terms[0].trim(); // ontoterms.trim();
			list.add("");
			list.addAll(variety.getPhenotypeByPtoco(cv, terms[0].trim(), dataset));
		}

		if (listPhenotypeListbox.size() == 0) {
			this.listboxPhenotypes.setModel(new SimpleListModel(list));
			if (list.size() == 2) {
				listboxPhenotypes.setSelectedIndex(1);
				listboxPhenotypes.setSelectedItem(listboxPhenotypes.getItemAtIndex(1));
				Events.postEvent("onSelect", listboxPhenotypes, null);
				// setPhenotypeConstraint();
			} else {
				listboxPhenotypes.setSelectedIndex(0);
				this.listboxPhenValue.setModel(new SimpleListModel(new ArrayList()));
			}
			// setPhenotypeConstraint();
		} else {
			ListboxPhenotype lastPhen = listPhenotypeListbox.get(listPhenotypeListbox.size() - 1);
			lastPhen.setModel(new SimpleListModel(list));
			if (list.size() == 2) {
				lastPhen.setSelectedIndex(1);
				lastPhen.setSelectedItem(lastPhen.getItemAtIndex(1));
				Events.postEvent("onSelect", lastPhen, null);
				// lastPhen.setPhenotypeConstraint();
			} else {
				lastPhen.setSelectedIndex(0);
				lastPhen.setModel(new SimpleListModel(new ArrayList()));
			}
			// lastPhen.onSelect();
		}
		if (terms != null)
			AppContext.debug(
					"getting phenototypes for " + cv + "  " + term + " " + terms[0].trim() + "  items=" + list.size());
		else
			AppContext.debug("getting phenototypes for " + cv + "  " + term + " items=" + list.size());
	}

	/**
	 * Add more phenotype query constraint
	 */
	@Listen("onClick =#buttonAddPhenConstraint")
	public void buttonAddPhenConstraint() {
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		Hbox hboxPhen = new Hbox();

		Listbox listboxPhenValues = new Listbox();
		listboxPhenValues.setMold("select");
		listboxPhenValues.setRows(1);

		List listQuanOperator = new ArrayList();
		listQuanOperator.add(VarietyFacade.COMPARATOR_EQUALS);
		listQuanOperator.add(VarietyFacade.COMPARATOR_LESSTHAN);
		listQuanOperator.add(VarietyFacade.COMPARATOR_GREATERTHAN);

		Listbox listboxComparator = new Listbox();
		listboxComparator.setMold("select");
		listboxComparator.setWidth("40px");
		listboxComparator.setRows(1);
		listboxComparator.setModel(new SimpleListModel(listQuanOperator));
		listboxComparator.setSelectedIndex(0);

		java.util.List listPhenotype = new java.util.ArrayList();
		listPhenotype.addAll(variety.getPhenotypeDefinitions(getDataset()).keySet());

		Listbox listboxMorePhenConstraint = new ListboxPhenotype(listboxPhenValues, variety, listboxComparator,
				getDataset());

		listboxMorePhenConstraint.setRows(1);
		listboxMorePhenConstraint.setMold("select");
		listboxMorePhenConstraint.setWidth("300px");

		listboxMorePhenConstraint.setModel(new SimpleListModel(listPhenotype));

		hboxPhen.appendChild(listboxMorePhenConstraint);
		hboxPhen.appendChild(listboxComparator);
		hboxPhen.appendChild(listboxPhenValues);

		hboxPhen.appendChild(vboxPhenConstraints.getLastChild().getLastChild());

		listPhenotypeListbox.add((ListboxPhenotype) listboxMorePhenConstraint);
		vboxPhenConstraints.appendChild(hboxPhen);
	}

	/**
	 * Add more passport query constraint
	 */
	@Listen("onClick =#buttonAddPassConstraint")
	public void buttonAddPassConstraint() {
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		Hbox hboxPass = new Hbox();

		Listbox listboxPassValues = new Listbox();
		listboxPassValues.setMold("select");
		listboxPassValues.setRows(1);

		java.util.List listPassport = new java.util.ArrayList();
		listPassport.addAll(variety.getPassportDefinitions(getDataset()).keySet());
		Listbox listboxMorePassConstraint = new ListboxPassport(listboxPassValues, variety, getDataset());
		listboxMorePassConstraint.setRows(1);
		listboxMorePassConstraint.setMold("select");
		listboxMorePassConstraint.setWidth("300px");

		listboxMorePassConstraint.setModel(new SimpleListModel(listPassport));

		hboxPass.appendChild(listboxMorePassConstraint);

		Label eqlabel = new Label();
		eqlabel.setValue(" = ");
		eqlabel.setPre(true);
		hboxPass.appendChild(eqlabel);
		hboxPass.appendChild(listboxPassValues);

		hboxPass.appendChild(vboxPassportConstraints.getLastChild().getLastChild());

		vboxPassportConstraints.appendChild(hboxPass);
	}

	/**
	 * passport field list selected, get unique values of passport
	 */
	@Listen("onSelect = #listboxPassport")
	public void setPassportConstraint() {

		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		AppContext.debug("passport selected:" + listboxPassport.getSelectedItem().getLabel());

		List listValues = new java.util.ArrayList();

		if (!listboxPassport.getSelectedItem().getLabel().trim().isEmpty()) {
			Iterator<CvTermUniqueValues> itValues = variety
					.getPassportUniqueValues(listboxPassport.getSelectedItem().getLabel(), getDataset()).iterator();
			while (itValues.hasNext()) {
				CvTermUniqueValues value = itValues.next();
				if (value == null)
					continue;
				listValues.add(value.getValue());
			}
		}

		listboxPassportValue.setModel(new SimpleListModel(listValues));
		if (listValues.size() > 0)
			listboxPassportValue.setSelectedIndex(0);
	}

	/**
	 * phenotype field list selected, get unique values of phenotype
	 */

	private Collection getPhenUniqueValues(Object retobj[]) {
		List listValues = new java.util.ArrayList();
		Iterator<CvTermUniqueValues> itValues = ((Set) retobj[0]).iterator();
		phenotypevalue_type = (Integer) retobj[1];
		while (itValues.hasNext()) {
			CvTermUniqueValues value = itValues.next();
			if (value == null) {
				AppContext.debug("null value");
				continue;
			}

			/*
			 * listValues.add( BigDecimal.valueOf(Double.valueOf( String.format("%.2f" ,
			 * value.getValue()).replaceAll("\\.0+$","") )) );
			 */
			// if (item.getQuanValue()!=null) val= String.format("%.2f" ,
			// value.getValue()).replaceAll("\\.0+$",""); //.ro
			// .toString().replaceAll("\\.0+","");

			// listValues.add(value.getValue() );

			listValues.add(value.getValue());
		}
		return listValues;
	}

	@Listen("onSelect = #listboxPhenotypes")
	public void setPhenotypeConstraint() {

		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		List listValues = new java.util.ArrayList();

		AppContext.debug("phenotype selected:" + listboxPhenotypes.getSelectedItem().getLabel());

		if (!listboxPhenotypes.getSelectedItem().getLabel().trim().isEmpty()) {
			String value = "";
			if (radioCoTrait.isSelected()) {
				String phenValues[] = listboxPhenotypes.getSelectedItem().getLabel().split("::");
				value = phenValues[1];
			} else {
				value = listboxPhenotypes.getSelectedItem().getLabel();
			}

			listValues = (List) getPhenUniqueValues(variety.getPhenotypeUniqueValues(value, getDataset())); // variety.getPhenotypeUniqueValues(
																											// listboxPhenotypes.getSelectedItem().getLabel()
																											// ).iterator();
		}
		listboxPhenValue.setModel(new SimpleListModel(listValues));
		if (listValues.size() > 0)
			listboxPhenValue.setSelectedIndex(0);
	}

	/**
	 * check varieties in database
	 * 
	 * @param varstr
	 * @return
	 */
	private Set checkVariety(String varstr) {
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		return variety.checkVariety(varstr, getDataset());
	}

	/*
	 * show variety query results
	 */
	private void show_varlist(Set varset) {
		List newvarlist = new ArrayList();
		newvarlist.addAll(varset);
		varsresult = newvarlist;
		displayResults(false);
		tabTable.setSelected(true);
		tabTable.setVisible(true);
		tabPhylo.setVisible(true);
		tabMDS.setVisible(false);
		istreebrowser = true;
	}

	/**
	 * Handle event from JSP/javascript page (phylotree frame)
	 * 
	 * @param event
	 */
	@Listen("onUser = #winVariety")
	// public void onUser$info(Event event){
	public void onUser(Event event) {
		String eventParam = event.getData().toString();
		// ForwardEvent eventx = (ForwardEvent)event;
		// String eventParam = eventx.getOrigin().getData().toString();
		// AppContext.debug( eventParam );
		// Messagebox.show(eventParam);

		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		String[] names = eventParam.split("\\&");
		if (names.length > 1) {
			Set varsSelected = checkVariety(names[0].replace("irisid=", "").replace("name=", "").replace("_", " "));
			varsSelected.addAll(checkVariety(names[1].replace("irisid=", "").replace("name=", "").replace("_", " ")));
			show_varlist(varsSelected);

		} else {
			String irisidOrName = eventParam.replace("_", " ");

			Variety varselected = null;
			if (irisidOrName.startsWith("irisid=")) {
				String irisids[] = irisidOrName.replace("irisid=", "").split(",");
				if (irisids.length > 1) {
					show_varlist(checkVariety(irisidOrName.replace("irisid=", "")));
				} else {
					varselected = variety.getGermplasmByIrisId(irisids[0], getDataset());
					show_passport(varselected);
				}

			} else if (irisidOrName.startsWith("name=")) {
				String varnames[] = irisidOrName.replace("name=", "").split(",");
				if (varnames.length > 1) {
					show_varlist(checkVariety(irisidOrName.replace("name=", "")));
				} else {

					/*
					 * varselected = variety.getGermplasmByName(varnames[0],getDataset());
					 * show_passport(varselected);
					 */
				}
			}
		}
	}

	/**
	 * When a row in variety result list is selected, show passport and phenotype
	 * data
	 */
	@Listen("onSelect = #listboxVarietyresult")
	public void selectVariety() {

		// if(!is3kDataset()) return;

		try {
			Listitem selItem = listboxVarietyresult.getSelectedItem();

			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

			String varname = null;
			String accession = null;
			Listcell lc = (Listcell) selItem.getChildren().get(0);
			Label lb = (Label) lc.getChildren().get(0);
			varname = lb.getValue();

			lc = (Listcell) selItem.getChildren().get(2);
			lb = (Label) lc.getChildren().get(0);
			accession = lb.getValue();

			Collection setvars = variety.getGermplasmsByName(varname, getDataset());
			if (setvars.size() == 1)
				show_passport((Variety) setvars.iterator().next());
			else {
				setvars = new HashSet();
				setvars.add(variety.getGermplasmsByAccession(accession, getDataset()));
				if (setvars.size() == 1)
					show_passport((Variety) setvars.iterator().next());
				else {
					setvars = variety.getGermplasmsByNameAccession(varname, accession, getDataset());
					if (setvars.size() == 1)
						show_passport((Variety) setvars.iterator().next());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		/*
		 * if(is3kDataset()) { lc = (Listcell)selItem.getChildren().get(2); lb =
		 * (Label)lc.getChildren().get(0); accession= lb.getValue();
		 * 
		 * Collection setvars = variety.getGermplasmsByName(varname, getDataset() );
		 * if(setvars.size()==1) show_passport( (Variety)setvars.iterator().next());
		 * else { setvars = variety.getGermplasmsByAccession(accession, getDataset() );
		 * if(setvars.size()==1) show_passport( (Variety)setvars.iterator().next());
		 * else { setvars = variety.getGermplasmsByNameAccession(varname, accession,
		 * getDataset() ); if(setvars.size()==1) show_passport(
		 * (Variety)setvars.iterator().next()); } } } else { lc =
		 * (Listcell)selItem.getChildren().get(2); lb = (Label)lc.getChildren().get(0);
		 * accession= lb.getValue();
		 * 
		 * Set setvars = variety.getGermplasmsByName(varname, getDataset() );
		 * if(setvars.size()==1) show_passport( (Variety)setvars.iterator().next());
		 * else { setvars = variety.getGermplasmsByAccession(accession, getDataset() );
		 * if(setvars.size()==1) show_passport( (Variety)setvars.iterator().next());
		 * else { setvars = variety.getGermplasmsByNameAccession(varname, accession,
		 * getDataset() ); if(setvars.size()==1) show_passport(
		 * (Variety)setvars.iterator().next()); } }
		 * 
		 * }
		 */

		// Variety selVar = variety.getGermplasmByName(varname,getDataset());
		// Variety selVar = variety.getGermplasmByAccession(value, dataset)
		// .getGermplasmByName(varname,getDataset());

		// show_passport(selVar);
	}

	/**
	 * Phylogenetic tree tab is clicked
	 */
	@Listen("onSelect = #tabPhylo")
	public void onselectTabPhylo() {

		if (isdonePhylo)
			return;

		if (istreebrowser) {
			iframePhylotree.setSrc("treeBrowser3k.htm");
			isdonePhylo = true;
			return;
		}

		// AppContext.debug("loading phylotree " + urlphylo);
		Clients.showBusy("Computing Phylogenetic Tree");
		gridGermplasm.setVisible(false);

		if (varsresult.size() > 2) {
			show_phylotree(varsresult);
		}

		Clients.clearBusy();
		isdonePhylo = true;
	}

	/**
	 * MDS tab is clicked
	 */
	@Listen("onSelect = #tabMDS")
	public void onselectTabMDS() {

		if (isdoneMDS)
			return;

		// Clients.showBusy("Computing MDS plot");
		gridGermplasm.setVisible(false);

		try {

			if (varsresult.size() > 2) {
				Iterator<Variety> listIds = varsresult.iterator();
				List<BigDecimal> ids = new ArrayList();
				StringBuffer varids = new StringBuffer();
				while (listIds.hasNext()) {
					BigDecimal id = listIds.next().getVarietyId();
					varids.append(id);
					ids.add(id);
					if (listIds.hasNext())
						varids.append(",");
				}
				AppContext.debug("showing mds for " + ids.size() + " varieties");
				variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
				plotXY(chartMDS, variety.getMapId2Variety(getDataset()),
						variety.constructMDSPlot(ids, "1", false, getDataset()), "Varieties MDS Plot",
						varids.toString(), null, true, null);
			}
			// Clients.clearBusy();
		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage());
		}

		isdoneMDS = true;
	}

	/**
	 * Show neighbors of selected variety as MDS
	 */
	public void showMDSNeighbors() {

		// chartMDSNeighbors.setModel(new DefaultXYModel());
		// showmds_allvars(chartMDSNeighbors, textboxGermDesignation.getValue(),null);
		// chartMDS.setModel(new DefaultXYModel());
		Set sethight = new HashSet();
		sethight.add(textboxGermDesignation.getValue());
		// showmds_allvars(chartMDS, textboxGermDesignation.getValue(),sethight);
		showmds_allvars(chartMDSNeighbors, textboxGermDesignation.getValue(), sethight);

		/*
		 * tabPhylo.setSelected(false); tabTable.setVisible(false);
		 * //hboxQuery.setVisible(false); tabMDS.setSelected(true);
		 */
	}

	@Listen("onClick = #checkboxShowMDSNeighbors")
	public void onclickCheckMDSNeighbors() {
		boolean isshow = checkboxShowMDSNeighbors.isChecked();
		if (isshow) {
			showMDSNeighbors();
		}
		this.divMDSNeighbors.setVisible(isshow);
	}

	/**
	 * Reset button, clear controls
	 */
	@Listen("onClick = #buttonReset")
	public void reset() {
		comboVarname.setValue("");
		comboIrisId.setValue("");
		comboCountry.setValue("");
		listboxSubpopulation.setSelectedIndex(0);
		this.comboAccession.setValue("");

		listboxPhenotypes.setSelectedIndex(0);
		listboxPhenValue.setModel(new SimpleListModel(new ArrayList()));
		listboxPhenComparator.setSelectedIndex(0);

		this.listboxMyVariety.setSelectedIndex(0);
		this.listboxPtocoterm.setSelectedIndex(0);

		Component hboxes = vboxPhenConstraints.getFirstChild().getNextSibling();
		List listRemove = new ArrayList();
		while (hboxes != null) {
			listRemove.add(hboxes);
			hboxes = hboxes.getNextSibling();
		}
		if (vboxPhenConstraints.getChildren().size() > 1)
			vboxPhenConstraints.getFirstChild().appendChild(vboxPhenConstraints.getLastChild().getLastChild());

		Iterator<Component> itRemove = listRemove.iterator();
		while (itRemove.hasNext()) {
			vboxPhenConstraints.removeChild(itRemove.next());
		}

		listboxPassport.setSelectedIndex(0);
		listboxPassportValue.setModel(new SimpleListModel(new ArrayList()));

		hboxes = vboxPassportConstraints.getFirstChild().getNextSibling();
		listRemove = new ArrayList();
		while (hboxes != null) {
			listRemove.add(hboxes);
			hboxes = hboxes.getNextSibling();
		}
		if (vboxPassportConstraints.getChildren().size() > 1)
			vboxPassportConstraints.getFirstChild().appendChild(vboxPassportConstraints.getLastChild().getLastChild());

		itRemove = listRemove.iterator();
		while (itRemove.hasNext()) {
			vboxPassportConstraints.removeChild(itRemove.next());
		}

	}

	/**
	 * display all varieties
	 */
	@Listen("onClick = #showallButton")
	public void showAll() {
		reset();
		tabTable.setSelected(true);
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		varsresult = new java.util.ArrayList<Variety>();
		varsresult.addAll(variety.getGermplasm(getDataset()));

		msgbox.setValue("ALL RESULT: " + varsresult.size() + " rows");

		displayResults(true);
	}

	/**
	 * Search button is clicked 1. check if inputs are valid 2. if variety name or
	 * iris id are set, show passport and phenotype else query using values for
	 * country and subpopulation 3. display result in table 4. if result is more
	 * than 2, generate phylogenetic tree
	 */
	@Listen("onClick = #buttonSearch")
	public void searchList3k() {
		try {

			Clients.evalJavaScript("myFunction();");

			AppContext.resetTimer("variety query start");
			tabboxDisplay.setVisible(false);

			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

			tabTable.setSelected(true);

			varsresult = new java.util.ArrayList<Variety>();

			if (comboVarname.getValue() != null && !comboVarname.getValue().isEmpty()
					&& ((comboIrisId.getValue() != null && !comboIrisId.getValue().isEmpty())
							|| (this.comboAccession.getValue() != null && !comboAccession.getValue().isEmpty()))) {
				Messagebox.show("INVALID INPUT: Only one of either Variety name or IRIS ID/Accession can be specified");
				return;
			}

			/*
			 * if(comboVarname.getValue()!=null && !comboVarname.getValue().isEmpty() ) {
			 * 
			 * String tmpvarname=comboVarname.getValue(); reset();
			 * comboVarname.setValue(tmpvarname); Variety varQuery =
			 * variety.getGermplasmByName( comboVarname.getValue() , getDataset());
			 * if(varQuery==null) { Messagebox.show( "VARIETY NOT FOUND: " +
			 * comboVarname.getValue().toUpperCase()); return; }
			 * msgbox.setValue(comboVarname.getValue().toUpperCase() + " PASSPORT DATA");
			 * 
			 * AppContext.resetTimer("passport query start");
			 * 
			 * show_passport( varQuery );
			 * 
			 * } else
			 */
			if (comboIrisId.getValue() != null && !comboIrisId.getValue().isEmpty()) {

				String tmpirisid = comboIrisId.getValue();
				reset();
				comboIrisId.setValue(tmpirisid);
				Variety varQuery = variety.getGermplasmByIrisId(comboIrisId.getValue(), getDataset());
				if (varQuery == null) {
					Messagebox.show("VARIETY NOT FOUND: " + comboIrisId.getValue().toUpperCase());
					return;
				}

				msgbox.setValue(comboIrisId.getValue().toUpperCase() + " PASSPORT DATA");

				AppContext.resetTimer("passport query start");

				show_passport(varQuery);
			} else if (comboAccession.getValue() != null && !comboAccession.getValue().isEmpty()) {
				Variety varQuery = variety.getGermplasmByAccession(comboAccession.getValue(), getDataset());
				if (varQuery == null) {
					Messagebox.show("VARIETY NOT FOUND: " + comboAccession.getValue().toUpperCase());
					return;
				}

				msgbox.setValue(comboAccession.getValue().toUpperCase() + " PASSPORT DATA");

				AppContext.resetTimer("passport query start");

				show_passport(varQuery);
			}

			else {

				if ((comboCountry == null || comboCountry.getValue().isEmpty())
						&& (listboxSubpopulation == null || listboxSubpopulation.getSelectedIndex() == 0)
						&& (listboxPassport.getSelectedItem() == null
								|| listboxPassport.getSelectedItem().getLabel().trim().isEmpty())
						&& (listboxPhenotypes.getSelectedItem() == null
								|| listboxPhenotypes.getSelectedItem().getLabel().trim().isEmpty())
						&& (comboVarname == null || comboVarname.getValue() == null
								|| comboVarname.getValue().trim().isEmpty())) {

					AppContext.debug("varname=" + comboVarname.getValue());

					Messagebox.show("INVALID INPUT: Provide at least one constraint");
					return;
				}

				gridGermplasm.setVisible(false);
				tabboxDisplay.setVisible(true);
				listboxVarietyresult.setVisible(true);

				if ((comboCountry == null || comboCountry.getValue().isEmpty())
						&& (listboxSubpopulation == null || listboxSubpopulation.getSelectedIndex() < 1)
						&& comboVarname.getValue().trim().isEmpty() && comboAccession.getValue().trim().isEmpty()) {
				} else {

					// query by example, with multiple constraints
					Variety example = new VarietyImpl();

					StringBuffer msg = new StringBuffer();

					if (comboVarname.getValue() != null && !comboVarname.getValue().trim().isEmpty()) {
						example.setName(comboVarname.getValue());
						msg.append(" DESIGNATION=" + comboVarname.getValue().toUpperCase());
					} else if (comboAccession.getValue() != null && !comboAccession.getValue().trim().isEmpty()) {
						example.setAccession(comboAccession.getValue());
						msg.append(" ACCESSION=" + comboAccession.getValue().toUpperCase());
					} else {
						// if(comboCountry==null) throw new RuntimeException("comboCountry==null");
						// if(comboCountry.getValue()==null) throw new
						// RuntimeException("comboCountry.getValue()==null");

						if (comboCountry.getValue() != null && !comboCountry.getValue().isEmpty()) {
							example.setCountry(comboCountry.getValue());
							msg.append(" COUNTRY=" + comboCountry.getValue().toUpperCase());
						}

						if (listboxSubpopulation == null)
							throw new RuntimeException("comboSubpopulation==null");
						if (listboxSubpopulation.getSelectedIndex() > 0) {
							example.setSubpopulation(listboxSubpopulation.getSelectedItem().getLabel());
							if (msg.length() > 0)
								msg.append(" AND ");

							msg.append(" SUBPOPULATION="
									+ listboxSubpopulation.getSelectedItem().getLabel().toUpperCase());
						}
					}

					msgbox.setValue("VARIETY WHERE " + msg.toString());

					if (variety == null)
						throw new RuntimeException("variety==null");
					if (example == null)
						throw new RuntimeException("example==null");
					if (varsresult == null)
						throw new RuntimeException("varsresult==null");

					varsresult.addAll(variety.getGermplasmByExample(example, getDataset()));
				}

				// passport contraints
				List listExtraheader = new ArrayList();
				Component hbchildPassConstraint = vboxPassportConstraints.getFirstChild();
				while (hbchildPassConstraint != null) {
					Listbox hblistboxPassport = (Listbox) hbchildPassConstraint.getFirstChild();
					Listbox hblistboxPassValue = (Listbox) hblistboxPassport.getNextSibling().getNextSibling();

					if (hblistboxPassport.getSelectedItem() != null
							&& !hblistboxPassport.getSelectedItem().getLabel().trim().isEmpty()) {
						String definition = hblistboxPassport.getSelectedItem().getLabel().trim();
						List<VarietyPlus> listVarsByPassport = variety.getVarietyByPassport(definition, getDataset(),
								hblistboxPassValue.getSelectedItem().getLabel().trim());

						varsresult = intersectVarietyListPlus(varsresult, listVarsByPassport, definition);
						listExtraheader.add(definition);
						String msg = msgbox.getValue().trim();
						if (!msg.isEmpty())
							msg += ",";

						msgbox.setValue(msg + definition + "=" + hblistboxPassValue.getSelectedItem().getLabel());
					}
					hbchildPassConstraint = hbchildPassConstraint.getNextSibling();
				}

				// phenotype constraints
				Component hbchildPhenConstraint = vboxPhenConstraints.getFirstChild();
				int listcount = 0;
				while (hbchildPhenConstraint != null) {
					Listbox hblistboxPhenotypes = (Listbox) hbchildPhenConstraint.getFirstChild();
					Listbox hblistboxPhenComparator = (Listbox) hblistboxPhenotypes.getNextSibling();
					Listbox hblistboxPhenValue = (Listbox) hblistboxPhenComparator.getNextSibling();

					if (hblistboxPhenotypes.getSelectedItem() != null
							&& !hblistboxPhenotypes.getSelectedItem().getLabel().trim().isEmpty()) {
						String definition = hblistboxPhenotypes.getSelectedItem().getLabel().trim();
						String queryValue;
						if (radioCoTrait.isSelected()) {
							String coValue[] = definition.split("::");
							queryValue = coValue[1];
						} else {
							queryValue = definition;
						}

						int phenotypetype;
						if (listcount == 0)
							phenotypetype = phenotypevalue_type;
						else
							phenotypetype = listPhenotypeListbox.get(listcount - 1).getPhenotype_type();

						List<VarietyPlus> listVarsByPhenotype = variety.getVarietyByPhenotype(queryValue, getDataset(),
								hblistboxPhenComparator.getSelectedItem().getLabel(),
								hblistboxPhenValue.getSelectedItem().getLabel().trim(), phenotypetype);

						varsresult = intersectVarietyListPlus(varsresult, listVarsByPhenotype, queryValue);
						listExtraheader.add(definition);
						String msg = msgbox.getValue().trim();
						if (!msg.isEmpty())
							msg += ",";

						msgbox.setValue(msg + definition + hblistboxPhenComparator.getSelectedItem().getLabel()
								+ hblistboxPhenValue.getSelectedItem().getLabel());
					}
					hbchildPhenConstraint = hbchildPhenConstraint.getNextSibling();
					listcount++;
				}

				if (listboxMyVariety.getSelectedItem() == null
						|| listboxMyVariety.getSelectedItem().getLabel().trim().isEmpty()) {
				} else {
					String msg = msgbox.getValue().trim();
					if (!msg.isEmpty())
						msg += ", AND VARIETY IN LIST " + listboxMyVariety.getSelectedItem().getLabel().trim();
					msgbox.setValue(msg);

					List newlist = new ArrayList();
					Iterator<Variety> itVar = varsresult.iterator();

					workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
					Set setVarlist = workspace.getVarieties(listboxMyVariety.getSelectedItem().getLabel().trim());

					varsresult.retainAll(setVarlist);
				}

				msgbox.setValue(msgbox.getValue() + " ... RESULT: " + varsresult.size() + " rows");
				msgbox.setStyle("font-color:black");

				displayResults(false, listExtraheader);
			}

			isdoneMDS = false;
			isdonePhylo = false;
			istreebrowser = false;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * Get intersection of variety list1 and list2
	 * 
	 * @param list1
	 * @param list2     varieties with valuename
	 * @param valuename additional variety property name
	 * @return
	 */
	private List intersectVarietyListPlus(java.util.Collection<Variety> list1, Collection<VarietyPlus> list2,
			String valuename) {

		List<Variety> listNew = new ArrayList<Variety>();

		if (list1.size() == 0) {
			// return (List)list2;

			Iterator<VarietyPlus> it2Var = list2.iterator();
			while (it2Var.hasNext()) {
				VarietyPlus var2 = it2Var.next();
				VarietyPlusPlus var1 = new VarietyPlusPlusImpl(var2, valuename);
				listNew.add(var1);
			}
			return listNew;
		}

		Map<BigDecimal, Variety> setList1Id = new HashMap();
		Iterator<Variety> itVar = list1.iterator();

		boolean list1IsVarietyPlus = false;
		boolean list1IsVarietyPlusPlus = false;

		// get vars in list1
		while (itVar.hasNext()) {
			Variety var = itVar.next();
			if (!list1IsVarietyPlus && var instanceof VarietyPlus) {
				list1IsVarietyPlus = true;
				if (!list1IsVarietyPlusPlus && var instanceof VarietyPlusPlus)
					list1IsVarietyPlusPlus = true;
			}
			setList1Id.put(var.getVarietyId(), var);
		}

		if (list1IsVarietyPlus) {

			if (list1IsVarietyPlusPlus) {
				// add value to map
				Iterator<VarietyPlus> it2Var = list2.iterator();
				while (it2Var.hasNext()) {
					VarietyPlus var2 = it2Var.next();
					if (setList1Id.containsKey(var2.getVarietyId())) {
						VarietyPlusPlus var1 = (VarietyPlusPlus) setList1Id.get(var2.getVarietyId());
						var1.addValue(valuename, var2.getValue());
						listNew.add(var1);
					}
				}

			} else {
				// create a VarietyPlusPlus from var1 and var2
				Iterator<VarietyPlus> it2Var = list2.iterator();
				while (it2Var.hasNext()) {
					VarietyPlus var2 = it2Var.next();
					if (setList1Id.containsKey(var2.getVarietyId())) {
						VarietyPlusPlus var1 = new VarietyPlusPlusImpl(
								(VarietyPlus) setList1Id.get(var2.getVarietyId()), var2, valuename);
						listNew.add(var1);
					}
				}
			}
		} else {
			// use var2 in return (Return is VarietyPlus list)
			// add vars in list2 only if in list 1
			Iterator<VarietyPlus> it2Var = list2.iterator();
			while (it2Var.hasNext()) {
				VarietyPlus var2 = it2Var.next();
				if (setList1Id.containsKey(var2.getVarietyId())) {
					// var2.setValueName(valuename);
					// listNew.add( var2 );

					VarietyPlusPlus var1 = new VarietyPlusPlusImpl(setList1Id.get(var2.getVarietyId()), var2,
							valuename);
					listNew.add(var1);

				}
			}
		}
		return listNew;

	}

	private void displayResults(boolean showAll) {
		displayResults(showAll, null);

	}

	private boolean is3kDataset() {
		return getDataset().contains("3k");
		// return
		// this.listboxDataset.getSelectedItem().getValue().equals(VarietyFacade.DATASET_SNPINDELV2_IUPAC);
	}

	private boolean isHdraDataset() {
		// return
		// this.listboxDataset.getSelectedItem().getValue().equals(VarietyFacade.DATASET_SNP_HDRA);
		return getDataset().contains("hdra");
	}

	private boolean isWissuwaDataset() {
		// return
		// this.listboxDataset.getSelectedItem().getValue().equals(VarietyFacade.DATASET_SNP_WISSUWA);
		return getDataset().contains("wissuwa9");
	}

	private boolean isGq92Dataset() {
		// return
		// this.listboxDataset.getSelectedItem().getValue().equals(VarietyFacade.DATASET_SNP_GOPAL92);
		return getDataset().contains("gq92");
	}

	private void displayResults(boolean showAll, Collection extrafield) {

		boolean hasExtraField = (extrafield != null && !extrafield.isEmpty());

		AppContext.debug("Displaying " + varsresult.size() + " varieties");

		// update table header
		Map<String, String> listheaders = new LinkedHashMap();

		listheaders.put("NAME", "name");
		if (isHdraDataset())
			listheaders.put("GSOR ID", "irisId");
		else
			listheaders.put("IRIS ID", "irisId");

		listheaders.put("ACCESSION", "accessionId");
		listheaders.put("SUBPOPULATION", "subpopulation");
		listheaders.put("COUNTRY", "country");

		/*
		 * if(is3kDataset()) { listheaders.put("NAME","name");
		 * listheaders.put("IRIS ID","irisId");
		 * listheaders.put("ACCESSION","accessionId");
		 * listheaders.put("SUBPOPULATION","subpopulation");
		 * listheaders.put("COUNTRY","country"); } else {
		 * listheaders.put("NAME","name"); listheaders.put("ACCESSION","accessionId");
		 * listheaders.put("SUBPOPULATION","fsSubpop"); }
		 */

		if (hasExtraField) {
			Iterator<String> itHead = extrafield.iterator();
			while (itHead.hasNext()) {
				String headlabel = itHead.next();
				listheaders.put(headlabel, "");
			}
		}

		Listhead lhd = listboxVarietyresult.getListhead();
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

		if (is3kDataset()) {
			if (varsresult.size() > 2) {
				tabPhylo.setVisible(true);
				tabMDS.setVisible(true);
			} else {
				tabPhylo.setVisible(false);
				tabMDS.setVisible(false);
			}
		} else {
			tabPhylo.setVisible(false);
			tabMDS.setVisible(false);
		}

		AppContext.debug(varsresult.toString());

		listboxVarietyresult.setModel(new SimpleListModel(varsresult));

		if (varsresult.size() > 0)
			hboxAddtolist.setVisible(true);
		else
			hboxAddtolist.setVisible(false);

		gridGermplasm.setVisible(false);
		tabboxDisplay.setVisible(true);

		if (varsresult.size() == 1)
			this.show_passport(varsresult.get(0));

	}

	/**
	 * Show passport details for variety
	 * 
	 * @param variety2
	 */
	private void show_passport(Variety variety2) {

		AppContext.debug("show_passport:" + variety2);
		gridGermplasm.setVisible(false);

		/*
		 * if(!is3kDataset()) { List varlist = new ArrayList(); varlist.add(variety2);
		 * varsresult=varlist; displayResults(false); tabboxDisplay.setVisible(true);
		 * return; }
		 */

		textboxGermDesignation.setValue(variety2.getName());
		if (variety2.getIrisId() == null)
			textboxIRISId.setValue("");
		else
			textboxIRISId.setValue(variety2.getIrisId());

		if (isHdraDataset())
			labelIRISId.setValue("GSOR ID");
		else
			labelIRISId.setValue("IRIS ID");

		if (variety2.getAccession() == null)
			textboxGermAccession.setValue("");
		else
			textboxGermAccession.setValue(variety2.getAccession());

		if (variety2.getCountry() == null)
			textboxGermCountry.setValue("");
		else
			textboxGermCountry.setValue(variety2.getCountry());
		if (variety2.getSubpopulation() == null)
			textboxGermSubpopulation.setValue("");
		else
			textboxGermSubpopulation.setValue(variety2.getSubpopulation());

		if (!is3kDataset()) {
			buttonDownloadFastq.setVisible(false);
			buttonDownloadFastqc.setVisible(false);
			buttonDownloadBAM.setVisible(false);
			buttonDownloadVCF.setVisible(false);
			this.chartMDSNeighbors.setVisible(false);
		} else {

			buttonDownloadFastq.setVisible(true);
			buttonDownloadFastqc.setVisible(true);
			buttonDownloadBAM.setVisible(true);
			buttonDownloadVCF.setVisible(true);
			this.chartMDSNeighbors.setVisible(true);

			buttonDownloadFastqc.setHref(AppContext.getFastqcURL() + variety2.getIrisId().replace(" ", "_") + ".html");
			buttonDownloadFastq.setHref(AppContext.getFastqURL() + variety2.getIrisId().replace(" ", "_"));
			if (AppContext.isIRRILAN()) {
				// buttonDownloadBAM.setHref("http://172.29.4.93/cgi-bin/fqget.pl?q="
				// + variety2.getIrisId().replace("IRIS", "").trim() + "&t=bam");
				// buttonDownloadVCF.setHref("http://172.29.4.93/cgi-bin/fqget.pl?q="
				// + variety2.getIrisId().replace("IRIS", "").trim() + "&t=vcf");
				buttonDownloadBAM.setHref(AppContext.getBamURL(variety2.getIrisId()));
				buttonDownloadVCF.setHref(AppContext.getVcfURL(variety2.getIrisId()));
			} else {

				// buttonDownloadBAM.setHref("https://s3.amazonaws.com/3kricegenome/Nipponbare/"
				// + variety2.getIrisId().replace("IRIS","").trim() + ".realigned.bam");
				// buttonDownloadVCF.setHref("https://s3.amazonaws.com/3kricegenome/Nipponbare/"
				// + variety2.getIrisId().replace("IRIS","").trim() + ".snp.vcf.gz" );
				buttonDownloadBAM.setHref(AppContext.getBamURL(variety2.getIrisId()));
				buttonDownloadVCF.setHref(AppContext.getVcfURL(variety2.getIrisId()));
			}
			buttonDownloadBAM.setVisible(true);
			buttonDownloadVCF.setVisible(true);

			if (this.checkboxShowMDSNeighbors.isChecked())
				showMDSNeighbors();
		}

		/*
		 * if(!is3kDataset()) { buttonDownloadFastq.setVisible(false);
		 * buttonDownloadFastqc.setVisible(false); buttonDownloadBAM.setVisible(false);
		 * buttonDownloadVCF.setVisible(false); listboxGermPassport.setModel(new
		 * SimpleListModel(new ArrayList())); listboxGermPhenotypes.setModel(new
		 * SimpleListModel(new ArrayList())); this.chartMDSNeighbors.setVisible(false);
		 * gridGermplasm.setVisible(true); return; }
		 */

		Set dataset = this.getDataset();

		java.util.List listPassport = new ArrayList();
		listPassport.addAll(variety.getPassportByVarietyid(variety2.getVarietyId()));
		AppContext.debug(listPassport.size() + " passports");
		listboxGermPassport.setModel(new SimpleListModel(listPassport));

		AppContext.resetTimer("phenotype query start");

		tabPassportIRGCIS.setSelected(true);

		java.util.List listPhens = variety.getPhenotypesByGermplasm(variety2, dataset);
		AppContext.debug(listPhens.size() + " phenotypes");
		listboxGermPhenotypes.setModel(new SimpleListModel(listPhens));

		gridGermplasm.setVisible(true);

		splitter.setOpen(true);

	}

	@Listen("onClick =#tabPassportGenesys")
	public void ontabpassportGenesys() {
		java.util.List listPassport = new ArrayList();

		String acc = textboxGermAccession.getValue().trim();
		if (acc == null || acc.isEmpty())
			acc = textboxGermDesignation.getValue().trim();

		listPassport.addAll(variety.getPassportByAccession(acc));
		AppContext.debug(listPassport.size() + " genesys passports");
		listboxGermPassportGenesys.setModel(new SimpleListModel(listPassport));

		for (Object o : listPassport) {
			Passport p = (Passport) o;
			if (p.getName().equals("germplasmDbId")) {

				Executions.getCurrent().sendRedirect("https://purl.org/germplasm/id/" + p.getValue(), "genesys");
				// Executions.getCurrent().sendRedirect("https://sandbox.genesys-pgr.org/a/"+p.getValue(),
				// "genesys");
				break;
			}
		}
	}

	/**
	 * Compute phylogenetic tree using jsp/phylotreeGermplasms.jsp
	 * 
	 * @param varids list of variety IDs
	 */
	private void show_phylotree(String varids) {

		int nvars = varids.split(",").length;
		int height = nvars * 21;
		int width = nvars * 30;

		int treescale = 1;
		getSession().removeValue("newick");
		getSession().removeValue("nvars");

		if (!varids.equals("all"))
			nvars = varids.split(",").length;

		iframePhylotree.setVisible(false);

		// String newick = variety.constructPhylotree( varlist.replace("-",
		// "_").replace(" ", "_").replace("'","").replace("(", "").replace(")",
		// "").replace("\"", "") );
		String phylos[] = variety.constructPhylotree(varids, "1", null, null, getDataset());
		String newick = phylos[0];
		newick = "'" + newick.replace("\"", "").replace(":-", ":") + "'";
		String phyloxml = phylos[1].replace("<branch_length>-", "<branch_length>");

		iframePhylotree.setStyle("height:" + Integer.toString(height) + "px;width:1500px");
		iframePhylotree.setScrolling("yes");

		getSession().putValue("newick", newick);
		getSession().putValue("nvars", nvars);

		String newickfile = "newick" + AppContext.createTempFilename();
		String outfile = AppContext.getTempDir() + newickfile;

		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(outfile));
			br.write(newick);
			br.close();

			br = new BufferedWriter(new FileWriter(outfile + ".xml"));
			br.write(phyloxml);
			br.close();

			String finalurl = AppContext.getHostname() + "/" + AppContext.getHostDirectory() + "/jsp/phylo.jsp";
			if (AppContext.getHostDirectory().isEmpty())
				finalurl = AppContext.getHostname() + "/jsp/phylo.jsp";

			finalurl += "?newick=" + newickfile + "&nvars=" + nvars;

			// String url = "jsp/phylotreeGermplasms.jsp?scale=" + treescale +
			// "&varid=session" + "&requestid=" +
			// (HttpSession)Sessions.getCurrent().getNativeSession();
			// AppContext.debug( url );
			iframePhylotree.setSrc(finalurl);
			iframePhylotree.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}

	private void show_phylotreeOld(String varids) {

		int nvars = varids.split(",").length;
		int height = nvars * 21;
		int width = nvars * 30;

		int treescale = 1;

		iframePhylotree.setStyle("height:" + Integer.toString(height) + "px;width:1500px");
		iframePhylotree.setScrolling("yes");

		iframePhylotree.setVisible(true);

		Sessions.getCurrent().setAttribute("varlist", varids);

		String url = "jsp/phylotreeGermplasms.jsp?scale=" + treescale + "&varid=session" + "&requestid="
				+ (HttpSession) Sessions.getCurrent().getNativeSession();
		AppContext.debug(url);
		iframePhylotree.setSrc(url);

	}

	/**
	 * create phylogenetic tree for varieties in list
	 * 
	 * @param varsresult1
	 */
	private void show_phylotree(List varsresult1) {
		StringBuffer varids = new StringBuffer();
		java.util.Iterator<Variety> itvars = varsresult1.iterator();
		while (itvars.hasNext()) {
			varids.append(itvars.next().getVarietyId());
			if (itvars.hasNext())
				varids.append(",");
		}
		show_phylotree(varids.toString());
	}

	/**
	 * Show MDS for all varieties
	 */

	@Listen("onClick = #buttonHighlight")
	public void highlight_vars() {
		if (listboxHighlightVariety.getSelectedIndex() > 0) {
			Set sethigh = new HashSet();
			sethigh.add(listboxHighlightVariety.getSelectedItem().getLabel().toUpperCase());
			showmds_allvars(chartMDS, null, sethigh);
		} else if (listboxHighlightVarietyList.getSelectedIndex() > 0) {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			Set sethigh = workspace.getVarieties(listboxHighlightVarietyList.getSelectedItem().getLabel().trim());
			showmds_allvars(chartMDS, null, sethigh);
		} else { // remove hightlight
			showmds_allvars(chartMDS, null, null);
		}
	}

	@Listen("onSelect = #listboxHighlightVariety")
	public void on_select_highlight_vars() {
		listboxHighlightVarietyList.setSelectedIndex(0);
		listboxMDSPhenotype.setSelectedIndex(0);
		if (listboxHighlightVariety.getSelectedIndex() > 0) {
			Set sethigh = new HashSet();
			sethigh.add(listboxHighlightVariety.getSelectedItem().getLabel().toUpperCase());
			showmds_allvars(chartMDS, null, sethigh);
		} else
			showmds_allvars(chartMDS, null, null);

	}

	@Listen("onSelect = #listboxHighlightVarietyList")
	public void on_select_highlight_list() {
		listboxHighlightVariety.setSelectedIndex(0);
		listboxMDSPhenotype.setSelectedIndex(0);
		if (listboxHighlightVarietyList.getSelectedIndex() > 0) {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			Set sethigh = new HashSet();
			Iterator itset = workspace.getVarieties(listboxHighlightVarietyList.getSelectedItem().getLabel().trim())
					.iterator();
			while (itset.hasNext()) {
				Object h = itset.next();
				if (h instanceof Variety) {
					sethigh.add(((Variety) h).getName().toUpperCase());
				} else if (h instanceof String) {
					sethigh.add(((String) h).toUpperCase());
				} else
					throw new RuntimeException(h.getClass().getCanonicalName() + " should be variety or variety name");
			}
			showmds_allvars(chartMDS, null, sethigh);
		} else
			showmds_allvars(chartMDS, null, null);

	}

	@Listen("onSelect = #listboxMDSPhenotype")
	public void on_select_mds_phen() {
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		listboxHighlightVarietyList.setSelectedIndex(0);
		listboxHighlightVariety.setSelectedIndex(0);
		if (listboxMDSPhenotype.getSelectedIndex() > 0) {
			String phenname = listboxMDSPhenotype.getSelectedItem().getLabel();
			List val = (List) getPhenUniqueValues(variety.getPhenotypeUniqueValues(phenname, getDataset()));
			// if(AppContext.getQuanTraits().contains(phenname)) {
			if (variety.getQuantTraits(getDataset()).contains(phenname)) {
				// qualitative
				Set sortedvals = new TreeSet();
				for (int i = 0; i < val.size(); i++) {
					try {
						double dval = Double.valueOf(val.get(i).toString());
						sortedvals.add(dval);
					} catch (Exception ex) {
						AppContext.debug("val=" + val.get(i) + "; " + val.get(i).getClass().getCanonicalName());
					}
				}

				List lsortedvals = new ArrayList();
				lsortedvals.addAll(sortedvals);

				// AppContext.debug( "vals=" + lsortedvals);
				double maxval = ((Number) lsortedvals.get(lsortedvals.size() - 1)).doubleValue();
				double minval = ((Number) lsortedvals.get(0)).doubleValue();

				Map<BigDecimal, Object> mapVar2Phenval = variety.getPhenotypeValues(phenname, getDataset());

				showmds_allvars(chartMDS, null, minval, maxval, mapVar2Phenval);

				labelMDSPhenotype.setValue("QUANITATIVE");
			} else {
				// categorical
				Set hashvals = new HashSet();

				try {
					for (int i = 0; i < val.size(); i++) {
						int dval = Double.valueOf(val.get(i).toString()).intValue();
						hashvals.add(dval);
					}
					Set sortedvals = new TreeSet(hashvals);
					List lsortedvals = new ArrayList();
					lsortedvals.addAll(sortedvals);
					int maxval = ((Number) lsortedvals.get(lsortedvals.size() - 1)).intValue();
					if (maxval > 1000) {
						int max1 = maxval;
						maxval = ((Number) lsortedvals.get(lsortedvals.size() - 2)).intValue();
						AppContext.debug("change catvalue max " + max1 + " to " + maxval);
					}
					int minval = ((Number) lsortedvals.get(0)).intValue();
					Map<BigDecimal, Object> mapVar2Phenval = variety.getPhenotypeValues(phenname, getDataset());
					showmds_allvars(chartMDS, null, minval, maxval, mapVar2Phenval);
					labelMDSPhenotype.setValue("CATEGORICAL (NUMBER-CODED)");
				} catch (Exception ex) {
					ex.printStackTrace();
					AppContext.debug("categorical value for " + phenname + " not numeric");
					labelMDSPhenotype.setValue("DESCRIPTIVE (TEXT-CODED)");

					Messagebox.show("Non-comparable categorical values:" + hashvals);
				}
			}
		} else {
			showmds_allvars(chartMDS, null, null);
			labelMDSPhenotype.setValue("");
		}

	}

	@Listen("onShowAllMDS = #winVariety")
	public void showmds_allvars() {
		showmds_allvars(chartMDS);
		this.tabMDS.setVisible(true);
		this.tabMDS.setSelected(true);
	}

	// @Listen("onShowAllMDSHDRA = #winVariety")
	// public void showmds_allvars_hdra() {
	//
	// listboxDataset.setSelectedIndex(1);
	// //Events.postEvent("onSelect", listboxDataset, 1);
	// onselectListboxdataset();
	// tabboxDisplay.setVisible(true);
	//
	// showmds_allvars(chartMDS);
	//
	// listboxHighlightVariety.setModel(new SimpleListModel(
	// AppContext.createUniqueUpperLowerStrings(
	// variety.getVarietyNames(VarietyFacade.DATASET_SNP_HDRA), false, true) ));
	// listboxHighlightVariety.setSelectedIndex(0);
	//
	// Set setPhenotype =
	// variety.getPhenotypeDefinitions(VarietyFacade.DATASET_SNP_HDRA).keySet();
	//
	// java.util.List listPhenotype = new java.util.ArrayList();
	// listPhenotype.add("");
	// listPhenotype.addAll( setPhenotype );
	// listboxMDSPhenotype.setModel(new SimpleListModel(listPhenotype));
	// listboxMDSPhenotype.setSelectedIndex(0);
	//
	// /*
	// <zscript><![CDATA[
	// listboxHighlightVariety.setModel(new SimpleListModel(
	// AppContext.createUniqueUpperLowerStrings(
	// variety.getVarietyNames(VarietyFacade.DATASET_SNPINDELV2_IUPAC), false, true)
	// ));
	// listboxHighlightVariety.setSelectedIndex(0);
	// listboxHighlightVarietyList.setModel(new SimpleListModel(listMyVarieties));
	// listboxHighlightVarietyList.setSelectedIndex(0);
	// ]]></zscript>
	//
	// </hbox>
	// <hbox>
	// <label value="Size by phenotype value: " pre="true"/>
	// <listbox id="listboxMDSPhenotype" mold="select" width="200px"/>
	// <label id="labelMDSPhenotype"/>
	//
	// <zscript><![CDATA[
	// listboxMDSPhenotype.setModel(new SimpleListModel(listPhenotype));
	// listboxMDSPhenotype.setSelectedIndex(0);
	// ]]></zscript>
	// */
	//
	// this.tabMDS.setVisible(true);
	// this.tabMDS.setSelected(true);
	//
	// }

	private void showmds_allvars(Charts mdsChart) {
		showmds_allvars(mdsChart, null, null);
	}

	/**
	 * show mds plot with center at variety centerName
	 * 
	 * @param mdsChart
	 * @param centerName
	 */
	private void showmds_allvars(Charts mdsChart, String centerName, Collection highlight) {
		try {
			mdsChart.setVisible(false);
			AppContext.debug("showmds_allvars: " + centerName);

			List listIds = new ArrayList();
			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

			listIds.addAll(variety.getMapId2Variety(getDataset()).keySet());

			StringBuffer varids = new StringBuffer();
			java.util.Iterator<Variety> itvars = listIds.iterator();
			while (itvars.hasNext()) {
				varids.append(itvars.next());
				if (itvars.hasNext())
					varids.append(",");
			}
			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
			plotXY(mdsChart, variety.getMapId2Variety(getDataset()),
					variety.constructMDSPlot(listIds, "1", true, getDataset()), "Varieties MDS Plot", varids.toString(),
					centerName, true, highlight);
			mdsChart.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	private void showmds_allvars(Charts mdsChart, String centerName, double min, double max,
			Map<BigDecimal, Object> mapVar2Val) {

		try {
			mdsChart.setVisible(false);
			AppContext.debug("showmds_allvars: " + centerName);

			List listIds = new ArrayList();
			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

			listIds.addAll(variety.getMapId2Variety(getDataset()).keySet());

			StringBuffer varids = new StringBuffer();
			java.util.Iterator<Variety> itvars = listIds.iterator();
			while (itvars.hasNext()) {
				varids.append(itvars.next());
				if (itvars.hasNext())
					varids.append(",");
			}
			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
			plotXY(mdsChart, variety.getMapId2Variety(getDataset()),
					variety.constructMDSPlot(listIds, "1", true, getDataset()), "Varieties MDS Plot", varids.toString(),
					centerName, true, min, max, mapVar2Val);
			mdsChart.setVisible(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@Listen("onPlotClick = #checkboxShowMDSLabel")
	private void clickShowMDSLabel() {
		ScatterPlotOptions plotOptions = chartMDS.getPlotOptions().getScatter();
		plotOptions.getDataLabels().setEnabled(checkboxShowMDSLabel.isChecked());

	}

	/**
	 * Plot xy values in MDS chart
	 * 
	 * @param mdsChart
	 * @param xy
	 * @param title
	 * @param varids
	 * @param centerName
	 */

	public static void plotXY(Charts mdsChart, Map<BigDecimal, Variety> mapId2Var, double xy[][], String title,
			String varids, String centerName, boolean showAll, Collection listHighlight) {

		String varid[] = varids.trim().split(",");
		// Map<BigDecimal,Variety> mapId2Var = variety.getMapId2Variety(getDataset());
		int n = varid.length;

		AppContext.debug("displaying plot:" + title + " " + xy.length + " points, " + n + " vars, " + mapId2Var.size()
				+ " mapId2Var.size");

		mdsChart.setModel(new DefaultXYModel());

		ScatterPlotOptions plotOptions = mdsChart.getPlotOptions().getScatter();
		Marker marker = plotOptions.getMarker();
		marker.setRadius(4);
		marker.getStates().getHover().setEnabled(true);
		marker.getStates().getHover().setLineColor("rgb(100,100,100)");
		plotOptions.getStates().getHover().getMarker().setEnabled(false);

		// plotOptions.getTooltip().setValueDecimals(3);

		plotOptions.getTooltip().setHeaderFormat("");
		// plotOptions.getTooltip().setPointFormat("{point.name}<br>{point.x},{point.y}");
		plotOptions.getTooltip().setPointFormat("{point.name}");

		plotOptions.getDataLabels().setFormat("{point.name}");
		plotOptions.getDataLabels().setEnabled(false);

		Map<String, Series> mapSubpop2Series = new HashMap();

		boolean hasCenter = false;
		if (centerName != null && !centerName.isEmpty())
			hasCenter = true;

		List listHighlightpoints = new ArrayList();
		Point centerpoint = null;
		int highcount = 0;
		for (int i = 0; i < n; i++) {

			Variety var = mapId2Var.get(BigDecimal.valueOf(Long.valueOf(varid[i])));

			Series series4var;
			if (var.getSubpopulation() == null || var.getSubpopulation().isEmpty()) {
				series4var = mapSubpop2Series.get("None");
				if (series4var == null) {
					series4var = mdsChart.getSeries(mapSubpop2Series.keySet().size());
					series4var.setName("none");
					series4var.setColor(Data.getSubpopulationColor("none"));
					mapSubpop2Series.put("none", series4var);
				}
			} else {
				if (mapSubpop2Series.containsKey(var.getSubpopulation()))
					series4var = mapSubpop2Series.get(var.getSubpopulation());
				else {
					series4var = mdsChart.getSeries(mapSubpop2Series.keySet().size());
					series4var.setName(var.getSubpopulation());
					series4var.setColor(Data.getSubpopulationColor(var.getSubpopulation()));
					mapSubpop2Series.put(var.getSubpopulation(), series4var);
				}
			}

			// if(hasHighlight &&
			// centerName.toUpperCase().equals(var.getName().toUpperCase())) {
			String acc = var.getAccession();
			String varname = var.getName();
			if (acc != null && !acc.isEmpty()) {
				varname += " [" + acc.toUpperCase() + "]";
			}
			if (listHighlight != null && (listHighlight.contains(var.getName().toUpperCase())
					|| listHighlight.contains(var.getAccession().toUpperCase()))) {
				series4var.addPoint(var.getName(), Double.valueOf(String.format("%.6f", xy[1][i])),
						Double.valueOf(String.format("%.6f", xy[0][i])));
				Point highlightedpoint = series4var.getPoint(series4var.getData().size() - 1);
				Marker ptmarker = highlightedpoint.getMarker();
				ptmarker.setRadius(ptmarker.getRadius().intValue() * 3);
				// ptmarker.getLineColor().setColor("red");
				ptmarker.setFillColor("red");
				ptmarker.setLineWidth(ptmarker.getLineWidth().intValue() * 3);
				highlightedpoint.setMarker(ptmarker);

				Point hpoint2 = new Point(varname, Double.valueOf(String.format("%.6f", xy[1][i])),
						Double.valueOf(String.format("%.6f", xy[0][i])));
				Marker ptmarker2 = hpoint2.getMarker();
				ptmarker2.setRadius(ptmarker2.getRadius().intValue() * 3);
				ptmarker2.setFillColor("red");
				ptmarker2.setLineWidth(ptmarker2.getLineWidth().intValue() * 3);
				hpoint2.setMarker(ptmarker2);
				listHighlightpoints.add(hpoint2);

				highcount++;
			} else
				series4var.addPoint(varname, Double.valueOf(String.format("%.6f", xy[1][i])),
						Double.valueOf(String.format("%.6f", xy[0][i])));

			if (centerName != null && centerName.toUpperCase().equals(var.getName().toUpperCase())) {
				centerpoint = series4var.getPoint(series4var.getData().size() - 1);
			}
		}

		if (listHighlight != null) {

			Series series4var = mdsChart.getSeries(mapSubpop2Series.keySet().size());
			series4var.setName("selection");
			series4var.setColor("red");
			Iterator<Point> itPoint = listHighlightpoints.iterator();
			while (itPoint.hasNext())
				series4var.addPoint(itPoint.next());

			AppContext.debug("highlighed count=" + highcount);
		}

	}

	public static void plotXY(Charts mdsChart, Map<BigDecimal, Variety> mapId2Var, double xy[][], String title,
			String varids, String centerName, boolean showAll, double min, double max,
			Map<BigDecimal, Object> mapVar2Val) {

		int maxsize = 12;
		int minsize = 3;
		AppContext.debug("max=" + max + ", min=" + min);
		mdsChart.setModel(new DefaultXYModel());

		ScatterPlotOptions plotOptions = mdsChart.getPlotOptions().getScatter();
		Marker marker = plotOptions.getMarker();
		// marker.setRadius(4);
		marker.setRadius(1);
		marker.getStates().getHover().setEnabled(true);
		marker.getStates().getHover().setLineColor("rgb(100,100,100)");
		plotOptions.getStates().getHover().getMarker().setEnabled(false);

		// plotOptions.getTooltip().setValueDecimals(3);

		plotOptions.getTooltip().setHeaderFormat("");
		// plotOptions.getTooltip().setPointFormat("{point.name}<br>{point.x},{point.y}");
		plotOptions.getTooltip().setPointFormat("{point.name}");

		plotOptions.getDataLabels().setFormat("{point.name}");
		plotOptions.getDataLabels().setEnabled(false);

		String varid[] = varids.trim().split(",");
		// Map<BigDecimal,Variety> mapId2Var = variety.getMapId2Variety(getDataset());
		int n = varid.length;
		Map<String, Series> mapSubpop2Series = new HashMap();

		boolean hasCenter = false;
		if (centerName != null && !centerName.isEmpty())
			hasCenter = true;

		Point centerpoint = null;
		int highcount = 0;
		for (int i = 0; i < n; i++) {

			Variety var = mapId2Var.get(BigDecimal.valueOf(Long.valueOf(varid[i])));

			Series series4var;
			if (var.getSubpopulation() == null || var.getSubpopulation().isEmpty()) {
				series4var = mapSubpop2Series.get("None");
				if (series4var == null) {
					series4var = mdsChart.getSeries(mapSubpop2Series.keySet().size());
					series4var.setName("none");
					series4var.setColor(Data.getSubpopulationColor("none"));
					mapSubpop2Series.put("none", series4var);
				}
			} else {
				if (mapSubpop2Series.containsKey(var.getSubpopulation()))
					series4var = mapSubpop2Series.get(var.getSubpopulation());
				else {
					series4var = mdsChart.getSeries(mapSubpop2Series.keySet().size());
					series4var.setName(var.getSubpopulation());
					series4var.setColor(Data.getSubpopulationColor(var.getSubpopulation()));
					mapSubpop2Series.put(var.getSubpopulation(), series4var);
				}
			}

			String acc = var.getAccession();
			String varname = var.getName();
			if (acc != null && !acc.isEmpty()) {
				varname += " [" + acc.toUpperCase() + "]";
			}

			if (mapVar2Val.containsKey(var.getVarietyId())) {
				series4var.addPoint(varname, Double.valueOf(String.format("%.6f", xy[1][i])),
						Double.valueOf(String.format("%.6f", xy[0][i])));
				Point highlightedpoint = series4var.getPoint(series4var.getData().size() - 1);
				Marker ptmarker = highlightedpoint.getMarker();
				double phenval = ((Number) mapVar2Val.get(var.getVarietyId())).doubleValue();
				// int rad=
				// Double.valueOf(ptmarker.getRadius().intValue()*(maxsize-minsize)*(phenval-min)/(max-min)).intValue()
				// + minsize;
				int rad = Double.valueOf((maxsize - minsize) * (phenval - min) / (max - min)).intValue() + minsize;
				// AppContext.debug("ptmarker.getRadius()=" + ptmarker.getRadius() +"; val=" +
				// phenval + "; rad=" + rad);
				ptmarker.setRadius(rad);
				// ptmarker.getLineColor().setColor("red");
				// ptmarker.setFillColor("black");
				// ptmarker.setLineWidth( ptmarker.getLineWidth().intValue()*3 );
				highlightedpoint.setMarker(ptmarker);
				highcount++;
			} else
			// series4var.addPoint(var.getName(), Double.valueOf(String.format(
			// "%.6f",xy[1][i])) , Double.valueOf(String.format( "%.6f",xy[0][i])) );
			{
				series4var.addPoint(varname, Double.valueOf(String.format("%.6f", xy[1][i])),
						Double.valueOf(String.format("%.6f", xy[0][i])));
				Point highlightedpoint = series4var.getPoint(series4var.getData().size() - 1);
				Marker ptmarker = highlightedpoint.getMarker();
				ptmarker.setFillColor("black");
				ptmarker.setRadius(minsize - 1);
				highlightedpoint.setMarker(ptmarker);
			}

			if (centerName != null && centerName.toUpperCase().equals(var.getName().toUpperCase())) {
				centerpoint = series4var.getPoint(series4var.getData().size() - 1);
			}
		}

		AppContext.debug("MDS highlight cnt=" + highcount);

		if (!showAll && centerpoint != null) {
			mdsChart.getXAxis().setMax(centerpoint.getX().doubleValue() + 0.005);
			mdsChart.getXAxis().setMin(centerpoint.getX().doubleValue() - 0.005);
			mdsChart.getYAxis().setMax(centerpoint.getY().doubleValue() + 0.005);
			mdsChart.getYAxis().setMin(centerpoint.getY().doubleValue() - 0.005);

		} else if (centerpoint != null) {

		}

	}

	/*
	 * @Listen("onPlotClick = #chartMDS") public void showVarietyFromMDS(ChartsEvent
	 * event) { // Open an invisible popup at where the point clicked. Point point =
	 * event.getPoint(); Variety varselected =
	 * variety.getGermplasmByName(point.getName(),getDataset());
	 * show_passport(varselected); }
	 * 
	 * @Listen("onPlotClick = #chartMDSNeighbors") public void
	 * showVarietyFromMDSNeighbors(ChartsEvent event) { // Open an invisible popup
	 * at where the point clicked. Point point = event.getPoint(); Variety
	 * varselected = variety.getGermplasmByName(point.getName(),getDataset());
	 * show_passport(varselected); }
	 */

	@Listen("onPlotClick = #chartMDS")
	public void showVarietyFromMDS(ChartsEvent event) {
		// Open an invisible popup at where the point clicked.
		Point point = event.getPoint();
		String nameacc = point.getName();
		Variety varselected = null;
		if (nameacc.endsWith("]")) {
			varselected = variety.getGermplasmByAccession(nameacc.split("\\[")[1].replace("]", ""), getDataset()); // .iterator().next();
		} else
			varselected = variety.getGermplasmByName(point.getName(), getDataset()).iterator().next();

		show_passport(varselected);
	}

	@Listen("onClick =#radioLegacyTrait")
	public void onclickLegacy() {
		java.util.List listPhenotype = new java.util.ArrayList();
		listPhenotype.add("");
		listPhenotype.add("Create phenotype list...");
		listPhenotype.addAll(workspace.getVarietyQuantPhenotypelistNames("3k"));
		listPhenotype.addAll(workspace.getVarietyCatPhenotypelistNames("3k"));

		if (radioLegacyTrait.isSelected()) {
			Set<String> setPhenotype = new HashSet();

			setPhenotype = variety.getTraits(AppContext.getStringValues(listboxDataset.getSelectedItems()), true)
					.keySet();
			// setPhenotype = new HashSet<String>(variety
			// .getAllTraits(AppContext.getStringValues(listboxDataset.getSelectedItems()),
			// true).values());
			TreeSet<String> ts = new TreeSet<String>();
			for (String phen : setPhenotype) {
				ts.add(phen.toLowerCase());
			}
			listPhenotype.addAll(ts);

			listboxPhenotypes.setModel(new SimpleListModel(listPhenotype));
		}
		listboxPhenotypes.setSelectedIndex(0);
	}

	@Listen("onClick =#radioCoTrait")
	public void onclickCOTerm() {
		java.util.List listPhenotype = new java.util.ArrayList();
		listPhenotype.add("");
		listPhenotype.add("Create phenotype list...");
		listPhenotype.addAll(workspace.getVarietyQuantPhenotypelistNames("3k"));
		listPhenotype.addAll(workspace.getVarietyCatPhenotypelistNames("3k"));
		if (radioCoTrait.isSelected()) {
			Set<String> setPhenotype = new HashSet();

			setPhenotype = variety.getTraits(AppContext.getStringValues(listboxDataset.getSelectedItems()), false)
					.keySet();
			// setPhenotype = new HashSet<String>(variety
			// .getAllTraits(AppContext.getStringValues(listboxDataset.getSelectedItems()),
			// false).values());
			// TreeSet<String> ts = new TreeSet<String>();
			listPhenotype.addAll(setPhenotype);

			listboxPhenotypes.setModel(new SimpleListModel(listPhenotype));

		}

		listboxPhenotypes.setSelectedIndex(0);
	}

	@Listen("onPlotClick = #chartMDSNeighbors")
	public void showVarietyFromMDSNeighbors(ChartsEvent event) {
		// Open an invisible popup at where the point clicked.
		Point point = event.getPoint();
		String nameacc = point.getName();
		Variety varselected = null;
		if (nameacc.endsWith("]")) {
			varselected = variety.getGermplasmByAccession(nameacc.split("\\[")[1].replace("]", ""), getDataset()); // .iterator().next();
		} else
			varselected = variety.getGermplasmByName(point.getName(), getDataset()).iterator().next();
		show_passport(varselected);
	}

	private void checkMultipleVarnames(final Combobox combo) {

		String varname = combo.getValue();

		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");

		Collection colVars = null;
		colVars = variety.getGermplasmsByName(varname, getDataset());
		/*
		 * if(this.radioUseVarname.isSelected()) colVars =
		 * variety.getGermplasmsByName(varname, getDataset()); else colVars =
		 * varietyfacade.getGermplasmsByAccession(varname, getDataset());
		 */

		if (colVars.size() > 1) {
			List listvars = new ArrayList();

			Iterator<Variety> itVar = colVars.iterator();
			while (itVar.hasNext()) {
				Variety var = itVar.next();
				listvars.add(var.getName().toUpperCase() + "  [" + var.getAccession() + "]");
			}
			;

			try {
				ListboxMessageBox.show("Select variety accession", "Multiple Varieties", listvars,
						new org.zkoss.zk.ui.event.EventListener() {
							@Override
							public void onEvent(Event e) throws Exception {

								if (e.getName().equals(ListboxMessageBox.ON_OK)) {
									// objSel= getObjSel(); //listboxOptions.getSelectedItem().getValue();
									// AppContext.debug("objSel=" + objSel);
									Object selobj = ListboxMessageBox.getObjSel();
									AppContext.debug("mainselected " + selobj);
									if (selobj != null) {

										combo.setValue(selobj.toString());
									}
								}
							}
						});

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
