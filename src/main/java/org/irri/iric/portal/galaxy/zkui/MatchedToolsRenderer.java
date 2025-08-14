package org.irri.iric.portal.galaxy.zkui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.irri.iric.galaxy.service.MyTool;
import org.irri.iric.portal.AppContext;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class MatchedToolsRenderer implements ListitemRenderer{
	Set setAvailInput;
	public MatchedToolsRenderer(Set availInputs) {
		super();
		this.setAvailInput=availInputs;
		
		AppContext.debug("MatchedToolsRenderer " + availInputs);
	}
	
	
	public MatchedToolsRenderer() {
		this(null);
	
	}

	
	@Override
	public void render(Listitem listitem, Object arg1, int arg2) throws Exception {
		// TODO Auto-generated method stub
		try {
		Object[] o=(Object[])arg1;
		
		
		listitem.setValue(arg1);

		//JSONObject t=(JSONObject)o[0];
		//MyTool mt=new MyTool(t);
		MyTool mt=(MyTool)o[0];
		//listitem.setValue(mt);

		addListcell(listitem, mt.getName());
		String sectionname=mt.getSectionId();
		if(!mt.getSectionName().isEmpty())  sectionname=mt.getSectionName();
		addListcell(listitem,sectionname);
		
		mt.setMatchedData((Set)o[2]);
		if(o[2]!=null) {
			
			Collection required=(Collection)o[2];
			Set matchedInputs=new HashSet();
			Set requiredNoColumn=new HashSet();
			for(Object r:required) {
				if( ((String)r).contains(":")) return;
				requiredNoColumn.add(r);
				if(setAvailInput!=null && setAvailInput.contains(r)) {
					matchedInputs.add(r);
				}
			}
			
			AppContext.debug("render " + mt.getName() + matchedInputs + "   " + requiredNoColumn + "  " + setAvailInput);
			if(setAvailInput!=null && matchedInputs.size()!=requiredNoColumn.size()) return;
			

			addListcell(listitem,requiredNoColumn.toString());
			
			//addListcell(listitem,o[2].toString());
		}
		else addListcell(listitem,"");

		mt.setAllParams((Set)o[1]);

		if(o[1]!=null) addListcell(listitem,o[1].toString());
		else addListcell(listitem,"");
		
		String labeldesc=mt.getLabel();
		if(!mt.getDescription().isEmpty())
			labeldesc+= " - " + mt.getDescription();
		
		addListcell(listitem, labeldesc);
		//addListcell(listitem,t.getUrl());
		} catch(Exception ex) {
			AppContext.debug("Matchedtool Renderer");
			ex.printStackTrace();
		}
	}
	
	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	private void addListcell(Listitem listitem, String value) {
		addListcell(listitem, value, STYLE_BORING);
	}

	private void addListcell(Listitem listitem, String value, String style) {
		Listcell lc = new Listcell();
		Label lb = new Label(value);
		if (!style.isEmpty())
			lb.setStyle(style);
		lb.setParent(lc);
		lc.setParent(listitem);
	}

}
