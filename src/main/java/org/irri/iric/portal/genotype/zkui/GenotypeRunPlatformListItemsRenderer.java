package org.irri.iric.portal.genotype.zkui;

import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;
import org.irri.iric.portal.AppContext;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class GenotypeRunPlatformListItemsRenderer implements ListitemRenderer {

	@Override
	public void render(Listitem arg0, Object arg1, int arg2) throws Exception {
		
		arg0.setValue(arg1);

		GenotypeRunPlatform v = (GenotypeRunPlatform) arg1;
		addListcell(arg0, v.getDataset());
		addListcell(arg0, v.getVariantset());
		addListcell(arg0, AppContext.datef.format(v.getDatePerformed().getTime()).replace("00:00:00", "").trim());
		addListcell(arg0, v.getMethod());
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
