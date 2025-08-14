package user.ui.module.workspace;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.irri.iric.ds.chado.dao.access.OrganismDAO;
import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImplAllelePvalue;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.zk.CookieController;
import org.irri.iric.portal.zk.ListboxMessageBox;
import org.irri.iric.portal.zk.SessionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@Controller
@Scope("session")
public class CreateSNPListController extends SelectorComposer<Component> {

	CookieController cookieController = new CookieController();
	SessionController sessionController = new SessionController();

	@Autowired
	private ListItemsDAO listitemsDAO;
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
	private Checkbox checkboxSNPAlelle;
	@Wire
	private Checkbox checkboxSNPPValue;
	@Wire
	private Checkbox checkboxVerifySNP;

	@Wire
	private Listbox listboxVariantset;

	@Wire
	private Textbox txtboxEditListname;

	@Wire
	private Listbox selectChromosome;

	@Wire
	private Textbox txtboxEditNewList;

	@Wire
	Button cancelButton;

	@Wire
	private Window createWindow;

	@Autowired
	// @Qualifier("OrganismDAO")
	private OrganismDAO organismdao;
	private User user;
	private Organism organism;
	private CustomList input;
	private boolean isMsgboxEventSuccess;
	private boolean isDoneModal;

	public CreateSNPListController() {
		super();
		AppContext.debug("created WorkspaceController:" + this);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		try {
			listitemsDAO = (ListItemsDAO) AppContext.checkBean(listitemsDAO, "ListItems");
			List listVS = new ArrayList();
			listVS.addAll(listitemsDAO.getSnpsets());

			List listContigs = new ArrayList();
			listContigs.add("");
			listContigs.add("chr01");
			listContigs.add("chr02");
			listContigs.add("chr03");
			listContigs.add("chr04");
			listContigs.add("chr05");
			listContigs.add("chr06");
			listContigs.add("chr07");
			listContigs.add("chr08");
			listContigs.add("chr09");
			listContigs.add("chr10");
			listContigs.add("chr11");
			listContigs.add("chr12");
			listContigs.add("ANY");

			selectChromosome.setModel(new SimpleListModel(listContigs));
			selectChromosome.setSelectedIndex(listContigs.size() - 1);

			if (listVS.size() < 4)
				listboxVariantset.setRows(listVS.size());
			SimpleListModel listmodelvs = new SimpleListModel(listVS);
			listmodelvs.setMultiple(true);
			listboxVariantset.setModel(listmodelvs);

			checkboxSNPAlelle.setChecked(false);
			checkboxSNPPValue.setChecked(false);

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

		input = new CustomList();
		input.setListname(txtboxEditListname.getValue());
//		input.setSnpList(txtboxEditNewList.getValue());
		input.setChromosome(selectChromosome.getSelectedItem().getValue());
		input.setSnpAllele(checkboxSNPAlelle.isChecked() ? true : false);
		input.setSnpPvalue(checkboxSNPPValue.isChecked() ? true : false);
		input.setVerifySnp(checkboxVerifySNP.isChecked() ? true : false);

		onbuttonSaveSNP();

	}

	private boolean onbuttonSaveSNP() {

		organismdao = (OrganismDAO) AppContext.checkBean(organismdao, "OrganismDAO");
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		listitemsDAO = (ListItemsDAO) AppContext.checkBean(listitemsDAO, "ListItems");

		int snpCnt = 0;

		organism = organismdao.getOrganismByID(9);

		if (input.getChromosome() == null)
			return false;
		String selchr = input.getChromosome();

		boolean hasAllele = input.isSnpAllele();
		boolean hasPvalue = input.isSnpPvalue();

		if (selchr.equals("ANY")) {

			String lines[] = txtboxEditNewList.getValue().trim().split("\n");

			Map<String, Map> mapChr2Pos2Pvalue = new HashMap();
			Map<String, Map> mapChr2Pos2Allele = new HashMap();

			Map<String, Set> mapChr2Set = new TreeMap();
			StringBuilder sb = new StringBuilder();

//			try (BufferedWriter writer = new BufferedWriter(new FileWriter(snpfile, true))) {
			for (int isnp = 0; isnp < lines.length; isnp++) {
				try {

					String chrposline = lines[isnp].trim();

					if (chrposline.isEmpty())
						continue;

//						writer.write(chrposline);
//						writer.newLine();

					System.out.println("Lines successfully written to the file.");

					String chrpos[] = chrposline.split("\\s+");
					String chr = "";
					try {
						int intchr = Integer.valueOf(chrpos[0]);
						if (intchr > 9)
							chr = "chr" + intchr;
						else
							chr = "chr0" + intchr;
					} catch (Exception ex) {
						chr = chrpos[0].toLowerCase();
					}

					BigDecimal pos = null;
					try {
						pos = BigDecimal.valueOf(Long.valueOf(chrpos[1]));
					} catch (Exception ex) {
						AppContext.debug("Invalid position chrome position");
						continue;
					}

					Map<BigDecimal, String> mapPos2Allele = mapChr2Pos2Allele.get(chr);
					if (mapPos2Allele == null) {
						mapPos2Allele = new HashMap();
						mapChr2Pos2Allele.put(chr, mapPos2Allele);
					}
					Map<BigDecimal, Double> mapPos2Pvalue = mapChr2Pos2Pvalue.get(chr);
					if (mapPos2Pvalue == null) {
						mapPos2Pvalue = new HashMap();
						mapChr2Pos2Pvalue.put(chr, mapPos2Pvalue);
					}

					if (hasAllele) {
						mapPos2Allele.put(pos, chrpos[2]);
						if (hasPvalue) {
							try {
								mapPos2Pvalue.put(pos, Double.valueOf(chrpos[3]));
							} catch (Exception ex) {
								AppContext.debug("Invalid p-value " + chrpos[3]);
							}
						}
					} else if (hasPvalue) {
						try {
							mapPos2Pvalue.put(pos, Double.valueOf(chrpos[2]));
						} catch (Exception ex) {
							AppContext.debug("Invalid number " + chrpos[2]);
						}
					}

					Set setPos = mapChr2Set.get(chr);
					if (setPos == null) {
						setPos = new HashSet();
						mapChr2Set.put(chr, setPos);
					}
					setPos.add(pos);

					
					if (snpCnt> 0)
						sb.append("\n");
					sb.append(chrposline);
					
					snpCnt++;

				} catch (Exception ex) {
					AppContext.debug("onbuttonSaveSNP exception: ");
					ex.printStackTrace();
					return false;
				}

			}
//			} catch (IOException e) {
//				System.out.println("Error writing to the file: " + e.getMessage());
//			}

			Set<MultiReferencePosition> setSNPDBPos = new HashSet();

			Set setSNP = null;
			Set setChrSNP = new HashSet();
			Iterator<String> itChr = mapChr2Set.keySet().iterator();
			while (itChr.hasNext()) {
				String chr = itChr.next();
				setSNP = mapChr2Set.get(chr);

				if (hasAllele || hasPvalue) {
					if (this.checkboxVerifySNP.isChecked()) {
						Iterator<SnpsAllvarsPos> itSnpsDB = genotype.checkSNPInChromosome(
								organism.getOrganismId().intValue(), chr, setSNP, getVariantSets()).iterator();
						while (itSnpsDB.hasNext()) {
							BigDecimal ipos = itSnpsDB.next().getPosition();
							setSNPDBPos.add(new MultiReferencePositionImplAllelePvalue(organism.getName(), chr, ipos,
									(String) mapChr2Pos2Allele.get(chr).get(ipos),
									(Double) mapChr2Pos2Pvalue.get(chr).get(ipos)));
						}

					}

					Iterator<BigDecimal> itPos = setSNP.iterator();
					while (itPos.hasNext()) {
						BigDecimal ipos = itPos.next();
						setChrSNP.add(new MultiReferencePositionImplAllelePvalue(organism.getName(), chr, ipos,
								(String) mapChr2Pos2Allele.get(chr).get(ipos),
								(Double) mapChr2Pos2Pvalue.get(chr).get(ipos)));
					}

				} else {
					if (input.isVerifySnp()) {
						Iterator<SnpsAllvarsPos> itSnpsDB = genotype.checkSNPInChromosome(
								organism.getOrganismId().intValue(), chr, setSNP, getVariantSets()).iterator();
						while (itSnpsDB.hasNext()) {
							setSNPDBPos.add(new MultiReferencePositionImpl(organism.getName(), chr,
									itSnpsDB.next().getPosition()));
						}

					}
					Iterator<BigDecimal> itPos = setSNP.iterator();
					while (itPos.hasNext()) {
						setChrSNP.add(new MultiReferencePositionImpl(organism.getName(), chr, itPos.next()));
					}
				}

			}

			Messagebox.show(
					"Found " + snpCnt + " out of " + lines.length
							+ ". Are you sure you want to proceed in making the list?",
					"Confirmation", new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO },
					Messagebox.QUESTION, event -> {
						if (Messagebox.ON_YES.equals(event.getName())) {
							makeList(sb, setChrSNP, setSNPDBPos, hasAllele, hasPvalue); // Call your list-making
																						// function
						}
					});

		} else {
			Messagebox.show("Single chromosome not handled");

		}

		return true;
	}

