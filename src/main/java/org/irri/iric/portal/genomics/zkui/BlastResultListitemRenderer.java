package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.LocalAlignment;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class BlastResultListitemRenderer implements ListitemRenderer {

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	@Override
	public void render(Listitem item, Object data, int arg2) throws Exception {
		

		LocalAlignment locus = (LocalAlignment) data;
		item.setValue(locus);
		// row.setAlign("center");

		addListcell(item, locus.getQacc());
		addListcell(item, locus.getQstart().toString());
		addListcell(item, locus.getQend().toString());
		addListcell(item, locus.getSacc());
		addListcell(item, locus.getSstart().toString());
		addListcell(item, locus.getSend().toString());
		String sstrand = "N/A";
		if (locus.getSstrand() != 0)
			sstrand = locus.getSstrand().toString();
		addListcell(item, sstrand);
		addListcell(item,
				locus.getMatches() + "/" + locus.getAlignmentLength() + " (" + locus.getPercentMatches() + "%)");
		addListcell(item, locus.getEvalue().toString());

		
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
