package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.MarkerAnnotation;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class MarkerListItemRenderer implements ListitemRenderer {

	@Override
	public void render(Listitem listitem, Object data, int arg2) throws Exception {
		
		MarkerAnnotation annot = (MarkerAnnotation) data;
		listitem.setValue(annot);
		listitem.setStyle("vertical-align:top;");

		//
		// addListcell(listitem, annot.getContig());
		// addListcell(listitem, annot.getPosition().toString());
		//
		// if(annot.getGenes()!=null && !annot.getGenes().isEmpty()) {
		// Iterator<Locus> itLocus=annot.getGenes().iterator();
		// StringBuffer buff=new StringBuffer();
		// while(itLocus.hasNext()) {
		// Locus loc= itLocus.next();
		// buff.append( loc.getUniquename() + " " + loc.getContig() + "[" +
		// loc.getFmin() + "-" + loc.getFmax() + "]" );
		// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
		// buff.append(" -" + loc.getDescription() );
		// }
		//
		// if(loc instanceof MergedLoci) {
		// MergedLoci ml=(MergedLoci)loc;
		// StringBuffer loci=new StringBuffer();
		// if(ml.getMSU7Name()!=null && !loc.getUniquename().startsWith("LOC_") )
		// loci.append(ml.getMSU7Name());
		// if(ml.getRAPRepName()!=null && ! (loc.getUniquename().startsWith("Os0") ||
		// loc.getUniquename().startsWith("Os1")) ) {
		// if(loci.length()>0) loci.append(",");
		// loci.append(ml.getRAPRepName());
		// }
		// if(ml.getRAPPredName()!=null && ! (loc.getUniquename().startsWith("Os0") ||
		// loc.getUniquename().startsWith("Os1")) ) {
		// if(loci.length()>0) loci.append(",");
		// loci.append(ml.getRAPPredName());
		// }
		// if(ml.getIRICName()!=null && ! loc.getUniquename().startsWith("OsNippo")) {
		// if(loci.length()>0) loci.append(",");
		// loci.append(ml.getIRICName());
		// }
		// if(loci.length()==0 && ml.getFGeneshName()!=null)
		// loci.append(ml.getFGeneshName());
		//
		// if(loci.length()>0) buff.append(" (" + loci + ")");
		// }
		//
		// if(itLocus.hasNext()) buff.append("; ");
		// }
		//
		// addListcell(listitem,buff.toString());
		//
		// } else addListcell(listitem,"");
		//
		// if(annot.getQTL1()!=null && !annot.getQTL1().isEmpty()) {
		//
		// Iterator<Locus> itLocus=annot.getQTL1().iterator();
		// StringBuffer buff=new StringBuffer();
		// while(itLocus.hasNext()) {
		// Locus loc= itLocus.next();
		// buff.append( loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax()
		// + "]" );
		// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
		// buff.append(" - " + loc.getDescription() );
		// }
		// if(itLocus.hasNext()) buff.append("; ");
		// }
		// addListcell(listitem,buff.toString());
		//
		// } else addListcell(listitem,"");
		//
		// if(annot.getQTL2()!=null && !annot.getQTL2().isEmpty()) {
		//
		// Iterator<Locus> itLocus=annot.getQTL2().iterator();
		// StringBuffer buff=new StringBuffer();
		// while(itLocus.hasNext()) {
		// Locus loc= itLocus.next();
		// buff.append( loc.getUniquename() + " [" + loc.getFmin() + "-" + loc.getFmax()
		// + "]" );
		// if(loc.getDescription()!=null && !loc.getDescription().isEmpty()) {
		// buff.append(" - " + loc.getDescription() );
		// }
		// if(itLocus.hasNext()) buff.append("; ");
		// }
		//
		// addListcell(listitem,buff.toString());
		// } else addListcell(listitem,"");
		//

	}
	/*
	 * 
	 * @Override public void render(Row row, Object data, int index) throws
	 * Exception { 
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

	public MarkerListItemRenderer() {
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
		if (!style.isEmpty())
			lb.setStyle(style);
		lb.setParent(lc);
		lc.setParent(listitem);
	}

}
