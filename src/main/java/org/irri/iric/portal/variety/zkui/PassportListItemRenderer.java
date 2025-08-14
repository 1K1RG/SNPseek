package org.irri.iric.portal.variety.zkui;

import org.irri.iric.ds.chado.domain.Passport;

//import org.irri.iric.portal.genotype.views.Snp2linesId;

import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class PassportListItemRenderer implements ListitemRenderer {

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
		
		// VIricstockPassport item = (VIricstockPassport)value;
		Passport item = (Passport) value;

		// keep value in listitem
		listitem.setValue(value);

		addListcell(listitem, item.getDefinition());
		addListcell(listitem, item.getName());
		addListcell(listitem, item.getValue());

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
