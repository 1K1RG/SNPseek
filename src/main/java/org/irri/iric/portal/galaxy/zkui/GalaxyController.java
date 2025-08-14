package org.irri.iric.portal.galaxy.zkui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;
//import javax.swing.JOptionPane;

//import org.codehaus.jettison.json.JSONObject;
import org.irri.iric.galaxy.service.GalaxyFacade;
import org.irri.iric.galaxy.service.MyTool;
import org.irri.iric.galaxy.service.MyWorksflow;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.EmptyInputException;
import org.irri.iric.portal.admin.JobsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.github.jmchilton.blend4j.galaxy.beans.Tool;

@Controller("GalaxyController")
@Scope(value = "session")
public class GalaxyController extends SelectorComposer<Window> {

	@Autowired
	@Qualifier("GalaxyFacade")
	private GalaxyFacade galaxy;

	private GalaxyCustomController customcontroller = new DefaultGalaxyController();
	// private GalaxyCustomController customcontroller; //=new
	// SnpseekGalaxyController();

	private Set limitWf;

	@Wire
	private Window winGalaxy;

	// @Autowired
	// @Qualifier("VarietyFacade")
	// private VarietyFacade varietyfacade;
	// @Autowired
	// @Qualifier("JobsFacade")
	// private JobsFacade jobsfacade;
	@Wire
	private Hbox hboxServer2;
	@Wire
	private Listbox listboxServer2;

	@Wire
	private Listbox listboxServer;
	@Wire
	private Listbox listData;
	@Wire
	private Listbox listFormat;
	@Wire
	private Listbox listTools;
	@Wire
	private Grid listInputs;

	@Wire
	private Listbox listSection;
	@Wire
	private Button buttonCommon;
	@Wire
	private Button buttonNone;
	@Wire
	private Button buttonAll;
	@Wire
	private Button buttonWorkflows;
	@Wire
	private Button buttonAllWorkflows;
	@Wire
	private Listbox listLists;

	@Wire
	private Button buttonDiscover;
	@Wire
	private Button buttonRun;
	@Wire
	private Div divDiscovery;
	@Wire
	private Checkbox checkboxAsync;
	// @Wire
	// private Label labelDownloadProgressMsg;
	// @Wire
	// private A aDownloadProgressURL;
	// @Wire
	// private A aGalaxyLink;
	@Wire
	private Vbox vboxProgress;
	@Wire
	private Button buttonUpload;
	@Wire
	private Fileupload uploadButton;
	@Wire

	private Listheader listheaderSection;
	@Wire
	private Label labelMatchedTools;
	@Wire
	private Iframe iframeHtmlResult;
	@Wire
	private Iframe iframeHtmlResult2;

	@Wire
	private Radiogroup radioGroupType;
	// @Wire
	// private Splitter splitter;
	@Wire
	private Checkbox checkboxShowQueryoptions;
	@Wire
	private Button buttonClearCache;

	@Wire
	private Vbox vboxIframeHtml;
	@Wire
	private boolean show_details = AppContext.isLocalhost();

	@Wire
	private Column columnParametername;
	@Wire
	private Column columnParametertype;

	@Wire
	private Listheader listheaderRequiredinput;

	@Wire
	private Listheader listheaderAllinput;

	/*
	 * public GalaxyController(GalaxyCustomController customcontroller) { super();
	 * this.customcontroller = customcontroller; }
	 */

	public GalaxyController() {
		super();
	}

	public void updateDisplay() {

	}

	@Listen("onClick =#buttonClearCache")
	public void onclearcache() {
		galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");
		galaxy.clearCache(listboxServer.getSelectedItem().getLabel());
	}

