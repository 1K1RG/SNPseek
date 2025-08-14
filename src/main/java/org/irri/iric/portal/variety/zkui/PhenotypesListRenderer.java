package org.irri.iric.portal.variety.zkui;

import org.irri.iric.ds.chado.domain.Phenotype;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class PhenotypesListRenderer implements ListitemRenderer {

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem,
	 * java.lang.Object, int)
	 */
	@Override
	public void render(Listitem listitem, Object value, int index) throws Exception {
		
		Phenotype item = (Phenotype) value;

		// keep value in listitem
		listitem.setValue(value);

		String val = "";
		if (item.getQualValue() != null)
			val = item.getQualValue().toString().replaceAll("\\.0+$", "");
		if (item.getQuanValue() != null)
			val = String.format("%.2f", item.getQuanValue()).replaceAll("\\.0+$", ""); // .ro
																						// .toString().replaceAll("\\.0+","");

		addListcell(listitem, item.getDefinition());
		addListcell(listitem, item.getName());
		addListcell(listitem, val);

	}

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
