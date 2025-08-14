package org.irri.iric.galaxy.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
//import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.AsyncJob;
import org.irri.iric.portal.admin.AsyncJobImpl;
import org.irri.iric.portal.admin.JobsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
//import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;

//import com.github.jmchilton.blend4j.galaxy.DefaultWebResourceFactoryImpl;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.MyGalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient.FileUploadRequest;
//import com.github.jmchilton.blend4j.galaxy.WebResourceFactory;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.Job;
import com.github.jmchilton.blend4j.galaxy.beans.OutputDataset;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolExecution;
import com.github.jmchilton.blend4j.galaxy.beans.ToolInputs;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowStepDefinition;
import com.sun.jersey.api.client.ClientResponse;

@Service("GalaxyFacade")
// @EnableAsync
public class GalaxyFacadeImpl implements GalaxyFacade {

	// private static PythonInterpreter interp=null;
	@Autowired
	@Qualifier("GalaxyJobsFacade")
	private JobsFacade jobsfacade;
	private static Map mapList2Ext = null;
	private static Map mapList2Class = null;

	private static Map<String, Map[]> mapServer2Wf = new HashMap();
	private static Map<String, Set> mapServer2WfSubwf = new HashMap();
	private static Map<String, Map[]> mapServer2Tools = new HashMap();
	private static Map mapToolParamLabel2Value = new HashMap();
	private static Map mapParamToolid2CondParam = new HashMap();
	private static Map mapWfStep2Toolstate = new LinkedHashMap();
	private static Map<String, Map<String, Map>> mapServer2Wf2New2OrigLabel = new HashMap();
	// private static Map<String,Map<String,Map>> mapServerWfname2Id=new HashMap();

	public static Object exists(Collection c, String id) {
		for (Object co : c) {
			if (((History) co).getName().equals(id)) {
				return co;
			}
		}
		return null;
	}

	@Override
	public void clearCache(String server) {
		// TODO Auto-generated method stub
		if (server == null || server.isEmpty()) {
			mapServer2Wf.clear();
			mapServer2WfSubwf.clear();
			mapServer2Tools.clear();
			mapServer2Wf2New2OrigLabel.clear();
			// mapServerWfname2Id.clear();
		} else {
			mapServer2Wf.remove(server);
			mapServer2WfSubwf.remove(server);
			mapServer2Tools.remove(server);
			mapServer2Wf2New2OrigLabel.remove(server);
			// mapServerWfname2Id.remove(server);

		}
		AppContext.debug("mapServer2Wf=" + mapServer2Wf);

		mapWfStep2Toolstate.clear();

	}

	/**
	 * Search for workflows that accepts varlist, snplist or locuslist
	 * (tabular,text)
	 * 
	 * snplist or locuslist (gff,BED)
	 * 
	 * genotype matrix (VCF,Plink)
	 */

	@Override
	// accepts all workflow
	// Map[] m= new Map[] {mapName2Wf, mapWf2Params, mapWfname2Wf};

	public Map[] discoverWorkflows(Set<String> nameStarts) {
		return discoverWorkflows(nameStarts, null);
	}

	@Override
	public Map[] discoverWorkflows(Set<String> nameStarts, Set<String> limitWf) {

		/*
		 * if(mapServer2Wf.containsKey(AppContext.getGalaxyAddress()) &&
		 * ((Map[])mapServer2Wf.get(AppContext.getGalaxyAddress()))!=null ) {
		 * 
		 * return mapServer2Wf.get(AppContext.getGalaxyAddress()); }
		 */

		if (mapServer2Wf.containsKey(AppContext.getGalaxyInstance())
				&& ((Map[]) mapServer2Wf.get(AppContext.getGalaxyInstance())) != null) {

			return mapServer2Wf.get(AppContext.getGalaxyInstance());
		}

		Map<MyTool, Set> mapTools2Params = discoverTools()[1];

		AppContext.debug("discoverWorkflows(Set<String> " + nameStarts + "  workflow=" + limitWf);
		Map<String, Set<MyWorksflow>> mapName2Wf = new HashMap();
		Map<MyWorksflow, Set<String>> mapWf2Params = new HashMap();
		Map<String, MyWorksflow> mapWfname2Wf = new HashMap();

		Set setSubWf = new HashSet();
		mapServer2WfSubwf.put(AppContext.getGalaxyInstance(), setSubWf);

		boolean gotall = true;

		Map mapWf2New2OrigLabel = new HashMap();
		mapServer2Wf2New2OrigLabel.put(AppContext.getGalaxyInstance(), mapWf2New2OrigLabel);
		// Map mapWfname2Id=new HashMap();
		// mapServerWfname2Id.put(AppContext.getGalaxyInstance(),mapWfname2Id);

		try {
			final GalaxyInstance instance = MyGalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),
					AppContext.getGalaxyKey(), 60, 60);

			// final GalaxyInstance instance2= new GalaxyInstanceImpl(new
			// MyGalaxyWebResourceFactoryImpl(AppContext.getGalaxyAddress(),AppContext.getGalaxyKey(),
			// 20000, 5000));

			// System.out.print("galaxy raw config: " +
			// instance.getConfigurationClient().getRawConfiguration());

			final WorkflowsClient workflowsClient = instance.getWorkflowsClient();

			// instance.getWorkflowsClient().showWorkflow(""). .getWebResource();

			boolean cachedWfDetails = false;

			Map<String, String> mapWfid2Wfdetails = new LinkedHashMap();
			Map<String, String> mapNewWfid2Wfdetails = new LinkedHashMap();
			if (cachedWfDetails) {
				try {
					AppContext.debug("reading " + AppContext.getHostname() + AppContext.getHostDirectory()
							+ "galaxy/galaxytools_" + AppContext.getGalaxyInstance() + ".txt");

					BufferedReader br = AppContext
							.bufferedReadURL(
									AppContext.getHostname() + AppContext.getHostDirectory()
											+ "galaxy/workflow_details_" + AppContext.getGalaxyInstance() + ".txt",
									null);
					String brline = null;
					if (br != null) {
						while ((brline = br.readLine()) != null) {
							String l[] = brline.split(",WORKFLOW_DATAILS,");
							mapWfid2Wfdetails.put(l[0], l[1]);
						}
						br.close();
					} else
						cachedWfDetails = false;

				} catch (Exception ex) {
					cachedWfDetails = false;
					ex.printStackTrace();
				}
			} else {
				/*
				 * BufferedReader br=new BufferedReader(new FileReader(AppContext.getTempDir()
				 * +"workflow_details_"+ AppContext.getGalaxyInstance() + ".txt" )); String
				 * brline=null; while( (brline=br.readLine())!=null ) { String l[]=
				 * brline.split(",WORKFLOW_DATAILS,"); mapWfid2Wfdetails.put( l[0], l[1]); }
				 * br.close();
				 */
			}

