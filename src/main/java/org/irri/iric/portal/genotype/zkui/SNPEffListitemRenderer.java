package org.irri.iric.portal.genotype.zkui;

import java.util.HashMap;
import java.util.Map;

import org.irri.iric.ds.chado.domain.SnpsEffect;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SNPEffListitemRenderer implements ListitemRenderer {

	/*
	 * ##INFO=<ID=ANN,Number=.,Type=String,
	 * Dscription="Functional annotations: 'Allele | Annotation | Annotation_Impact | Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank | HGVS.c | HGVS.p | cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | ERRORS / WARNINGS / INFO' "
	 * > ##INFO=<ID=LOF,Number=.,Type=String,
	 * Description="Predicted loss of function effects for this variant. Format: 'Gene_Name | Gene_ID | Number_of_transcripts_in_gene | Percent_of_transcripts_affected' "
	 * > ##INFO=<ID=NMD,Number=.,Type=String,
	 * Description="Predicted nonsense mediated decay effects for this variant. Format: 'Gene_Name | Gene_ID | Number_of_transcripts_in_gene | Percent_of_transcripts_affected' "
	 * > #CHROM POS ID REF ALT QUAL FILTER INFO FORMAT SAMPLE_DUMM_dummy
	 */

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	@Override
	public void render(Listitem arg0, Object arg1, int arg2) throws Exception {

		SnpsEffect snp = (SnpsEffect) arg1;
		// String[] anns = snp.getAnnotation().split("\\|");
		// addListcell(arg0, snp.getChr().toString());

		addListcell(arg0, snp.getContig() + "-" + snp.getFmin()); // .getPosition());
		// for(int i=0;i<anns.length; i++) addListcell(arg0, anns[i]);

		addListcell(arg0, Integer.valueOf(snp.getScore().intValue()).toString());

		/*
		 * <listheader label="POSITION" sort="auto" width="8%"/> 
		 * <listheader label="&quot;deleteriousness&quot rank;" sort="auto" width="50px"/>
		 * <listheader label="ALT" sort="auto" width="20px"/> 
		 * <listheader label="ANNOT" sort="auto" width="80px"/> 
		 * <listheader label="IMPACT" sort="auto" width="50px"/> 
		 * <listheader label="GENE" sort="auto" width="50px"/>
		 * <listheader label="TRANS BIOTYPE" sort="auto" width="50px"/> 
		 * <listheader label="HGVS.c" sort="auto" width="50px"/> 
		 * <listheader label="HGVS.P" sort="auto" width="50px"/> 
		 * <listheader label="ANN" sort="auto" /> 
		 * <listheader label="LOF" sort="auto" width="3%"/> 
		 * <listheader label="NMD" sort="auto"width="3%"/>
		 */

		/*
		 * List<> snp.getANNObj() Map<String,String> mapInfos=new HashMap(); String
		 * infos[]=snp.getAnnotation().split(";"); for(int i=0;i<infos.length; i++) {
		 * if(infos[i].startsWith("LOF=")) mapInfos.put("LOF", infos[i].replace("LOF=",
		 * "")); else if(infos[i].startsWith("NMD=")) mapInfos.put("NMD",
		 * infos[i].replace("NMD=", "")); } addListcell(arg0,
		 * (mapInfos.containsKey("LOF")? mapInfos.get("LOF").replace(",","\n"):""));
		 * addListcell(arg0, (mapInfos.containsKey("NMD")?
		 * mapInfos.get("NMD").replace(",","\n"):""));
		 */

		Map<String, String> mapInfos = new HashMap();
		String infos[] = snp.getAnnotation().split(";");
		for (int i = 0; i < infos.length; i++) {
			if (infos[i].startsWith("ANN="))
				mapInfos.put("ANN", infos[i].replace("ANN=", ""));
			else if (infos[i].startsWith("LOF="))
				mapInfos.put("LOF", infos[i].replace("LOF=", ""));
			else if (infos[i].startsWith("NMD="))
				mapInfos.put("NMD", infos[i].replace("NMD=", ""));
		}

		if (mapInfos.containsKey("ANN")) {
			String[] rowList = mapInfos.get("ANN").split(",");
			Map<Integer, String> column = new HashMap<Integer, String>();
			for (int i = 0; i <= 15; i++)
				column.put(i, "");

			for (String row : rowList) {
				row = row.replaceAll("[|]", "|space");
				String[] annList = row.split("\\|");
				for (int i = 0; i <= 15; i++) {
					String str =  (column.get(i).isEmpty() ? column.get(i) : column.get(i)+"\n"); 
					column.put(i, (!annList[i].equals(null)  && !annList[i].equals("space") ? str + annList[i].replace("space", "") : str+"-"));
				}
			}
			
			for (int i = 0; i <= 15; i++) {
				addListcell(arg0, column.get(i));
			}

			

		} else {
			for (int i = 0; i < 15; i++) {
				addListcell(arg0, "");
			}
		}

		// addListcell(arg0, (mapInfos.containsKey("ANN") ?
		// mapInfos.get("ANN").replace(",", "\n") : ""));
		addListcell(arg0, (mapInfos.containsKey("LOF") ? mapInfos.get("LOF").replace(",", "\n") : ""));
		addListcell(arg0, (mapInfos.containsKey("NMD") ? mapInfos.get("NMD").replace(",", "\n") : ""));

	}

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
