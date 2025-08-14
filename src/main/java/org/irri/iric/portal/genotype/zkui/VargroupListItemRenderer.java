package org.irri.iric.portal.genotype.zkui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.variety.service.Data;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class VargroupListItemRenderer implements ListitemRenderer<List> {

	// private Map<String,String> mapGroup2Color;
	/*
	 * public VargroupListItemRenderer(Map<String, String> mapGroup2Color) {
	 * super(); this.mapGroup2Color = mapGroup2Color; }
	 */

	private Map mapKgroup2AppendValues;
	private List lHeader;

	public VargroupListItemRenderer() {

	}

	public VargroupListItemRenderer(List lHeader, Map mapKgroup2AppendValues) {
		super();
		this.mapKgroup2AppendValues = mapKgroup2AppendValues;
		this.lHeader = lHeader;
	}

	public void appendValues(List lHeader, Map mapKgroup2AppendValues) {
		if (this.mapKgroup2AppendValues == null) {
			this.mapKgroup2AppendValues = mapKgroup2AppendValues;
			this.lHeader = lHeader;
		} else {
			this.lHeader.addAll(lHeader);
			Iterator<String> itk = this.mapKgroup2AppendValues.keySet().iterator();
			while (itk.hasNext()) {
				String k = itk.next();
				List appendList = (List) mapKgroup2AppendValues.get(k);
				if (appendList != null)
					((List) this.mapKgroup2AppendValues.get(k)).addAll(appendList);
				else {
					for (int i = 0; i < lHeader.size(); i++)
						((List) this.mapKgroup2AppendValues.get(k)).add("");
				}
			}
		}
	}

	@Override
	public void render(Listitem listitem, List arg1, int row) throws Exception {
		
		listitem.setValue(arg1);
		/*
		 * Iterator<String> itStr=arg1.iterator(); while(itStr.hasNext()) { String
		 * str=itStr.next(); if(str.equals("0")) str=""; addListcell (listitem, str); }
		 */
		Iterator itStr = arg1.iterator();
		String grp = (String) itStr.next();
		int group = Integer.valueOf(grp);
		String style = "color:" + Data.getKgroupColor(group);
		addListcell(listitem, grp, style);
		addListcell(listitem, (String) itStr.next(), style); // subpop/count
		addListcell(listitem, itStr.next().toString()); // varieties
		addListcell(listitem, String.format("%.02f", (((Double) itStr.next()) * 100.0))); // freq
		int poscnt = 0;
		while (itStr.hasNext()) {
			String str = (String) itStr.next();
			addListcell(listitem, str);
			/*
			 * String sti="color:black;align:center"; if(str.equals("A"))
			 * sti=SNPRowRendererStyle.STYLE_A; else if(str.equals("T"))
			 * sti=SNPRowRendererStyle.STYLE_T; else if(str.equals("G"))
			 * sti=SNPRowRendererStyle.STYLE_G; else if(str.equals("C"))
			 * sti=SNPRowRendererStyle.STYLE_C; addListcell (listitem, str,sti);
			 */
			poscnt++;
		}

		AppContext.debug(poscnt + " cols inrender for " + grp);

		if (mapKgroup2AppendValues != null) {
			List lValues = (List) mapKgroup2AppendValues.get(grp);
			if (lValues == null) {
				for (int i = 0; i < lHeader.size(); i++)
					addListcell(listitem, "");
			} else {
				for (int i = 0; i < lHeader.size(); i++) {
					Object v = lValues.get(i);
					if (v == null)
						addListcell(listitem, "");
					else if (v instanceof Double)
						addListcell(listitem, String.format("%.02f", ((Double) v).doubleValue()));
					else
						addListcell(listitem, v.toString());
				}
			}
		}

	}

	private void addListcell(Listitem listitem, String value) {
		addListcell(listitem, value, "");
	}

	private void addListcell(Listitem listitem, String value, String style) {
		Listcell lc = new Listcell();
		Label lb = new Label(value);
		lb.setPre(true);
		if (!style.isEmpty())
			lb.setStyle(style);
		lb.setParent(lc);
		lc.setParent(listitem);
	}

	public Map getMapKgroup2AppendValues() {
		return mapKgroup2AppendValues;
	}
}
