package org.irri.iric.galaxy.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;

public class MyTool {
	
	String id;
	JSONObject j;
	Set sParams;
	Map mParam2Ext;
	Set sMatchedData;

	public MyTool(String id) {
		super();
		this.id = id;
	}
	
	public MyTool(JSONObject o) {
		super();
		j=o;
		try {
			id=o.getString("id");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	String getId() {
		return id;
	}
	
	public Collection<String> getAllParams() {
		//return null;
		return sParams;
		
	}
	
	public String getParamValue(String p) {
		try {
			return j.getString(p);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public Collection getMapParam2ClassExt(String p) {
		return (Collection)mParam2Ext.get(p);
	}
	
	public void setMapParam2ClassExt(Map m) {
		mParam2Ext=m;
			
	}
	
	public String getName() {
		try {
			return j.getString("name");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getSectionId() {
		try {
			return j.getString("panel_section_id");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getSectionName() {
		try {
			return j.getString("panel_section_name");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void setAllParams(Set s) {
		sParams=s;
	}
	public void setMatchedData(Set s) {
		sMatchedData=s;
	}
	
	public String getLabel() {
		try {
			return j.getString("label");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
		
	}
	public String getDescription() {
		try {
			return j.getString("description");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return  getId().equals( ((MyTool)obj).getId());
	}
	
	
	
}
