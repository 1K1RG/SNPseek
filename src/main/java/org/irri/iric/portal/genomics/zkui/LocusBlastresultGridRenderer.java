package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.LocalAlignment;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class LocusBlastresultGridRenderer implements RowRenderer {

	@Override
	public void render(Row row, Object data, int index) throws Exception {
		

		LocalAlignment locus = (LocalAlignment) data;
		row.setValue(locus);
		// row.setAlign("center");

		// new Label(locus.getUniquename()).setParent(row);
		new Label(locus.getQacc()).setParent(row);
		new Label(locus.getQstart().toString()).setParent(row);
		new Label(locus.getQend().toString()).setParent(row);
		new Label(locus.getSacc()).setParent(row);
		new Label(locus.getSstart().toString()).setParent(row);

		new Label(locus.getSend().toString()).setParent(row);

		String sstrand = "N/A";
		if (locus.getSstrand() != 0)
			sstrand = locus.getSstrand().toString();
		new Label(sstrand).setParent(row);
		new Label(locus.getMatches() + "/" + locus.getAlignmentLength() + " (" + locus.getPercentMatches() + "%)")
				.setParent(row);
		;
		new Label(locus.getEvalue().toString()).setParent(row);

		/*
		 * <column label="QUERY" width="150px"/> <column label="Q START" width="100px"/>
		 * <column label="Q END" width="100px"/> <column label="SUBJECT"
		 * width="1500px"/> <column label="S START" width="100px"/> <column
		 * label="S END" width="100px" /> <column label="MATCHES" width="100px" />
		 * <column label="E VALUE" width="100px" />
		 */

	}

}
