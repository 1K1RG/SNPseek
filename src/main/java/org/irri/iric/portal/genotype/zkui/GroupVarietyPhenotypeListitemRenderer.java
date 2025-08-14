package org.irri.iric.portal.genotype.zkui;

import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class GroupVarietyPhenotypeListitemRenderer implements ListitemRenderer {

	@Override
	public void render(Listitem arg0, Object arg1, int arg2) throws Exception {
		
		arg0.setValue(arg1);
		Object[] arr = (Object[]) arg1;
		addListcell(arg0, (String) arr[0]);
		addListcell(arg0, (String) arr[1]);
		addListcell(arg0, (String) arr[2]);
		if (arr[3] instanceof String)
			addListcell(arg0, (String) arr[3]);
		else if (arr[3] instanceof Number)
			addListcell(arg0, String.format("%.2f", ((Number) arr[3]).doubleValue()));
		else
			addListcell(arg0, arr[3].toString());
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
}
