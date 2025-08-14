package org.irri.iric.portal.genomics.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.irri.iric.galaxy.service.GalaxyFacade;
import org.irri.iric.portal.AppContext;
//import org.testng.Assert;
import org.irri.iric.portal.admin.JobsFacade;
import org.irri.iric.portal.config.IpAddressesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

public class GalaxyAltSeqGenerator {


	@Autowired
	@Qualifier("GalaxyJobsFacade")	
	private JobsFacade jobsfacade;
	@Autowired
	@Qualifier("GalaxyFacade")
	private GalaxyFacade galaxy;
	

	@Autowired
	private static org.irri.iric.portal.config.IpAddressesConfig ipProp;
	
	String refseq = null; // "msu7.all.chrs.fa";
	// String outfile="out4/altgenes";
	String outdir = null;
	String jobid = null;
	String workdir = null;
	String varlistpath = null; // "MANIFEST_AWS";
	//String gatkpath = null; // workdir + "GenomeAnalysisTK.jar";

	public GalaxyAltSeqGenerator(String destdir,String jobid_) {
		super();
		// TODO Auto-generated constructor stub
		outdir = destdir;
		jobid=jobid_;
		workdir = AppContext.getFlatfilesDir() + "getvcfseq/";

		varlistpath = workdir + "MANIFEST_AWS";
		//gatkpath = workdir + "GenomeAnalysisTK.jar";

	}

	String[] getAltSequence( String varlistpath, Map<String, String> intervals, String reference, boolean sync) {

		// windows
		/*
		 * String gatkpath =
		 * "E:\\My Document\\MyEclipse 2015 CI Workspace\\iric_portal_backend\\lib\\GenomeAnalysisTK.jar"
		 * ; String varlistpath = "E:/My Document/Transfer/MANIFEST_AWS"; String
		 * outfile="E:/My Document/Transfer/domestic4genes3k"; String
		 * refseq="E:\\My Document\\Transfer\\msu7\\msu7.all.chrs.fa"; String
		 * tempdir="E:/My Document/Transfer/temp/";
		 */

		// grchpc settings
		// String refseq="msu7.all.chrs.fsa";

		/*
		 * java -jar GenomeAnalysisTK.jar \ -T FastaAlternateReferenceMaker \ -R
		 * reference.fasta \ -o output.fasta \ -L input.intervals \ -V input.vcf \
		 * [--snpmask mask.vcf]
		 */
		// refseq=workdir+ "msu7.all.chrs.fa";

		
		if (reference.toLowerCase().equals("nipponbare") || reference.toLowerCase().equals("japonica nipponbare") ) {
			refseq = workdir + "msu7.all.chrs.fa";
		} else {
			workdir = workdir + reference + "/";
			refseq = workdir + reference + ".fasta";

			// 9311
			// ScaffoldUn0
			// Scaffold

			// kas
			// UM
		}

		try {
			
			
			BufferedWriter bwsnp = new BufferedWriter(new FileWriter(outdir + "locuslist.bed"));
			for(String interval:intervals.values()) {
				String cols[]=interval.split("\\:");
				String cols2[]=cols[1].split("\\-");
				bwsnp.append( cols[0]+"\t"+  (Integer.parseInt(cols2[0])-1)+"\t"+cols2[1]+"\n");
			}
			bwsnp.close();
			
			ipProp = (IpAddressesConfig) AppContext.checkBean(ipProp, "ipAddressesConfig");
			
			String status=runWorkflow_vcf2fasta_by_gffmerge_gatk("http://"+ ipProp +":8080", 
					"6a3612b1923b952e7d749b51eb5e0175",
					jobid, new File(outdir + "locuslist.bed"),new File(varlistpath),
					  new File(outdir + jobid + ".zip"),reference,sync);
			return new String[] {status,null};
//			
//			while(true) {
//				String status=runWorkflow_vcf2fasta_by_gffmerge_gatk(AppContext.getGalaxyAddress(), 
//						AppContext.getGalaxyKey(),
//						jobid, new File(outdir + "locuslist.bed"),new File(varlistpath),
//						  new File(outdir + jobid + ".zip"));
//
//
//				if(status.equals("new"))
//					writeStatus(JobsFacade.JOBSTATUS_SUBMITTED);
//				else if(status.equals("ok")) 
//					writeStatus(JobsFacade.JOBSTATUS_DONE);
//				else if(status.equals("queued"))
//					writeStatus(JobsFacade.JOBSTATUS_SUBMITTED);
//				else if(status.equals("running"))
//					writeStatus(JobsFacade.JOBSTATUS_STARTING);
//				else { 
//					writeStatus(JobsFacade.JOBSTATUS_ERROR);
//					break;
//				}
//				if(status.equals("ok")) {
//					// copy from galaxy to local server
//					break;				
//				}
//				if(sync)
//					TimeUnit.SECONDS.sleep(30);
//				else
//					break;
//			}
//			
		} catch (Exception ex) {
			ex.printStackTrace();
			return new String[] {JobsFacade.JOBSTATUS_ERROR,ex.getMessage()};
		}

	}
	