			int allparamcnt = 0;
			int wfcount = 0;
			int nosub = 1;
			while (nosub >= 0) {

				for (Workflow wfo : workflowsClient.getWorkflows()) {

					// if(!wfo.getName().equals("Get Triple Timeseries copy")) continue;
					if (limitWf != null && !limitWf.contains(wfo.getName()))
						continue;

					try {
						WorkflowDetails workflowDetails = workflowsClient.showWorkflow(wfo.getId());
						if (limitWf != null && !limitWf.contains(workflowDetails.getName()))
							continue;

						// System.out.println(wf.getName() + " " + workflowDetails.getUrl());
						MyWorksflow mwf = new MyWorksflow(wfo);
						// if(mwf.getName().equals("Plots timeseries")) continue;

						if (mwf.getId() == null || mwf.getId().isEmpty())
							continue;

						Map mapParamprop2Toolparamlabel = new HashMap();

						Map mapThisWfStep2Toolstate = new LinkedHashMap();

						Set params = new LinkedHashSet();
						Set paramsremovestartswith = new HashSet();
						String wfjson = null;
						JSONObject jsonObj = null;
						try {
							if (cachedWfDetails) {
								wfjson = mapWfid2Wfdetails.get(mwf.getId());
								if (wfjson == null) {
									wfjson = workflowsClient.exportWorkflow(mwf.getId());
									wfjson = wfjson.replace("\n", "").replace("\t", "");
									mapNewWfid2Wfdetails.put(mwf.getId(), wfjson);
								}
							} else {
								wfjson = workflowsClient.exportWorkflow(mwf.getId());
								wfjson = wfjson.replace("\n", "").replace("\t", "");
								mapWfid2Wfdetails.put(mwf.getId(), wfjson);
							}

							jsonObj = new JSONObject(wfjson);
							// }
						} catch (Exception ex) {
							ex.printStackTrace();
							continue;
						}
						/*
						 * if (mwf.getName().toLowerCase().startsWith("query and simulate") ||
						 * mwf.getName().toLowerCase().startsWith("cleaned time series copy") ||
						 * mwf.getName().toLowerCase().startsWith("fit_price_stoch_models") ) {
						 * System.out.println("wfjson=" + wfjson); }
						 */
						JSONObject stepsarr = jsonObj.getJSONObject("steps");

						// skip if has subworkflow
						boolean hassubworkflow = false;
						for (int i = 0; i < stepsarr.length(); i++) {
							JSONObject o = stepsarr.getJSONObject(String.valueOf(i));
							if (o.getString("type").equals("subworkflow")) {
								hassubworkflow = true;
								break;
							}
						}

						if (nosub == 1) { // dont accept wf with subworkflow on first pass
							if (hassubworkflow)
								continue;
						} else if (nosub == 0) { // dont accept wf without subworkflow on 2nd pass
							if (!hassubworkflow)
								continue;
							setSubWf.add(mwf.getId());
						}

						wfcount++;
						AppContext.debug("wf " + wfcount + "   " + mwf.getName() + "   id=" + mwf.getId());

						Map mapNew2OrigLabel = new HashMap();
						mapWf2New2OrigLabel.put(mwf.getId(), mapNew2OrigLabel);
						// mapWfname2Id.put(mwf.getName(), mwf.getId());

						mwf.setJSONObject(jsonObj);
						mapWfname2Wf.put(jsonObj.getString("name"), mwf);
						if (jsonObj.has("annotation"))
							mwf.setAnnotation(jsonObj.getString("annotation"));
						boolean acceptWf = false;
						Map mapInpcons2Stepno = new HashMap();
						Set<String> setToolFreeInputs = new LinkedHashSet();
						int paramcnt = 0;
						for (int i = 0; i < stepsarr.length(); i++) {
							JSONObject o = stepsarr.getJSONObject(String.valueOf(i));
							String alllabel = null;
							String label = null;

							if (o.getString("type").equals("subworkflow")) {
								// get subworkflow parameters, prepend swf step no#
								// o.getJSONArray("inputs");
								// o.getJSONArray("outputs");
								// o.getString("label");
								String wfname = o.getString("name");

								// copied from type=tool
								label = o.getString("label");
								if (label == null)
									label = o.getString("name");
								// String
								// toolstate=o.getString("tool_state").replace("\\","").replace("\"{","{").replace("}\"","}").replace("\"[","[").replace("]\"","]").replace("\"\"",
								// "\"").replace(" \",", " \"\",");
								// mapWfStep2Toolstate.put( wf.getId() +"#" + i +"#" + o.getString("tool_id") ,
								// toolstate);
								// if(toolstate!=null && !toolstate.isEmpty()) mapThisWfStep2Toolstate.put(
								// mwf.getName() +"#" + i + "#" + o.getString("tool_id") , toolstate);

								Set connected_inputs = new HashSet();
								if (o.has("input_connections")) {
									JSONObject inps = o.getJSONObject("input_connections");
									Iterator itInp = inps.keys();

									while (itInp.hasNext()) {
										String inpname = (String) itInp.next();
										connected_inputs.add(inpname);
										Integer inpstep = (Integer) ((JSONObject) inps.get(inpname)).get("id");
										String inpstepname = inpstep + "-" + inpname;
										mapInpcons2Stepno.put(inpstepname, String.valueOf(i));
									}
								}
								if (o.has("inputs")) {
									JSONArray inps = o.getJSONArray("inputs");
									// Iterator itInp=inps.keys();

									for (int iinp = 0; iinp < inps.length(); iinp++) {
										JSONObject inp = inps.getJSONObject(iinp);

										// while(itInp.hasNext()){
										// Object inpno=itInp.next();
										// JSONObject inp=inps.getJSONObject(inpno.toString());

										String inpname = inp.getString("name");
										if (connected_inputs.contains(inpname))
											continue;

										AppContext.debug("tool free input=" + o.get("tool_id") + "   " + i + "#"
												+ inpname + "#" + o.get("tool_id") + "   " + inp.toString());
										setToolFreeInputs.add(i + "#" + inpname + "#" + o.get("tool_id"));
										// setToolFreeInputs.add(i+"#"+inpname+"#"+o.get("tool_id") +":tool:" +
										// inp.getString("description"));
										acceptWf = true;

										mapParamprop2Toolparamlabel.put(i + "#" + inpname + "#" + o.get("tool_id"),
												label);

									}
								}
								AppContext.debug("step subwokflow " + wfname);

								/*
								 * Set subwfparams=mapWf2Params.get(mapWfname2Id.get(wfname));
								 * if(subwfparams!=null) { Set sparam=mapWf2Params.get(mwf); if(sparam==null) {
								 * sparam=new HashSet(); mapWf2Params.put(mwf , sparam); } sparam.addAll(
								 * subwfparams ); AppContext.debug("added " + subwfparams.size() +
								 * " free params"); }
								 */
								Set setparam = new LinkedHashSet();
								for (Object swfparam : mapWf2Params.get(mapWfname2Wf.get(wfname))) {
									if (((String) swfparam).contains("data_input"))
										continue;

									String stopnoinp[] = ((String) swfparam).split("#", 3);
									swfparam = i + "#" + stopnoinp[0] + "|" + stopnoinp[1] + "#" + stopnoinp[2];
									setparam.add(swfparam);
								}
								params.addAll(setparam);
								AppContext.debug("added " + setparam.size() + "/"
										+ mapWf2Params.get(mapWfname2Wf.get(wfname)).size() + " free params from "
										+ wfname + "\n" + setparam.toString());

								AppContext.debug("setToolFreeInputs=" + setToolFreeInputs);

								continue;

							} else if (o.getString("type").equals("parameter_input")) {
								// System.out.println( o.getString("label") + " " + o.getString("annotation") +
								// " " + o.getString("tool_state"));
								JSONObject jsonstate = new JSONObject(o.getString("tool_state"));
								// System.out.println( jsonstate.get("optional") + " " +
								// jsonstate.get("parameter_type"));
								label = o.getString("label");
								if (label == null)
									label = o.getString("name");
								// alllabel=label+":"+
								// jsonstate.get("parameter_type")+":"+jsonstate.get("optional")+":"+o.getString("annotation");
								String annotrun[] = o.getString("annotation").split("\\:");
								alllabel = label + ":" + jsonstate.get("parameter_type") + ":"
										+ (annotrun.length > 1 ? annotrun[1] : "") + ":"
										+ (o.has("value") ? o.getString("value") : "") + ":" + String.valueOf(i) + "-"
										+ annotrun[0] + ":"
										+ (o.has("optional") ? (o.getString("optional").trim().equals("1")
												|| o.getString("optional").trim().equals("True") ? "True" : "False")
												: "False");

								AppContext.debug("alllabel " + jsonObj.getString("name") + "  " + alllabel);

							} else if (o.getString("type").equals("data_input")) {
								label = o.getString("label");
								if (label == null)
									label = o.getString("name");
								// System.out.println( o.getString("label") + " " + o.getString("annotation") +
								// " " + o.getString("tool_state"));
								alllabel = label + ":data_input:" + o.getString("annotation").replace(":", "") + ":"
										+ (o.has("value") ? o.getString("value").replace(":", "") : "") + ":"
										+ String.valueOf(i) + ":"
										+ (o.has("optional") ? (o.getString("optional").trim().equals("1")
												|| o.getString("optional").trim().toLowerCase().equals("true") ? "True"
														: "False")
												: "False");
								AppContext.debug("alllabel " + jsonObj.getString("name") + "  " + alllabel);
							} else if (o.getString("type").equals("tool")) {
								label = o.getString("label");
								if (label == null)
									label = o.getString("name");
								String toolstate = o.getString("tool_state").replace("\\", "").replace("\"{", "{")
										.replace("}\"", "}").replace("\"[", "[").replace("]\"", "]")
										.replace("\"\"", "\"").replace(" \",", " \"\",");
								// mapWfStep2Toolstate.put( wf.getId() +"#" + i +"#" + o.getString("tool_id") ,
								// toolstate);
								if (toolstate != null && !toolstate.isEmpty())
									mapThisWfStep2Toolstate.put(mwf.getName() + "#" + i + "#" + o.getString("tool_id"),
											toolstate);

								Set connected_inputs = new HashSet();
								if (o.has("input_connections")) {
									JSONObject inps = o.getJSONObject("input_connections");
									Iterator itInp = inps.keys();

									while (itInp.hasNext()) {
										String inpname = (String) itInp.next();
										connected_inputs.add(inpname);
										Integer inpstep = (Integer) ((JSONObject) inps.get(inpname)).get("id");
										String inpstepname = inpstep + "-" + inpname;
										mapInpcons2Stepno.put(inpstepname, String.valueOf(i));
									}
								}
								if (o.has("inputs")) {
									JSONArray inps = o.getJSONArray("inputs");
									// Iterator itInp=inps.keys();

									for (int iinp = 0; iinp < inps.length(); iinp++) {
										JSONObject inp = inps.getJSONObject(iinp);

										// while(itInp.hasNext()){
										// Object inpno=itInp.next();
										// JSONObject inp=inps.getJSONObject(inpno.toString());

										String inpname = inp.getString("name");
										if (connected_inputs.contains(inpname))
											continue;

										AppContext.debug("tool free input=" + o.get("tool_id") + "   " + i + "#"
												+ inpname + "#" + o.get("tool_id") + "   " + inp.toString());
										setToolFreeInputs.add(i + "#" + inpname + "#" + o.get("tool_id"));
										// setToolFreeInputs.add(i+"#"+inpname+"#"+o.get("tool_id") +":tool:" +
										// inp.getString("description"));
										acceptWf = true;

										mapParamprop2Toolparamlabel.put(i + "#" + inpname + "#" + o.get("tool_id"),
												label);

									}
								}
								AppContext.debug("step tool " + o.getString("label"));
								continue;

							} else {
								AppContext.debug("step " + o.getString("type") + "  " + o.getString("label"));
								continue;
							}

							if (nameStarts == null) {
								if (label == null || label.equals("null")) {
									label = o.getString("name");
								}
								if (label == null || label.equals("null")) {
									// String n="param_" + allparamcnt;
									label = "param"; // + allparamcnt;
									// continue;
								}
								acceptWf = true;
								allparamcnt++;

								if (mapNew2OrigLabel.containsKey(label)) {
									JOptionPane.showMessageDialog(null,
											"Duplicate data_input label " + label + " in workflow " + wfo.getName());
									return null;
								}

								mapNew2OrigLabel.put(label, label);
								// mapNew2OrigLabel.put(label +"_" + allparamcnt, label);
								// label=label +"_" + allparamcnt;

								try {
									Set setWf = mapName2Wf.get(label);
									if (setWf == null) {
										setWf = new HashSet();
										mapName2Wf.put(label, setWf);
									}
									setWf.add(mwf);
								} catch (Exception ex) {
									AppContext.debug(jsonObj.getString("name") + " step=" + i + " label=" + label
											+ " n=ANY  alllabel=" + alllabel);
									ex.printStackTrace();
									throw ex;
								}
								alllabel = label + ":" + o.getString("type") + ":"
										+ o.getString("annotation").replace(":", "") + ":"
										+ (o.has("value") ? o.getString("value").replace(":", "") : "") + ":"
										+ String.valueOf(i) + ":"
										+ (o.has("optional") ? (o.getString("optional").trim().equals("1")
												|| o.getString("optional").trim().equals("True") ? "True" : "False")
												: "");
								AppContext.debug("mapName2Wf.key=" + mapName2Wf.keySet());
								AppContext.debug("mapName2Wf.values=" + mapName2Wf.values());

							} else {
								for (String n : nameStarts) {
									try {
										if (label.toLowerCase().startsWith(n.toLowerCase())) {
											Set setWf = mapName2Wf.get(n);
											if (setWf == null) {
												setWf = new HashSet();
												mapName2Wf.put(n, setWf);
											}
											setWf.add(mwf);
											acceptWf = true;
										}
									} catch (Exception ex) {
										AppContext.debug(jsonObj.getString("name") + " step=" + i + " label=" + label
												+ " n=" + n + "   alllabel=" + alllabel);
										ex.printStackTrace();
										throw ex;
									}
								}
							}

							/*
							 * if(acceptWf) { AppContext.debug("WF " + mwf.getName() + " accepted:  label="
							 * + alllabel); params.add(alllabel); } else { AppContext.debug("WF " +
							 * mwf.getName() + " not accepted:  label=" + alllabel); params.add(alllabel);
							 * 
							 * }
							 */
							params.add(alllabel);
							paramcnt++;

						} // all steps

						// if(params.size()>0) mapWf2Params.put(mwf,params);

						if (!acceptWf) {
							AppContext.debug("wf not included");
							// System.out.println("wfjson=" + wfjson);
						}

						// AppContext.debug("mapWfStep2Toolstate=" + mapWfStep2Toolstate);

						mapWfStep2Toolstate.putAll(mapThisWfStep2Toolstate);

						if (acceptWf) {
							for (Object itoolstep : mapThisWfStep2Toolstate.keySet()) {
								String toolstep = (String) itoolstep;
								int i = Integer.valueOf(toolstep.split("\\#")[1]);

								MyTool tool = new MyTool(toolstep.split("\\#")[2]);

								AppContext.debug(
										"toolstep=" + toolstep + "    state=" + mapThisWfStep2Toolstate.get(toolstep));

								Set toolstepparams = new LinkedHashSet();

								JSONObject jsonts = new JSONObject((String) mapThisWfStep2Toolstate.get(toolstep));
								// .put( mwf.getName() +"#" + i , toolstate);
								Iterator tsit = jsonts.keys();
								while (tsit.hasNext()) {
									String k1 = (String) tsit.next();
									String inpname = k1;
									try {
										JSONObject cond = jsonts.getJSONObject(k1);
										AppContext.debug("k1=" + k1 + "   " + cond.toString());
										Iterator tsit2 = cond.keys();
										while (tsit2.hasNext()) {
											String k2 = (String) tsit2.next();
											try {
												String classrt = null;
												if (k2.equals("__class__")
														&& cond.getString(k2).equals("RuntimeValue")) {
													classrt = cond.getString(k2);
													AppContext.debug("k3=" + k2 + "   " + classrt);
												} else {
													JSONObject rt = cond.getJSONObject(k2);
													AppContext.debug("k2=" + k2 + "   " + rt.toString());
													classrt = rt.getString("__class__");
												}
												if (classrt.equals("RuntimeValue")) {
													// equals( "{\"__class__\": \"RuntimeValue\"}"

													Set toolparams = (Set) mapTools2Params.get(tool);
													AppContext.debug("tool=" + tool + "    toolparams=" + toolparams);
													for (Object oparam : toolparams) {
														String paramprops[] = ((String) oparam).split("\\:");
														String labels[] = paramprops[0].split("\\|");
														String label = null;
														if (labels.length == 4 && k1.equals(labels[2])
																&& k2.equals(labels[3])) {
															label = k1 + "|" + k2;
														} else if (labels.length == 3 && k1.equals(labels[1])
																&& k2.equals(labels[2])) {
															label = k1 + "|" + k2;
														} else if (labels[0].equals(k1)) {
															label = k1;
														} else
															continue;

														String paramdesc = null;
														// String freepar=i+"#"+inpname+"#"+ tool.getId();
														String freepar = i + "#" + label + "#" + tool.getId();

														if (!setToolFreeInputs.contains(freepar)) {
															// check cond
															if (label.contains("|")) {
																if (!setToolFreeInputs.contains(i + "#"
																		+ label.split("\\|")[0] + "#" + tool.getId()))
																	continue;
															} else
																continue;
														}

														if (label.equals(inpname) || (label.contains("|")
																&& label.split("\\|")[0].equals(inpname))) {

															paramdesc = paramprops[1];
															String toolparamlabel = (String) mapParamprop2Toolparamlabel
																	.get(i + "#" + k1 + "#" + tool.getId());
															if (toolparamlabel == null || toolparamlabel.equals("null")
																	|| toolparamlabel.isEmpty()) {
																if (paramprops.length > 2)
																	paramdesc += ":" + paramprops[2];
																else
																	paramdesc += ":";
															} else {
																if (paramprops.length > 2)
																	paramdesc += ":" + toolparamlabel + "|"
																			+ paramprops[2];
																else
																	paramdesc += ":" + toolparamlabel;
															}

															if (paramprops.length > 3)
																paramdesc += ":" + paramprops[3];
															else
																paramdesc += ":";

															if (paramprops.length > 4)
																paramdesc += ":" + paramprops[4];
															else
																paramdesc += ":";

															if (paramprops.length > 5)
																paramdesc += ":" + paramprops[5];
															else
																paramdesc += ":";
															// AppContext.debug("paramdesc=" +
															// (paramdesc.length()>500?paramdesc.substring(0,500):paramdesc));

															// alllabel=(String)sparams;

															// if(label.contains("|")) {
															// String freepars[]=freepar.split("\\#");
															// condfreepar=freepars[0]+"#"+label+"#"+freepars[2];
															// paramsremovestartswith.add(freepars[0]+"#"+label.split("\\|")[0]+"#"+freepars[2]);
															// }

															if (paramdesc != null) {
																AppContext.debug("wf param=" + freepar + ":"
																		+ (paramdesc.length() > 500
																				? paramdesc.substring(0, 500)
																				: paramdesc));
																toolstepparams.add(freepar + ":" + paramdesc);

																// params.add( freepar+":"+paramdesc);
															}

														} else {
															// AppContext.debug("label=" + label + " inpname=" +
															// inpname);
														}

													}
													;

												}
											} catch (Exception ex) {
												// not runtime
											}
										}

									} catch (Exception ex) {
										// not a cond
									}

								}

								// sort params based on tool order here!
								Set sortedtoolstepparams = new LinkedHashSet();
								Set toolparams = (Set) mapTools2Params.get(tool);
								// AppContext.debug("tool=" + tool +" toolparams=" + toolparams);

								if (toolparams == null) {
									params.addAll(toolstepparams);
									AppContext.debug("no params for tool " + tool.getId());
								} else {
									for (Object oparam : toolparams) {
										String paramprops[] = ((String) oparam).split("\\:");
										String labels[] = paramprops[0].split("\\|");
										String label = null;
										if (labels.length == 4) {
											label = labels[2] + "|" + labels[3];
										} else if (labels.length == 3) {
											label = labels[1] + "|" + labels[2];
										} else if (labels.length == 1) {
											label = labels[0];
										} else
											continue;

										for (Object toolstepreorder : toolstepparams) {
											if (!((String) toolstepreorder)
													.startsWith(i + "#" + label + "#" + tool.getId() + ":"))
												continue;
											sortedtoolstepparams.add(toolstepreorder);
											break;
										}
									}
									if (sortedtoolstepparams.size() != toolstepparams.size()) {
										throw new RuntimeException("sortedtoolstepparams.size()!=toolstepparams "
												+ sortedtoolstepparams.size() + ", " + toolstepparams.size());
									}
									params.addAll(sortedtoolstepparams);
								}
							}

						}

						/*
						 * if(params!=null) { Set sparam=mapWf2Params.get(mwf); if(sparam==null) {
						 * sparam=new HashSet(); mapWf2Params.put(mwf , sparam); } sparam.addAll( params
						 * ); }
						 */
						mapWf2Params.put(mwf, params);

						AppContext.debug("workflow params: " + mwf.getName() + "   " + params);

						/*
						 * //System.out.println(workflowsClient.exportWorkflow(wf.getId() )); //PyList
						 * cxxTestGenArgs = new PyList(); Set params=new HashSet(); for(final
						 * Map.Entry<String, WorkflowInputDefinition> inputEntry :
						 * workflowDetails.getInputs().entrySet()) {
						 * //inputEntry.getValue().getValue().ge String label =
						 * inputEntry.getValue().getLabel(); // + ":" + inputEntry.getValue().
						 * for(String n:nameStarts) {
						 * if(label.toLowerCase().startsWith(n.toLowerCase())) { Set
						 * setWf=mapName2Wf.get(n); if(setWf==null) { setWf=new HashSet();
						 * mapName2Wf.put(n,setWf); } setWf.add(wf); } } params.add(label); }
						 * if(params.size()>0) mapWf2Params.put(wf,params);
						 */
					} catch (Exception ex) {
						ex.printStackTrace();
						Messagebox.show("workflow: " + wfo.getId() + "\n" + ex.getMessage(), "Error", Messagebox.OK,
								Messagebox.EXCLAMATION);
						gotall = false;
					}
				} // for forkflow
				nosub--;

			} // while(nosub)

