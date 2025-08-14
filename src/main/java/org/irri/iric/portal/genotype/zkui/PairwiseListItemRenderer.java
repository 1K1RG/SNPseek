package org.irri.iric.portal.genotype.zkui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class PairwiseListItemRenderer implements SNPRowRendererStyle, ListitemRenderer {

	private VariantStringData data;
	private GenotypeQueryParams params;

	// private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	// private static String STYLE_BORING = "";
	private static int colorMode = COLOR_MISMATCH;

	private Map<Position, Set<Character>> mapPos2Nonsynalleles;

	public PairwiseListItemRenderer(VariantStringData data, GenotypeQueryParams params) {
		super();
		this.data = data;
		this.params = params;

		mapPos2Nonsynalleles = data.getSnpstringdata().getMapPos2NonsynAlleles();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem,
	 * java.lang.Object, int)
	 */
	@Override
	public void render(Listitem listitem, Object value, int index) throws Exception {
		// Snp2linesId item = (Snp2linesId)value;
		// Snps2Vars item = (Snps2Vars)value;

		listitem.setStyle("align:center");

		// keep value in listitem
		// listitem.setValue(data);

		Object obj[] = (Object[]) value;

		// String refnuc = (String)obj[2];
		String var1nuc = "";
		String var2nuc = "";

		// AppContext.debug(index + "\t" + obj[0] + "\t" + obj[1] + "\t" + obj[2] + "\t"
		// + obj[3]);

		if (!params.isbShowNPBPositions()) {
			/*
			 * // contigname addListcell(listitem, ""); addListcell(listitem, "");
			 * addListcell(listitem, ""); if( obj[0]==null) addListcell(listitem, ""); else
			 * addListcell(listitem, obj[0].toString());
			 * 
			 * // position if(obj[1]==null) addListcell(listitem,""); else
			 * addListcell(listitem, obj[1].toString().replace(".00","").replace(".0",""));
			 */

			addListcell(listitem, "");
			addListcell(listitem, "");
			addListcell(listitem, "");
			String contig = ((Position) obj[1]).getContig();
			try {
				int chr = Integer.valueOf(contig);
				if (chr < 10)
					contig = "chr0" + chr;
				else
					contig = "chr" + chr;
			} catch (Exception ex) {
			}
			;

			addListcell(listitem, contig);
			addListcell(listitem, ((Position) obj[1]).getPosition().toString().replace(".00", ""));

			String refnuc = (String) obj[2];
			if (refnuc == null)
				addListcell(listitem, "");
			else {
				if (params.isbColorByMismatch())
					addListcell(listitem, refnuc);
				else
					addListcell(listitem, refnuc, getColor(refnuc));
			}
			var1nuc = (String) obj[3];
			var2nuc = (String) obj[4];

		} else {

			// contigrefname
			if (obj[0] == null || obj[1] == null)
				addListcell(listitem, "");
			else
				addListcell(listitem, obj[0].toString());
			// refposition
			if (obj[1] == null)
				addListcell(listitem, "");
			else
				addListcell(listitem, obj[1].toString().replace(".00", ""));

			String refnuc = (String) obj[2];
			if (refnuc == null)
				addListcell(listitem, "");
			else {
				if (params.isbColorByMismatch())
					addListcell(listitem, refnuc);
				else
					addListcell(listitem, refnuc, getColor(refnuc));
			}

			/*
			 * // npb contig addListcell(listitem, obj[3].toString()); // npb position
			 * addListcell(listitem, obj[4].toString().replace(".00","").replace(".0",""));
			 */

			String contig = ((Position) obj[4]).getContig();
			try {
				int chr = Integer.valueOf(contig);
				if (chr < 10)
					contig = "chr0" + chr;
				else
					contig = "chr" + chr;
			} catch (Exception ex) {
			}
			;
			addListcell(listitem, contig);
			addListcell(listitem, ((Position) obj[4]).getPosition().toString().replace(".00", ""));

			refnuc = (String) obj[5];
			if (params.isbColorByMismatch())
				addListcell(listitem, refnuc);
			else
				addListcell(listitem, refnuc, getColor(refnuc));
			var1nuc = (String) obj[6];
			var2nuc = (String) obj[7];

		}

		// BigDecimal pos = (Double)obj[1];
		// BigDecimal pos = BigDecimal.valueOf( (Double)obj[1] );

		// BigDecimal pos = (BigDecimal)obj[1] ;
		// Set setcharNonsyn = mapPos2Nonsynalleles.get(pos);
		Position pos = (Position) obj[1];
		Set setcharNonsyn = mapPos2Nonsynalleles.get(pos);

		if (setcharNonsyn == null)
			setcharNonsyn = new HashSet();
		// AppContext.debug("nonsyn alleles at " + pos + " " + setcharNonsyn);

		if (params.isbHighlightNonsynSnps() || params.isbNonsynSnps() || params.isbNonsynPlusSpliceSnps()) {
			if (params.isbColorByMismatch()) {
				// addListcell(listitem, refnuc);
				if (var1nuc.equals(var2nuc)) {
					if (!var2nuc.isEmpty() && setcharNonsyn.contains(var2nuc.charAt(0))) {
						addListcell(listitem, var1nuc, STYLE_NONSYNONYMOUS);
						addListcell(listitem, var2nuc, STYLE_NONSYNONYMOUS);
					} else {
						addListcell(listitem, var1nuc);
						addListcell(listitem, var2nuc);
					}
				} else {
					if (!var1nuc.isEmpty() && setcharNonsyn.contains(var1nuc.charAt(0)))
						addListcell(listitem, var1nuc, STYLE_NONSYNONYMOUS);
					else
						addListcell(listitem, var1nuc, STYLE_INTERESTING);

					if (!var2nuc.isEmpty() && setcharNonsyn.contains(var2nuc.charAt(0)))
						addListcell(listitem, var2nuc, STYLE_NONSYNONYMOUS);
					else
						addListcell(listitem, var2nuc, STYLE_INTERESTING);
				}
			} else if (params.isbColorByAllele()) {
				// addListcell(listitem, refnuc, getColor(refnuc));

				if (!var1nuc.isEmpty() && setcharNonsyn.contains(var1nuc.charAt(0)))
					addListcell(listitem, var1nuc, STYLE_NONSYNONYMOUS);
				else
					addListcell(listitem, var1nuc, getColor(var1nuc));

				if (!var2nuc.isEmpty() && setcharNonsyn.contains(var2nuc.charAt(0)))
					addListcell(listitem, var2nuc, STYLE_NONSYNONYMOUS);
				else
					addListcell(listitem, var2nuc, getColor(var2nuc));
			}

		} else {
			if (params.isbColorByMismatch()) {
				// addListcell(listitem, refnuc);

				if (var1nuc.equals(var2nuc)) {
					addListcell(listitem, var1nuc);
					addListcell(listitem, var2nuc);
				} else {
					addListcell(listitem, var1nuc, STYLE_INTERESTING);
					addListcell(listitem, var2nuc, STYLE_INTERESTING);
				}
			} else if (params.isbColorByAllele()) {
				// addListcell(listitem, refnuc, getColor(refnuc));
				addListcell(listitem, var1nuc, getColor(var1nuc));
				addListcell(listitem, var2nuc, getColor(var2nuc));
			}
		}

		// //if(querymode == GenotypeFacade.snpQueryMode.SNPQUERY_VARIETIES) {
		// // two varieties are alleles with each other
		// // all are interesting
		// addListcell(listitem, (String)obj[3]);
		//
		// String nuc2s[] = ((String)obj[4]).split("/");
		//
		// String nuc1 = nuc1s[0];
		// String nuc2 = nuc2s[0];
		// String nucref = (String)obj[2];
		//
		//
		//

		// if(colorMode==COLOR_MISMATCH) {
		//
		// addListcell(listitem, nucref) ;
		//
		// if( (nuc1_allele2!=null && !nuc1_allele2.isEmpty()) ||
		// (nuc2_allele2!=null && !nuc2_allele2.isEmpty()) )
		// {
		//
		// if(this.isGraySynonymous) {
		// if( nuc1_allele2!=null && !nuc1_allele2.isEmpty() )
		// {
		// if( item.isVar1Nonsyn() )
		// addListcell(listitem, nuc1 + "/" + nuc1_allele2, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc1 + "/" + nuc1_allele2, STYLE_SYNONYMOUS);
		// }
		// else {
		// if( item.isVar1Nonsyn() )
		// addListcell(listitem, nuc1, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc1, STYLE_SYNONYMOUS);
		// }
		//
		// if( nuc2_allele2!=null && !nuc2_allele2.isEmpty() )
		// {
		// if( item.isVar2Nonsyn() )
		// addListcell(listitem, nuc2 + "/" + nuc2_allele2, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc2 + "/" + nuc2_allele2, STYLE_SYNONYMOUS);
		// }
		// else {
		// if( item.isVar2Nonsyn() )
		// addListcell(listitem, nuc2, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc2, STYLE_SYNONYMOUS);
		// }
		//
		//
		// } else {
		// if( nuc1_allele2!=null && !nuc1_allele2.isEmpty() )
		// addListcell(listitem, nuc1 + "/" + nuc1_allele2, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc1, STYLE_INTERESTING);
		//
		// if( nuc2_allele2!=null && !nuc2_allele2.isEmpty() )
		// addListcell(listitem, nuc2 + "/" + nuc2_allele2, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc2, STYLE_INTERESTING);
		// }
		// }
		// else if(nuc1.equals(nuc2)) {
		// if(this.isGraySynonymous) {
		// if(item.isVar1Nonsyn())
		// addListcell(listitem, nuc1);
		// else
		// addListcell(listitem, nuc1,STYLE_SYNONYMOUS);
		//
		// if(item.isVar2Nonsyn())
		// addListcell(listitem, nuc2) ;
		// else
		// addListcell(listitem, nuc2,STYLE_SYNONYMOUS);
		// } else {
		// addListcell(listitem, nuc1);
		// addListcell(listitem, nuc2) ;
		// }
		//
		//
		// } else
		// {
		// if(this.isGraySynonymous) {
		// if(item.isVar1Nonsyn())
		// addListcell(listitem, nuc1, STYLE_INTERESTING);
		// else
		// addListcell(listitem, nuc1, STYLE_SYNONYMOUS);
		//
		// if(item.isVar2Nonsyn())
		// addListcell(listitem, nuc2, STYLE_INTERESTING) ;
		// else
		// addListcell(listitem, nuc2, STYLE_SYNONYMOUS);
		// } else {
		//
		// addListcell(listitem, nuc1, STYLE_INTERESTING);
		// addListcell(listitem, nuc2, STYLE_INTERESTING) ;
		// }
		// }
		//
		// } else {
		// addListcell(listitem, nucref, getColor(nucref)) ;
		//
		// if(this.isGraySynonymous) {
		// if(nuc1_allele2!=null && !nuc1_allele2.isEmpty()) {
		// if(item.isVar1Nonsyn())
		// addListcell(listitem, nuc1 + "/" + nuc1_allele2, STYLE_HETERO );
		// else
		// addListcell(listitem, nuc1 + "/" + nuc1_allele2, STYLE_SYNONYMOUS);
		// }
		// else {
		// if(item.isVar1Nonsyn())
		// addListcell(listitem, nuc1, getColor(nuc1)) ;
		// else
		// addListcell(listitem, nuc1, STYLE_SYNONYMOUS) ;
		// }
		//
		// if(nuc2_allele2!=null && !nuc2_allele2.isEmpty()) {
		// if(item.isVar2Nonsyn())
		// addListcell(listitem, nuc2 + "/" + nuc2_allele2, STYLE_HETERO );
		// else
		// addListcell(listitem, nuc2 + "/" + nuc2_allele2, STYLE_SYNONYMOUS );
		// }
		// else {
		// if(item.isVar2Nonsyn())
		// addListcell(listitem, nuc2, getColor(nuc2)) ;
		// else
		// addListcell(listitem, nuc2, STYLE_SYNONYMOUS) ;
		// }
		//
		// } else
		// {
		// if(nuc1_allele2!=null && !nuc1_allele2.isEmpty())
		// addListcell(listitem, nuc1 + "/" + nuc1_allele2, STYLE_HETERO );
		// else
		// addListcell(listitem, nuc1, getColor(nuc1)) ;
		//
		// if(nuc2_allele2!=null && !nuc2_allele2.isEmpty())
		// addListcell(listitem, nuc2 + "/" + nuc2_allele2, STYLE_HETERO );
		// else
		// addListcell(listitem, nuc2, getColor(nuc2)) ;
		// }
		// }

		/*
		 * } else if(querymode== GenotypeFacade.snpQueryMode.SNPQUERY_REFERENCE) { //
		 * variety is allele with reference // interesting is allele with reference
		 * String nuc1 = item.getVar1nuc(); String nuc2 = item.getVar2nuc(); String
		 * nucref = item.getRefnuc();
		 * 
		 * addListcell(listitem, nucref); if(nucref.equals(nuc1)) addListcell(listitem,
		 * nuc1) ; else addListcell(listitem, nuc1, STYLE_INTERESTING) ;
		 * 
		 * if(nucref.equals(nuc2)) addListcell(listitem, nuc2); else
		 * addListcell(listitem, nuc2, STYLE_INTERESTING) ;
		 * 
		 * 
		 * } else if(querymode==GenotypeFacade.snpQueryMode.SNPQUERY_ALLVARIETIESPOS) {
		 * // all allele positions, // interesting is alleles String nuc1 =
		 * item.getVar1nuc(); String nuc2 = item.getVar2nuc(); String nucref =
		 * item.getRefnuc(); addListcell(listitem, nucref) ;
		 * 
		 * if(nuc2.equals(nuc1)) { addListcell(listitem, nuc1) ; addListcell(listitem,
		 * nuc2) ; } else { addListcell(listitem, nuc1 , STYLE_INTERESTING) ;
		 * addListcell(listitem, nuc2, STYLE_INTERESTING) ; } }
		 */

	}

	private String getColor(String nuc) {

		if (nuc == null)
			return "color:black";

		if (nuc.equals("A"))
			return STYLE_A;
		if (nuc.equals("T"))
			return STYLE_T;
		if (nuc.equals("G"))
			return STYLE_G;
		if (nuc.equals("C"))
			return STYLE_C;

		return "color:black";
	}

	private void addListcell(Listitem listitem, String value) {
		addListcell(listitem, value, STYLE_BORING);
	}

	private void addListcell(Listitem listitem, String value, String style) {

		Listcell lc = new Listcell();
		Label lb = new Label(value);
		if (!style.isEmpty())
			lb.setStyle(style + ";text-align:center;align:center");
		lb.setParent(lc);
		lc.setParent(listitem);
	}
	//
	// public void setVar1(String var1) {
	// this.var1 = var1;
	// }
	//
	// public void setVar2(String var2) {
	// this.var2 = var2;
	// }
	//
	// public void setVarref(String varref) {
	// this.varref = varref;
	// }
	//
	// public void setQuerymode(GenotypeFacade.snpQueryMode querymode) {
	// //this.querymode = querymode;
	// }
	//
	//
	// public void setColorMode(int mode) {
	// this.colorMode = mode;
	// }
	//
	// public void setGraySynonymous(boolean isgray) {
	// this.isGraySynonymous = isgray;
	// }

}
