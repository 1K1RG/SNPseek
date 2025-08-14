package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class LocusListItemRenderer implements ListitemRenderer {

	// private Map mapUniquename2Description;

	private String prefixDesc = "";

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";
	boolean showoverlap = true;

	/*
	 * public LocusGridRenderer(Map mapUniquename2Description) { super();
	 * this.mapUniquename2Description = mapUniquename2Description; }
	 */

	@Override
	public void render(Listitem listitem, Object data, int index) throws Exception {

		Locus locus = (Locus) data;
		listitem.setValue(locus);

		addListcell(listitem, locus.getUniquename());
		addListcell(listitem, locus.getContig());
		addListcell(listitem, locus.getFmin().toString());
		addListcell(listitem, locus.getFmax().toString());
		addListcell(listitem, locus.getStrand().toString());

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
			addListcell(listitem, loci.toString());
		} else
			addListcell(listitem, "");

		if (locus.getDescription() == null) {
			addListcell(listitem, "");
		} else {
			if (prefixDesc == null) {
				addListcell(listitem, locus.getDescription().split("\\s+", 2)[1]);
			} else {
				addListcell(listitem, prefixDesc + locus.getDescription());
			}
		}
	}

	public LocusListItemRenderer(boolean showoverlap) {
		super();
		this.showoverlap = showoverlap;
	}

	public LocusListItemRenderer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocusListItemRenderer(String prefixDesc) {
		super();
		this.prefixDesc = prefixDesc;
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