	void writeStatus(String status) {
		jobsfacade = (JobsFacade) AppContext.checkBean(jobsfacade, "JobsFacade");
		jobsfacade.setStatus(new File(outdir).getName() + ".status", status);
	}


	  public static Object exists(Collection c, String id) {
		  for(Object co:c) {
			  if( ((History)co).getName().equals(id)) {
				  return co;
			  }
		  }
		  return null;
	  }
	  
	  public  String  runWorkflow_vcf2fasta_by_gffmerge_gatk(final String url, final String apiKey,
			  String jobid, File snplist, File samplelist, File outfile,String ref,boolean sync) throws Exception {
		  galaxy = (GalaxyFacade) AppContext.checkBean(galaxy, "GalaxyFacade");
		  
		  GalaxyInstance instance = GalaxyInstanceFactory.get(url, apiKey);
		  WorkflowsClient workflowsClient = instance.getWorkflowsClient();
		  Workflow matchingWorkflow=null;
		  for(Workflow wf:workflowsClient.getWorkflows()) {
			  if(wf.getName().equals("vcf2fasta")) matchingWorkflow=wf;
				  
		  }
		  Map mapInputname2Filename=new HashMap();
		  mapInputname2Filename.put("locuslist", new String[] {snplist.getAbsolutePath(), "data_input","0"});
		  mapInputname2Filename.put("samplelist", new String[] {samplelist.getAbsolutePath(), "data_input","1"});
		  mapInputname2Filename.put("reference", new String[] {ref, "text","vcf2fasta_tabgatk"});
		  
		  
		  if(sync) {
			  String status[]=galaxy.runWorkflow(matchingWorkflow, mapInputname2Filename, jobid);
			  return status[0];
		  } else {
			  String status[]=galaxy.runWorkflowAsync(matchingWorkflow, mapInputname2Filename, jobid);
			  return status[0];
		  }
		  
		  //return galaxy.runWorkflow(matchingWorkflow, mapInputname2Filename, jobid, outfile);
			
	  }
	  
	  
//	  public  String  runWorkflow_vcf2fasta_by_gffmerge_gatk_orig(final String url, final String apiKey,
//			  String jobid, File snplist, File samplelist, File outfile) throws Exception {
//	    final GalaxyInstance instance = GalaxyInstanceFactory.get(url, apiKey);
//	    final WorkflowsClient workflowsClient = instance.getWorkflowsClient();
//
//	    AppContext.debug("Connectiing to galaxy at:" + url + "  key:"+apiKey);
//	    
//	    // Find history
//	    final HistoriesClient historyClient = instance.getHistoriesClient();
//	    
//	    History histNewjob=(History)exists( historyClient.getHistories(),jobid);
//	    if(histNewjob!=null) {
//	    	// already created, get result (last dataset in history)
//	    	System.out.println("Retrieving previous run result"); 
//	    	
//	    	
//	    	boolean success=true;
//	    	boolean running=false;
//	    	boolean queued=false;
//	    	boolean newjob=false;
//	    	
//	    	JobsClient jobClient = instance.getJobsClient();
//	    	for(Job j:jobClient.getJobsForHistory(histNewjob.getId())) {
//	    		System.out.println("job tool:" + j.getToolId() + " " +  j.getState() + " " + j.getUpdated());    
//	    		success= success && j.getState().equals("ok"); 
//	    		running=running || j.getState().equals("running");
//	    		queued=queued || j.getState().equals("queued");
//	    		newjob=newjob || j.getState().equals("new");
//	    		
//	    	}
//	    	if(!success ) {
//	    		if(running) return "running";
//	    		if(queued) return "queued";    		
//	    		if(newjob) return "new";    		
//	    		return "failed";
//	    	}
//	    	
//	    	HistoryContents outputDataset=null;
//	        for(HistoryContents historyDataset : historyClient.showHistoryContents(histNewjob.getId())) {
//	        		outputDataset=historyDataset;
//	        }
//	        historyClient.downloadDataset(histNewjob.getId(), outputDataset.getId(), outfile);
//	        AppContext.debug("downloaded " + histNewjob.getId() + " " + outputDataset.getId() + " to " + outfile.getAbsolutePath());
//	    	return "ok";
//	    }
//	    
//	    histNewjob= historyClient.create(new History(jobid));
//	    
//	    final ToolsClient toolsClient = instance.getToolsClient();
//	    
//	    ClientResponse respSnplist= toolsClient.uploadRequest(new FileUploadRequest(histNewjob.getId(),snplist));
//	    ClientResponse respSAmplelist= toolsClient.uploadRequest(new FileUploadRequest(histNewjob.getId(),samplelist));
//	    //respSnplist.gete
//
//	    //respSAmplelist.getEntity()
//	    
//	    
//	    //toolsClient.upload(new FileUploadRequest(histNewjob.getId(),snplist));
//		//  Assert.assertNotNull(histNewjob);
//		  String input1Id = null;
//		  String input2Id = null;
//		  for(final HistoryContents historyDataset : historyClient.showHistoryContents(histNewjob.getId())) {		  
//		    System.out.println("Hist dataset:" + historyDataset.getId() + " " + historyDataset.getName());
//		    if(historyDataset.getName().equals( snplist.getName())) {
//		      input1Id = historyDataset.getId();
//		    }
//		    else if(historyDataset.getName().equals(samplelist.getName())) {
//		      input2Id = historyDataset.getId();
//		    }
//		  }
//	   
//
//	    Workflow matchingWorkflow = null;
//	    for(Workflow workflow : workflowsClient.getWorkflows()) {
//	      if(workflow.getName().equals("vcf2fasta")) {
//	        matchingWorkflow = workflow;
//	      }
//	    }
//
//	    try {
//	    System.out.println("Running workflow " + AppContext.readURL(AppContext.getGalaxyAddress() + matchingWorkflow.getUrl())); 
//	    }catch(Exception ex) {
//	    	ex.printStackTrace();
//	    }
//	    
//	    final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(matchingWorkflow.getId());
//	    String workflowInput1Id = null;
//	    String workflowInput2Id = null;
//	    String workflowInput3Id = null;
//	    for(final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
//	      final String label = inputEntry.getValue().getLabel();
//	      if(label.equals("locuslist")) {
//	        workflowInput1Id = inputEntry.getKey();
//	      } else if(label.equals("samplelist")) {
//	        workflowInput2Id = inputEntry.getKey();
//	      } else if(label.equals("reference")) {
//		        workflowInput3Id = inputEntry.getKey();
//		  }
//	    }
//	    
//	    //WorkflowInputs.WorkflowInput
//
//	    final WorkflowInputs inputs = new WorkflowInputs();
//	    inputs.setDestination(new WorkflowInputs.ExistingHistory(histNewjob.getId()));
//	    inputs.setWorkflowId(matchingWorkflow.getId());
//	    inputs.setInput(workflowInput1Id, new WorkflowInputs.WorkflowInput(input1Id, WorkflowInputs.InputSourceType.HDA));
//	    inputs.setInput(workflowInput2Id, new WorkflowInputs.WorkflowInput(input2Id, WorkflowInputs.InputSourceType.HDA));
//	    inputs.setStepParameter(2, "reference", "Nipponbare");
//	    //inputs.setInput(workflowInput3Id, new WorkflowInputs.WorkflowInput(input2Id, new InputSourceType("Nipponbare")));
//	    final WorkflowOutputs output = workflowsClient.runWorkflow(inputs);
//	    
//	    
//	    System.out.println("Running workflow in history " + output.getHistoryId());
//	    String output2Id=null;
//	    for(String outputId : output.getOutputIds()) {
//	      System.out.println("  Workflow writing to output id " + outputId);
//	      output2Id=outputId;
//	    }
//	    
//	    return runWorkflow_vcf2fasta_by_gffmerge_gatk(url,apiKey,
//	  		  jobid,snplist,samplelist,outfile,true);
//	    
//	    
//	  }
	
}