	private void makeList(StringBuilder sb, Set setChrSNP, Set<MultiReferencePosition> setSNPDBPos, boolean hasAllele,
			boolean hasPvalue) {

		input.setSnpList(sb.toString());

		if (input.isVerifySnp())
			onbuttonSaveSNPInChr(setChrSNP, setSNPDBPos, null, hasAllele, hasPvalue);
		else
			onbuttonSaveSNPInChr(setChrSNP, null, null, hasAllele, hasPvalue);

		Events.postEvent(Events.ON_CLOSE, createWindow, input);

	}

	private Set getVariantSets() {
		Set s = new LinkedHashSet();
		for (Listitem item : listboxVariantset.getSelectedItems()) {
			s.add(item.getLabel());
		}
		return s;
	}

	private void onbuttonSaveSNPInChr(Set setSNP, Set setSNPDBPos, Set setCoreSNPDBPos, final boolean hasAllele,
			final boolean hasPvalue) {
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		if (setCoreSNPDBPos == null && setSNPDBPos != null) {
			setCoreSNPDBPos = new HashSet(setSNPDBPos);
		}

		isMsgboxEventSuccess = false;
		isDoneModal = false;

		final String chr = input.getChromosome();
		final String newlistname = input.getListname().replaceAll(":", "").trim();

		if (setSNPDBPos == null && setCoreSNPDBPos == null) {

			if (workspace.addSnpPositionList(chr, newlistname, setSNP, hasAllele, hasPvalue)) {

				AppContext.debug(newlistname + " added with " + setSNP.size() + " items");

//				listboxVarieties.setVisible(false);
//
//				buttonCreate.setVisible(true);
//				buttonDelete.setVisible(true);
//				buttonSave.setVisible(false);
//				buttonCancel.setVisible(false);
//
//				Events.sendEvent("onClick", radioSNP, null);
//				afterButtonSave(true);
			} else {
				Messagebox.show("Failed to add list", "OPERATION FAILED", Messagebox.OK, Messagebox.EXCLAMATION);
//				afterButtonSave(false);
			}
			return;
		}

		final Set setMatched = new TreeSet(setSNP);
		setMatched.retainAll(setSNPDBPos);

		if (setMatched.size() == 0) {
			Messagebox.show("No identified SNP positions", "WARNING", Messagebox.OK, Messagebox.EXCLAMATION);
//			afterButtonSave(false);
		}

		// list not in snp universe
		Set setMinus = new TreeSet(setSNP);
		setMinus.removeAll(setSNPDBPos);

		// list in snp universe not in core
		Set setMatchedNotInCore = new TreeSet(setMatched);
		setMatchedNotInCore.removeAll(setCoreSNPDBPos);

		if (setMinus.size() > 0 || setMatchedNotInCore.size() > 0) {

			if (setMatched.size() > 0) {
				StringBuffer buff = new StringBuffer();
				if (setMinus.size() > 0) {
					buff.append("Not SNP positions: " + setMinus.size() + "\n");
					Iterator itVar = setMinus.iterator();
					while (itVar.hasNext()) {
						buff.append(itVar.next());
						buff.append("\n");
					}
				}

				buff.append("SNP positions in the list: " + setMatched.size() + "\n");

				if (setMatchedNotInCore.size() > 0) {
					buff.append("SNP positions not in Core set: " + setMatchedNotInCore.size() + "\n");
					Iterator itVar = setMatchedNotInCore.iterator();
					while (itVar.hasNext()) {
						buff.append(itVar.next());
						buff.append("\n");
					}
				}

				if (newlistname.isEmpty()) {
					Messagebox.show("Provide unique list name", "INVALID VALUE", Messagebox.OK, Messagebox.EXCLAMATION);
//					afterButtonSave(false);
				}
				if (workspace.getSnpPositions(chr, newlistname) != null
						&& !workspace.getSnpPositions(chr, newlistname).isEmpty()) {
					Messagebox.show("Listname already exists", "INVALID VALUE", Messagebox.OK, Messagebox.EXCLAMATION);
//					afterButtonSave(false);
				}

//				if (this.checkboxAutoconfirm.isChecked() || setSNP.size() > 50) {
				if (setSNP.size() > 50) {
					AppContext.debug("Adding SNP list");
					if (workspace.addSnpPositionList(chr, newlistname, setMatched, hasAllele, hasPvalue)) {

						AppContext.debug(newlistname + " added with " + setMatched.size() + " items");

//						listboxVarieties.setVisible(false);
//
//						buttonCreate.setVisible(true);
//						buttonDelete.setVisible(true);
//						buttonSave.setVisible(false);
//						buttonCancel.setVisible(false);

						try {
							String tmpreportfile = AppContext.getTempDir() + "savesnplist-report-"
									+ AppContext.createTempFilename() + ".txt";
							String filetype = "text/plain";
							Filedownload.save(buff.toString(), filetype, tmpreportfile);
							org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
							AppContext.debug("snplist-report downlaod complete!" + tmpreportfile + " Downloaded to:"
									+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						Messagebox.show(
								"SNP List with " + setMatched.size() + " positions created with name" + newlistname,
								"OPERATION SUCCESFUL", Messagebox.OK, Messagebox.EXCLAMATION);

//						Events.sendEvent("onClick", radioSNP, null);
//						afterButtonSave(true);

					} else {
						Messagebox.show("Failed to add list", "OPERATION FAILED", Messagebox.OK,
								Messagebox.EXCLAMATION);
//						afterButtonSave(false);
					}

				} else {
					List listmsg = new ArrayList();
					String[] lines = buff.toString().split("\n");
					for (int iline = 0; iline < lines.length; iline++)
						listmsg.add(lines[iline]);
					try {
						ListboxMessageBox.show("Do you want to proceed?", "Create SNP List",
								Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, listmsg,
								new org.zkoss.zk.ui.event.EventListener() {
									@Override
									public void onEvent(Event e) throws Exception {

										if (e.getName().equals(Messagebox.ON_YES)) {

											AppContext.debug("Adding SNP list");

											if (workspace.addSnpPositionList(chr, newlistname, setMatched, hasAllele,
													hasPvalue)) {

												AppContext.debug(
														newlistname + " added with " + setMatched.size() + " items");

//												listboxVarieties.setVisible(false);
//												vboxEditNewList.setVisible(false);
//												buttonCreate.setVisible(true);
//												buttonDelete.setVisible(true);
//												buttonSave.setVisible(false);
//												buttonCancel.setVisible(false);
//
//												Events.sendEvent("onClick", radioSNP, null);
//												afterButtonSave(true);

											} else {
												Messagebox.show("Failed to add list", "OPERATION FAILED", Messagebox.OK,
														Messagebox.EXCLAMATION);
//												afterButtonSave(false);
											}

										} else {
//											afterButtonSave(false);
										}
									}
								});
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}

			} else {
				Messagebox.show("No identified SNP positions", "WARNING", Messagebox.OK, Messagebox.EXCLAMATION);
//				afterButtonSave(false);

			}

		} else {

			AppContext.debug("Adding SNP list");

			if (workspace.addSnpPositionList(chr, newlistname, setMatched, hasAllele, hasPvalue)) {

				AppContext.debug(newlistname + " added with " + setMatched.size() + " items");

//				listboxVarieties.setVisible(false);
//				vboxEditNewList.setVisible(false);
//				buttonCreate.setVisible(true);
//				buttonDelete.setVisible(true);
//				buttonSave.setVisible(false);
//				buttonCancel.setVisible(false);
//
//				Events.sendEvent("onClick", radioSNP, null);
//				afterButtonSave(true);

			} else {
				Messagebox.show("Failed to add list", "OPERATION FAILED", Messagebox.OK, Messagebox.EXCLAMATION);
//				afterButtonSave(false);
			}

		}

	}

}
