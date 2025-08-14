package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class LocusGridRenderer implements RowRenderer {

	// private Map mapUniquename2Description;

	private String prefixDesc = "";

	/*
	 * public LocusGridRenderer(Map mapUniquename2Description) { super();
	 * this.mapUniquename2Description = mapUniquename2Description; }
	 */

	@Override
	public void render(Row row, Object data, int index) throws Exception {
		

		// VLocusNotes locus = (VLocusNotes)data;

		// row.setAlign("center");

		Locus locus = (Locus) data;
		new Label(locus.getUniquename()).setParent(row);
		new Label(locus.getContig()).setParent(row);
		new Label(locus.getFmin().toString()).setParent(row);
		new Label(locus.getFmax().toString()).setParent(row);
		new Label(locus.getStrand().toString()).setParent(row);

		if (locus instanceof MergedLoci) {
			MergedLoci ml = (MergedLoci) locus;
			StringBuffer loci = new StringBuffer();
			if (ml.getMSU7Name() != null && !locus.getUniquename().startsWith("LOC_"))
				loci.append(ml.getMSU7Name());
			if (ml.getRAPRepName() != null
					&& !(locus.getUniquename().startsWith("Os0") || locus.getUniquename().startsWith("Os1"))) {
				if (loci.length() > 0)
					loci.append(",");
				loci.append(ml.getRAPRepName());
			}
			if (ml.getRAPPredName() != null
					&& !(locus.getUniquename().startsWith("Os0") || locus.getUniquename().startsWith("Os1"))) {
				if (loci.length() > 0)
					loci.append(",");
				loci.append(ml.getRAPPredName());
			}
			if (ml.getIRICName() != null && !locus.getUniquename().startsWith("OsNippo")) {
				if (loci.length() > 0)
					loci.append(",");
				loci.append(ml.getIRICName());
			}

			if (loci.length() == 0 && ml.getFGeneshName() != null)
				loci.append(ml.getFGeneshName());

			new Label(loci.toString()).setParent(row);
		} else
			new Label("").setParent(row);

		if (locus.getDescription() == null)
			new Label("").setParent(row);
		else {
			if (prefixDesc == null) {
				new Label(locus.getDescription().split("\\s+", 2)[1]).setParent(row);
			} else
				new Label(prefixDesc + locus.getDescription()).setParent(row);
		}

		/*
		 * if(data instanceof VLocusCvtermCvtermpath ) { CvTerm cvterm = (CvTerm)data;
		 * new Label( cvterm.getDefinition()); }
		 */

		/*
		 * if(data instanceof VLocusNotes) { VLocusNotes locus = (VLocusNotes)data; new
		 * Label(locus.getUniquename()).setParent(row); new
		 * Label(locus.getContig()).setParent(row); new
		 * Label(locus.getFmin().toString()).setParent(row); new
		 * Label(locus.getFmax().toString()).setParent(row); new
		 * Label(locus.getStrand().toString()).setParent(row); new Label(
		 * locus.getNotes() ).setParent(row); } else if(data instanceof VLocusCvterm) {
		 * VLocusCvterm locus = (VLocusCvterm)data;
		 * 
		 * new Label(locus.getUniquename()).setParent(row); new
		 * Label(locus.getContig()).setParent(row); new
		 * Label(locus.getFmin().toString()).setParent(row); new
		 * Label(locus.getFmax().toString()).setParent(row); new
		 * Label(locus.getStrand().toString()).setParent(row); new Label(
		 * locus.getCvterm()).setParent(row); }
		 */

		/*
		 * if(mapUniquename2Description!=null) { Object descr =
		 * mapUniquename2Description.get(locus.getUniquename()); if(descr!=null) new
		 * Label((String)descr).setParent(row); }
		 */
	}

	public LocusGridRenderer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocusGridRenderer(String prefix) {
		super();
		// TODO Auto-generated constructor stub
		prefixDesc = prefix;
	}

}
