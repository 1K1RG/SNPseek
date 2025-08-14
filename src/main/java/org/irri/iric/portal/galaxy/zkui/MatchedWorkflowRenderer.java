package org.irri.iric.portal.galaxy.zkui;

import java.util.Collection;
import java.util.Set;

import org.irri.iric.galaxy.service.MyWorksflow;
import org.irri.iric.portal.AppContext;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Vbox;

import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

public class MatchedWorkflowRenderer implements ListitemRenderer{

	//private Map mapTool2Param;
	//private Map mapTool2Match;
	Set setAvailInput;
	boolean showdetails=false;
	public MatchedWorkflowRenderer(Set availInputs, boolean details) {
		super();
		
		this.setAvailInput=availInputs;
		AppContext.debug("MatchedWorkflowRenderer " + availInputs);
		showdetails=details;

	}
	public MatchedWorkflowRenderer() {
		this(null,false);
	}

	@Override
	public void render(Listitem listitem, Object arg1, int arg2) throws Exception {
		// TODO Auto-generated method stub
		Object[] o=null;
		try {
			o=(Object[])arg1;
			
			listitem.setValue(arg1);
			MyWorksflow t=(MyWorksflow)o[0];
			//addListcell(listitem,t.getName());
			//addListcell(listitem,"");
			//addListcell(listitem,o[2].toString());
			//addListcell(listitem,o[1].toString());
//			Collection required=(Collection)o[2];
//
//			AppContext.debug("render " + t.getName() + "   "  + required + "  " + setAvailInput);
//
//			Set matchedInputs=new HashSet();
//			Set requiredNoColumn=new HashSet();
//			for(Object r:required) {
//				if( ((String)r).contains(":")) {
//					AppContext.debug("skipping input " + r );
//					continue;
//				}
//				requiredNoColumn.add(r);
//				if(setAvailInput!=null && setAvailInput.contains(r)) {
//					matchedInputs.add(r);
//				}
//			}
//			
//			AppContext.debug("render " + t.getName() + "  "  + matchedInputs + "   " + requiredNoColumn + "  " + setAvailInput);
//			if(setAvailInput!=null && matchedInputs.size()!=requiredNoColumn.size()) { 
//				AppContext.debug("skipping tool " + t.getName() );
//				return;
//			}
			
			
			addListcell(listitem,t.getName());
			addListcell(listitem,"");

			if(showdetails) {
			addListcell(listitem,o[1]);
			addListcell(listitem,o[2]);
			} else {
				addListcell(listitem,"");
				addListcell(listitem,"");

			}
			
			String annotlink[]=new String[] {};
			try {
				if( t instanceof MyWorksflow) {
					annotlink=((MyWorksflow)t).getAnnotation().split("\\|");
				}
				else annotlink=(new MyWorksflow(t)).getAnnotation().split("\\|");
			} catch(Exception ex) {
				
			}
			String link=null;
			if(annotlink.length>1) link=annotlink[1];
			
			if(annotlink.length>0) 
				addListcell(listitem, annotlink[0],  link );
			else 
				addListcell(listitem, "",  link );
			
		} catch(Exception ex) {
			AppContext.debug("Matchedworkflow Renderer  o[2]="+ o[2] + "  o[1]="+o[1] + "  wf=" + ((Workflow)o[0]).getName());
			ex.printStackTrace();
		}
	}
	
	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	private void addListcell(Listitem listitem, Object value) {
		addListcell(listitem, value, null);
	}

	private void addListcell(Listitem listitem, Object value, String link) {
		String style=STYLE_BORING;
		Listcell lc = new Listcell();
		
		if(link==null) {
			if(value instanceof String) {
				Label lb = new Label((String)value);
				if (!style.isEmpty())
					lb.setStyle(style);
				lb.setParent(lc);
			}
			else if(value instanceof Collection) {
				Vbox vb=new Vbox();
				for(Object o:(Collection)value) {
					String labeldec[]=((String)o).split(":");
					Label lb=null;
					if(labeldec.length>2)
						lb = new Label(labeldec[0] + " - " + labeldec[2]);
					else {
						lb = new Label(labeldec[0] + " - " );
						AppContext.debug("labeldec.length<=2  " +   (String)o);
					}
					if (!style.isEmpty())
						lb.setStyle(style);
					lb.setParent(vb);
				}
				vb.setParent(lc);
			}
		} else {
			Vbox vbox=new Vbox();
			Label lb = new Label((String)value);
			if (!style.isEmpty())
				lb.setStyle(style);
			lb.setParent(vbox);
			A a=new A();
			a.setLabel(link);
			a.setHref(link);
			a.setTarget("galaxy");
			a.setParent(vbox);
			vbox.setParent( lc);
		}
		lc.setParent(listitem);
	}

}
