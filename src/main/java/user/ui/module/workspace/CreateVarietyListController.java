package user.ui.module.workspace;

import java.math.BigDecimal;
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

import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.zk.CookieController;
import org.irri.iric.portal.zk.SessionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import user.ui.module.VarietyList;

@Controller
@Scope("session")
public class CreateVarietyListController extends SelectorComposer<Component> {

	CookieController cookieController = new CookieController();
	SessionController sessionController = new SessionController();

	private boolean isMsgboxEventSuccess = false;
	private boolean isDoneModal = false;

	@Autowired
	private ListItemsDAO listitemsdao;
	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;
	@Autowired
	private VarietyFacade variety;
	@Autowired
	private GenotypeFacade genotype;
	@Autowired
	private GenomicsFacade genomics;

	@Wire
	private Checkbox checkboxSavedata;

	@Wire
	private Checkbox checkboxAutoconfirm;
	@Wire
	private Checkbox checkboxVerifySNP;

	@Wire
	private Label labelMsgSNP;

	@Wire
	private Radiogroup rgPhenotype;
	@Wire
	private Listheader listheaderPosition;

	@Wire
	private Listbox listboxListnames;
	@Wire
	private Listbox listboxVarieties;
	@Wire
	private Listheader listheaderPhenotype;

	@Wire
	private Listbox listboxPositions;
	@Wire
	private Listbox listboxLocus;
	@Wire
	private Button buttonQueryIric;
	@Wire
	private Button buttonCreate;
	@Wire
	private Button buttonSave;
	@Wire
	private Button buttonCancel;
	@Wire
	private Button buttonDelete;
	@Wire
	private Vbox vboxEditNewList;
	@Wire
	private Textbox txtboxEditNewList;
	@Wire
	private Textbox txtboxEditListname;
	@Wire
	private Button buttonDownload;

	@Wire
	private Button buttonUpload;

	@Wire
	private Radio radioVariety;
	@Wire
	private Radio radioSNP;
	@Wire
	private Radio radioLocus;

	@Wire
	private Radio searchWhiteSpace;
	@Wire
	private Listbox selectChromosome;
	@Wire
	private Div divMsgVariety;
	@Wire
	private Div divMsgSNP;

	@Wire
	private Div divMsgLocus;
	@Wire
	private Label labelNItems;

	@Wire
	private Div divSetOps;
	@Wire
	private Button buttonUnion;
	@Wire
	private Button buttonIntersect;
	@Wire
	private Button buttonAminusB;
	@Wire
	private Button buttonBminusA;
	@Wire
	private Textbox textboxResultSet;

	@Wire
	private Vbox vboxListMembers;

	@Wire
	private Textbox textboxFrom;

	@Wire
	private Checkbox checkboxSNPAlelle;
	@Wire
	private Checkbox checkboxSNPPValue;
	@Wire
	private Label labelMsgFormat;
	@Wire
	private Div divSNPMoreData;

	@Wire
	private Hbox hboxDataset;

	@Wire
	private Label labelVarietyFormat;

	@Wire
	private Div divHasPhenotype;

	@Wire
	private Radio radioQuantitative;
	@Wire
	private Radio radioCategorical;
	@Wire
	private Radio radioNoPhenotype;
	@Wire
	private Listbox listboxVariantset;
	@Wire
	private Listbox listboxDataset;
	@Wire
	private Bandbox bandboxVarietyset;

	@Wire
	Button cancelButton;

	@Wire
	private Window createWindow;
	private Textbox textboxPhenotypename;
	private HashSet<String> lst_dataset;
	private Set<Variety> varset;
	private boolean displayListboxVarietySet;

