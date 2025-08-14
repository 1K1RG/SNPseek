package org.irri.iric.portal.galaxy.zkui;

import java.util.List;
import java.util.Set;


public interface GalaxyCustomController {

	void generateDataFiles();
	Set checkEmbedList(String embed);
	Set getCommonClass();
	Set getCommonExt();
	String getGalaxyHistoryViewLink(String instance, String historyid);	
	String getGalaxyAddress(String instance);
	String getGalaxyKey(String instance);
	List getGalaxyInstances();

}
