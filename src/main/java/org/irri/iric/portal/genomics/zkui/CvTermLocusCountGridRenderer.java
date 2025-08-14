package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.CvTermLocusCount;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class CvTermLocusCountGridRenderer implements RowRenderer {

	@Override
	public void render(Row row, Object data, int index) throws Exception {
		
		CvTermLocusCount go = (CvTermLocusCount) data;
		new Label(go.getAccession()).setParent(row);
		new Label(go.getName()).setParent(row);
		new Label(go.getCount().toString()).setParent(row);
		new Label(go.getCV()).setParent(row);
	}

}