	public CreateVarietyListController() {
		super();
		AppContext.debug("created WorkspaceController:" + this);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		try {

			lst_dataset = new HashSet<String>();

			displayListboxVarietySet = false;

			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
			List listDatasets = variety.getDatasets();

			SimpleListModel m = new SimpleListModel(listDatasets);
			m.setMultiple(true);
			listboxDataset.setModel(m);
			listboxDataset.setSelectedIndex(0);

			AppContext.debug("doAfterCompose ..done");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick =#cancelButton")
	public void cancelbutton() {
		Events.postEvent(Events.ON_CLOSE, createWindow, null);

	}

	@Listen("onClick =#buttonSave")
	public void createbutton() {

		displayListboxVarietySet = false;

		varset = null;
		if (txtboxEditListname.getValue().trim().isEmpty()) {
			Messagebox.show("Please provide a unique list name");
			return;
		}

		lst_dataset = new HashSet<String>();
		for (Listitem item : listboxDataset.getSelectedItems()) {
			lst_dataset.add(item.getValue());
		}

		if (onbuttonSaveVariety()) {

			CustomList userList = new CustomList();
			userList.setListname(txtboxEditListname.getValue());
			userList.setListboxVarietySetVisible(displayListboxVarietySet);
			userList.setLst_dataset(lst_dataset);
			userList.setPhenotype(rgPhenotype.getSelectedIndex());
			userList.setVarietyList(txtboxEditNewList.getValue());
			userList.setListboxVarietySetVisible(displayListboxVarietySet);
			userList.setVarietySets(varset);
			Events.postEvent(Events.ON_CLOSE, createWindow, userList);
		}

	}

	private boolean onbuttonSaveVariety() {

		isMsgboxEventSuccess = false;
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		List listNoMatch = new ArrayList();
		final Set setMatched = new TreeSet();
		String lines[] = txtboxEditNewList.getValue().trim().split("\n");
		Map<String, Object[]> mapVar2Phen = new HashMap();
		Set setNames = new HashSet();
		Set lst_var = new HashSet();

		Set setPhennames = new LinkedHashSet();
		System.out.println(rgPhenotype.getSelectedIndex());
		int intPhenotype = Integer.valueOf(rgPhenotype.getSelectedIndex());

		if (intPhenotype != 0) {
			String phennames[] = textboxPhenotypename.getValue().trim().split(",");
			for (int ip = 0; ip < phennames.length; ip++) {
				setPhennames.add(phennames[ip].trim());
			}
			if (phennames.length != setPhennames.size()) {
				Messagebox.show("There are duplicates in phenotype names");
				return false;
			}
		}

		for (int i = 0; i < lines.length; i++) {
			Variety var = null;
			String varstr = null;
			Double phenvalue[] = null;
			if (lines[i].trim().isEmpty())
				continue;
			if (intPhenotype == 0)
				varstr = lines[i].trim().toUpperCase();
			else {
				String varstrs[] = lines[i].trim().split(",");
				varstr = varstrs[0].trim().toUpperCase();
				if (intPhenotype == 1) {
					Double dvalue[] = new Double[setPhennames.size()];
					for (int ip = 0; ip < setPhennames.size(); ip++) {
						try {
							String strval = varstrs[ip + 1].trim();
							if (strval.isEmpty())
								continue;
							try {
								dvalue[ip] = Double.valueOf(strval);
							} catch (Exception ex) {
								ex.printStackTrace();
								Messagebox.show("Invalid quantitative value " + strval + " in variety " + varstr);
								return false;
							}
						} catch (Exception ex1) {
							ex1.printStackTrace();
							Messagebox.show("Number of columns did not match for variety " + varstr);
							return false;
						}
					}
					mapVar2Phen.put(varstr, dvalue);
				} else if (intPhenotype == 2) {
					String svalue[] = new String[setPhennames.size()];
					for (int ip = 0; ip < setPhennames.size(); ip++) {
						try {
							String strval = varstrs[ip + 1].trim();
							if (strval.isEmpty())
								continue;
							svalue[ip] = strval;
						} catch (Exception ex1) {
							ex1.printStackTrace();
							Messagebox.show("Number of columns did not match for variety " + varstr);
							return false;
						}
					}
					mapVar2Phen.put(varstr, svalue);
				}

			}
			if (varstr.isEmpty()) {
				if (intPhenotype > 0) {
					Messagebox.show("Blank variety name encountered");
					return false;
				} else
					continue;
			}

			setNames.add(varstr);

		}

		Set<Variety> varset;

		if (searchWhiteSpace.isSelected())
			varset = new HashSet(variety.getGermplasmByNamesWithSpace(setNames, lst_dataset));
		else
			varset = new HashSet(variety.getGermplasmByNames(setNames, lst_dataset));
		
		if (varset.size() < setNames.size())
			varset.addAll(variety.getGermplasmByIrisIds(setNames, lst_dataset));
		if (varset.size() < setNames.size())
			varset.addAll(variety.getGermplasmsByAccession(setNames, lst_dataset));

		if (varset.size() == 0) {
			Messagebox.show("No identified varieties", "WARNING", Messagebox.OK, Messagebox.EXCLAMATION);
			return false;
		}

		if (varset.size() < setNames.size()) {

			Set<String> setTerms = new HashSet();
			for (Variety var : varset) {
				setTerms.add(var.getName());
				setTerms.add(var.getAccession());
				setTerms.add(var.getIrisId());
				setTerms.add(var.getIrisId().replace("IRIS_", "").replace("IRIS", "").trim());
			}
			Set<String> setNamesQuery = new HashSet(setNames);
			setNamesQuery.removeAll(setTerms);
			StringBuffer buffNot = new StringBuffer();
			buffNot.append("Check ");
			Iterator<String> itvar = setNamesQuery.iterator();
			while (itvar.hasNext()) {
				String varl = itvar.next();
				buffNot.append(varl);
				if (itvar.hasNext())
					buffNot.append(",");
			}

			Messagebox.show(
					"Only " + varset.size() + " of " + setNames.size() + " variety names are recognized:" + buffNot,
					"WARNING", Messagebox.OK, Messagebox.EXCLAMATION);
			return false;
		}

		this.varset = varset;
//		input.setVarietySets(varset);

		Map<BigDecimal, Variety> mapVarid2var = new HashMap();
		Iterator<Variety> itvar = varset.iterator();
		while (itvar.hasNext()) {
			Variety var = itvar.next();
			mapVarid2var.put(var.getVarietyId(), var);
		}

		Set setRemovevar = new HashSet();
		Map<BigDecimal, Object[]> mapVarid2Phen = new HashMap();
		if (intPhenotype > 0) {
			Iterator<String> itvarstr = mapVar2Phen.keySet().iterator();
			while (itvarstr.hasNext()) {
				String varstr = itvarstr.next();
				BigDecimal varid = AppContext.getVarunique2Id(varstr, true, true, true, mapVarid2var);

				Variety var = mapVarid2var.get(varid);
				if (var == null) {
					Collection colvar = variety.getGermplasmByName(varstr, lst_dataset);
					if (colvar == null || colvar.isEmpty()) {
						var = variety.getGermplasmByIrisId(varstr, lst_dataset);
						if (var == null)
							var = variety.getGermplasmByAccession(varstr, lst_dataset);
						if (var == null) {
							AppContext.debug(varstr + " not found");
							continue;
						}
						varid = var.getVarietyId();
						mapVarid2Phen.put(varid, mapVar2Phen.get(varstr));
					} else {
						Iterator itvar2 = colvar.iterator();
						while (itvar2.hasNext()) {
							Variety var2 = (Variety) itvar2.next();
							mapVarid2Phen.put(var2.getVarietyId(), mapVar2Phen.get(varstr));
						}
					}

				} else {
					mapVarid2Phen.put(varid, mapVar2Phen.get(varstr));
				}

			}
			List lPhennames = new ArrayList();
			lPhennames.addAll(setPhennames);
			addVarlist(varset, intPhenotype, lPhennames, mapVarid2Phen);
			return true;
		} else {
			addVarlist(varset);
			return true;
		}

	}

	private void addVarlist(Set setMatched) {
		addVarlist(setMatched, 0, null, null);
	}

	private void addVarlist(Set setMatched, int hasPhenotype, List phennames, Map<BigDecimal, Object[]> mapVarid2Phen) {
		if (setMatched.size() > 0) {

			AppContext.debug("Adding variety list");

			if (txtboxEditListname.getValue().trim().isEmpty()) {
				Messagebox.show("Provide unique list name", "INVALID VALUE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}
			if (workspace.getVarieties(txtboxEditListname.getValue().trim()) != null
					&& !workspace.getVarieties(txtboxEditListname.getValue().trim()).isEmpty()) {
				Messagebox.show("Listname already exists", "INVALID VALUE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}
			if (mapVarid2Phen != null && setMatched.size() != mapVarid2Phen.size()) {
				Messagebox.show(
						"Variety size not equal to phenotype size " + setMatched.size() + ", " + mapVarid2Phen.size(),
						"INVALID ENTRIES", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}
			if (workspace.addVarietyList(txtboxEditListname.getValue().trim(), setMatched, lst_dataset, hasPhenotype,
					phennames, mapVarid2Phen)) {

				AppContext.debug(txtboxEditListname.getValue().trim() + " added with " + setMatched.size() + " items");

				// listboxVarieties.setVisible(true);

				displayListboxVarietySet = true;

//				Events.sendEvent("onClick", radioVariety, null);

			} else {
				Messagebox.show("Failed to add list", "OPERATION FAILED", Messagebox.OK, Messagebox.EXCLAMATION);
			}

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
		bandboxVarietyset.setValue(str);
	}

	@Listen("onOpen = #bandboxVarietyset")
	public void onOpencheckboxdroplistGenotyperun(OpenEvent e) throws InterruptedException {
		AppContext.debug("e.isOpen()=" + e.isOpen() + " text=" + bandboxVarietyset.getText() + "  value="
				+ bandboxVarietyset.getValue());
		if (e.isOpen()) {
			setVarietyset(getDataset());
			return;
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

	}
}
