package org.irri.iric.portal.variety.zkui;

import org.irri.iric.ds.chado.domain.CvTerm;
import org.irri.iric.portal.AppContext;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class CvListItemRenderer implements ListitemRenderer {

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "color:black";

	@Override
	public void render(Listitem listitem, Object value, int index) throws Exception {
		

		String strdisplay = "";
		if (value != null) {
			CvTerm item = (CvTerm) value;
			strdisplay = item.getDefinition();

			AppContext.debug(
					"<listitem label=\"" + item.getDefinition() + "\" value=\"" + item.getCvTermId() + "\" />");

			listitem.setValue(item.getCvTermId());
		} else {
			listitem.setValue(0);
			listitem.setSelected(true);
		}

		addListcell(listitem, strdisplay);
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
