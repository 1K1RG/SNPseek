package org.irri.iric.galaxy.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

public interface GalaxyFacade {

	Map[] discoverTools(Set<String> nameStarts, Set<String> classes, Set<String> formats, Set<String> sections);
	Map[] discoverWorkflows(Set<String> nameStarts);
	Map[] discoverWorkflows(Set<String> nameStarts, Set<String> limitWf);
	List<String> getDatatypes();
	List<String> getDataformats(); 
	List<String> getFileexts();
	List<String> getDataclass();
	Collection getSections();
	String[] runWorkflow(Workflow matchingWorkflow, Map<String, String[]> mapParam2Value, String jobid);
	String[] runWorkflowAsync(Workflow wf, Map<String, String[]> mapParam2Value, String jobid);

	//Future<AsyncJobReport> runWorkflowAsync(Workflow matchingWorkflow, Map<String, String> mapInputname2Filename, String jobid, File outfile);
	String updateStatus(String jobid, String histid);
	Map[] discoverTools();
	Map getMapList2Data();
	void clearCache(String string);
		 

}