	@Listen("onSelect =#listboxServer")
	public void onselectServer() {
		try {
			if (listboxServer.getSelectedItem().getLabel().isEmpty())
				return;

			AppContext.setGalaxyInstance(listboxServer.getSelectedItem().getLabel(), customcontroller);
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick =#checkboxShowQueryoptions")
	public void onclickShow() {
		try {
			divDiscovery.setVisible(checkboxShowQueryoptions.isChecked());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onSelect =#listboxServer2")
	public void onselectServer2() {
		try {
			if (listboxServer2.getSelectedItem().getLabel().isEmpty())
				return;
			AppContext.setGalaxyInstance(listboxServer2.getSelectedItem().getLabel(), customcontroller);
			oncheckTooltype();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void selectAllListbox(Listbox l) {
		l.selectAll();
	}

	private void selectItemsListbox(Listbox l, Set selabels) {
		Set selItems = new HashSet();
		for (Listitem i : l.getItems()) {
			if (selabels.contains(i.getLabel()))
				selItems.add(i);
		}
		l.setSelectedItems(selItems);
	}

	private void unselectAllItemsListbox(Listbox l) {
		Set selItems = new HashSet();
		for (Listitem i : l.getItems()) {
			i.setSelected(false);
		}
	}

	private Set getSelectItemsListbox(Listbox l) {
		Set selItems = new HashSet();
		for (Listitem i : l.getItems()) {
			if (i.isSelected())
				selItems.add(i.getLabel());
		}
		return selItems;
	}

	@Listen("onCheck =#radioGroupType")
	public void oncheckTooltype() {
		try {
			String sel = radioGroupType.getSelectedItem().getLabel();
			if (sel.equals("Workflows")) {
				Events.sendEvent(new Event("onClick", buttonWorkflows));
				labelMatchedTools.setValue("Workflows accepting available data lists");
			} else if (sel.equals("Tools")) {
				// select all sections,
				selectAllListbox(listSection);
				// select avaiable in embed

				Set<String> setParamHasData = ((Map) getSession().getAttribute("param_vals")).keySet();
				AppContext.debug("setParamHasData=" + setParamHasData);
				galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");
				// Map<String,String> mapList2Data=galaxy.getMapList2Data();
				Map<String, Set> mapList2Data = galaxy.getMapList2Data();
				Set selparams = new HashSet();
				for (String param : setParamHasData) {
					Set selclassext = mapList2Data.get(param);
					if (selclassext != null) {
						selparams.addAll(selclassext);
					}
				}
				AppContext.debug("selecting classes,exts " + selparams);

				// listData.setSelectedItems(new HashSet());
				// listFormat.setSelectedItems(new HashSet());

				unselectAllItemsListbox(listData);
				unselectAllItemsListbox(listFormat);

				AppContext.debug("ubselecteditems data  " + getSelectItemsListbox(listData));

				selectItemsListbox(listData, selparams);
				selectItemsListbox(listFormat, selparams);

				AppContext.debug("selecteditems data  " + getSelectItemsListbox(listData));
				AppContext.debug("selected items format  " + getSelectItemsListbox(listFormat));
				AppContext.debug("selected items sections  " + getSelectItemsListbox(listSection));

				Events.sendEvent(new Event("onClick", buttonDiscover));
				labelMatchedTools.setValue("Tools accepting available data class, file exts");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object[] onclickRun_(boolean sync) throws Exception {

		// labelDownloadProgressMsg.setVisible(false);
		// aDownloadProgressURL.setVisible(false);
		// aGalaxyLink.setVisible(false);

		Object[] ret = null;
		try {
			// generatePlinkfiles();
			customcontroller.generateDataFiles();

			galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");

			int COL_VARNAME = 0;
			int COL_STEP = 1;
			int COL_PARAM = 2;
			int COL_VALUE = 3;
			int COL_TYPE = 4;

			Map<String, String[]> mapParam2Value = new HashMap();
			for (Object o : listInputs.getRows().getChildren()) {
				List rowcols = ((Row) o).getChildren();
				String param = ((Label) rowcols.get(COL_VARNAME)).getValue();

				String value = "";
				if (rowcols.get(COL_VALUE) instanceof Textbox)
					value = ((Textbox) rowcols.get(COL_VALUE)).getValue().trim();
				else if (rowcols.get(COL_VALUE) instanceof Listbox)
					value = ((Listbox) rowcols.get(COL_VALUE)).getSelectedItem().getLabel().trim();
				else if (rowcols.get(COL_VALUE) instanceof Datebox) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					value = format.format(((Datebox) rowcols.get(COL_VALUE)).getValue());
				} else if (rowcols.get(COL_VALUE) instanceof Hbox)
					value = ((Textbox) ((Hbox) rowcols.get(COL_VALUE)).getFirstChild()).getValue().trim();

				if (value.isEmpty() && !((Label) rowcols.get(COL_PARAM)).getValue().startsWith("(Optional)")) {

					Messagebox.show(((Label) rowcols.get(COL_PARAM)).getValue() + " is required", "Missing",
							Messagebox.OK, Messagebox.EXCLAMATION);
					return null;
				}

				String typestepno[] = ((Textbox) rowcols.get(COL_TYPE)).getValue().trim().split("\\:");
				String step = null;
				if (typestepno.length > 1)
					step = typestepno[1];
				mapParam2Value.put(param, new String[] { value, typestepno[0], step });
			}

			String jobid = null;

			if (listTools.getSelectedItem() == null)
				throw new EmptyInputException("Please select a workflow");

			Object[] selobj = listTools.getSelectedItem().getValue();

			List paramlist = new ArrayList();
			if (selobj[0] instanceof Tool) {
			} else if (selobj[0] instanceof MyWorksflow) {
				MyWorksflow wf = (MyWorksflow) selobj[0];

				AppContext.debug("Executing wf " + wf.getName() + " with params: ");
				for (String k : mapParam2Value.keySet()) {
					String vals[] = mapParam2Value.get(k);
					AppContext.debug(k + " --> " + vals[0] + "   " + vals[1] + "   " + vals[2]);
				}

				// labelDownloadProgressMsg.setVisible(false);
				// aDownloadProgressURL.setVisible(false);
				// aGalaxyLink.setVisible(false);

				// jobid="galaxywf-"+ wf.getId().replace(" ","_").replace("-","_") + "-" +
				// AppContext.createTempFilename();
				jobid = "galaxywf-" + wf.getName().replace(" ", "_").replace(":", "_").replace("-", "_").toLowerCase()
						+ "-" + AppContext.createTempFilename();
				if (sync) {
					AppContext.debug("running synch job " + jobid);
					AppContext.debug("Executing wf " + wf.getName() + " with params " + mapParam2Value);
					String[] status = galaxy.runWorkflow(wf, mapParam2Value, jobid);
					AppContext.debug("runWorkflow Synch result:" + status[0]);
					if (status[0].equals(JobsFacade.JOBSTATUS_DONE)) {
						// AppContext.renameFile(new File(AppContext.getTempDir()+status[1]+".zip"),
						// AppContext.getTempDir()+jobid+".zip");
						try {

							/*
							 * if( new File(AppContext.getTempDir()+jobid+".zip").exists()) {
							 * Filedownload.save(new File(AppContext.getTempDir()+jobid+".zip"),
							 * "application/zip"); } else if( new
							 * File(AppContext.getTempDir()+jobid+".txt").exists()) { Filedownload.save(new
							 * File(AppContext.getTempDir()+jobid+".txt"), "text/plain"); }
							 */

							boolean donwloaded = false;
							for (String ext : AppContext.getFilextMime().keySet()) {
								if (new File(AppContext.getTempDir() + jobid + ext).exists()) {
									Filedownload.save(new File(AppContext.getTempDir() + jobid + ext),
											AppContext.getFilextMime().get(ext));
									donwloaded = true;
									break;
								}
							}
							if (!donwloaded) {
								Messagebox.show("Unable to identify file type for job " + jobid, "Warning",
										Messagebox.OK, Messagebox.EXCLAMATION);
								if (new File(AppContext.getTempDir() + jobid).exists()) {
									Filedownload.save(new File(AppContext.getTempDir() + jobid),
											"application/octet-stream");
								}
							}

							/*
							 * this.aGalaxyLink.setLabel("Galaxy link: " +
							 * customcontroller.getGalaxyHistoryViewLink(AppContext.getGalaxyInstance(),
							 * status[1])); aGalaxyLink.setHref( customcontroller.getGalaxyHistoryViewLink(
							 * AppContext.getGalaxyInstance(), status[1])); aGalaxyLink.setVisible(true);
							 */
							addProgresslink("Job is submitted. Please monitor progress at ", status[1], null);

							/*
							 * if(status.length>2) { // has html view String
							 * urlhrml=AppContext.readURL(status[2]);
							 * urlhrml=urlhrml.substring(urlhrml.indexOf("http:"),
							 * (urlhrml.indexOf(".html")+5 )); AppContext.debug("displaying frame " +
							 * urlhrml ); iframeHtmlResult.setSrc(urlhrml);
							 * iframeHtmlResult.setVisible(true);
							 * 
							 * } else iframeHtmlResult.setVisible(false);
							 */

							if (status.length > 2) {
								// has html view

								List l = vboxIframeHtml.getChildren();
								for (Object obj : l) {
									vboxIframeHtml.removeChild((Component) obj);
								}
								for (int ihtml = 2; ihtml < status.length; ihtml++) {
									Hbox hbox20 = new Hbox();
									hbox20.setHeight("20px");
									hbox20.setParent(vboxIframeHtml);
									Iframe iframe = new Iframe();
									iframe.setWidth("100%");

									String urlhrml = AppContext.readURL(status[ihtml]);
									urlhrml = urlhrml.substring(urlhrml.indexOf("http:"),
											(urlhrml.indexOf(".html") + 5));
									AppContext.debug("displaying frame " + urlhrml);
									iframe.setSrc(urlhrml);

									iframe.setVisible(true);
									iframe.setParent(vboxIframeHtml);
								}
							} else
								vboxIframeHtml.setVisible(false);

							// iframeHtmlResult2.setVisible(false);afterco

						} catch (Exception ex) {
							ex.printStackTrace();
							try {
								Filedownload.save(new File(jobid + ".txt"), "text/plain");
							} catch (Exception ex2) {
								ex2.printStackTrace();
								Messagebox.show(ex.getMessage(), "ERROR", Messagebox.OK, Messagebox.ERROR);
							}

						}
					} else {
						Messagebox.show(status[1], status[0], Messagebox.OK, Messagebox.ERROR);
					}

					ret = status;
				} else {
					AppContext.debug("running asynch job " + jobid);
					AppContext.debug("Submitting asynch wf " + wf.getName() + " with params " + mapParam2Value);
					/*
					 * Future future=galaxy.runWorkflowAsync(wf, mapParam2Value, jobid, new
					 * File(AppContext.getTempDir()+jobid)); ret=new Object[] {jobid,future};
					 */
					String[] status = galaxy.runWorkflowAsync(wf, mapParam2Value, jobid);

					if (status[0].equals(JobsFacade.JOBSTATUS_DONE)) {
						try {

							boolean donwloaded = false;
							for (String ext : AppContext.getFilextMime().keySet()) {
								if (new File(AppContext.getTempDir() + jobid + ext).exists()) {
									Filedownload.save(new File(AppContext.getTempDir() + jobid + ext),
											AppContext.getFilextMime().get(ext));
									donwloaded = true;
									break;
								}
							}
							if (!donwloaded) {
								Messagebox.show("Unable to identify file type for job " + jobid, "Warning",
										Messagebox.OK, Messagebox.EXCLAMATION);
								if (new File(AppContext.getTempDir() + jobid).exists()) {
									Filedownload.save(new File(AppContext.getTempDir() + jobid),
											"application/octet-stream");
								}
							}
							// this.aGalaxyLink.setLabel("Galaxy link: " +
							// customcontroller.getGalaxyHistoryViewLink(AppContext.getGalaxyInstance(),
							// status[1]));
							// aGalaxyLink.setHref(
							// customcontroller.getGalaxyAddress(AppContext.getGalaxyInstance()) +
							// "/histories/view?id=" + status[1]);
							// aGalaxyLink.setVisible(true);
							addProgresslink("Job is submitted. Please monitor progress at ", status[1], null);

						} catch (Exception ex) {
							ex.printStackTrace();
							try {
								Filedownload.save(new File(jobid + ".zip"), "application/zip");
							} catch (Exception ex2) {
								ex2.printStackTrace();
							}

						}
					} else if (status[0].equals(JobsFacade.JOBSTATUS_SUBMITTED)) {
						/*
						 * this.labelDownloadProgressMsg.
						 * setValue("Job is submitted. Please monitor progress at ");
						 * this.aDownloadProgressURL.setLabel( AppContext.getHostname() +
						 * AppContext.getHostDirectory() + "_jobs.zul?jobid=" + jobid);
						 * this.aDownloadProgressURL.setHref( AppContext.getHostname() +
						 * AppContext.getHostDirectory() + "_jobs.zul?jobid=" + jobid);
						 * 
						 * this.aGalaxyLink.setLabel("Galaxy link: " +
						 * customcontroller.getGalaxyHistoryViewLink( AppContext.getGalaxyInstance(),
						 * status[1])); aGalaxyLink.setHref( customcontroller.getGalaxyHistoryViewLink(
						 * AppContext.getGalaxyInstance(), status[1])); //enableDownloadButtons(false);
						 * labelDownloadProgressMsg.setVisible(true);
						 * aDownloadProgressURL.setVisible(true); aGalaxyLink.setVisible(true);
						 */

						addProgresslink("Job is submitted. Please monitor progress at ", status[1], jobid);

					} else if (status[0].equals(JobsFacade.JOBSTATUS_ERROR)) {
						addProgresslink((String) status[0] + ": " + jobid + " " + status[1], null, null);

						Messagebox.show(status[1], status[0], Messagebox.OK, Messagebox.EXCLAMATION);

						/*
						 * aGalaxyLink.setVisible(false);
						 * this.labelDownloadProgressMsg.setValue((String)status[0] + ": " + jobid + " "
						 * + status[1]); labelDownloadProgressMsg.setVisible(true);
						 */

					} else {
						/*
						 * this.labelDownloadProgressMsg.setValue((String)status[0] + ": " + jobid);
						 * labelDownloadProgressMsg.setVisible(true);
						 * this.aGalaxyLink.setLabel("Galaxy link: " +
						 * customcontroller.getGalaxyAddress(AppContext.getGalaxyInstance()) +
						 * "/histories/view?id=" + status[1]); aGalaxyLink.setHref(
						 * customcontroller.getGalaxyHistoryViewLink(AppContext.getGalaxyInstance(),
						 * status[1])); aGalaxyLink.setVisible(true);
						 */
						addProgresslink((String) status[0] + ": " + jobid, status[1], null);
					}

				}

			}
		} catch (EmptyInputException ex) {
			ex.printStackTrace();
			Messagebox.show(ex.toString(), "ERROR", Messagebox.OK, Messagebox.EXCLAMATION);
		}

//		catch (Exception ex) {
//			ex.printStackTrace();
//			Messagebox.show(ex.getMessage(), "ERROR", Messagebox.OK, Messagebox.EXCLAMATION);
//		}
		return ret;

	}

	// @Wire
	// private Label labelDownloadProgressMsg;
	// @Wire
	// private A aDownloadProgressURL;
	// @Wire
	// private A aGalaxyLink;
	private void addProgresslink(String message, String historyid, String jobid) {
		new Label(message).setParent(vboxProgress);
		if (jobid != null) {
			A joblink = new A();
			joblink.setLabel(AppContext.getHostname() + "/_jobs.zul?jobid=" + jobid);
			joblink.setHref(AppContext.getHostname() + "/_jobs.zul?jobid=" + jobid);
			joblink.setTarget("_blank");
			joblink.setParent(vboxProgress);
		}

		if (historyid != null) {
			A link = new A();
			link.setLabel("Galaxy link: " + customcontroller.getGalaxyAddress(AppContext.getGalaxyInstance())
					+ "/histories/view?id=" + historyid);
			link.setHref(customcontroller.getGalaxyHistoryViewLink(AppContext.getGalaxyInstance(), historyid));
			link.setTarget("_blank");
			link.setParent(vboxProgress);
		}
		Hbox h = new Hbox();
		h.setHeight("15px");
		h.setParent(vboxProgress);
	}

	@Listen("onClick =#buttonRun")
	public void onclickRun() {

		try {
			if (!checkboxAsync.isChecked()) {
				// labelDownloadProgressMsg.setVisible(false);
				// aDownloadProgressURL.setVisible(false);
				onclickRun_(true);
			} else {

				onclickRun_(false);
				// labelDownloadProgressMsg.setVisible(true);
				// aDownloadProgressURL.setVisible(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof InvocationTargetException) {
				if (ex.getCause() != null) {
					if (ex.getCause() instanceof Exception) {
						((Exception) ex.getCause()).printStackTrace();
					} else {
						AppContext.debug(ex.getCause().toString());
					}
				}
			} else {

				if (ex.getCause() != null) {
					if (ex.getCause() instanceof Exception) {
						((Exception) ex.getCause()).printStackTrace();
					} else {
						AppContext.debug(ex.getCause().toString());
					}
				}
			}
		}
	}

	// public Callable<AsyncJobReport> callableGalaxyRun() {
	// return new Callable<AsyncJobReport>() {
	// @Override
	// public AsyncJobReport call() throws Exception {
	// String msg = "";
	// String jobid = "";
	// String url = "";
	// Future futureReportjob = null;
	// try {
	//
	// jobsfacade = (JobsFacade) AppContext.checkBean(jobsfacade, "JobsFacade");
	//
	// /*
	// if (params.getSubmitter() == null) {
	// msg = "Submitter ID required for long jobs.";
	// } else if (jobsfacade.checkSubmitter(params.getSubmitter())) {
	// msg = "You have a running long job. Please try again when that job is done.";
	// } else
	// */
	// if(true){
	// Object ret[]=onclickRun_(false);
	//
	// AsyncJob job = new AsyncJobImpl((String)ret[0],"galaxy_run",
	// "submitter");
	// futureReportjob = (Future)ret[1]; //
	// genotype.querydownloadGenotypeAsync(params);
	// job.setFuture(futureReportjob);
	// jobid = job.getJobId();
	// url = job.getUrl();
	// msg = jobsfacade.JOBSTATUS_SUBMITTED;
	// if (jobsfacade.addJob(job)) {
	// //Object ret[]=onclickRun_(false);
	// AppContext.debug( "Added job " + jobid);
	// } else {
	// //msg = jobsfacade.JOBSTATUS_REFUSED;
	// AppContext.debug( "Refused job " + jobid);
	// }
	// }
	//
	// AppContext.debug("callable galaxy run.. submitted");
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// // Messagebox.show("call():" + ex.getMessage());
	// msg = ex.getMessage();
	//
	// }
	// return new AsyncJobReport(jobid, msg, url, futureReportjob);
	// }
	// };
	// }
	//
	//

	@Listen("onUpload =#uploadButton")
	public void onUpload(Event event) {
		AppContext.debug(event.toString());
		try {
			org.zkoss.util.media.Media media = ((UploadEvent) event).getMedia();
			media.getStringData();
			Messagebox.show("Upload " + media.getName());

		} catch (Exception ex) {
			AppContext.debug(ex.getMessage());
			ex.getStackTrace();
			Messagebox.show("Upload failed");
		}
	}

	@Command
	public void onFileUpload(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();

		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;

			Media media = upEvent.getMedia();

			// Initialize components
			String name = media.getName();
			String format = media.getFormat();

			byte[] bFile = media.getByteData();

			AppContext.debug(String.format("File Name: %s, Format: %s", name, format));
		}
	}

	@Listen("onClick =#buttonUpload")
	public void onclickUploadLists() {

		try {
			Fileupload.get(new EventListener() {
				@Override
				public void onEvent(Event event) throws Exception {
					try {
						org.zkoss.util.media.Media media = ((UploadEvent) event).getMedia();
						media.getStringData();
						// Messagebox.show("Upload " + media.getName());
						AppContext.debug("Upload " + media.getName());

					} catch (Exception ex) {
						AppContext.debug(ex.getMessage());
						ex.getStackTrace();
						// Messagebox.show("Upload failed");
						AppContext.debug("Upload failed");
					}
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onSelect =#listTools")
	public void onselectTools() {

		// if(true) return;

		try {
			Object obj = listTools.getSelectedItem().getValue();
			// String haplofilename=(String)getSession().getAttribute("haplofilename");
			Map<String, String> mapParams = (Map) getSession().getAttribute("param_vals");

			String haplofilename = null;
			if (mapParams != null)
				haplofilename = (String) mapParams.get("filename");
			galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");
			// Map<String,String> mapList2Data=galaxy.getMapList2Data();
			Map<String, Set> mapList2Data = galaxy.getMapList2Data();

			AppContext.debug("mapList2Data=" + mapList2Data);
			AppContext.debug("mapParams=" + mapParams);

			List paramlist = new ArrayList();
			if (obj instanceof MyTool) {
				AppContext.debug("Tool instanceof MyTool");

				MyTool mt = (MyTool) obj;
				((Listheader) listTools.getListhead().getFirstChild()).setLabel("Tool name");
				// for(String p:mt.getParamNames()) {
				for (String p : mt.getAllParams()) {
					String pprops = p;
					AppContext.debug("p=" + p);

					p = p.split("\\:")[0];
					String paramval = mt.getParamValue(p);
					// set param value if pembed contains param class
					if (mapParams != null) {
						for (String pembed : mapParams.keySet()) {
							AppContext.debug("pembed=" + pembed);

							Set setParamClassext = new HashSet(mt.getMapParam2ClassExt(p));
							if (!mapList2Data.containsKey(pembed))
								continue;

							AppContext.debug("pembed=" + pembed + ",  mapList2Data.get(pembed)"
									+ mapList2Data.get(pembed) + "\n" + "setParamClassext=" + setParamClassext);

							Set setEmbedClassext = new HashSet(mapList2Data.get(pembed));
							setParamClassext.retainAll(setEmbedClassext);
							if (setParamClassext.size() > 0) {
								paramval = (String) mapParams.get(pembed);
								AppContext.debug("auto-assigned " + p + " = " + paramval + ",  matched classext="
										+ setParamClassext);
							}
						}
					}
					// paramlist.add(new String[] {p,paramval});
					paramlist.add(new String[] { pprops, paramval });

				}
			} else if (obj instanceof Object[]) {

				AppContext.debug("Tool instanceof Object[]");
				Object[] selobj = (Object[]) obj; // new Object[] {t, mapTools2Params.get(t), mapTool2Terms.get(t)});
				((Listheader) listTools.getListhead().getFirstChild()).setLabel("Workflow name");
				for (String p : (Set<String>) selobj[1]) {
					AppContext.debug("p=" + p);
					String paramval = "";
					if (mapParams != null) {
						// set param value if param name starts with pembed
						for (String pembed : mapParams.keySet()) {
							AppContext.debug("pembed=" + pembed);
							if (p.startsWith(pembed)) {
								paramval = (String) mapParams.get(pembed);
								AppContext.debug("auto-assigned " + p + " = " + paramval);
							}
						}
					}
					if (paramval == null)
						paramval = "";

					if (haplofilename != null) {
						if (p.startsWith("locuslist"))
							paramlist.add(new String[] { p, haplofilename + ".bed" });
						else if (p.startsWith("snpmatrix"))
							paramlist.add(new String[] { p, haplofilename + ".ped" });
						else if (p.startsWith("snplist"))
							paramlist.add(new String[] { p, haplofilename + ".map" });
						else if (p.startsWith("samplelist"))
							paramlist.add(new String[] { p, haplofilename + ".txt" });
						else if (p.startsWith("phenolist"))
							paramlist.add(new String[] { p, haplofilename + ".pheno" });
						else
							paramlist.add(new String[] { p, paramval });
					} else
						paramlist.add(new String[] { p, paramval });

					/*
					 * if(haplofilename!=null) { if(p.startsWith("locuslist")) paramlist.add(new
					 * String[] {p,AppContext.getTempDir()+haplofilename+".bed"}); else
					 * if(p.startsWith("snpmatrix")) paramlist.add(new String[]
					 * {p,AppContext.getTempDir()+haplofilename+".ped"}); else
					 * if(p.startsWith("snplist")) paramlist.add(new String[]
					 * {p,AppContext.getTempDir()+haplofilename+".map"}); else
					 * if(p.startsWith("samplelist")) paramlist.add(new String[]
					 * {p,AppContext.getTempDir()+haplofilename+".txt"}); } else { String
					 * paramval=(String)getSession().getAttribute(p); if(paramval==null)
					 * paramval=""; paramlist.add(new String[] {p,paramval}); }
					 */
				}
			} else if (obj instanceof Object[]) {
				AppContext.debug("Tool instanceof " + obj.getClass());
			}
			listInputs.setRowRenderer(new InputParamsRowRenderer());

			// sort by first element of paramlist
			/*
			 * Set sortedparam=new TreeSet(new InputParamsComparator());
			 * sortedparam.addAll(paramlist ); paramlist.clear();
			 * paramlist.addAll(sortedparam );
			 */
			listInputs.setModel(new ListModelList(paramlist));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	class InputParamsComparator implements Comparator<String[]> {

		@Override
		public int compare(String[] o1, String[] o2) {
			// TODO Auto-generated method stub
			String s1 = o1[0];
			if (s1.charAt(1) == '#')
				s1 = "0" + s1;
			String s2 = o2[0];
			if (s2.charAt(1) == '#')
				s2 = "0" + s2;

			return s1.compareTo(s2);
		}

	}

	@Listen("onClick =#buttonCommon")
	public void onclickCommon() {
		try {
			Set sC = customcontroller.getCommonClass();
			Set<Listitem> selItems = new HashSet();
			for (Listitem i : listData.getItems()) {
				if (sC.contains(i.getLabel())) {
					selItems.add(i);
				}
			}
			listData.setSelectedItems(selItems);

			Set sC2 = customcontroller.getCommonExt();
			Set<Listitem> selItems2 = new HashSet();
			for (Listitem i : listFormat.getItems()) {
				if (sC2.contains(i.getLabel())) {
					selItems2.add(i);
				}
			}
			listFormat.setSelectedItems(selItems2);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onClick =#buttonNone")
	public void onclickNone() {

	}

	@Listen("onClick =#buttonAll")
	public void onclickAll() {

	}

	@Listen("onClick =#buttonAllWorkflows")
	public void onclickAllWorkflows() {
		AppContext.debug("onClick = buttonAllWorkflows");
		onclickWorkflows_(null);
		Map<String, String> mapParams = new HashMap();
		mapParams.put("filename", "all-wf");
		getSession().setAttribute("param_vals", mapParams);
	}

	@Listen("onClick =#buttonWorkflows")
	public void onclickWorkflows() {
		((Listheader) listTools.getListhead().getFirstChild()).setLabel("Workflow name");
		listheaderSection.setVisible(false);
		labelMatchedTools.setValue("Workflows accepting selected data lists");
		galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");
		Set sList = new HashSet();
		for (Listitem i : listLists.getSelectedItems()) {
			sList.add(i.getLabel());
		}
		onclickWorkflows_(sList);
	}

	public void onclickWorkflows_(Set sList) {

		try {
			AppContext.debug("galaxy.onclickWorkflows_(sName, null, sSection)");
			Map[] m = galaxy.discoverWorkflows(null, limitWf);

			// Map[] m= new Map[] {mapName2Wf, mapWf2Params, mapWfname2Wf};

			Map<String, Set<MyWorksflow>> mapTerm2Wf = m[0];

			Map<MyWorksflow, Set<String>> mapTool2Terms = new HashMap();
			for (String name : mapTerm2Wf.keySet()) {
				for (MyWorksflow t : mapTerm2Wf.get(name)) {
					Set terms = mapTool2Terms.get(t);
					if (terms == null) {
						terms = new HashSet();
						mapTool2Terms.put(t, terms);
					}
					terms.add(name);
				}
			}

			// add free parameters as inputs
			Map<MyWorksflow, Set<String>> mapWf2Param = m[1];
			for (MyWorksflow t : mapWf2Param.keySet()) {
				Set terms = mapTool2Terms.get(t);
				if (terms == null) {
					terms = new HashSet();
					mapTool2Terms.put(t, terms);
				}
				terms.addAll(mapWf2Param.get(t));
			}

			Set setAvailInput = sList;
			Map mapTools2Params = m[1];
			List<Object[]> lTools = new ArrayList();
			for (MyWorksflow t : mapTool2Terms.keySet()) {

				Collection allinputs = (Collection) mapTool2Terms.get(t);

				// ppContext.debug("render " + t.getName() + " " + required + " " +
				// setAvailInput);

				Set matchedInputs = new HashSet();
				Set requiredNoColumn = new HashSet();
				for (Object r : allinputs) {
					String inputfields[] = ((String) r).split("\\:");
					if (inputfields.length > 5 && inputfields[5].equals("True")) {
						AppContext.debug("skipping optional " + r);
						continue;
					}
					if (((String) r).contains(":")) {
						AppContext.debug("skipping input " + r);
						continue;
					}
					// if(phenolist_sample:data_input:::2:
					requiredNoColumn.add(r);
					if (setAvailInput != null && setAvailInput.contains(inputfields[0].split("_")[0])) {
						matchedInputs.add(r);
					}
				}

				AppContext.debug("render " + t.getName() + "  " + matchedInputs + "   " + requiredNoColumn + "  "
						+ setAvailInput);
				if (setAvailInput != null && matchedInputs.size() < requiredNoColumn.size()) {
					AppContext.debug("skipping tool " + t.getName());
					continue;
				}

				// lTools.add( new Object[] {t, requiredNoColumn, mapTool2Terms.get(t)});
				lTools.add(new Object[] { t, mapTools2Params.get(t), mapTool2Terms.get(t) });
			}

			if (sList == null)
				labelMatchedTools.setValue("All Workflows");
			else
				labelMatchedTools.setValue("Workflows accepting data lists:" + sList);

			AppContext.debug("mapTool2Terms=" + mapTool2Terms.keySet());

			listTools.setItemRenderer(new MatchedWorkflowRenderer(null, show_details));
			listTools.setModel(new SimpleListModel(lTools));
			// display details of tools

			listInputs.setModel(new SimpleListModel(new ArrayList()));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick =#buttonDiscover")
	public void onclickDiscover() {
		((Listheader) listTools.getListhead().getFirstChild()).setLabel("Tool name");
		listheaderSection.setVisible(true);
		labelMatchedTools.setValue("Tools accepting selected sections, data and formats");

		try {
			galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");

			AppContext.debug("galaxy=" + galaxy);

			// Set setAvailDatatypes = (Set)getSession().getAttribute("datatypes");

			// discover workflows/tools that consumes this datatypes,
			// workflows are ranked based on number of types it consumes

			/*
			 * Set<String> sName=new HashSet<String>(); sName.add("locuslist");
			 * sName.add("samplelist");
			 * 
			 * Set<String> sSection=new HashSet<String> (); sSection.add("SNP-Seek Tools");
			 * sSection.add("Clustering");
			 */

			Set sSection = new HashSet();
			for (Listitem i : listSection.getSelectedItems()) {
				sSection.add(i.getLabel());
			}

			Set sClass = new HashSet();
			for (Listitem i : listData.getSelectedItems()) {
				sClass.add(i.getLabel());
			}
			Set sFormat = new HashSet();
			for (Listitem i : listFormat.getSelectedItems()) {
				sFormat.add(i.getLabel());
			}
			if (sSection.isEmpty()) {

				sSection = null;
			}

			// AppContext.debug("galaxy.discoverWorkflows(sName, null)");
			// galaxy.discoverWorkflows(sName);

			AppContext.debug("galaxy.discoverTools(sName, null, sSection)");
			Map mapst[] = galaxy.discoverTools(null, sClass, sFormat, sSection);
			Map<String, Set<MyTool>> mapName2Tools = mapst[2];
			Map<String, Set<MyTool>> mapData2Tools = mapst[0];
			Map<MyTool, Set<String>> mapTools2Params = mapst[1];

			// rank tools by number of matching terms
			Map<MyTool, Set<String>> mapTool2Terms = new HashMap();
			for (String name : mapName2Tools.keySet()) {
				for (MyTool t : mapName2Tools.get(name)) {
					Set terms = mapTool2Terms.get(t);
					if (terms == null) {
						terms = new HashSet();
						mapTool2Terms.put(t, terms);
					}
					terms.add(name);
				}
			}
			Map<MyTool, Set<String>> mapTool2Data = new HashMap();
			for (String name : mapData2Tools.keySet()) {
				for (MyTool t : mapData2Tools.get(name)) {
					Set terms = mapTool2Data.get(t);
					if (terms == null) {
						terms = new HashSet();
						mapTool2Data.put(t, terms);
					}
					terms.add(name);
				}
			}

			Set addedtools = new HashSet();
			List<Object[]> lTools = new ArrayList();
			int addterms = 0;
			int adddata = 0;
			mapTool2Terms.remove(null);
			for (MyTool t : mapTool2Terms.keySet()) {
				lTools.add(new Object[] { t, mapTools2Params.get(t), mapTool2Terms.get(t) });
				addedtools.add(t);
				addterms++;
			}

			mapTool2Data.remove(null);
			for (MyTool t : mapTool2Data.keySet()) {
				if (addedtools.contains(t))
					continue;
				lTools.add(new Object[] { t, mapTools2Params.get(t), mapTool2Data.get(t) });
				addedtools.add(t);
				adddata++;
			}

			/*
			 * mapTools2Params.remove(null); for(JSONObject t:mapTools2Params.keySet()) {
			 * if(addedtools.contains(t)) continue; lTools.add( new Object[] {t,
			 * mapTools2Params.get(t), mapTool2Terms.get(t)}); addparams++; }
			 */

			System.out.println("tools match by terms =" + addterms + ", by data=" + adddata);

			listTools.setItemRenderer(new MatchedToolsRenderer(new HashSet(lTools)));
			listTools.setModel(new SimpleListModel(lTools));
			// display details of tools

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}

	private void init() {
		try {
			galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");

			String embed = Executions.getCurrent().getParameter("embed");

			if (embed == null || embed.isEmpty()) {
				listTools.setModel(new ListModelList(new ArrayList()));
				listInputs.setModel(new ListModelList(new ArrayList()));
				hboxServer2.setVisible(false);
				// splitter.setOpen(true);

			} else {
				// splitter.setOpen(false);
			}

			if (true) {
				// mapData2Tools,mapTools2Params,mapName2Tools,mapExt2Tools,
				// mapSectionid2Sectonname

				Map[] maps = galaxy.discoverTools();

				List l = new ArrayList();
				l.addAll(new TreeSet(maps[0].keySet()));
				ListModelList m = new ListModelList(new ArrayList(l));
				m.setMultiple(true);
				listData.setModel(m);

				l = new ArrayList();
				l.addAll(new TreeSet(maps[3].keySet()));
				m = new ListModelList(new ArrayList(l));
				m.setMultiple(true);
				listFormat.setModel(m);

				l = new ArrayList();
				l.addAll(new TreeSet(maps[4].values()));
				l.remove(null);
				// l.remove("");
				l.remove("None");
				if (l.contains("")) {
					// l.remove("");
					// l.add("NONE");
				}

				m = new ListModelList(new ArrayList(l));
				m.setMultiple(true);
				listSection.setModel(m);

				/*
				 * ListModelList m=new ListModelList( new ArrayList(galaxy.getDatatypes()));
				 * m.setMultiple(true); listData.setModel(m);
				 * 
				 * List setExt=galaxy.getFileexts(); setExt.remove("");
				 * setExt.remove("_export.txt.gz"); setExt.remove("None");
				 * 
				 * m=new ListModelList( new ArrayList(setExt)); m.setMultiple(true);
				 * listFormat.setModel(m); m=new ListModelList( new
				 * ArrayList(galaxy.getSections())); m.setMultiple(true);
				 * listSection.setModel(m);
				 */
			}

			Set setList = customcontroller.checkEmbedList(embed);

			/*
			 * if(embed!=null && embed.equals("genotype")) {
			 * 
			 * Set setList=new HashSet();
			 * 
			 * setList.add("snplist"); setList.add("snpmatrix"); setList.add("samplelist");
			 * setList.add("locuslist");
			 * 
			 * Map mapParamvals=(Map)getSession().getValue( "param_vals");
			 * if(mapParamvals!=null && mapParamvals.get("sample2pheno")!=null) {
			 * setList.add("phenolist"); };
			 * 
			 * Set setInclude=new HashSet(); for(Listitem i:listLists.getItems()) {
			 * if(setList.contains(i.getLabel())) setInclude.add(i); };
			 * 
			 * try { listLists.setSelectedItems(setInclude); Events.sendEvent(new
			 * Event("onClick", buttonWorkflows));
			 * labelMatchedTools.setValue("Workflows accepting available data lists");
			 * 
			 * } catch(Exception e) { e.printStackTrace(); } divDiscovery.setVisible(false);
			 * }
			 */
			if (setList != null) {

				Set setInclude = new HashSet();
				for (Listitem i : listLists.getItems()) {
					if (setList.contains(i.getLabel()))
						setInclude.add(i);
				}
				;

				try {
					listLists.setSelectedItems(setInclude);
					Events.sendEvent(new Event("onClick", buttonWorkflows));
					labelMatchedTools.setValue("Workflows accepting available data lists");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {

		try {
			// show_details=false;//AppContext.isLocalhost();
			List l = customcontroller.getGalaxyInstances();
			AppContext.setGalaxyInstance((String) l.get(0), customcontroller);

			super.doAfterCompose(comp);
			listboxServer.setModel(new SimpleListModel(l));
			listboxServer.setSelectedIndex(0);
			listboxServer2.setModel(new SimpleListModel(l));
			listboxServer2.setSelectedIndex(0);

			Map<String, String[]> mapParams = Executions.getCurrent().getParameterMap();
			String[] wf = mapParams.get("workflow");
			if (wf != null) {
				limitWf = new HashSet();
				for (int iw = 0; iw < wf.length; iw++) {
					limitWf.add(wf[iw]);
				}
			}

			galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");
			if (mapParams.get("reset") != null) {
				galaxy.clearCache(null);
			}

			init();

			divDiscovery.setVisible(mapParams.get("discover") != null);
			showDetails(mapParams.get("details") != null);

			// showDetails(AppContext.isLocalhost());

			winGalaxy.getPage().addEventListener(Events.ON_DESKTOP_RECYCLE, new EventListener() {
				@Override
				public void onEvent(Event event) throws Exception {
					// TODO Auto-generated method stub
					AppContext.debug("winGalaxy.getPage Events.ON_DESKTOP_RECYCLE,");
					String[] refresh = mapParams.get("refresh");
					if (refresh != null) {
						Executions.sendRedirect(null);
					}
				}
			});

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	void showDetails(boolean show) {
		columnParametername.setVisible(show);
		columnParametertype.setVisible(show);
		hboxServer2.setVisible(show);
		listheaderSection.setVisible(show);
		listheaderRequiredinput.setVisible(show);
		listheaderAllinput.setVisible(show);
	}

}