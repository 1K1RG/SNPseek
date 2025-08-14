package org.irri.iric.portal.genomics.zkui;

//import org.irri.iric.portal.chado.domain.VLocusCvterm;
//import org.irri.iric.portal.chado.domain.VLocusCvtermCvtermpath;
//import org.irri.iric.portal.chado.domain.VLocusNotes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genomics.service.GenomicsFacadeImpl;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class MarkerVarListItemRenderer implements ListitemRenderer {

	private Set annotations = new LinkedHashSet();

	public MarkerVarListItemRenderer(Set annotations) {
		super();
		this.annotations = annotations;
		AppContext.debug("Rendering " + annotations);
	}

	@Override
	public void render(Listitem listitem, Object data, int arg2) throws Exception {
		MarkerAnnotation marker = (MarkerAnnotation) data;
		listitem.setValue(data);
		listitem.setStyle("vertical-align:top;");

		String row[] = GenomicsFacadeImpl.MarkerAnnotationTable((MarkerAnnotation) data, annotations, "\n");
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < row.length; i++) {
			addListcell(listitem, row[i]);
			buf.append(row[i]).append("\t|\t");

			if (i == 0) {
				if (marker.getMinusLogPvalue() != null) {
					addListcell(listitem, AppContext.decf.format(marker.getMinusLogPvalue()));
					buf.append(row[i]).append("\t|\t");
				} else {
					addListcell(listitem, "");
					buf.append("\t|\t");
				}
			}

		}
		// AppContext.debug(buf.toString());
	}

	// @Override
	public void renderOld(Listitem listitem, Object data, int arg2) throws Exception {

		MarkerAnnotation annot = (MarkerAnnotation) data;
		listitem.setValue(annot);

		Collection colPos = annot.getAnnotation(MarkerAnnotation.MARKER_POSITION);
		if (colPos == null || colPos.isEmpty()) {
			addListcell(listitem, annot.getContigPosition().toString());
		} else {
			StringBuffer buffpos = new StringBuffer();
			Iterator<Position> itPos = new TreeSet(colPos).iterator();
			while (itPos.hasNext()) {
				buffpos.append(itPos.next());
				if (itPos.hasNext())
					buffpos.append(",");
			}
			addListcell(listitem, buffpos.toString());
		}

		// if(annot.getGenes()!=null && !annot.getGenes().isEmpty()) {
		if (annot.getAnnotation(MarkerAnnotation.GENE_MODEL) != null
				&& !annot.getAnnotation(MarkerAnnotation.GENE_MODEL).isEmpty()) {
			// Iterator<Locus> itLocus=annot.getGenes().iterator();
			Iterator<Locus> itLocus = annot.getAnnotation(MarkerAnnotation.GENE_MODEL).iterator();
			StringBuffer buff = new StringBuffer();
			while (itLocus.hasNext()) {
				Locus loc = itLocus.next();
				buff.append(
						loc.getUniquename() + " " + loc.getContig() + " [" + loc.getFmin() + "-" + loc.getFmax() + "]");
				if (loc.getDescription() != null && !loc.getDescription().isEmpty()) {
					buff.append(" -" + loc.getDescription());
				}

				if (loc instanceof MergedLoci) {
					MergedLoci ml = (MergedLoci) loc;
					StringBuffer loci = new StringBuffer();
					if (ml.getMSU7Name() != null && !loc.getUniquename().startsWith("LOC_"))
						loci.append(ml.getMSU7Name());
					if (ml.getRAPRepName() != null
							&& !(loc.getUniquename().startsWith("Os0") || loc.getUniquename().startsWith("Os1"))) {
						if (loci.length() > 0)
							loci.append(",");
						loci.append(ml.getRAPRepName());
					}
					if (ml.getRAPPredName() != null
							&& !(loc.getUniquename().startsWith("Os0") || loc.getUniquename().startsWith("Os1"))) {
						if (loci.length() > 0)
							loci.append(",");
						loci.append(ml.getRAPPredName());
					}
					if (ml.getIRICName() != null && !loc.getUniquename().startsWith("OsNippo")) {
						if (loci.length() > 0)
							loci.append(",");
						loci.append(ml.getIRICName());
					}
					if (loci.length() == 0 && ml.getFGeneshName() != null)
						loci.append(ml.getFGeneshName());

					if (loci.length() > 0)
						buff.append(" (" + loci + ")");
				}

				if (itLocus.hasNext())
					buff.append("; ");
			}

			addListcell(listitem, buff.toString());

		} else
			addListcell(listitem, "");

		// AppContext.debug(annot.getContig() + " " + annot.getPosition() + " " +
		// annot.getAnnotations());

		Iterator itAnnots = annotations.iterator();
		while (itAnnots.hasNext()) {
			String annotname = (String) itAnnots.next();
			// if(annot.getAnnotation(annotname).size()>0)
			// AppContext.debug(annot.getAnnotation(annotname).size() + " " + annotname + "
			// annotations");
			if (annotname.toUpperCase().equals(MarkerAnnotation.GENE_MODEL))
				continue;
			if (annot.getAnnotation(annotname) != null) {
				Iterator<Locus> itLocus = annot.getAnnotation(annotname).iterator();
				StringBuffer buff = new StringBuffer();
				while (itLocus.hasNext()) {
					Locus loc = itLocus.next();
					buff.append(loc.getUniquename() + " " + loc.getContig() + "[" + loc.getFmin() + "-" + loc.getFmax()
							+ "]");
					if (loc.getDescription() != null && !loc.getDescription().isEmpty()) {
						buff.append(" - " + loc.getDescription());
					}
					if (itLocus.hasNext())
						buff.append("; ");
				}
				addListcell(listitem, buff.toString());
			} else
				addListcell(listitem, "");
		}

		/*
		 * if(annot.getQTL1()!=null && !annot.getQTL1().isEmpty()) {
		 * 
		 * Iterator<Locus> itLocus=annot.getQTL1().iterator(); StringBuffer buff=new
		 * StringBuffer(); while(itLocus.hasNext()) { Locus loc= itLocus.next();
		 * buff.append( loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax()
		 * + "]" ); if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
		 * buff.append(" - " + loc.getDescription() ); } if(itLocus.hasNext())
		 * buff.append("; "); } addListcell(listitem,buff.toString());
		 * 
		 * } else addListcell(listitem,"");
		 * 
		 * if(annot.getQTL2()!=null && !annot.getQTL2().isEmpty()) {
		 * 
		 * Iterator<Locus> itLocus=annot.getQTL2().iterator(); StringBuffer buff=new
		 * StringBuffer(); while(itLocus.hasNext()) { Locus loc= itLocus.next();
		 * buff.append( loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax()
		 * + "]" ); if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
		 * buff.append(" - " + loc.getDescription() ); } if(itLocus.hasNext())
		 * buff.append("; "); }
		 * 
		 * addListcell(listitem,buff.toString()); } else addListcell(listitem,"");
		 * 
		 */

	}
	/*
	 * 
	 * @Override public void render(Row row, Object data, int index) throws
	 * Exception {
	 * 
	 * 
	 * 
	 * MarkerAnnotation annot = (MarkerAnnotation)data;
	 * 
	 * new Label(annot.getContig()).setParent(row); new
	 * Label(annot.getPosition().toString()).setParent(row);
	 * if(annot.getGenes()!=null && !annot.getGenes().isEmpty()) { Iterator<Locus>
	 * itLocus=annot.getGenes().iterator(); StringBuffer buff=new StringBuffer();
	 * while(itLocus.hasNext()) { Locus loc= itLocus.next(); buff.append(
	 * loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax() + "]" );
	 * if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
	 * buff.append(" -" + loc.getDescription() ); }
	 * 
	 * if(loc instanceof MergedLoci) { MergedLoci ml=(MergedLoci)loc; StringBuffer
	 * loci=new StringBuffer(); if(ml.getMSU7Name()!=null &&
	 * !loc.getUniquename().startsWith("LOC_") ) loci.append(ml.getMSU7Name());
	 * if(ml.getRAPRepName()!=null && ! (loc.getUniquename().startsWith("Os0") ||
	 * loc.getUniquename().startsWith("Os1")) ) { if(loci.length()>0)
	 * loci.append(","); loci.append(ml.getRAPRepName()); }
	 * if(ml.getRAPPredName()!=null && ! (loc.getUniquename().startsWith("Os0") ||
	 * loc.getUniquename().startsWith("Os1")) ) { if(loci.length()>0)
	 * loci.append(","); loci.append(ml.getRAPPredName()); }
	 * if(ml.getIRICName()!=null && ! loc.getUniquename().startsWith("OsNippo")) {
	 * if(loci.length()>0) loci.append(","); loci.append(ml.getIRICName()); }
	 * if(loci.length()==0 && ml.getFGeneshName()!=null)
	 * loci.append(ml.getFGeneshName());
	 * 
	 * if(loci.length()>0) buff.append(" (" + loci + ")"); }
	 * 
	 * if(itLocus.hasNext()) buff.append("; "); }
	 * 
	 * 
	 * 
	 * new Label(buff.toString()).setParent(row); } else new
	 * Label("").setParent(row); if(annot.getQTL1()!=null &&
	 * !annot.getQTL1().isEmpty()) {
	 * 
	 * Iterator<Locus> itLocus=annot.getQTL1().iterator(); StringBuffer buff=new
	 * StringBuffer(); while(itLocus.hasNext()) { Locus loc= itLocus.next();
	 * buff.append( loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax()
	 * + "]" ); if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
	 * buff.append(" - " + loc.getDescription() ); } if(itLocus.hasNext())
	 * buff.append("; "); } new Label(buff.toString()).setParent(row); } else new
	 * Label("").setParent(row); if(annot.getQTL2()!=null &&
	 * !annot.getQTL2().isEmpty()) {
	 * 
	 * Iterator<Locus> itLocus=annot.getQTL2().iterator(); StringBuffer buff=new
	 * StringBuffer(); while(itLocus.hasNext()) { Locus loc= itLocus.next();
	 * buff.append( loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax()
	 * + "]" ); if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
	 * buff.append(" - " + loc.getDescription() ); } if(itLocus.hasNext())
	 * buff.append("; "); } new Label(buff.toString()).setParent(row); } else new
	 * Label("").setParent(row);
	 * 
	 * 
	 * }
	 */

	public MarkerVarListItemRenderer() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	private void addListcell(Listitem listitem, String value) {
		addListcell(listitem, value, STYLE_BORING);
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