			if (cachedWfDetails) {
				if (mapNewWfid2Wfdetails.size() > 0) {
					mapWfid2Wfdetails.putAll(mapNewWfid2Wfdetails);
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							AppContext.getTempDir() + "workflow_details_" + AppContext.getGalaxyInstance() + ".txt"));
					for (String wfid : mapWfid2Wfdetails.keySet()) {
						bw.write(wfid + ",WORKFLOW_DATAILS," + mapWfid2Wfdetails.get(wfid) + "\n");
					}
					bw.close();
					AppContext.debug("workflow details written to " + AppContext.getTempDir() + "workflow_details_"
							+ AppContext.getGalaxyInstance() + ".txt");
				}
			} else {
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						AppContext.getTempDir() + "workflow_details_" + AppContext.getGalaxyInstance() + ".txt"));
				for (String wfid : mapWfid2Wfdetails.keySet()) {
					bw.write(wfid + ",WORKFLOW_DATAILS," + mapWfid2Wfdetails.get(wfid) + "\n");
				}
				bw.close();
				AppContext.debug("workflow details written to " + AppContext.getTempDir() + "workflow_details_"
						+ AppContext.getGalaxyInstance() + ".txt");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Error", Messagebox.OK, Messagebox.EXCLAMATION);
		}

		Map[] m = new Map[] { mapName2Wf, mapWf2Params, mapWfname2Wf };
		if (gotall) {
			// cache
			mapServer2Wf.put(AppContext.getGalaxyInstance(), m);
		}

		AppContext.debug("mapName2Wf.values()=" + mapName2Wf.values());
		AppContext.debug("mapWfname2Wf=" + mapWfname2Wf.keySet());
		AppContext.debug("mapWf2Params.values()=" + mapWf2Params.keySet());

		AppContext.debug("Get Triple Timeseries copy workflowparam="
				+ mapWf2Params.get(mapWfname2Wf.get("Get Triple Timeseries copy")));
		return m;
	}

	// @Override
	// public Map[] discoverWorkflows2(Set<String> nameStarts) {
	//
	// /*
	// if(mapServer2Wf.containsKey(AppContext.getGalaxyAddress())
	// && ((Map[])mapServer2Wf.get(AppContext.getGalaxyAddress()))!=null ) {
	//
	// return mapServer2Wf.get(AppContext.getGalaxyAddress());
	// }
	// */
	//
	// if(mapServer2Wf.containsKey(AppContext.getGalaxyInstance())
	// && ((Map[])mapServer2Wf.get(AppContext.getGalaxyInstance()))!=null ) {
	//
	// return mapServer2Wf.get(AppContext.getGalaxyInstance());
	// }
	//
	// Map<MyTool,Set> mapTools2Params=discoverTools()[1];
	//
	// AppContext.debug("discoverWorkflows(Set<String> " + nameStarts );
	// Map<String,Set<Workflow>> mapName2Wf=new HashMap();
	// Map<Workflow, Set<String>> mapWf2Params=new HashMap();
	// Map<String, MyWorksflow> mapWfname2Wf=new HashMap();
	// boolean gotall=true;
	//
	// try {
	// final GalaxyInstance instance =
	// MyGalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),AppContext.getGalaxyKey(),
	// 120,360);
	//
	// //final GalaxyInstance instance2= new GalaxyInstanceImpl(new
	// MyGalaxyWebResourceFactoryImpl(AppContext.getGalaxyAddress(),AppContext.getGalaxyKey(),
	// 20000, 5000));
	//
	// //System.out.print("galaxy raw config: " +
	// instance.getConfigurationClient().getRawConfiguration());
	//
	// final WorkflowsClient workflowsClient = instance.getWorkflowsClient();
	//
	// //instance.getWorkflowsClient().showWorkflow(""). .getWebResource();
	//
	// for(Workflow wf:workflowsClient.getWorkflows()) {
	//
	// try {
	// WorkflowDetails workflowDetails = workflowsClient.showWorkflow(wf.getId());
	// //System.out.println(wf.getName() + " " + workflowDetails.getUrl());
	// MyWorksflow mwf=new MyWorksflow(wf);
	// AppContext.debug("wf " + mwf.getName() + " id=" + wf.getId());
	//
	// if(wf.getId()==null || wf.getId().isEmpty()) continue;
	//
	//
	// Set params=new HashSet();
	// String wfjson=workflowsClient.exportWorkflow(wf.getId());
	// //System.out.println("wfjson=" + wfjson);
	// JSONObject jsonObj = new JSONObject(wfjson);
	// JSONObject stepsarr=jsonObj.getJSONObject("steps");
	// mwf.setJSONObject(jsonObj);
	// mapWfname2Wf.put(jsonObj.getString("name") , mwf);
	// if(jsonObj.has("annotation"))
	// mwf.setAnnotation(jsonObj.getString("annotation"));
	// boolean acceptWf=false;
	// Map mapInpcons2Stepno=new HashMap();
	// Set<String> setToolFreeInputs=new HashSet();
	// for(int i=0; i<stepsarr.length(); i++) {
	// JSONObject o=stepsarr.getJSONObject(String.valueOf(i));
	// String alllabel=null;
	// String label=null;
	// if(o.getString("type").equals("parameter_input")) {
	// //System.out.println( o.getString("label") + " " + o.getString("annotation")
	// + " " + o.getString("tool_state"));
	// JSONObject jsonstate = new JSONObject(o.getString("tool_state"));
	// //System.out.println( jsonstate.get("optional") + " " +
	// jsonstate.get("parameter_type"));
	// label= o.getString("label");
	// //alllabel=label+":"+
	// jsonstate.get("parameter_type")+":"+jsonstate.get("optional")+":"+o.getString("annotation");
	// String annotrun[]=o.getString("annotation").split("\\:");
	// alllabel=label+":"+ jsonstate.get("parameter_type")+":"+
	// (annotrun.length>1?annotrun[1]:"")
	// +":"+(o.has("value")?o.getString("value"):"")
	// +":"+String.valueOf(i)+"-"+annotrun[0]+":"+(o.has("optional")?(o.getString("optional").trim().equals("1")||o.getString("optional").trim().equals("True")?"True":"False"):"")
	// ;
	//
	// AppContext.debug("alllabel " + jsonObj.getString("name") + " " + alllabel);
	//
	// }
	// else if(o.getString("type").equals("data_input")) {
	// label= o.getString("label");
	// //System.out.println( o.getString("label") + " " + o.getString("annotation")
	// + " " + o.getString("tool_state"));
	// alllabel= label+":data_input:"+o.getString("annotation").replace(":",
	// "")+":"+(o.has("value")?o.getString("value").replace(":",
	// ""):"")+":"+String.valueOf(i)+":"+(o.has("optional")?(o.getString("optional").trim().equals("1")||o.getString("optional").trim().equals("True")?"True":"False"):"");
	// AppContext.debug("alllabel " + jsonObj.getString("name") + " " + alllabel);
	// }
	// else if(o.getString("type").equals("tool")) {
	// Set connected_inputs=new HashSet();
	// if(o.has("input_connections")) {
	// JSONObject inps= o.getJSONObject("input_connections");
	// Iterator itInp=inps.keys();
	//
	// while(itInp.hasNext()){
	// String inpname=(String)itInp.next();
	// connected_inputs.add(inpname);
	// Integer inpstep=(Integer)((JSONObject)inps.get(inpname)).get("id");
	// String inpstepname=inpstep+"-"+inpname;
	// mapInpcons2Stepno.put(inpstepname , String.valueOf(i));
	// }
	// }
	// if(o.has("inputs")) {
	// JSONArray inps= o.getJSONArray("inputs");
	// //Iterator itInp=inps.keys();
	//
	// for(int iinp=0; iinp<inps.length(); iinp++) {
	// JSONObject inp=inps.getJSONObject(iinp);
	//
	// //while(itInp.hasNext()){
	// // Object inpno=itInp.next();
	// // JSONObject inp=inps.getJSONObject(inpno.toString());
	//
	// String inpname=inp.getString("name");
	// if(connected_inputs.contains(inpname)) continue;
	//
	// setToolFreeInputs.add(i+"#"+inpname+"#"+o.get("tool_id"));
	//
	// }
	// }
	// continue;
	//
	// } else continue;
	//
	// if(nameStarts==null) {
	// acceptWf=true;
	// Set setWf=mapName2Wf.get("ANY");
	// if(setWf==null) {
	// setWf=new HashSet();
	// mapName2Wf.put("ANY",setWf);
	// }
	// setWf.add(mwf);
	//
	// }
	// else {
	// for(String n:nameStarts) {
	// try {
	// if(label.toLowerCase().startsWith(n.toLowerCase())) {
	// Set setWf=mapName2Wf.get(n);
	// if(setWf==null) {
	// setWf=new HashSet();
	// mapName2Wf.put(n,setWf);
	// }
	// setWf.add(mwf);
	// acceptWf=true;
	// }
	// } catch(Exception ex) {
	// AppContext.debug(jsonObj.getString("name") + " step=" + i + " label=" + label
	// + " n=" + n + " alllabel=" + alllabel);
	// ex.printStackTrace();
	// throw ex;
	// }
	// }
	// }
	//
	// /*
	// if(acceptWf) {
	// AppContext.debug("WF " + mwf.getName() + " accepted: label=" + alllabel);
	// params.add(alllabel);
	// }
	// else {
	// AppContext.debug("WF " + mwf.getName() + " not accepted: label=" + alllabel);
	// params.add(alllabel);
	//
	// }
	// */
	// params.add(alllabel);
	//
	// }
	// //if(params.size()>0) mapWf2Params.put(mwf,params);
	// if(acceptWf) {
	//
	// mwf.setInpconections2Stepno(mapInpcons2Stepno);
	// mwf.setToolFreeInputs(setToolFreeInputs);
	//
	// for(String freepar:setToolFreeInputs) {
	// //i+"-"+inpname+"-"+o.get("tool_id")
	// // tool param
	// //String paramprops=pdict.get("name") + ":"+ pdict.get("type") + ":" +
	// (pdict.has("label")?pdict.get("label"):"") +":"+
	// // (pdict.has("value")?pdict.get("value"):"");
	//
	//
	// String paramdesc="Runtime parameter " + freepar;
	// String inpname=freepar.split("\\#")[1];
	// MyTool m=new MyTool(freepar.split("\\#")[2]);
	//
	// AppContext.debug("getting freeparam for tool " + m.getId() + " = " +
	// mapTools2Params.get( m ));
	// if (mapTools2Params.get( m )==null) continue;
	//
	// String alllabel=null;
	// String condfreepar=freepar;
	// for(Object sparams:mapTools2Params.get( m )) {
	// String paramprops[]=((String)sparams).split("\\:");
	//
	// String label=paramprops[0];
	// if(label.equals(inpname) || (label.contains("|") &&
	// label.split("\\|")[0].equals(inpname)) ) {
	//
	// if(paramprops.length>2)
	// paramdesc=paramprops[2];
	// else
	// paramdesc="";
	//
	// if(paramprops.length>3)
	// paramdesc+=":"+paramprops[3];
	// else
	// paramdesc+=":";
	//
	// if(paramprops.length>4)
	// paramdesc+=":"+paramprops[4];
	// else
	// paramdesc+=":";
	//
	// if(paramprops.length>5)
	// paramdesc+=":"+paramprops[5];
	// else
	// paramdesc+=":";
	// alllabel=(String)sparams;
	//
	// if(label.contains("|")) {
	// String freepars[]=freepar.split("\\#");
	// condfreepar=freepars[0]+"#"+label+"#"+freepars[2];
	// }
	// }
	// }
	//
	// //mapParamToolid2CondParam
	//
	// params.add(condfreepar+":parameter_input:"+paramdesc);
	//
	// AppContext.debug("tool alllabel " + jsonObj.getString("name") + " " +
	// alllabel);
	// AppContext.debug("wf freeparam " + jsonObj.getString("name") + " " +
	// condfreepar+":parameter_input:"+paramdesc);
	//
	//
	// }
	// AppContext.debug(mwf.getName() + " mapInpcons2Stepno=" + mapInpcons2Stepno);
	// AppContext.debug(" setToolFreeInputs=" + setToolFreeInputs);
	//
	// //alllabel=label+":"+ jsonstate.get("parameter_type")+":"+
	// (annotrun.length>1?annotrun[1]:"")
	// +":"+(o.has("value")?o.getString("value"):"")
	// +":"+String.valueOf(i)+"-"+annotrun[0];
	//
	// mapWf2Params.put(mwf,params);
	// }
	//
	// /*
	// //System.out.println(workflowsClient.exportWorkflow(wf.getId() ));
	// //PyList cxxTestGenArgs = new PyList();
	// Set params=new HashSet();
	// for(final Map.Entry<String, WorkflowInputDefinition> inputEntry :
	// workflowDetails.getInputs().entrySet()) {
	// //inputEntry.getValue().getValue().ge
	// String label = inputEntry.getValue().getLabel(); // + ":" +
	// inputEntry.getValue().
	// for(String n:nameStarts) {
	// if(label.toLowerCase().startsWith(n.toLowerCase())) {
	// Set setWf=mapName2Wf.get(n);
	// if(setWf==null) {
	// setWf=new HashSet();
	// mapName2Wf.put(n,setWf);
	// }
	// setWf.add(wf);
	// }
	// }
	// params.add(label);
	// }
	// if(params.size()>0) mapWf2Params.put(wf,params);
	// */
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// Messagebox.show("workflow: " + wf.getId() + "\n" +ex.getMessage(), "Error",
	// Messagebox.OK, Messagebox.EXCLAMATION);
	// gotall=false;
	// }
	// }
	//
	// }catch(Exception ex) {
	// ex.printStackTrace();
	// Messagebox.show(ex.getMessage(), "Error", Messagebox.OK,
	// Messagebox.EXCLAMATION);
	// }
	//
	// Map[] m= new Map[] {mapName2Wf, mapWf2Params, mapWfname2Wf};
	// if(gotall) {
	// // cache
	// mapServer2Wf.put(AppContext.getGalaxyInstance(), m);
	// }
	//
	// return m;
	// }

	private Set findMatches(Set setMatch, JSONArray dfxlist, Map<String, String> mapDFX2Class, String tag) {
		Set matched = new HashSet();
		for (int i = 0; i < dfxlist.length(); i++) {
			try {
				Object o = dfxlist.get(i);
				if (o == null)
					continue;
				String dfx = o.toString();

				String classtype = dfx;
				if (!tag.equals("file_ext"))
					classtype = mapDFX2Class.get(dfx);

				// System.out.println("dfx=" + dfx + " class=" + classtype);
				if (classtype == null) {
					AppContext.debug("No " + tag + " class for tag " + dfx);
				} else {
					if (setMatch != null) {
						if (setMatch.contains(dfx))
							matched.add(classtype);
					} else
						matched.add(classtype);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

	/*
	 * private Set findMatches(Set setMatch, PyList dfxlist, Map<String,String>
	 * mapDFX2Class, String tag) { Set matched=new HashSet(); Iterator
	 * it=dfxlist.iterator(); while(it.hasNext()){ Object o=it.next(); if(o==null)
	 * continue; String dfx=o.toString(); String classtype=mapDFX2Class.get(dfx);
	 * //System.out.println("dfx=" + dfx + "  class=" + classtype);
	 * if(classtype==null) { AppContext.debug("No " + tag + " class for tag " +
	 * dfx); } else { if(setMatch.contains(dfx)) matched.add(classtype); } } return
	 * matched; }
	 */

	@Override
	public List getSections() {
		List list = new ArrayList();
		AppContext.debug("discoverTools(Set<String> nameStarts, Set<String> formats, Set<String> sections)");
		try {
			final GalaxyInstance instance = MyGalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),
					AppContext.getGalaxyKey());
			final ToolsClient toolClient = instance.getToolsClient();
			for (ToolSection ts : toolClient.getTools()) {
				list.add(ts.getName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	/*
	 * public boolean exec_with_timeout(PythonInterpreter pi , String cmd, long ms)
	 * { Thread jythonThread = new Thread(new Runnable() {
	 * 
	 * @Override public void run() { try { pi.exec(cmd); } catch(Exception ex) {
	 * ex.printStackTrace(); } } }); jythonThread.start();
	 * 
	 * new Thread(new Runnable() {
	 * 
	 * @Override public void run() { try { Thread.sleep(ms);
	 * if(jythonThread.isAlive()) jythonThread.interrupt(); } catch
	 * (InterruptedException e) { e.printStackTrace(); } } }).start();
	 * 
	 * while(jythonThread.isAlive()) { if(jythonThread.isInterrupted()) return
	 * false; try { Thread.sleep(1000); } catch(Exception ex) {
	 * ex.printStackTrace(); } } if(jythonThread.isInterrupted()) return false;
	 * return true; }
	 */
	@Override
	public Map[] discoverTools() {
		return discoverTools(null, null, null, null);
	}

	// @Override
	@Override
	public Map[] discoverTools(Set<String> nameStarts, Set<String> classes, Set<String> formats, Set<String> sections) {
		if (mapServer2Tools.containsKey(AppContext.getGalaxyInstance()))
			return mapServer2Tools.get(AppContext.getGalaxyInstance());

		boolean gotall = true;
		AppContext.debug("discoverTools(Set<String> nameStarts, Set<String> formats, Set<String> sections)");

		AppContext.debug("nameStarts=" + nameStarts);
		AppContext.debug("classes=" + classes);
		AppContext.debug("formats=" + formats);
		AppContext.debug("sections=" + sections);
		// {mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
		// mapExt2Class,mapClass2Class};
		Set skipToolId = new HashSet();
		/*
		 * skipToolId.add( "Convert characters1"); skipToolId.add(
		 * "52.76.88.51/repos/dereeper/numeric_matrix_encoder/bglr:encode/1.0.3");
		 * skipToolId.add(
		 * "toolshed.g2.bx.psu.edu/repos/iuc/bedtools/bedtools_jaccard/2.27.1+galaxy1");
		 * skipToolId.add( "GeneBed_Maf_Fasta2"); skipToolId.add(
		 * "__EXTRACT_DATASET__");
		 */

		Map mapClass2Dat = DatatypeMaps.getDatatypes()[0];
		Map mapClass2Format = DatatypeMaps.getDatatypes()[2];
		Map mapSection2Section = new LinkedHashMap();

		if (classes != null || formats != null) {
			if (classes != null && formats != null) {
				for (String s : classes) {
					formats.add((String) mapClass2Dat.get(s));
					String f = (String) mapClass2Format.get(s);
					if (f != null)
						formats.add(f);
				}
			} else if (classes != null) {
				formats = classes;
			}

			formats.remove("");
			formats.remove("null");
			formats.remove(null);
			AppContext.debug("match edams=" + formats);
		}

		Map<MyTool, Set<String>> mapTools2Params = new HashMap();

		Map<String, Set<MyTool>> mapName2Tools = new HashMap();
		Map<String, Set<MyTool>> mapData2Tools = new HashMap();
		Map<String, Set<MyTool>> mapExt2Tools = new HashMap();

		try {

			Map<String, String> mapDatatypes[] = DatatypeMaps.getDatatypes();
			// {mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
			// mapExt2Class}
			int sectioncnt = 1;
			Set setMatchData = new HashSet();
			Set setMatchFormat = new HashSet();
			Set setMatchExt = new HashSet();

			/*
			 * InetAddress myHost = InetAddress.getLocalHost();
			 * System.out.println(myHost.getHostName());
			 * System.out.println(myHost.getHostAddress());
			 * System.out.println(myHost.getCanonicalHostName());
			 */

			AppContext.debug("reading " + AppContext.getHostname() + AppContext.getHostDirectory()
					+ "galaxy/galaxytools_" + AppContext.getGalaxyInstance() + ".txt");
			BufferedReader br = AppContext.bufferedReadURL(AppContext.getHostname() +"/"+ AppContext.getHostDirectory()
					+ "galaxy/galaxytools_" + AppContext.getGalaxyInstance() + ".txt", null);
			// BufferedReader br=new BufferedReader(new
			// FileReader(AppContext.getTomcatWebappsDir()+AppContext.getHostDirectory()
			// +"galaxy/galaxytools_" + AppContext.getGalaxyInstance()+".txt"));
			// BufferedReader br=new BufferedReader(new
			// FileReader(AppContext.getFlatfilesDir() + "galaxytools_" +
			// AppContext.getGalaxyInstance()+".txt"));

			String line = null;

			while ((line = br.readLine()) != null) {

				if (!line.startsWith("{"))
					continue;
				JSONObject jo = null;
				try {
					jo = new JSONObject(line);
				} catch (Exception ex) {
					ex.printStackTrace();
					continue;
				}

				MyTool mt = new MyTool(jo);

				Map mapThisToolParamLabel2Value = new LinkedHashMap();
				Map mapThisParamToolid2CondParam = new LinkedHashMap();

				try {
					Set<String> matchedExt = new LinkedHashSet();
					Set<String> matchedData = new LinkedHashSet();
					Set<String> matchedName = new LinkedHashSet();

					if (sections != null) {
						if (jo.has("panel_section_id") || jo.has("panel_section_name")) {
							String sectionid = "";
							String sectionname = "";
							if (jo.has("panel_section_id"))
								sectionid = jo.getString("panel_section_id");
							if (jo.has("panel_section_name"))
								sectionname = jo.getString("panel_section_name");

							if (sections.contains(sectionname))
								mapSection2Section.put(sectionid, sectionname);
							else
								continue;
						} else {
							if (sections.contains("NONE") || sections.contains(""))
								mapSection2Section.put("", "");
							else
								continue;
						}
					} else {
						String sectionname = "";
						String sectionid = "";
						if (jo.has("panel_section_id"))
							sectionid = jo.getString("panel_section_id");
						if (jo.has("panel_section_name"))
							sectionname = jo.getString("panel_section_name");

						mapSection2Section.put(sectionid, sectionname);
					}

					Map mapParams2ClassExts = new HashMap();

					Set params = new LinkedHashSet();
					JSONArray inputs = jo.getJSONArray("inputs");
					for (int it = 0; it < inputs.length(); it++) {
						JSONObject pdict = inputs.getJSONObject(it);
						String pname = (String) pdict.get("name");
						String paramprops = pdict.getString("name").replace(":", "") + ":" + pdict.get("type") + ":"
								+ (pdict.has("label") ? pdict.getString("label").replace(":", "") : "") + ":"
								+ (pdict.has("value") ? pdict.getString("value").replace(":", "") : "") + ":"
								+ (pdict.has("optional") ? (pdict.get("optional").toString().trim().equals("1")
										|| pdict.get("optional").toString().trim().equals("True") ? "True" : "False")
										: "");
						// System.out.println( "paramprops= " + paramprops);
						if (nameStarts != null) {
							for (String n : nameStarts) {
								if (pname.toLowerCase().startsWith(n.toLowerCase())) {
									// matchedName.add(pname+":"+ pdict.get("extensions"));
									// matchedName.add(pname);
									matchedName.add(paramprops);
								}
							}
						}
						Set matchedDataTool = new HashSet();
						Set matchedExtTool = new HashSet();
						if (pdict.get("type").equals("data")) {
							if (pdict.has("edam_data")) {
								JSONArray edams_data = pdict.getJSONArray("edam_data");
								if (edams_data != null && edams_data.length() > 0)
									matchedDataTool
											.addAll(findMatches(formats, edams_data, mapDatatypes[1], "edam_data"));
							}
							if (pdict.has("edam_formats")) {
								JSONArray edams_format = pdict.getJSONArray("edam_formats");
								if (edams_format != null && edams_format.length() > 0)
									matchedDataTool
											.addAll(findMatches(formats, edams_format, mapDatatypes[3], "edam_format"));
							}
						}
						if (pdict.has("extensions")) {
							JSONArray ext = pdict.getJSONArray("extensions");
							if (ext != null && ext.length() > 0)
								matchedExtTool.addAll(findMatches(formats, ext, mapDatatypes[5], "file_ext"));
						}
						if (pdict.get("type").equals("conditional") && pdict.has("cases")) {

							// {'id':'get_ts', 'name':'Get
							// timeseries','panel_section_id':'tstools','panel_section_name':"TS
							// Tools",'description':"Get price or MW
							// series",'inputs':[{'name':'simsettings','label':"Simulation
							// settings",'optional':"False",'type':'data','edam_data':[data_0006],'edam_formats':[format_3464],'extensions':[ts_simsettings]},
							// {'name':'resourcetype','type':'conditional','casename':'type','casevalue':'GEN','cases':
							// "LD|resourcetype|ldvalue:Value:price:False:select:[['Price Peso/MW', 'price',
							// True], ['MW', 'mw', False]]"

							// mapWfStep2Toolstate.get(mt.getId()+"#"+ , urlVariables)

							// pdict.get("type")
							JSONArray cases = pdict.getJSONArray("cases");
							for (int icase = 0; icase < cases.length(); icase++) {
								String caseattr[] = cases.getString(icase).split(":");

								// if(!caseattr[0].split("|")[0].equals( ) { // conditional case value from
								// tool_state in wf-step-tool_id
								// continue;
								// }
								// caseattr[0]=caseattr[0].split("|",2)[1];

								// 'cases':["GEN|resourcetype|resourcegen:GEN resource:value:optional:select:
								// "GEN|resourcetype|resourcegen:GEN resource:1ACNPC_G01:False:select:[['1
								paramprops = pdict.getString("casename") + "|" + caseattr[0] + ":" + caseattr[4] + ":"
										+ caseattr[1] + ":" + caseattr[2] + ":"
										+ (caseattr[3].toLowerCase().trim().equals("1")
												|| caseattr[3].toLowerCase().trim().equals("true") ? "True" : "False")
										+ ":";
								// transform:select:Transformation :logabs:False:none|ab
								// String paramprops=pdict.getString("name").replace(":","") + ":"+
								// pdict.get("type") + ":" +
								// (pdict.has("label")?pdict.getString("label").replace(":",""):"") +":"+
								// (pdict.has("value")?pdict.getString("value").replace(":",""):"")+":"+(pdict.has("optional")?(pdict.get("optional").toString().trim().equals("1")||pdict.get("optional").toString().trim().equals("True")?"True":"False"):"");

								if (caseattr[4].equals("select")) {
									StringBuffer labels = new StringBuffer();
									Map mapVal2Label = new HashMap();
									JSONArray options = new JSONObject("{'options':" + caseattr[5] + "}")
											.getJSONArray("options");
									// Map mapThisToolParamLabel2Value=new LinkedHashMap();
									for (int ioptions = 0; ioptions < options.length(); ioptions++) {
										JSONArray labvalsel = options.getJSONArray(ioptions);
										// if(edams_data!=null && edams_data.length()>0)
										// matchedDataTool.addAll(findMatches(formats, edams_data, mapDatatypes[1],
										// "edam_data"));

										// mapThisToolParamLabel2Value.put( jo.getString("id")+"-"+ pname + "-" +
										// labvalsel.getString(0) , labvalsel.getString(1));
										mapThisToolParamLabel2Value.put(jo.getString("id") + "-"
												+ caseattr[0].split("\\|", 2)[1] + "-" + labvalsel.getString(0),
												labvalsel.getString(1));
										labels.append(labvalsel.getString(0));
										mapVal2Label.put(labvalsel.getString(1), labvalsel.getString(0));
										if (ioptions < options.length() - 1) {
											labels.append("|");
										}
										if (caseattr[5].length() < 1000) {
											// AppContext.debug("conditional cases param2label=" +
											// jo.getString("id")+"-"+ pname + "-" + labvalsel.getString(0) +" ---> " +
											// labvalsel.getString(1));
											AppContext.debug("conditional cases param2label=" + jo.getString("id") + "-"
													+ caseattr[0].split("\\|", 2)[1] + "-" + labvalsel.getString(0)
													+ "   --->   " + labvalsel.getString(1));
										}

									}
									String props[] = paramprops.split("\\:");
									/*
									 * AppContext.debug("paramprops=" +paramprops );
									 * 
									 * String sellabels=(String)mapVal2Label.get(props[3]); if(sellabels==null)
									 * sellabels=""; paramprops=props[0]+":"+props[1]+":"+props[2] //+
									 * " ("+labels.toString().replace("|",",")+")" +":"+sellabels+":";
									 * if(props.length>4) paramprops+=props[4];
									 */
									paramprops += labels;
									// paramprops+=":"+labels;
									AppContext.debug("conditional select paramprops=" + paramprops);

									// Map mapThisParamToolid2CondParam=new LinkedHashMap();
									if (props[0].contains("|")) {
										// conditional
										mapThisParamToolid2CondParam
												.put(props[0].split("\\|")[0] + "#" + jo.getString("id"), props[0]);
									} else {
										mapThisParamToolid2CondParam.put(props[0] + "#" + jo.getString("id"), props[0]);
									}

									// AppContext.debug( "mapThisToolParamLabel2Value=" +
									// mapThisToolParamLabel2Value);
									// AppContext.debug( "mapThisParamToolid2CondParam=" +
									// mapThisParamToolid2CondParam);

									mapToolParamLabel2Value.putAll(mapThisToolParamLabel2Value);
									mapParamToolid2CondParam.putAll(mapThisParamToolid2CondParam);
								} else {
									AppContext.debug("conditional paramprops=" + paramprops);

								}
								params.add(paramprops);

							}

						}
						if (pdict.get("type").equals("select")) {
							if (pdict.has("options")) {
								JSONArray options = pdict.getJSONArray("options");
								StringBuffer labels = new StringBuffer();
								Map mapVal2Label = new HashMap();
								// Map mapThisToolParamLabel2Value=new LinkedHashMap();
								for (int ioptions = 0; ioptions < options.length(); ioptions++) {
									JSONArray labvalsel = options.getJSONArray(ioptions);
									// if(edams_data!=null && edams_data.length()>0)
									// matchedDataTool.addAll(findMatches(formats, edams_data, mapDatatypes[1],
									// "edam_data"));
									mapThisToolParamLabel2Value.put(
											jo.getString("id") + "-" + pname + "-" + labvalsel.getString(0),
											labvalsel.getString(1));
									labels.append(labvalsel.getString(0));
									mapVal2Label.put(labvalsel.getString(1), labvalsel.getString(0));
									if (ioptions < options.length() - 1) {
										labels.append("|");
									}
									if (options.toString().length() < 1000) {
										AppContext.debug("select param2label=" + jo.getString("id") + "-" + pname + "-"
												+ labvalsel.getString(0) + "   --->   " + labvalsel.getString(1));
									}

								}
								// AppContext.debug("paramprops=" +paramprops );
								String props[] = paramprops.split("\\:");
								String sellabels = (String) mapVal2Label.get(props[3]);
								if (sellabels == null)
									sellabels = "";
								paramprops = props[0] + ":" + props[1] + ":" + props[2] // + "
																						// ("+labels.toString().replace("|",",")+")"
										+ ":" + sellabels + ":";
								if (props.length > 4)
									paramprops += props[4];
								paramprops += ":" + labels;
								// paramprops+=":"+labels;
								AppContext.debug("select paramprops=" + paramprops);

								// Map mapThisParamToolid2CondParam=new LinkedHashMap();
								if (props[0].contains("|")) {
									// conditional
									mapThisParamToolid2CondParam
											.put(props[0].split("\\|")[0] + "#" + jo.getString("id"), props[0]);
								} else {
									mapThisParamToolid2CondParam.put(props[0] + "#" + jo.getString("id"), props[0]);
								}

								mapToolParamLabel2Value.putAll(mapThisToolParamLabel2Value);
								mapParamToolid2CondParam.putAll(mapThisParamToolid2CondParam);
							}
						}
						params.add(paramprops);

						Set matchedDataExtTool = new HashSet();
						matchedDataExtTool.addAll(matchedDataTool);
						matchedDataExtTool.addAll(matchedExtTool);
						mapParams2ClassExts.put(pname, matchedDataExtTool);

						matchedExt.addAll(matchedExtTool);
						matchedData.addAll(matchedDataTool);

						// AppContext.debug("tool input:" + mt.getId() + " " + paramprops);
					}
					if (formats != null) {
						// matchedData.addAll(matchedExt);
						if (matchedData.size() > 0 || matchedExt.size() > 0) {
							mapTools2Params.put(mt, params);
							System.out.println("tool=" + mt.getName() + ",  matchedData=" + matchedData
									+ "  matchedExt=" + matchedExt);
							mt.setMapParam2ClassExt(mapParams2ClassExts);
						}
					} else {
						mapTools2Params.put(mt, params);
						mt.setMapParam2ClassExt(mapParams2ClassExts);
					}

					if (mt.getId().equals("get_ts")) {
						AppContext.debug("get_ts params=" + params);
						// AppContext.debug("get_ts params=" + mapTools2Params.get(new
						// MyTool("get_ts")));
					}

					for (String term : matchedData) {
						Set setTools = mapData2Tools.get(term);
						if (setTools == null) {
							setTools = new LinkedHashSet();
							mapData2Tools.put(term, setTools);
						}
						setTools.add(mt);
					}
					for (String term : matchedExt) {
						Set setTools = mapExt2Tools.get(term);
						if (setTools == null) {
							setTools = new LinkedHashSet();
							mapExt2Tools.put(term, setTools);
						}
						setTools.add(mt);
					}
					for (String term : matchedName) {
						Set setTools = mapName2Tools.get(term);
						if (setTools == null) {
							setTools = new LinkedHashSet();
							mapName2Tools.put(term, setTools);
						}
						setTools.add(mt);
					}

					AppContext.debug("mapThisToolParamLabel2Value=" + mapThisToolParamLabel2Value);
					AppContext.debug("mapThisParamToolid2CondParam=" + mapThisParamToolid2CondParam);

				} catch (Exception ex) {
					AppContext.debug("job  = " + jo.get("id"));
					ex.printStackTrace();
					gotall = false;
					// throw ex;

				}

			}
			br.close();
			AppContext.debug("success reading tools file");
			/*
			 * if(formats!=null || classes!=null) { mapData2Tools.putAll(mapName2Tools); }
			 */

			// interp.close();
			AppContext.debug("done close");

		} catch (Exception ex) {
			ex.printStackTrace();
			gotall = false;
		}

		mapData2Tools.remove(null);
		mapTools2Params.remove(null);
		mapName2Tools.remove(null);
		mapExt2Tools.remove(null);

		Map[] m = new Map[] { mapData2Tools, mapTools2Params, mapName2Tools, mapExt2Tools, mapSection2Section };
		if (gotall) {
			mapServer2Tools.put(AppContext.getGalaxyInstance(), m);
		}

		AppContext.debug("get_ts copy toolparams=" + mapTools2Params.get(new MyTool("get_ts")));
		return m;

	}

	//
	// public Map[] discoverTools4(Set<String> nameStarts, Set<String> classes,
	// Set<String> formats, Set<String> sections) {
	// AppContext.debug("discoverTools(Set<String> nameStarts, Set<String> formats,
	// Set<String> sections)");
	//
	// //{mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
	// mapExt2Class,mapClass2Class};
	// Set skipToolId=new HashSet();
	// /*
	// skipToolId.add( "Convert characters1");
	// skipToolId.add(
	// "52.76.88.51/repos/dereeper/numeric_matrix_encoder/bglr:encode/1.0.3");
	// skipToolId.add(
	// "toolshed.g2.bx.psu.edu/repos/iuc/bedtools/bedtools_jaccard/2.27.1+galaxy1");
	// skipToolId.add( "GeneBed_Maf_Fasta2");
	// skipToolId.add( "__EXTRACT_DATASET__");
	// */
	//
	// Map mapClass2Dat=DatatypeMaps.getDatatypes()[0];
	// Map mapClass2Format=DatatypeMaps.getDatatypes()[2];
	//
	// for(String s:classes) {
	// formats.add((String)mapClass2Dat.get(s));
	// String f=(String)mapClass2Format.get(s);
	// if(f!=null) formats.add(f);
	// }
	// formats.remove("");
	// formats.remove("null");
	//
	// AppContext.debug("match edams=" + formats);
	//
	// Map<Tool,Set<String>> mapTools2Params=new HashMap();
	//
	// Map<String,Set<Tool>> mapName2Tools=new HashMap();
	// Map<String,Set<Tool>> mapData2Tools=new HashMap();
	//
	// try {
	// final GalaxyInstance instance =
	// GalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),AppContext.getGalaxyKey());
	// final ToolsClient toolClient = instance.getToolsClient();
	//
	// //PythonInterpreter interp = AppContext.getPythonInterp();
	// PythonInterpreter interp = new PythonInterpreter();
	// //AppContext.getPythonInterp();
	//
	// interp.exec("import sys");
	// interp.exec("try:\n"+
	// " import bioblend\n"+
	// "except:\n" +
	// //" !{sys.executable} -m pip install bioblend");
	// " print('error import bioblend')");
	// interp.exec("from bioblend import galaxy");
	// interp.exec("gi = galaxy.GalaxyInstance(url='http://13.250.4.164:8080',
	// key='dd7ecdf0096f104c0e3ac8fd7f8f8136')");
	//
	// /*
	// interp.exec("try:\n"+
	// " import eventlet\n" +
	// "except:\n" +
	// //" !{sys.executable} -m pip install eventlet");
	// " print('error import eventlet')");
	// interp.exec("from eventlet.timeout import Timeout");
	// */
	//
	// interp.exec("from bioblend.galaxy import GalaxyInstance");
	// interp.exec("from bioblend.galaxy.tools import ToolClient");
	// interp.exec("toolClient = ToolClient(gi)");
	//
	// //mapAll=new Map[] {mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class,
	// mapClass2Ext, mapExt2Class};
	//
	// Map<String,String> mapDatatypes[]=DatatypeMaps.getDatatypes();
	// //{mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
	// mapExt2Class}
	// int sectioncnt=1;
	// Set setMatchData=new HashSet();
	// Set setMatchFormat=new HashSet();
	// Set setMatchExt=new HashSet();
	// for(ToolSection wf:toolClient.getTools()) {
	//
	// System.out.println(sectioncnt + " " + wf.getName().toUpperCase());
	// sectioncnt++;
	// if(sections!=null && !sections.contains(wf.getName())) continue;
	// int toolcnt=1;
	//
	//
	// for(Tool t:wf.getElems()) {
	// System.out.println(toolcnt + " " + t.getId() + " " + t.getName() + " " +
	// t.getDescription() + " " + t.getUrl());
	//
	// if(t.getName()==null) {
	// continue;
	// }
	//
	// // JSONObject jsonObj = new
	// JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
	// if(t.getUrl()!=null && !t.getUrl().isEmpty()) {
	// JSONObject jsonObj=new JSONObject(AppContext.readURL(
	// AppContext.getGalaxyAddress() + t.getUrl() ));
	// System.out.println(jsonObj.toString(4));
	// }
	// //jsonObj.keys()
	//
	// toolcnt++;
	//
	// if(skipToolId.contains(t.getId())) continue;
	// try {
	// interp.exec(
	// "result='error'\n" +
	// "try:\n" +
	// " showtool=toolClient.show_tool(" + t.getId() + ",
	// io_details=True,link_details=False)\n" +
	// " result=showtool\n" +
	// " ti=showtool\n" +
	// "except Exception as ex:\n" +
	// " result='timeout'\n" +
	// " print('exception: ' + str(ex))");
	//
	// /*
	// interp.exec("timeout = Timeout(10, Exception('Timeout'))\n" +
	// "result='error'\n" +
	// "try:\n" +
	// " showtool=toolClient.show_tool(" + t.getId() + ",
	// io_details=True,link_details=False)\n" +
	// " result=showtool\n" +
	// " ti=showtool\n" +
	// "except Exception as ex:\n" +
	// " result='timeout'\n" +
	// "finally:\n" +
	// " timeout.cancel()\n");
	// */
	//
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// Object ins= interp.get("result");
	// System.out.println("result=" + ins);
	// continue;
	// }
	// if(interp.get("ti")==null) continue;
	// interp.exec("if 'inputs' in ti.keys():\n inputParams = ti['inputs']\nelse:\n
	// inputParams=[]\n");
	//
	// PyList pl=(PyList)interp.get("inputParams");
	//
	// if(pl.size()==0) {
	// System.out.println("inputParams.len=0");
	// continue;
	// }
	//
	// Set<String> matchedData=new LinkedHashSet();
	// Set<String> matchedName=new LinkedHashSet();
	//
	// Set params=new LinkedHashSet();
	//
	// Iterator itPl=pl.iterator();
	// while(itPl.hasNext()) {
	// PyDictionary pdict= (PyDictionary)itPl.next();
	// String pname=(String)pdict.get("name");
	// String paramprops=pdict.get("name") + ":" + pdict.get("type") + ":" +
	// pdict.get("extensions") +":" + pdict.get("label");
	// System.out.println( " " + paramprops);
	// for(String n:nameStarts) {
	// if(pname.toLowerCase().startsWith(n.toLowerCase())) {
	// matchedName.add(pname+":"+ pdict.get("extensions"));
	// //matchedName.add(pname);
	// }
	// }
	// if(pdict.get("type").equals("data")) {
	// PyDictionary edams= (PyDictionary)pdict.get("edam");
	// if(edams==null || edams.equals("None")) {}
	// else {
	// PyList datalist= (PyList)edams.get("edam_data");
	// PyList formatlist= (PyList)edams.get("edam_formats");
	//
	// System.out.println(datalist + "\n" + formatlist);
	// if(datalist!=null && !datalist.equals("None"))
	// matchedData.addAll(findMatches(formats, datalist, mapDatatypes[1],
	// "edam_data"));
	// if(formatlist!=null && !formatlist.equals("None"))
	// matchedData.addAll(findMatches(formats, formatlist, mapDatatypes[3],
	// "edam_format"));
	// }
	// }
	// PyList extslist= (PyList)pdict.get("extensions");
	// if(extslist!=null && !extslist.equals("None")) {
	// matchedData.addAll(findMatches(formats, extslist, mapDatatypes[5],
	// "file_ext"));
	// System.out.println(extslist);
	// }
	// params.add(pdict.get("name") + ":" + pdict.get("type") + ":" +
	// pdict.get("extensions") +":" + pdict.get("label") );
	// }
	// if(matchedData.size()>0) {
	// mapTools2Params.put(t, params);
	// System.out.println("matchedParams=" + matchedData);
	// interp.exec("print(inputParams)");
	// }
	// interp.exec("del ti");
	//
	// for(String term:matchedData) {
	// Set setTools=mapData2Tools.get(term);
	// if(setTools==null) {
	// setTools=new LinkedHashSet();
	// mapData2Tools.put(term, setTools);
	// }
	// setTools.add(t);
	// }
	// for(String term:matchedName) {
	// Set setTools=mapData2Tools.get(term);
	// if(setTools==null) {
	// setTools=new LinkedHashSet();
	// mapName2Tools.put(term, setTools);
	// }
	// setTools.add(t);
	// }
	// }
	// }
	//
	// interp.exec("del gi");
	//
	// mapData2Tools.putAll(mapName2Tools);
	// AppContext.debug("done loop");
	// //interp.cleanup();
	// //interp.close();
	// AppContext.debug("done close");
	//
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// }
	// return new Map[] {mapData2Tools,mapTools2Params};
	// }
	//
	//
	// public Map[] discoverTools3(Set<String> nameStarts, Set<String> classes,
	// Set<String> formats, Set<String> sections) {
	// AppContext.debug("discoverTools(Set<String> nameStarts, Set<String> formats,
	// Set<String> sections)");
	//
	// //{mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
	// mapExt2Class,mapClass2Class};
	// Set skipToolId=new HashSet();
	// skipToolId.add( "Convert characters1");
	// skipToolId.add(
	// "52.76.88.51/repos/dereeper/numeric_matrix_encoder/bglr:encode/1.0.3");
	// skipToolId.add(
	// "toolshed.g2.bx.psu.edu/repos/iuc/bedtools/bedtools_jaccard/2.27.1+galaxy1");
	// skipToolId.add( "GeneBed_Maf_Fasta2");
	// skipToolId.add( "__EXTRACT_DATASET__");
	//
	// Map mapClass2Dat=DatatypeMaps.getDatatypes()[0];
	// Map mapClass2Format=DatatypeMaps.getDatatypes()[2];
	//
	// for(String s:classes) {
	// formats.add((String)mapClass2Dat.get(s));
	// String f=(String)mapClass2Format.get(s);
	// if(f!=null) formats.add(f);
	// }
	// formats.remove("");
	// formats.remove("null");
	//
	// AppContext.debug("match edams=" + formats);
	//
	// Map<Tool,Set<String>> mapTools2Params=new HashMap();
	//
	// Map<String,Set<Tool>> mapName2Tools=new HashMap();
	// Map<String,Set<Tool>> mapData2Tools=new HashMap();
	//
	// try {
	// final GalaxyInstance instance =
	// GalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),AppContext.getGalaxyKey());
	// final ToolsClient toolClient = instance.getToolsClient();
	//
	// //PythonInterpreter interp = AppContext.getPythonInterp();
	// PythonInterpreter interp = new PythonInterpreter();
	// //AppContext.getPythonInterp();
	//
	// interp.exec("import sys");
	// interp.exec("from java.lang import Exception");
	// interp.exec("from bioblend.galaxy import GalaxyInstance");
	// interp.exec("from bioblend.galaxy.tools import ToolClient");
	// interp.exec("gi = GalaxyInstance(url='" + AppContext.getGalaxyAddress() + "',
	// key='" + AppContext.getGalaxyKey()+ "')");
	// //interp.exec("gi.set_get_max_retries(5)");
	// interp.exec("print(\"retrydelay= \" + str(gi.get_retry_delay))");
	// interp.exec("print(\"maxattempts= \" + str(gi.max_get_attempts))");
	// interp.exec("toolClient = ToolClient(gi)");
	// interp.exec("toolClient.set_max_get_retries(5)");
	//
	// //mapAll=new Map[] {mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class,
	// mapClass2Ext, mapExt2Class};
	//
	// Map<String,String> mapDatatypes[]=DatatypeMaps.getDatatypes();
	// //{mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
	// mapExt2Class}
	// int sectioncnt=1;
	// Set setMatchData=new HashSet();
	// Set setMatchFormat=new HashSet();
	// Set setMatchExt=new HashSet();
	// for(ToolSection wf:toolClient.getTools()) {
	//
	// System.out.println(sectioncnt + " " + wf.getName().toUpperCase());
	// sectioncnt++;
	// if(sections!=null && !sections.contains(wf.getName())) continue;
	// int toolcnt=1;
	//
	//
	// for(Tool t:wf.getElems()) {
	// System.out.println(toolcnt + " " + t.getId() + " " + t.getName() + " " +
	// t.getDescription() + " " + t.getUrl());
	//
	// if(t.getName()==null) {
	// continue;
	// }
	//
	// // JSONObject jsonObj = new
	// JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
	// if(t.getUrl()!=null && !t.getUrl().isEmpty()) {
	// JSONObject jsonObj=new JSONObject(AppContext.readURL(
	// AppContext.getGalaxyAddress() + t.getUrl() ));
	// System.out.println(jsonObj.toString(4));
	// }
	// //jsonObj.keys()
	//
	// toolcnt++;
	//
	// if(skipToolId.contains(t.getId())) continue;
	//
	//
	// try {
	// //interp.exec("try:\n ti=toolClient.show_tool(tool_id='" + t.getId() +
	// "',io_details=True)\n print(ti)\nexcept Exception as e:\n print(str(e))\n
	// instance = sys.exc_info()[1]");
	// boolean done=exec_with_timeout(interp,"try:\n
	// ti=toolClient.show_tool(tool_id='" + t.getId() + "',io_details=True)\n
	// print(ti)\nexcept Exception as e:\n print(str(e))\n instance =
	// sys.exc_info()[1]",1000);
	// if(!done) {
	// System.out.println("Interrupted: " + t.getId() + " " + t.getName());
	//
	//
	// //interp.close();
	// interp = new PythonInterpreter(); //AppContext.getPythonInterp();
	//
	// interp.exec("import sys");
	// interp.exec("from java.lang import Exception");
	// interp.exec("from bioblend.galaxy import GalaxyInstance");
	// interp.exec("from bioblend.galaxy.tools import ToolClient");
	// interp.exec("gi = GalaxyInstance(url='" + AppContext.getGalaxyAddress() + "',
	// key='" + AppContext.getGalaxyKey()+ "')");
	// //interp.exec("gi.set_get_max_retries(5)");
	// interp.exec("print(\"retrydelay= \" + str(gi.get_retry_delay))");
	// interp.exec("print(\"maxattempts= \" + str(gi.max_get_attempts))");
	// interp.exec("toolClient = ToolClient(gi)");
	// interp.exec("toolClient.set_max_get_retries(5)");
	//
	//
	// continue;
	// }
	//
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// Object ins= interp.get("instance");
	// if(ins==null) {
	// System.out.println("interp.get(\"instance\")==null");
	// continue;
	// }
	// System.out.println(ins.getClass());
	// System.out.println(ins);
	//
	// continue;
	// }
	// interp.exec("hasti = 'no'\nif \"ti\" in self.__dict__:\n hasti = 'yes'");
	// if( interp.get("ti")==null ||
	// ((PyObject)interp.get("hasti")).toString().equals("no")) {
	// System.out.println("ti not defined");
	// continue;
	// }
	// interp.exec("if 'inputs' in ti.keys():\n inputParams = ti['inputs']\nelse:\n
	// inputParams=[]\n");
	// //System.out.println(interp.get("inputParams"));
	// //System.out.println(interp.get("inputParams").getClass());
	// PyList pl=(PyList)interp.get("inputParams");
	//
	// Set<String> matchedData=new LinkedHashSet();
	// Set<String> matchedName=new LinkedHashSet();
	//
	// Set params=new LinkedHashSet();
	//
	// Iterator itPl=pl.iterator();
	// while(itPl.hasNext()) {
	// PyDictionary pdict=(PyDictionary)itPl.next();
	// String pname=(String)pdict.get("name");
	// String paramprops=pdict.get("name") + ":" + pdict.get("type") + ":" +
	// pdict.get("extensions") +":" + pdict.get("label");
	// System.out.println( " " + paramprops);
	// for(String n:nameStarts) {
	// if(pname.toLowerCase().startsWith(n.toLowerCase())) {
	// matchedName.add(pname+":"+ pdict.get("extensions"));
	// //matchedName.add(pname);
	// }
	// }
	// if(pdict.get("type").equals("data")) {
	// PyDictionary edams= (PyDictionary)pdict.get("edam");
	// if(edams==null || edams.equals("None")) {}
	// else {
	// PyList datalist= (PyList)edams.get("edam_data");
	// PyList formatlist= (PyList)edams.get("edam_formats");
	//
	// System.out.println(datalist + "\n" + formatlist);
	// if(datalist!=null && !datalist.equals("None"))
	// matchedData.addAll(findMatches(formats, datalist, mapDatatypes[1],
	// "edam_data"));
	// if(formatlist!=null && !formatlist.equals("None"))
	// matchedData.addAll(findMatches(formats, formatlist, mapDatatypes[3],
	// "edam_format"));
	// }
	// }
	// PyList extslist= (PyList)pdict.get("extensions");
	// if(extslist!=null && !extslist.equals("None")) {
	// matchedData.addAll(findMatches(formats, extslist, mapDatatypes[5],
	// "file_ext"));
	// System.out.println(extslist);
	// }
	// params.add(pdict.get("name") + ":" + pdict.get("type") + ":" +
	// pdict.get("extensions") +":" + pdict.get("label") );
	// }
	// if(matchedData.size()>0) {
	// mapTools2Params.put(t, params);
	// System.out.println("matchedParams=" + matchedData);
	// interp.exec("print(inputParams)");
	// }
	// interp.exec("del ti");
	//
	// for(String term:matchedData) {
	// Set setTools=mapData2Tools.get(term);
	// if(setTools==null) {
	// setTools=new LinkedHashSet();
	// mapData2Tools.put(term, setTools);
	// }
	// setTools.add(t);
	// }
	// for(String term:matchedName) {
	// Set setTools=mapData2Tools.get(term);
	// if(setTools==null) {
	// setTools=new LinkedHashSet();
	// mapName2Tools.put(term, setTools);
	// }
	// setTools.add(t);
	// }
	// }
	// }
	//
	// interp.exec("del gi");
	//
	// mapData2Tools.putAll(mapName2Tools);
	// AppContext.debug("done loop");
	// //interp.cleanup();
	// //interp.close();
	// AppContext.debug("done close");
	//
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// }
	// return new Map[] {mapData2Tools,mapTools2Params};
	// }
	//

	@Override
	public List<String> getDatatypes() {
		return new ArrayList(DatatypeMaps.getDatatypes()[0].keySet());
		// Map[] {mapClass2Dat, mapDat2Class, mapClass2For, mapFor2Class, mapClass2Ext,
		// mapExt2Class};

	}

	@Override
	public List<String> getDataformats() {
		return new ArrayList(DatatypeMaps.getDatatypes()[3].keySet());
	}

	@Override
	public List<String> getDataclass() {
		return new ArrayList(DatatypeMaps.getDatatypes()[6].keySet());
	}

	@Override
	public List<String> getFileexts() {
		return new ArrayList(DatatypeMaps.getDatatypes()[5].keySet());

	}

	private void writeStatus(String msg) {

	}

	@Override
	public String[] runWorkflowAsync(Workflow matchingWorkflow, Map<String, String[]> mapInputname2Filename,
			String jobid) {

		jobsfacade = (JobsFacade) AppContext.checkBean(jobsfacade, "GalaxyJobsFacade");

		String status[] = runWorkflow_(matchingWorkflow, mapInputname2Filename, jobid, false);

		if (status[0].equals("ok"))
			status = new String[] { JobsFacade.JOBSTATUS_DONE, status[1] };
		else if (status[0].equals("failed") || status[0].equals("error"))
			status = new String[] { JobsFacade.JOBSTATUS_ERROR, status[1] };
		else
			status = new String[] { JobsFacade.JOBSTATUS_SUBMITTED, status[1] };

		writeStatus(status[0]);

		if (status[0].equals(JobsFacade.JOBSTATUS_SUBMITTED)) {
			AsyncJob aj = new AsyncJobImpl(jobid, status[1]);
			jobsfacade.addJob(aj);
		}

		return status;
	}

	// @Async("threadPoolTaskExecutor")
	// public Future runWorkflowAsync(Workflow matchingWorkflow, Map<String,String>
	// mapInputname2Filename,
	// String jobid, File outfile) {
	// while(true) {
	// String status=runWorkflow_( matchingWorkflow, mapInputname2Filename,
	// jobid, outfile);
	// if(status.equals(JobsFacade.JOBSTATUS_DONE)) break;
	//
	// writeStatus(status);
	// /*
	// if(status.equals("new"))
	// writeStatus(JobsFacade.JOBSTATUS_SUBMITTED);
	// else if(status.equals("ok"))
	// writeStatus(JobsFacade.JOBSTATUS_DONE);
	// else if(status.equals("queued"))
	// writeStatus(JobsFacade.JOBSTATUS_SUBMITTED);
	// else if(status.equals("running"))
	// writeStatus(JobsFacade.JOBSTATUS_STARTING);
	// else {
	// writeStatus(JobsFacade.JOBSTATUS_ERROR);
	// AppContext.debug("status=" + status);
	// return new AsyncResult<String>(null); //JobsFacade.JOBSTATUS_ERROR;
	// }
	// if(status.equals("ok")) {
	// // copy from galaxy to local server
	// break;
	// }
	// */
	//
	// try {
	// TimeUnit.SECONDS.sleep(30);
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// };
	// }
	// return new AsyncResult<String>(null); //JobsFacade.JOBSTATUS_DONE;
	// }

	@Override
	public String[] runWorkflow(Workflow matchingWorkflow, Map<String, String[]> mapInputname2Filename, String jobid) {
		long maxwaitsec = 60 * 60;
		long seccnt = 0;
		while (true) {
			String[] status = runWorkflow_(matchingWorkflow, mapInputname2Filename, jobid, true);

			if (status.length > 2) {
				if (status[0].equals("ok")) {
					String[] ret = new String[status.length + 2];
					ret[0] = JobsFacade.JOBSTATUS_DONE;
					ret[1] = status[1];
					for (int ir = 2; ir < status.length; ir++)
						ret[ir] = status[ir];
					return ret;
				}
			}
			if (status[0].equals("ok"))
				return new String[] { JobsFacade.JOBSTATUS_DONE, status[1] };
			if (status[0].equals("failed"))
				return new String[] { JobsFacade.JOBSTATUS_ERROR, status[1] };

			AppContext.debug("runWorkflow waiting seccnt=" + seccnt);
			writeStatus(status[0]);

			/*
			 * if(status.equals("new")) writeStatus(JobsFacade.JOBSTATUS_SUBMITTED); else
			 * if(status.equals("ok")) writeStatus(JobsFacade.JOBSTATUS_DONE); else
			 * if(status.equals("queued")) writeStatus(JobsFacade.JOBSTATUS_SUBMITTED); else
			 * if(status.equals("running")) writeStatus(JobsFacade.JOBSTATUS_STARTING); else
			 * { writeStatus(JobsFacade.JOBSTATUS_ERROR); AppContext.debug("status=" +
			 * status); return JobsFacade.JOBSTATUS_ERROR; } if(status.equals("ok")) { //
			 * copy from galaxy to local server break; }
			 */
			try {
				TimeUnit.SECONDS.sleep(30);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			;
			seccnt += 30;
			if (seccnt > maxwaitsec) {
				AppContext.debug("runWorkflow wait timeout after " + maxwaitsec + " secs");
				break;
			}
		}
		return new String[] { JobsFacade.JOBSTATUS_ERROR, null };
	}

	@Override
	public String updateStatus(String jobid, String histid) {

		GalaxyInstance instance = MyGalaxyInstanceFactory.get(AppContext.getGalaxyAddress(), AppContext.getGalaxyKey());
		HistoriesClient historyClient = instance.getHistoriesClient();
		if (histid == null) {
			History histNewjob = (History) exists(historyClient.getHistories(), jobid);
			if (histNewjob == null)
				return JobsFacade.JOBSTATUS_MISSING;
			histid = histNewjob.getId();
		}
		if (histid != null) {
			System.out.println("Retrieving " + jobid + " previous run result");
			String status[] = updateRunning(histid, instance, historyClient, jobid);
			if (status[0].equals("ok"))
				return JobsFacade.JOBSTATUS_DONE;
			else if (status[0].equals("running"))
				return JobsFacade.JOBSTATUS_STARTING;
			else if (status[0].equals("queued"))
				return JobsFacade.JOBSTATUS_SUBMITTED;
			else if (status[0].equals("new"))
				return JobsFacade.JOBSTATUS_SUBMITTED;

			return JobsFacade.JOBSTATUS_ERROR;
		}
		return JobsFacade.JOBSTATUS_MISSING;
	}

	private String[] updateRunning(String historyId, GalaxyInstance instance, HistoriesClient historyClient,
			String jobid) {

		// already created, get result (last dataset in history)
		System.out.println("updateRunning  Retrieving previous run result");
		List htmloutputs = new ArrayList();

		try {

			boolean success = true;
			boolean running = false;
			boolean queued = false;
			boolean newjob = false;
			boolean error = false;
			boolean hasJob = false;

			JobsClient jobClient = instance.getJobsClient();
			List lError = new ArrayList();
			for (Job j : jobClient.getJobsForHistory(historyId)) {
				System.out.println("job tool:" + j.getToolId() + " " + j.getState() + " " + j.getUpdated());
				success = success && j.getState().equals("ok");
				running = running || j.getState().equals("running");
				queued = queued || j.getState().equals("queued");
				newjob = newjob || j.getState().equals("new");
				error = error || j.getState().equals("error");
				hasJob = true;
				if (j.getState().equals("error")) {
					lError.add(j.getToolId());
				}
				;

			}
			if (!hasJob) {
				System.out.println("history " + historyId + " has no job");
				success = false;
				queued = true;
			}
			System.out.println(
					"success=" + success + ", running=" + running + ", newjob=" + newjob + ", queued=" + queued);
			HistoryContents outputDataset = null;
			if (!success) {
				if (running)
					return new String[] { "running", historyId };
				if (queued)
					return new String[] { "queued", historyId };
				if (newjob)
					return new String[] { "new", historyId };

				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(AppContext.getTempDir() + jobid + ".error"));
					bw.write("Error is steps: " + lError + "\n\nSee details in Galaxy " + AppContext.getGalaxyAddress()
							+ "/histories/view?id=" + historyId);
					bw.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (error)
					return new String[] { "failed", historyId };

				return new String[] { "failed", historyId };
			}

			try {
				// last visible dataset in history is output
				AppContext.debug("showHistoryContents:");
				int htmlcnt = 0;
				for (HistoryContents historyDataset : historyClient.showHistoryContents(historyId)) {
					Dataset ds = historyClient.showDataset(historyId, historyDataset.getId());
					if (ds.getVisible()) {
						outputDataset = historyDataset;

						if (ds.getDataTypeExt().equals("html") || ds.getDataType().equals("html")) {
							htmlcnt++;
							htmloutputs.add(ds.getFullDownloadUrl());
							AppContext.debug("html output " + htmlcnt + "  " + ds.getFullDownloadUrl());

							AppContext.downloadURL(ds.getFullDownloadUrl(), new File(
									AppContext.getTempDir() + jobid + "-" + htmlcnt + "." + ds.getDataTypeExt()));
						}
					}

					AppContext.debug(historyDataset.getName() + "\t" + historyDataset.getState() + "  "
							+ historyDataset.getType() + "  " + historyDataset.getHistoryContentType() + "  "
							+ historyDataset.isPurged() + "  " + historyDataset.isDeleted() + "  " + ds.getVisible());
				}

				System.out.println("file =" + outputDataset.getId() + ",  Job done..downloading history " + historyId
						+ ", job " + jobid + " from galaxy to snpseek/temp ");

				// historyClient.downloadDataset(historyId, outputDataset.getId(), outfile);
				Dataset ds = historyClient.showDataset(historyId, outputDataset.getId());
				System.out.println("ds.getFileSize()=" + ds.getFileSize() + "\n ds.getDownloadUrl()="
						+ ds.getDownloadUrl() + "\n ds.getFullDownloadUrl()=" + ds.getFullDownloadUrl()
						+ "\n ds.getGalaxyUrl()=" + ds.getGalaxyUrl() + "\n ds.getUrl()=" + ds.getUrl()
						+ "\n ds.getDataType()=" + ds.getDataType() + ", ds.getDataTypeExt()=" + ds.getDataTypeExt()
						+ "\n ds.getDataTypeClass()=" + ds.getDataTypeClass());

				AppContext.downloadURL(ds.getFullDownloadUrl(),
						new File(AppContext.getTempDir() + jobid + "." + ds.getDataTypeExt()));

				// historyClient.downloadDataset(historyId, outputDataset.getId(), new
				// File(AppContext.getTempDir() + jobid + "-2.zip"));

			} catch (Exception ex) {
				ex.printStackTrace();
				/*
				 * try { historyClient.downloadDataset(historyId, outputDataset.getId(), new
				 * File(jobid + ".zip")); } catch(Exception ex2) { ex2.printStackTrace(); }
				 */
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(AppContext.getTempDir() + jobid + ".error"));
					bw.write("Error in update: " + ex.getMessage() + "\n\nTry to reload this page.");
					bw.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
				return new String[] { "failed", historyId };
			}

			if (htmloutputs.size() > 0) {
				String[] retstr = new String[htmloutputs.size() + 2];
				retstr[0] = "ok";
				retstr[1] = historyId;
				for (int ir = 0; ir < htmloutputs.size(); ir++) {
					retstr[ir + 2] = (String) htmloutputs.get(ir);
				}
				return retstr;
			} else
				return new String[] { "ok", historyId };
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String[] { "failed", historyId };
	}

	private String[] runTool_(MyTool matchingTool, Map<String, String[]> mapInputname2Filename, String jobid,
			boolean sync) {
		return runToolWorkflow_(matchingTool, null, mapInputname2Filename, jobid, sync);

	}
	// mapInputname2Filename
	// Map<paramname, String[value,type,tool_id]

	private String[] runToolWorkflow_(MyTool matchingTool, Workflow matchingWorkflow,
			Map<String, String[]> mapInputname2Filename, String jobid, boolean sync) {

		// mapParam2Value.put( param ,new String[] {value, typestepno[0],step});

		AppContext.debug("in runToolWorkflow_ ");

		GalaxyInstance instance = null;
		try {

			if (sync)
				instance = GalaxyInstanceFactory.get(AppContext.getGalaxyAddress(), AppContext.getGalaxyKey());
			else
				instance = MyGalaxyInstanceFactory.get(AppContext.getGalaxyAddress(), AppContext.getGalaxyKey());

			// final GalaxyInstance instance =
			// GalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),
			// AppContext.getGalaxyKey());

			// Find history
			final HistoriesClient historyClient = instance.getHistoriesClient();

			History histNewjob = (History) exists(historyClient.getHistories(), jobid);
			if (histNewjob != null) {
				// already created, get result (last dataset in history)
				System.out.println(
						"runToolWorkflow_ Retrieving " + jobid + " previous run result  history " + histNewjob.getId());
				return updateRunning(histNewjob.getId(), instance, historyClient, jobid);
			}

			histNewjob = historyClient.create(new History(jobid));
			System.out.println("runToolWorkflow_ New history " + histNewjob.getId() + "  " + jobid);

			final ToolsClient toolsClient = instance.getToolsClient();

			AppContext.debug("mapInputname2Filename:");
			for (String inpname : mapInputname2Filename.keySet()) {
				String fnames[] = mapInputname2Filename.get(inpname);
				AppContext.debug(inpname + " -->" + fnames[0] + "  " + fnames[1] + "  " + fnames[2]);
				if (fnames[1].equals("data_input")) {
					String inputfname = mapInputname2Filename.get(inpname)[0].trim();
					AppContext.debug("loading param " + inpname + " from file " + inputfname);
					String fname = new File(inputfname).getName();
					try {
						if (fname.equals(inputfname))
							toolsClient.uploadRequest(new FileUploadRequest(histNewjob.getId(),
									new File(AppContext.getTempDir() + fname)));
						else
							toolsClient.uploadRequest(new FileUploadRequest(histNewjob.getId(), new File(inputfname)));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// respSAmplelist.getEntity()

			Map<String, String> mapInputname2Datasetid = new HashMap();
			// toolsClient.upload(new FileUploadRequest(histNewjob.getId(),snplist));
			// Assert.assertNotNull(histNewjob);
			String input1Id = null;
			String input2Id = null;
			for (final HistoryContents historyDataset : historyClient.showHistoryContents(histNewjob.getId())) {
				System.out.println("Hist dataset:" + historyDataset.getId() + " " + historyDataset.getName());
				for (String inpname : mapInputname2Filename.keySet()) {
					if (mapInputname2Filename.get(inpname)[1].equals("data_input")) {
						String shortname = new File(mapInputname2Filename.get(inpname)[0]).getName();
						if (historyDataset.getName().equals(shortname)) {
							mapInputname2Datasetid.put(inpname, historyDataset.getId());
							AppContext.debug(shortname + " == " + historyDataset.getName() + "  " + inpname + " --> "
									+ historyDataset.getId());
							break;
						} else {
							AppContext.debug(shortname + " != " + historyDataset.getName());
						}
					}
				}
			}

			if (matchingWorkflow != null) {

				final WorkflowsClient workflowsClient = instance.getWorkflowsClient();
				final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(matchingWorkflow.getId());
				String workflowInput1Id = null;
				String workflowInput2Id = null;

				if (mapServer2Wf2New2OrigLabel == null
						|| mapServer2Wf2New2OrigLabel.get(AppContext.getGalaxyInstance()) == null) {
					discoverWorkflows(null);
				}
				Map mapNew2OrigLabel = mapServer2Wf2New2OrigLabel.get(AppContext.getGalaxyInstance())
						.get(matchingWorkflow.getId());

				Map<String, String> mapInpname2InputEntryKey = new HashMap();

				Map mapParam2Stepno = new HashMap();
				AppContext.debug("inputEntry.labels, value, key:");
				for (final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs()
						.entrySet()) {
					String label = inputEntry.getValue().getLabel();
					AppContext.debug(label + "  " + inputEntry.getValue().getLabel() + "   "
							+ inputEntry.getValue().getValue() + "  " + inputEntry.getKey());
					for (String inpname : mapInputname2Filename.keySet()) {
						if (label.equals(inpname)) {
							mapInpname2InputEntryKey.put(label, inputEntry.getKey());
							AppContext.debug("mapInpname2InputEntryKey:" + label + " -> " + inputEntry.getKey());
							break;
						}
					}
					// inputEntry.getValue().get
					mapParam2Stepno.put(inputEntry.getValue().getLabel(), inputEntry.getKey());
					AppContext.debug(
							"WorkflowInputDefinition  " + inputEntry.getKey() + " " + inputEntry.getValue().getLabel()
									+ " " + inputEntry.getValue().getUuid() + "  " + inputEntry.getValue().getValue());
				}
				System.out.println("Steps..");
				for (final Map.Entry<String, WorkflowStepDefinition> inputEntry : workflowDetails.getSteps()
						.entrySet()) {
					System.out.println(inputEntry.getKey() + "  " + inputEntry.getValue().getType() + "  "
							+ inputEntry.getValue());
					// inputEntry.
				}
				final WorkflowInputs inputs = new WorkflowInputs();
				inputs.setDestination(new WorkflowInputs.ExistingHistory(histNewjob.getId()));
				inputs.setWorkflowId(matchingWorkflow.getId());
				AppContext.debug("mapInputname2Filename.keys=" + mapInputname2Filename.keySet());
				AppContext.debug("setting data_input parameters:");
				for (String inpname : mapInpname2InputEntryKey.keySet()) {
					AppContext.debug(inpname + "  " + mapInputname2Filename.get(inpname)[0] + "  "
							+ mapInputname2Filename.get(inpname)[1] + "  " + mapInputname2Filename.get(inpname)[2]);
					if (!mapInputname2Filename.get(inpname)[1].equals("data_input"))
						continue;
					System.out.println(
							"setting input_file=" + inpname + ",  inputentryid=" + mapInpname2InputEntryKey.get(inpname)
									+ ", datasetid=" + mapInputname2Datasetid.get(inpname));
					inputs.setInput(mapInpname2InputEntryKey.get(inpname), new WorkflowInputs.WorkflowInput(
							mapInputname2Datasetid.get(inpname), WorkflowInputs.InputSourceType.HDA));
					// inputs.setInput(workflowInput2Id, new WorkflowInputs.WorkflowInput(input2Id,
					// WorkflowInputs.InputSourceType.HDA));
				}

				// get MyWorkflow
				Map mapWfname2Wf = discoverWorkflows(null)[2];
				MyWorksflow mwf = (MyWorksflow) mapWfname2Wf.get(workflowDetails.getName());

				// mapParam2Value.put( param ,new String[] {value, typestepno[0],step});

				System.out.println(mapParam2Stepno);
				for (String param : mapInputname2Filename.keySet()) {
					System.out.println("param=" + param + "  " + mapInputname2Filename.get(param)[0] + "   "
							+ mapInputname2Filename.get(param)[1] + "   " + mapInputname2Filename.get(param)[2]);
					if (mapInputname2Filename.get(param)[1].equals("data_input"))
						continue;
					// String step=(String)mapParam2Stepno.get(param);

					String step = null;
					String toolId = null;
					try {
						// toolId= mapInputname2Filename.get(param)[2].split("\\#")[1];
						// step= mapInputname2Filename.get(param)[2].split("\\#")[0];
						String[] paramsSplit = param.split("\\#");
						if (paramsSplit.length == 3) {
							toolId = paramsSplit[2];
							step = paramsSplit[0];
						}
						if (toolId == null)
							continue;
					} catch (Exception ex) {
						ex.printStackTrace();
						AppContext.debug("param=" + param);
					}

					// System.out.println("setting param=" + param + " , step=" + step +", value=" +
					// mapInputname2Filename.get(param)[0]);
					// inputs.setStepParameter( Integer.parseInt(step),new ToolParameter(param,
					// mapInputname2Filename.get(param)[0]));
					// inputs.setStepParameter(Integer.parseInt(step), "Input parameter",
					// mapInputname2Filename.get(param)[0]);
					// inputs .setStepParameter(Integer.parseInt(step), "Input parameter",
					// mapInputname2Filename.get(param)[0]);

					if (mapInputname2Filename.get(param)[0] != null
							&& !((String) mapInputname2Filename.get(param)[0]).trim().isEmpty()) {

						String[] paramsplit = param.split("\\#");
						if (paramsplit.length == 3) {
							// i+"#"+inpname+"#"+o.get("tool_id")
							// type=data

							if (mapToolParamLabel2Value.containsKey(
									toolId + "-" + paramsplit[1] + "-" + mapInputname2Filename.get(param)[0].trim())) {
								String optionvalue = (String) mapToolParamLabel2Value.get(toolId + "-" + paramsplit[1]
										+ "-" + mapInputname2Filename.get(param)[0].trim());
								System.out.println("setting select setStepParameter param=" + paramsplit[1]
										+ ", step(unused)=" + step + "  toolId=" + toolId + ", label="
										+ mapInputname2Filename.get(param)[0] + ", value=" + optionvalue);
								inputs.setStepParameter(Integer.valueOf(paramsplit[0]), paramsplit[1], optionvalue);

							} else {
								System.out.println("mapToolParamLabel2Value not found " + toolId + "-" + paramsplit[1]
										+ "-" + mapInputname2Filename.get(param)[0].trim());
								System.out.println("mapToolParamLabel2Value=" + mapToolParamLabel2Value);
								System.out.println("setting setStepParameter param=" + paramsplit[1] + ",  step="
										+ paramsplit[0] + ", inpname=" + paramsplit[1] + ", toolid=" + paramsplit[2]
										+ ", value=" + mapInputname2Filename.get(param)[0]);
								inputs.setStepParameter(Integer.valueOf(paramsplit[0]), paramsplit[1],
										mapInputname2Filename.get(param)[0].trim());
							}
						} else {

							// type=data_input

							// System.out.println("setting step=" + step + ", Input parameter , value=" +
							// mapInputname2Filename.get(param)[0]);
							// inputs.setStepParameter(step, "Input parameter",
							// mapInputname2Filename.get(param)[0].trim());
							if (toolId != null) {
								if (mapToolParamLabel2Value.containsKey(
										toolId + "-" + param + "-" + mapInputname2Filename.get(param)[0].trim())) {
									String optionvalue = (String) mapToolParamLabel2Value.get(
											toolId + "-" + param + "-" + mapInputname2Filename.get(param)[0].trim());
									System.out.println("setting select setToolParameter param=" + param
											+ ", step(unused)=" + step + "  toolId=" + toolId + ", label="
											+ mapInputname2Filename.get(param)[0] + ", value=" + optionvalue);
									inputs.setToolParameter(toolId, param, optionvalue);

								} else {

									// System.out.println("mapToolParamLabel2Value not found " +
									// toolId+"-"+paramsplit[1]+"-"+ mapInputname2Filename.get(param)[0].trim());
									// System.out.println("mapToolParamLabel2Value=" + mapToolParamLabel2Value);
									System.out.println("setting setToolParameter param=" + param + ", step(unused)="
											+ step + "  toolId=" + toolId + ", value="
											+ mapInputname2Filename.get(param)[0]);
									inputs.setToolParameter(toolId, param, mapInputname2Filename.get(param)[0].trim());
								}
							}
							// System.out.println("setting param=" + param + ", step(unused)=" + step+ "
							// toolId=" + toolId +", value=" + mapInputname2Filename.get(param)[0]);
							// inputs.set .seti .setToolParameter(toolId,param,
							// mapInputname2Filename.get(param)[0].trim());
							// inputs.setInput(mapInpname2InputEntryKey.get(inpname), new WorkflowInputs.
							// .WorkflowInput(mapInputname2Datasetid.get(inpname),
							// WorkflowInputs.InputSourceType.HDA));

							// String steptool=mwf.getInpconections2Stepno( step + "-" + param);
							// System.out.println("setting steptool=" + steptool + " param="+ param + "
							// value=" + mapInputname2Filename.get(param)[0] +" assigned from " + step + "-"
							// + param);
							// inputs.setStepParameter(steptool, param,
							// mapInputname2Filename.get(param)[0].trim());
						}

					}

				}

				WorkflowOutputs output = null;
				try {

					// setParameters_normalized
					// workflowsClient.setParameters_normalized(true);
					inputs.setParametersNormalized(
							mapServer2WfSubwf.get(AppContext.getGalaxyInstance()).contains(matchingWorkflow.getId()));
					output = workflowsClient.runWorkflow(inputs);
				} catch (Exception ex) {
					ex.printStackTrace();
					return new String[] { "failed", ex.getMessage() };
				}

				System.out.println("getOutputIds=" + output.getOutputIds());

				JobsClient jc = instance.getJobsClient();
				for (Job j : jc.getJobsForHistory(output.getHistoryId())) {
					System.out.println("job: " + j.getId() + " " + j.getState() + "  " + j.getToolId() + "  "
							+ j.getUpdated() + "  " + j.getUrl());
				}
				// workflowsClient.( ) .showWorkflow(output.)
				System.out.println("job running in " + histNewjob.getUrl());

				System.out.println("Running workflow in history " + output.getHistoryId());
				String output2Id = null;
				for (String outputId : output.getOutputIds()) {
					System.out.println("  Workflow writing to output id " + outputId);

				}

			}

			else if (matchingTool != null) {

				final Tool toolDetails = toolsClient.showTool(matchingTool.getId());
				String workflowInput1Id = null;
				String workflowInput2Id = null;

				/*
				 * final History history = TestHelpers.createTestHistoryObject(instance,
				 * "randomlineshistorytest"); final String inputId =
				 * TestHelpers.populateTestDataset(instance, history.getId(), "1\n2\n3\n");
				 * 
				 * final Map<String, Object> parameters = new HashMap<String, Object>(); final
				 * Map<String, String> inputDict = new HashMap<String, String>();
				 * inputDict.put("src", "hda"); inputDict.put("id", inputId);
				 * parameters.put("input", inputDict); parameters.put("num_lines", "1"); final
				 * ToolInputs toolInputs = new ToolInputs("random_lines1", parameters);
				 * client.create(history, toolInputs);
				 */
				final Map<String, Object> parameters = new HashMap<String, Object>();
				for (String p : matchingTool.getAllParams()) {
					MyToolParam tp = new MyToolParam(p);
					String paramname = tp.getParamProp("name");
					// param types
					// text, integer, float, boolean, genomebuild, select, color, data_column,
					// hidden, hidden_data, baseurl, file, ftpfile, data, data_collection,
					// library_data, drill_down
					if (tp.getParamProp("type").equals("data")) {
						Map<String, String> inputDict = new HashMap<String, String>();
						inputDict.put("src", "hda");
						inputDict.put("id", mapInputname2Datasetid.get(paramname));
						parameters.put(paramname, inputDict);
					}
				}
				for (String param : mapInputname2Filename.keySet()) {
					System.out.println(mapInputname2Filename.get(param)[0] + "   " + mapInputname2Filename.get(param)[1]
							+ "   " + mapInputname2Filename.get(param)[2]);
					if (mapInputname2Filename.get(param)[1].equals("data_input"))
						continue;

					if (mapInputname2Filename.get(param)[0] != null
							&& !((String) mapInputname2Filename.get(param)[0]).trim().isEmpty())
						parameters.put(param, mapInputname2Filename.get(param)[0].trim());
				}
				ToolInputs toolInputs = new ToolInputs(matchingTool.getId(), parameters);
				ToolExecution toolexe = toolsClient.create(histNewjob, toolInputs);
				System.out.println("getOutputIds=" + toolexe.getOutputs());

				JobsClient jc = instance.getJobsClient();
				for (Job j : jc.getJobsForHistory(histNewjob.getId())) {
					System.out.println("job: " + j.getId() + " " + j.getState() + "  " + j.getToolId() + "  "
							+ j.getUpdated() + "  " + j.getUrl());
				}
				// workflowsClient.( ) .showWorkflow(output.)
				System.out.println("job running in " + histNewjob.getUrl());

				System.out.println("Running Tool in history " + histNewjob.getId());
				OutputDataset output2Id = null;
				for (OutputDataset outputds : toolexe.getOutputs()) {
					System.out.println("  Tool writing to output id " + outputds.getId());
					output2Id = outputds;
				}
			}

			return updateRunning(histNewjob.getId(), instance, historyClient, jobid);
			// return runWorkflow_( matchingWorkflow, mapInputname2Filename,jobid);
		} catch (Exception ex) {
			ex.printStackTrace();
			AppContext.debug("mapParamToolid2CondParam=" + mapParamToolid2CondParam);
			AppContext.debug("mapToolParamLabel2Value=" + mapToolParamLabel2Value);
			return new String[] { "failed", ex.getMessage() };
		}

		// return new String[] {"failed",null};
	}

	private String[] runWorkflow_(Workflow matchingWorkflow, Map<String, String[]> mapInputname2Filename, String jobid,
			boolean sync) {
		return runToolWorkflow_(null, matchingWorkflow, mapInputname2Filename, jobid, sync);
	}

	// returns galaxy status (ok,new,running, etc), historyid
	// private String[] runWorkflow_2(Workflow matchingWorkflow, Map<String,
	// String[]> mapInputname2Filename,
	// String jobid) {
	//
	//
	// final GalaxyInstance instance =
	// GalaxyInstanceFactory.get(AppContext.getGalaxyAddress(),
	// AppContext.getGalaxyKey());
	// final WorkflowsClient workflowsClient = instance.getWorkflowsClient();
	//
	// // Find history
	// final HistoriesClient historyClient = instance.getHistoriesClient();
	//
	// History histNewjob=(History)exists( historyClient.getHistories(),jobid);
	// if(histNewjob!=null) {
	// // already created, get result (last dataset in history)
	// System.out.println("Retrieving " + jobid + " previous run result");
	// return updateRunning(histNewjob.getId(), instance, historyClient,jobid);
	// }
	//
	// try {
	//
	// histNewjob= historyClient.create(new History(jobid));
	//
	// final ToolsClient toolsClient = instance.getToolsClient();
	//
	// for(String inpname:mapInputname2Filename.keySet()) {
	// String fnames[]=mapInputname2Filename.get(inpname);
	// if(fnames[1].equals("data_input")) {
	// AppContext.debug("loading param " + inpname + " from file " +
	// mapInputname2Filename.get(inpname)[0]);
	// ClientResponse resp=toolsClient.uploadRequest(new
	// FileUploadRequest(histNewjob.getId(),new
	// File(mapInputname2Filename.get(inpname)[0])));
	// }
	// }
	//
	// //respSAmplelist.getEntity()
	//
	// Map<String,String> mapInputname2Datasetid=new HashMap();
	// //toolsClient.upload(new FileUploadRequest(histNewjob.getId(),snplist));
	// // Assert.assertNotNull(histNewjob);
	// String input1Id = null;
	// String input2Id = null;
	// for(final HistoryContents historyDataset :
	// historyClient.showHistoryContents(histNewjob.getId())) {
	// //System.out.println("Hist dataset:" + historyDataset.getId() + " " +
	// historyDataset.getName());
	// for(String inpname:mapInputname2Filename.keySet()) {
	// if(mapInputname2Filename.get(inpname)[1].equals("data_input")) {
	// String shortname=new File(mapInputname2Filename.get(inpname)[0]).getName();
	// if(historyDataset.getName().equals(shortname)) {
	// mapInputname2Datasetid.put(inpname , historyDataset.getId());
	// break;
	// }
	// }
	// }
	// }
	//
	// final WorkflowDetails workflowDetails =
	// workflowsClient.showWorkflow(matchingWorkflow.getId());
	// String workflowInput1Id = null;
	// String workflowInput2Id = null;
	//
	// /*
	// try {
	// System.out.println( AppContext.readURL(AppContext.getGalaxyAddress() +
	// workflowDetails.getUrl() ));
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// }
	// */
	// Map<String,String> mapInpname2InputEntryKey=new HashMap();
	//
	//
	// Map mapParam2Stepno=new HashMap();
	//
	// for(final Map.Entry<String, WorkflowInputDefinition> inputEntry :
	// workflowDetails.getInputs().entrySet()) {
	// String label = inputEntry.getValue().getLabel();
	// for(String inpname:mapInputname2Filename.keySet()) {
	// if( label.equals(inpname)) {
	// mapInpname2InputEntryKey.put(label, inputEntry.getKey());
	// break;
	// }
	// }
	// mapParam2Stepno.put( inputEntry.getValue().getLabel(), inputEntry.getKey());
	// AppContext.debug("WorkflowInputDefinition " + inputEntry.getKey() + " " +
	// inputEntry.getValue().getLabel() + " " + inputEntry.getValue().getUuid() + "
	// " + inputEntry.getValue().getValue());
	// }
	//
	//
	// System.out.println("Steps..");
	// for(final Map.Entry<String, WorkflowStepDefinition> inputEntry :
	// workflowDetails.getSteps().entrySet()) {
	// System.out.println(inputEntry.getKey() + " " +
	// inputEntry.getValue().getType() + " " + inputEntry.getValue().toString());
	// }
	//
	//
	// final WorkflowInputs inputs = new WorkflowInputs();
	// inputs.setDestination(new
	// WorkflowInputs.ExistingHistory(histNewjob.getId()));
	// inputs.setWorkflowId(matchingWorkflow.getId());
	//
	//
	// for(String inpname:mapInpname2InputEntryKey.keySet()) {
	//
	// System.out.println("setting input_file=" + inpname + ", inputentryid=" +
	// mapInpname2InputEntryKey.get(inpname) + ", datasetid=" +
	// mapInputname2Datasetid.get(inpname));
	// inputs.setInput(mapInpname2InputEntryKey.get(inpname), new
	// WorkflowInputs.WorkflowInput(mapInputname2Datasetid.get(inpname),
	// WorkflowInputs.InputSourceType.HDA));
	// //inputs.setInput(workflowInput2Id, new
	// WorkflowInputs.WorkflowInput(input2Id, WorkflowInputs.InputSourceType.HDA));
	// }
	//
	// System.out.println(mapParam2Stepno);
	// for(String param:mapInputname2Filename.keySet()) {
	// System.out.println( mapInputname2Filename.get(param)[0] + " " +
	// mapInputname2Filename.get(param)[1] + " " +
	// mapInputname2Filename.get(param)[2]);
	// if(mapInputname2Filename.get(param)[1].equals("data_input")) continue;
	// //String step=(String)mapParam2Stepno.get(param);
	// String step= mapInputname2Filename.get(param)[2].split("-")[1];
	// if(step==null) continue;
	// System.out.println("setting param=" + param + " , step=" + step +", value=" +
	// mapInputname2Filename.get(param)[0]);
	// //inputs.setStepParameter( Integer.parseInt(step),new ToolParameter(param,
	// mapInputname2Filename.get(param)[0]));
	// //inputs.setStepParameter(Integer.parseInt(step), "Input parameter",
	// mapInputname2Filename.get(param)[0]);
	// //inputs .setStepParameter(Integer.parseInt(step), "Input parameter",
	// mapInputname2Filename.get(param)[0]);
	//
	// if(mapInputname2Filename.get(param)[0]!=null && !
	// ((String)mapInputname2Filename.get(param)[0]).trim().isEmpty())
	// inputs.setToolParameter(step,param, mapInputname2Filename.get(param)[0]);
	//
	// }
	//
	// //System.out.println("setting param= reference 'Input parameter' , step=" + 2
	// +", value=nipponbare");
	// // inputs.setToolParameter("vcf2fasta_tabgatk", "reference", "nipponbare");
	//
	// //workflowsClient.runWorkflowResponse(inputs).
	//
	// //final ClientResponse response =
	// workflowsClient.runWorkflowResponse(inputs);
	// //System.out.println("response=" + response.toString());
	//
	// final WorkflowOutputs output = workflowsClient.runWorkflow(inputs);
	//
	// System.out.println( "getOutputIds=" + output.getOutputIds());
	//
	// JobsClient jc= instance.getJobsClient();
	// for(Job j:jc.getJobsForHistory(output.getHistoryId())) {
	// System.out.println("job: " + j.getId() + " " + j.getState() + " " +
	// j.getToolId() + " " + j.getUpdated() + " " + j.getUrl());
	// }
	//
	// //workflowsClient.( ) .showWorkflow(output.)
	// System.out.println("job running in " + histNewjob.getUrl());
	//
	// System.out.println("Running workflow in history " + output.getHistoryId());
	// String output2Id=null;
	// for(String outputId : output.getOutputIds()) {
	// System.out.println(" Workflow writing to output id " + outputId);
	// output2Id=outputId;
	// }
	//
	// return updateRunning(histNewjob.getId() , instance, historyClient,jobid);
	// //return runWorkflow_( matchingWorkflow, mapInputname2Filename,jobid);
	// } catch(Exception ex) {
	// ex.printStackTrace();
	// }
	//
	// return new String[] {"failed",null};
	// }
	//

	public String runWorkflow_vcf2fasta_by_gffmerge_gatk(final String url, final String apiKey, String jobid,
			File snplist, File samplelist, File outfile) throws Exception {
		final GalaxyInstance instance = GalaxyInstanceFactory.get(url, apiKey);
		final WorkflowsClient workflowsClient = instance.getWorkflowsClient();

		// Find history
		final HistoriesClient historyClient = instance.getHistoriesClient();

		History histNewjob = (History) exists(historyClient.getHistories(), jobid);
		if (histNewjob != null) {
			// already created, get result (last dataset in history)
			System.out.println("Retrieving previous run result");

			boolean success = true;
			boolean running = false;
			boolean queued = false;
			boolean newjob = false;

			boolean error = false;

			JobsClient jobClient = instance.getJobsClient();
			boolean hasJob = false;
			for (Job j : jobClient.getJobsForHistory(histNewjob.getId())) {
				System.out.println("job tool:" + j.getToolId() + " " + j.getState() + " " + j.getUpdated());
				success = success && j.getState().equals("ok");
				running = running || j.getState().equals("running");
				queued = queued || j.getState().equals("queued");
				newjob = newjob || j.getState().equals("new");
				error = error || j.getState().equals("error");
				hasJob = true;
			}
			if (!hasJob) {
				success = false;
				System.out.println("history " + histNewjob.getId() + "  has no job");
			}
			if (!success) {
				if (error)
					return "failed";
				if (running)
					return "running";
				if (queued)
					return "queued";
				if (newjob)
					return "new";
				return "failed";
			}

			HistoryContents outputDataset = null;
			for (HistoryContents historyDataset : historyClient.showHistoryContents(histNewjob.getId())) {
				outputDataset = historyDataset;
			}
			historyClient.downloadDataset(histNewjob.getId(), outputDataset.getId(), outfile);
			return "ok";
		}

		histNewjob = historyClient.create(new History(jobid));

		final ToolsClient toolsClient = instance.getToolsClient();

		ClientResponse respSnplist = toolsClient.uploadRequest(new FileUploadRequest(histNewjob.getId(), snplist));
		ClientResponse respSAmplelist = toolsClient
				.uploadRequest(new FileUploadRequest(histNewjob.getId(), samplelist));
		// respSnplist.gete

		// respSAmplelist.getEntity()

		// toolsClient.upload(new FileUploadRequest(histNewjob.getId(),snplist));
		// Assert.assertNotNull(histNewjob);
		String input1Id = null;
		String input2Id = null;
		for (final HistoryContents historyDataset : historyClient.showHistoryContents(histNewjob.getId())) {
			System.out.println("Hist dataset:" + historyDataset.getId() + " " + historyDataset.getName());
			if (historyDataset.getName().equals("snplist.bed")) {
				input1Id = historyDataset.getId();
			}
			if (historyDataset.getName().equals("varlist.txt")) {
				input2Id = historyDataset.getId();
			}
		}

		Workflow matchingWorkflow = null;
		for (Workflow workflow : workflowsClient.getWorkflows()) {
			if (workflow.getName().equals("vcf2fasta_by_gffmerge_gatk")) {
				matchingWorkflow = workflow;
			}
		}

		final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(matchingWorkflow.getId());
		String workflowInput1Id = null;
		String workflowInput2Id = null;
		for (final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
			final String label = inputEntry.getValue().getLabel();
			if (label.equals("snplist")) {
				workflowInput1Id = inputEntry.getKey();
			}
			if (label.equals("samplelist")) {
				workflowInput2Id = inputEntry.getKey();
			}
		}

		final WorkflowInputs inputs = new WorkflowInputs();
		inputs.setDestination(new WorkflowInputs.ExistingHistory(histNewjob.getId()));
		inputs.setWorkflowId(matchingWorkflow.getId());
		inputs.setInput(workflowInput1Id,
				new WorkflowInputs.WorkflowInput(input1Id, WorkflowInputs.InputSourceType.HDA));
		inputs.setInput(workflowInput2Id,
				new WorkflowInputs.WorkflowInput(input2Id, WorkflowInputs.InputSourceType.HDA));
		final WorkflowOutputs output = workflowsClient.runWorkflow(inputs);

		System.out.println("Running workflow in history " + output.getHistoryId());
		String output2Id = null;
		for (String outputId : output.getOutputIds()) {
			System.out.println("  Workflow writing to output id " + outputId);
			output2Id = outputId;
		}

		return runWorkflow_vcf2fasta_by_gffmerge_gatk(url, apiKey, jobid, snplist, samplelist, outfile);

	}

	@Override
	public Map getMapList2Data() {
		// TODO Auto-generated method stub

		// Map[] mapList2ClassEXt=DatatypeMaps.getDatatypesHeirarchy();
		if (true) {
			mapList2Ext = new HashMap();
			Set setInterval = new HashSet();

			mapList2Ext.put("locuslist", setInterval);
			setInterval.add("gff");
			setInterval.add("bed");
			setInterval.addAll((Set) DatatypeMaps.getDatatypesHeirarchy()[0].get("interval"));
			setInterval.addAll((Set) DatatypeMaps.getDatatypesHeirarchy()[1].get("interval"));

			Set setSnp = new HashSet();
			mapList2Ext.put("snplist", setSnp);
			setSnp.add("gff");
			setSnp.add("bed");
			setSnp.add("map");
			setSnp.add("vcf");
			setSnp.addAll((Set) DatatypeMaps.getDatatypesHeirarchy()[0].get("interval"));
			setSnp.addAll((Set) DatatypeMaps.getDatatypesHeirarchy()[1].get("interval"));

			Set setMatrix = new HashSet();
			mapList2Ext.put("snpmatrix", setMatrix);
			setMatrix.add("ped");
			setMatrix.add("encode");
			setMatrix.add("hapmap");
			setMatrix.addAll((Set) DatatypeMaps.getDatatypesHeirarchy()[0].get("genetics"));
			setMatrix.addAll((Set) DatatypeMaps.getDatatypesHeirarchy()[1].get("genetics"));

			Set setSample = new HashSet();
			mapList2Ext.put("samplelist", setSample);
			setSample.add("txt");
			setSample.add("csv");
			setSample.add("tsv");

			Set setPheno = new HashSet();
			mapList2Ext.put("phenolist", setPheno);
			setSample.add("pheno");
			setSample.add("csv");
			setSample.add("tsv");
			setSample.add("txt");
		}
		return mapList2Ext;
	}

}
