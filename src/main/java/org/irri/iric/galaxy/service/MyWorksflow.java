package org.irri.iric.galaxy.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;

import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

public class MyWorksflow extends Workflow {
	
	String annotation;
	Workflow wf;
	Map mapInpcons2Stepno=new HashMap();
	Set setToolFreeInputs=new HashSet();
	public MyWorksflow(Workflow wf) {
		super();
		this.wf = wf;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public String getName() {
		return wf.getName();
	}
	public void setName(String name) {
		wf.setName(name);
	}
	public void setId(String id) {
		wf.setId(id);
	}
	public void setUrl(String url) {
		wf.setUrl(url);
	}
	public String getUrl() {
		return wf.getUrl();
	}
	public String getId() {
		return wf.getId();
	}
	public int hashCode() {
		//return wf.hashCode(); // .getName().hashCode();
		return wf.getName().hashCode();
	}
	public boolean equals(Object obj) {
		//return wf.equals( (MyWorksflow)obj);
		return wf.getName().equals( ((MyWorksflow)obj).getName() );
		
		
		/*
		if (obj instanceof MyWorksflow)
			return wf.getName().equals(((MyWorksflow)obj).getName());
		else if (obj instanceof Workflow)
			return wf.equals(((Workflow)obj).getName());
		return false;
		*/	
	}
	public String toString() {
		return wf.toString();
	}
	public void setJSONObject(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		
	}
	public void setInpconections2Stepno(Map mapInpcons2Stepno) {
		// TODO Auto-generated method stub
		this.mapInpcons2Stepno=mapInpcons2Stepno;
	}
	
	public String getInpconections2Stepno(String inputstepname) {
		// TODO Auto-generated method stub
		return (String)mapInpcons2Stepno.get(inputstepname);
	}
	public void setToolFreeInputs(Set setToolFreeInputs) {
		// TODO Auto-generated method stub
		this.setToolFreeInputs=setToolFreeInputs;
	}
	
	public Set getToolFreeInputs() {
		// TODO Auto-generated method stub
		return setToolFreeInputs;
	}
	

}
