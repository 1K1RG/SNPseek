package org.irri.iric.portal.genotype.zkui;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImplAllelePvalue;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.variety.service.Data;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkmax.zul.MatrixRenderer;

/**
 * Renders the variant table
 * 
 * @author LMansueto
 *
 */
public class Object2StringMultirefsMatrixRenderer implements MatrixRenderer, SNPRowRendererStyle {

	private VariantStringData tabledata;
	private GenotypeQueryParams params;

	private int frozenCols = AppContext.getSnpMatrixFrozenCols(); // 5;

	private Map<String, String> mapPos2Queryallele;
	private Map<Position, Set<Character>> mapPos2NonsynAlleles;
	// private Set<Position> setSnpInExonPos;
	private Set<Position> setDonorPos;
	private Set<Position> setAcceptorPos;
	private Map<Integer, Position> mapIdx2Pos;
	private Map<BigDecimal, BigDecimal> mapRefPos2MSU7Pos;

	private Set<Position> setPosGapRegion;
	private Set<Position> setPosDeletionRegion;

	private Component firstRow = null;

	private Map<Position, String> mapPos2Refnuc;
	private boolean hasPhenotype = false;

	public Object2StringMultirefsMatrixRenderer(VariantStringData data, GenotypeQueryParams params) {
		super();

		firstRow = null;

		this.tabledata = data;
		this.params = params;
		this.hasPhenotype = params.getPhenotype() != null && !params.getPhenotype().isEmpty();
		if (hasPhenotype)
			frozenCols++;

		if (params.isbIndel() && params.isbAlignIndels()) {
			setPosGapRegion = tabledata.getIndelstringdata().getSetPosGapRegion();
			setPosDeletionRegion = tabledata.getIndelstringdata().getSetPosDeletionRegion();
		}

		mapIdx2Pos = new HashMap();
		if (params.isbSNP() && tabledata.getSnpstringdata() != null) {
			if (params.isbHighlightNonsynSnps() || params.isbNonsynSnps() || params.isbNonsynPlusSpliceSnps()) {
				mapPos2NonsynAlleles = tabledata.getSnpstringdata().getMapPos2NonsynAlleles(); // .getMapIdx2NonsynAlleles();
				// setSnpInExonPos = tabledata.getSnpstringdata().getSetSnpInExonPos();
				if (params.isbIndel() && params.isbAlignIndels()) {
					mapPos2NonsynAlleles.putAll(tabledata.getMapPos2NonsynAlleles());
				}
			}

			// if(params.isbColorSpliceSNP()) {
			if (true) {
				setDonorPos = tabledata.getSnpstringdata().getSetSnpSpliceDonorPos();
				setAcceptorPos = tabledata.getSnpstringdata().getSetSnpSpliceAcceptorPos();

				AppContext.debug("setDonorPos=" + setDonorPos.size() + "; setAcceptorPos=" + setAcceptorPos.size());
			}

			/*
			 * if(params.isbNonsynSnps() || params.isbNonsynPlusSpliceSnps()) {
			 * Iterator<SnpsAllvarsPos> itPos= data.getListPos().iterator(); int iidx=0;
			 * while(itPos.hasNext()) { SnpsAllvarsPos pos=itPos.next(); Set setchars =
			 * mapPos2NonsynAlleles.get(pos); boolean nonsyn=setchars!=null &&
			 * setchars.size()>0; if( (params.isbNonsynSnps() && nonsyn ) ||
			 * (params.isbNonsynPlusSpliceSnps() && (nonsyn || setDonorPos.contains(pos) ||
			 * setAcceptorPos.contains(pos))) || ( params.isbIndel() &&
			 * params.isbAlignIndels() &&
			 * (setPosGapRegion.contains(pos)||setPosDeletionRegion.contains(pos) ) ) ) {
			 * mapIdx2Pos.put(iidx, pos); iidx++; } } } else
			 * mapIdx2Pos=data.getMapIdx2Pos();
			 */
			mapIdx2Pos = data.getMapIdx2Pos();

			/*
			 * AppContext.debug("mapIdx2Pos"); AppContext.debug("data.getMapIdx2Pos()=" +
			 * data.getMapIdx2Pos().size() + ", mapIdx2Pos=" + mapIdx2Pos.size()) ;
			 * 
			 * Iterator<Integer> itIdx=mapIdx2Pos.keySet().iterator();
			 * while(itIdx.hasNext()) { Integer idx=itIdx.next(); Position
			 * pos=mapIdx2Pos.get(idx); AppContext.debug(idx + " " + pos.getPosition() + " "
			 * + (mapPos2NonsynAlleles!=null && mapPos2NonsynAlleles.containsKey(pos)?
			 * "nonsyn" : "") + " " + (setPosGapRegion!=null &&
			 * setPosGapRegion.contains(pos)? "gap":"") + " " + (setPosDeletionRegion!=null
			 * && setPosDeletionRegion.contains(pos)?"del":"")); }
			 */

		}

		// if(!data.isNipponbareReference()) {
		// mapRefPos2MSU7Pos = new HashMap();
		// Map<BigDecimal,MultiReferencePosition> mapMSU2Others =
		// data.getMapMSU7Pos2ConvertedPos();
		// Iterator<BigDecimal> itMSU = mapMSU2Others.keySet().iterator();
		// while( itMSU.hasNext() ) {
		// BigDecimal msu7pos = itMSU.next();
		// MultiReferencePosition otherrefpos = mapMSU2Others.get(msu7pos);
		// if(otherrefpos==null) continue;
		// mapRefPos2MSU7Pos.put( otherrefpos.getPosition() , msu7pos);
		// }
		//
		// if(params.isbSNP()) {
		//
		// setDonorPos=new HashSet();
		// Iterator<BigDecimal> itPos =
		// tabledata.getSnpstringdata().getSetSnpSpliceDonorPos().iterator();
		// while(itPos.hasNext()) {
		// MultiReferencePosition otherrefpos = mapMSU2Others.get(itPos.next() );
		// if(otherrefpos==null) continue;
		// setDonorPos.add( otherrefpos.getPosition() );
		// }
		// setAcceptorPos = new HashSet();
		// itPos = tabledata.getSnpstringdata().getSetSnpSpliceAcceptorPos().iterator();
		// while(itPos.hasNext()) {
		// MultiReferencePosition otherrefpos = mapMSU2Others.get(itPos.next() );
		// if(otherrefpos==null) continue;
		// setAcceptorPos.add( otherrefpos.getPosition() );
		// }
		// }
		//
		// if( params.isbIndel() && params.isbAlignIndels() ) { // tabledata.hasIndel())
		// {
		// setPosGapRegion = new HashSet();
		// Iterator<BigDecimal> itPos =
		// tabledata.getIndelstringdata().getSetPosGapRegion().iterator();
		// while(itPos.hasNext()) {
		// MultiReferencePosition otherrefpos = mapMSU2Others.get(itPos.next() );
		// if(otherrefpos==null) continue;
		// setPosGapRegion.add( otherrefpos.getPosition() );
		// }
		//
		// setPosDeletionRegion = new HashSet();
		// Iterator<Long> itPosLong =
		// tabledata.getIndelstringdata().getSetPosDeletionRegion().iterator();
		// while(itPosLong.hasNext()) {
		// MultiReferencePosition otherrefpos = mapMSU2Others.get(
		// BigDecimal.valueOf(itPosLong.next()) );
		// if(otherrefpos==null) continue;
		// setPosDeletionRegion.add( otherrefpos.getPosition().longValueExact() );
		// }
		// }
		//
		// }

		mapPos2Refnuc = new TreeMap();

		if (params.isbAlleleFilter() || params.isVarAlleleFilter()) {
			mapPos2Queryallele = new HashMap();
			Iterator<Position> itPos = params.getPoslist().iterator();
			while (itPos.hasNext()) {
				MultiReferencePositionImplAllelePvalue pos = (MultiReferencePositionImplAllelePvalue) itPos.next();
				mapPos2Queryallele.put(pos.getContig() + "-" + pos.getPosition(), pos.getAllele());
			}

			AppContext.debug("rederer mapPos2Queryallele:" + mapPos2Queryallele);
		}

		AppContext.debug("Object2StringMatrixRenderer..started");
	}

	@Override
	public String renderCell(Component owner, Object data, // List<String> data,
			int rowIndex, int colIndex) throws Exception {

		try {

			if (rowIndex == 0 && colIndex == 0)
				firstRow = owner;

			if (colIndex > (frozenCols - 1)) {

				int tableidx = colIndex - frozenCols;
				String cellval = "";
				Object cellobj = ((Object[]) ((List) data).get(colIndex))[tableidx + frozenCols];
				if (cellobj != null)
					cellval = cellobj.toString();

				Position snppos = mapIdx2Pos.get(tableidx);

				String indelposref = "";
				if (params.isbIndel() && params.isbAlignIndels()) { // tabledata.hasIndel()) {
					if (tabledata.getIndelstringdata().getMapIndelpos2Refnuc() == null) {
					} else
						indelposref = tabledata.getIndelstringdata().getMapIndelpos2Refnuc().get(snppos); // tabledata.getListPos().get(tableidx).getPos()
																											// );
				}

				if (cellval.equals("-"))
					cellval = "&#151;"; // "&#151;"

				if (mapPos2NonsynAlleles != null) {
					if (!cellval.isEmpty()) {

						if (snppos != null) {
							Set setnonsyn = mapPos2NonsynAlleles.get(snppos);

							if (setnonsyn == null) {
								// synonymous
							} else {

								if (cellval.contains("/")) {
									if (setnonsyn.contains(cellval.charAt(0)) || setnonsyn.contains(cellval.charAt(2)))
										return "<div style=\"" + STYLE_NONSYNONYMOUS + "\">" + cellval + "</div>";
								} else {
									if (setnonsyn.contains(cellval.charAt(0)))
										return "<div style=\"" + STYLE_NONSYNONYMOUS + "\">" + cellval + "</div>";
								}
							}
						}
					}
				}

				if (cellval.isEmpty())
					return "";

				if (mapPos2Refnuc.get(snppos) == null)
					AppContext.debug("mapPos2Refnuc.get(snppos)==null,  pos=" + snppos);
				if (cellval == null)
					AppContext.debug("cellval==null,  pos=" + snppos);
				if (indelposref == null) {
					indelposref = snppos.getRefnuc();
				}

				if (params.isbColorByMismatch()) {

					if (params.isbAlleleFilter() || params.isVarAlleleFilter()) {
						// if(cellval.equals("*") || cellval.equals("-") || cellval.equals("&#151;") ||
						// cellval.equals(this.mapPos2Queryallele.get(snppos.getContig()+"-"+snppos.getPosition())
						// ) )
						// if(cellval.equals(this.mapPos2Queryallele.get(snppos.getContig()+"-"+snppos.getPosition())
						// ) )

						if (!mapPos2Queryallele.containsKey(snppos.getContig() + "-" + snppos.getPosition())) {
							throw new RuntimeException(
									snppos.getContig() + "-" + snppos.getPosition() + " not in " + mapPos2Queryallele);
						}

						if (cellval
								.contains(this.mapPos2Queryallele.get(snppos.getContig() + "-" + snppos.getPosition())))
							return "<div   style=\"" + STYLE_MATCH + "\">" + cellval + "</div>";
						else {

							// if(params.isbSNP() && params.isbColorSpliceSNP()) {
							if (params.isbSNP()) {
								if (setDonorPos.contains(mapIdx2Pos.get(tableidx))) {
									return "<div   style=\"" + STYLE_SPLICE_DONOR + "\">" + cellval + "</div>";
								}
								if (setAcceptorPos.contains(mapIdx2Pos.get(tableidx))) {
									return "<div  style=\"" + STYLE_SPLICE_ACCEPTOR + "\">" + cellval + "</div>";
								}
							}
							return "<div    style=\"text-align:center\">" + cellval + "</div>";
						}

					} else {

						// if(cellval.equals("*") || cellval.equals("-") || cellval.equals("&#151;") ||
						// cellval.equals( tabledata.getListPos().get(tableidx).getRefnuc() ) ||
						// (indelposref!=null && cellval.equals(indelposref)) )

						// Position snppos = mapIdx2Pos.get(tableidx);
						if (cellval.equals("*") || cellval.equals("-") || cellval.equals("&#151;")
								|| cellval.equals(mapPos2Refnuc.get(snppos))
								|| (!indelposref.isEmpty() && cellval.equals(indelposref)))
							return "<div    style=\"text-align:center\">" + cellval + "</div>";
						else {

							// if(params.isbSNP() && params.isbColorSpliceSNP()) {
							if (params.isbSNP()) {
								if (setDonorPos.contains(mapIdx2Pos.get(tableidx))) {
									return "<div   style=\"" + STYLE_SPLICE_DONOR + "\">" + cellval + "</div>";
								}
								if (setAcceptorPos.contains(mapIdx2Pos.get(tableidx))) {
									return "<div  style=\"" + STYLE_SPLICE_ACCEPTOR + "\">" + cellval + "</div>";
								}
							}

							return "<div   style=\"" + STYLE_MISMATCH + "\">" + cellval + "</div>";
						}
					}
				} else if (params.isbColorByAllele()) {

					if (cellval.equals("*") || cellval.equals("-") || cellval.equals("&#151;")
							|| (!indelposref.isEmpty() && cellval.equals(indelposref)))
						return "<div   style=\"text-align:center\">" + cellval + "</div>";
					else {

						// if(params.isbSNP() && params.isbColorSpliceSNP()) {
						if (params.isbSNP()) {
							if (setDonorPos.contains(mapIdx2Pos.get(tableidx))) {
								return "<div   style=\"" + STYLE_SPLICE_DONOR + "\">" + cellval + "</div>";
							}
							if (setAcceptorPos.contains(mapIdx2Pos.get(tableidx))) {
								return "<div   style=\"" + STYLE_SPLICE_ACCEPTOR + "\">" + cellval + "</div>";
							}
						}
						if (cellval.equals("A"))
							return "<div   style=\"" + STYLE_A + "\">" + cellval + "</div>";
						else if (cellval.equals("C"))
							return "<div   style=\"" + STYLE_C + "\">" + cellval + "</div>";
						else if (cellval.equals("G"))
							return "<div   style=\"" + STYLE_G + "\">" + cellval + "</div>";
						else if (cellval.equals("T"))
							return "<div   style=\"" + STYLE_T + "\">" + cellval + "</div>";
						else if (cellval.contains("/"))
							return "<div    style=\"" + STYLE_HETERO + "\">" + cellval + "</div>";
					}
				}

				return "";
			} else {

				// AppContext.debug( ((Object[])((List)data).get(colIndex))[colIndex].toString()
				// );

				// return ((Object[])((List)data).get(colIndex))[colIndex].toString();

				Object[] obj = (Object[]) ((List) data).get(colIndex);
				if (obj == null) {
					// AppContext.debug("Object[]==null at ("+ rowIndex+","+colIndex+")");
					return "";
				}
				if (obj[colIndex] == null) {
					// AppContext.debug("Object[colIndex]==null at ("+ rowIndex+","+colIndex+")");
					return "";
				}

				if (colIndex == 3) { // subpop

					return "<div   style=\"background:" + Data.getSubpopulationColor((String) obj[colIndex]) + "\">"
							+ obj[colIndex] + "</div>";
				}

				/*
				 * if(obj[colIndex] instanceof Number) return String.valueOf(obj[colIndex]);
				 * return obj[colIndex].toString();
				 */

				if (obj[colIndex] instanceof String)
					return (String) obj[colIndex];
				// else return String.format("%.2f", (Number)obj[colIndex]).replace(".00","");
				else if (obj[colIndex] instanceof Long || obj[colIndex] instanceof Integer)
					return obj[colIndex].toString();
				else if (obj[colIndex] instanceof Number)
					// return Double.valueOf(
					// ((Number)obj[colIndex]).doubleValue()).toString().replace(".00",""); //
					// Math.round(a) obj[colIndex].toString();
					return Double.valueOf(((Number) obj[colIndex]).doubleValue()).toString(); // .replace(".00",""); //
																								// Math.round(a)
																								// obj[colIndex].toString();
				else
					return obj[colIndex].toString().replace(".00", "");

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	// public String renderCellOrig(Component owner, Object data, //List<String>
	// data,
	// int rowIndex, int colIndex) throws Exception {
	//
	//
	// //AppContext.debug("renderCell " + rowIndex + "," + colIndex + " " + data );
	//
	// //if(initDisplay) {
	//
	// if(colIndex>2) {
	// //return ((Object[])data)[colIndex].toString();
	//
	// //AppContext.debug( ((List)data).get(colIndex).getClass() + " j="+ colIndex +
	// " " + ((List)data).get(colIndex));
	// //return ((List)data).get(colIndex) .toString();
	//
	// int tableidx=colIndex-frozenCols;
	// String cellval = "";
	// Object cellobj = ((Object[])((List)data).get(colIndex))[tableidx];
	// if(cellobj!=null) cellval= cellobj.toString();
	//
	// //AppContext.debug( rowIndex + "," + colIndex + " " + cellval);
	//
	// String indelposref="";
	// if(params.isbIndel() && params.isbAlignIndels() ) { // tabledata.hasIndel())
	// {
	// //if(tabledata.getIndelstringdata()!=null &&
	// tabledata.getIndelstringdata().getMapIndelpos2Refnuc()!=null)
	// if(tabledata.getIndelstringdata().getMapIndelpos2Refnuc()==null)
	// ;
	// //AppContext.debug("tabledata.getIndelstringdata().getMapIndelpos2Refnuc()==null");
	// else
	// indelposref=tabledata.getIndelstringdata().getMapIndelpos2Refnuc().get(
	// tabledata.getListPos().get(tableidx).getPos() );
	// if(indelposref==null) AppContext.info("indelposref==null");
	// }
	//
	// if(cellval.equals("-")) cellval="&#151;"; // "&#151;"
	//
	// if(mapTableIdx2NonsynAlleles!=null) {
	// if(!cellval.isEmpty()) {
	// Integer snpidx= mapTableIdx2SnpIdx.get(tableidx);
	// if(snpidx!=null && setSnpInExonTableIdx.contains(snpidx)) {
	// //Set setnonsyn =
	// mapTableIdx2NonsynAlleles.get(BigDecimal.valueOf(snpidx.longValue()));
	// Set setnonsyn = mapTableIdx2NonsynAlleles.get(snpidx);
	// if(setnonsyn==null) {
	// // synonymous
	// } else {
	// if(setnonsyn.contains(cellval.charAt(0))) {
	// return "<div style=\"" + STYLE_NONSYNONYMOUS + "\">" + cellval + "</div>";
	// }
	// }
	//
	// /*
	// if(setnonsyn==null || !setnonsyn.contains(cellval.charAt(0))) {
	// // synonymous
	// //return "<div style=\"" + STYLE_SYNONYMOUS + "\">" + cellval + "</div>";
	// } else
	// return "<div style=\"" + STYLE_NONSYNONYMOUS + "\">" + cellval + "</div>";
	// */
	// }
	// }
	// }
	//
	//
	// if(cellval.isEmpty())
	// return "";
	// else if(cellval.equals("*") || cellval.equals("-") ||
	// cellval.equals("&#151;") || cellval.equals(
	// tabledata.getListPos().get(tableidx).getRefnuc() ) || (indelposref!=null &&
	// cellval.equals(indelposref)) )
	// return "<div style=\"text-align:center\">" + cellval + "</div>";
	// else {
	//
	// //if(params.isbSNP() && params.isbColorSpliceSNP()) {
	// if(params.isbSNP()) {
	// if( setDonorPos.contains( mapIdx2Pos.get(tableidx) ) ) {
	// return "<div style=\"" + STYLE_SPLICE_DONOR + "\">" + cellval + "</div>";
	// }
	// if( setAcceptorPos.contains( mapIdx2Pos.get(tableidx))) {
	// return "<div style=\"" + STYLE_SPLICE_ACCEPTOR + "\">" + cellval + "</div>";
	// }
	// }
	//
	// return "<div style=\"" + STYLE_MISMATCH + "\">" + cellval + "</div>";
	// }
	//
	//
	// }
	// else if (colIndex==2) {
	// BigDecimal varid = mapOrder2VarId.get(rowIndex);
	// return tabledata.getMapVariety2Mismatch().get(varid).toString();
	// }
	// else if (colIndex==1)
	// return mapVarId2Var.get( mapOrder2VarId.get(rowIndex) ).getIrisId();
	// else {
	// return "<div style=\"width:150px\">" +
	// mapVarId2Var.get(mapOrder2VarId.get(rowIndex) ).getName() + "</div>" ;
	// }
	//
	// }

	@Override
	public String renderHeader(Component owner, Object data, // List<String> data,
			int rowIndex, int colIndex) throws Exception {

		try {
			// AppContext.debug("renderHeader " + rowIndex + "," + colIndex + " " + data );

			Set setHeaderPosGapRegion = null;
			Set setHeaderPosDeletionRegion = null;

			if (params.isbIndel() && params.isbAlignIndels()) {
				if (rowIndex == 0) {
					setHeaderPosGapRegion = setPosGapRegion;
					setHeaderPosDeletionRegion = setPosDeletionRegion;
				} else if (rowIndex == 2 && params.isbShowNPBPositions()) {
					setHeaderPosGapRegion = tabledata.getIndelstringdata().getSetPosGapRegion();
					setHeaderPosDeletionRegion = tabledata.getIndelstringdata().getSetPosDeletionRegion();
				}
			}

			int tableidx = colIndex - frozenCols;

			if (rowIndex == 0 || (rowIndex == 2 && params.isbShowNPBPositions())) {
				// Position posobj = ((List<Position>)data).get(colIndex);

				// String posvalue=posobj.getContig();
				// if(!posvalue.isEmpty())
				// posvalue+="-"+posobj.getPosition().stripTrailingZeros().toString();

				// posobj.getPosition().

				String posvalue = ((List) data).get(colIndex).toString();
				// AppContext.debug( posvalue);

				if (posvalue.isEmpty())
					return "";

				String posvalueonly = posvalue;
				if (colIndex >= frozenCols && !tabledata.isNipponbareReference() && posvalue.contains("-")) { // mapRefPos2MSU7Pos!=null)
																												// {
					posvalueonly = posvalue.split("\\-")[1].trim();
				}

				String color = "black";

				if (colIndex >= frozenCols) {
					if (params.isbIndel() && params.isbAlignIndels()) { // tabledata.hasIndel()) {

						if (setHeaderPosGapRegion.contains(mapIdx2Pos.get(tableidx))) // BigDecimal.valueOf(
																						// Double.valueOf(posvalueonly)))
																						// )
							color = "green";
						else if (setHeaderPosDeletionRegion.contains(mapIdx2Pos.get(tableidx))) // BigDecimal.valueOf(Double.valueOf(posvalueonly)))
																								// )
							color = "blue";

						// }

					}
				}

				if (rowIndex == 0 || (rowIndex == 2 && params.isbShowNPBPositions()))
					return "<div style=\"color:" + color + "\"><span class=\"cvertical\">" + posvalue + "</span></div>";
				else
					return "<div style=\"color:" + color + "\">" + posvalue + "</div>";

			} else {
				// allele values

				if (data == null)
					AppContext.debug("data=null at rowIndex=" + rowIndex + ",colIndex=" + colIndex);

				String refnuc = (String) ((List) data).get(colIndex);
				if (colIndex >= frozenCols && (refnuc == null || refnuc.isEmpty())) {
					// tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-frozenCols);
					BigDecimal pos = tabledata.getListPos().get(colIndex - frozenCols).getPosition();
					if (tabledata.getIndelstringdata() != null
							&& tabledata.getIndelstringdata().getMapIndelpos2Refnuc() != null)
						refnuc = tabledata.getIndelstringdata().getMapIndelpos2Refnuc().get(pos);
				}

				if (colIndex >= frozenCols) {

					Position snppos = mapIdx2Pos.get(colIndex - frozenCols);

					if (snppos == null) {
						AppContext.debug("snppos==null at " + (colIndex - frozenCols));
					}

					if (rowIndex == 1) {
						if (refnuc == null)
							mapPos2Refnuc.put(snppos, "");
						else
							mapPos2Refnuc.put(snppos, refnuc.trim());
					}

					if (refnuc == null)
						return "";

					if (colIndex >= frozenCols && !mapPos2Refnuc.get(snppos).equals(refnuc)) {
						if (refnuc.equals("-") || refnuc.equals(""))
							refnuc = "&#151;"; // "&#151;"

						String ttt = "<div align=\"center\" style=\"" + STYLE_MISMATCH + "\" >"
								+ refnuc.replace("null", "").trim() + "</div>";
						// String ttt = "<label align=\"center\" style=\"" + STYLE_MISMATCH + "\" >" +
						// refnuc.replace("null", "").trim() + "</label>" ;

						// String ttt = "<div style=\"" + STYLE_MISMATCH + "\" tooltip=\"headertooltip_"
						// + colIndex + "\">" + refnuc.replace("null", "").trim() + "</div>" +
						// "<popup id=\"headertooltip_" + colIndex + "\"><include
						// src=\"biglistbox_headerpopup.zul\" colIdx=\"" + colIndex + "\"/></popup>";
						// AppContext.debug(ttt);
						return ttt;

					} else {
						if (refnuc.equals("-") || refnuc.equals(""))
							refnuc = "&#151;"; // "&#151;"
						String ttt = "<div align=\"center\" >" + refnuc.replace("null", "").trim() + "</div>";
						// String ttt = "<label align=\"center\" >" + refnuc.replace("null", "").trim()
						// + "</label>" ;

						// String ttt = "<div align=center tooltip=\"headertooltip_" + colIndex + "\">"
						// + refnuc.replace("null", "").trim() + "</div>" +
						// "<popup id=\"headertooltip_" + colIndex + "\"><include
						// src=\"biglistbox_headerpopup.zul\" colIdx=\"" + colIndex + "\"/></popup>" ;
						// AppContext.debug(ttt);
						return ttt;

					}

				} else {
					if (colIndex == 3) {
						String matchval = ((List) data).get(colIndex).toString();
						if (matchval != null)
							return matchval;
					}

				}

				/*
				 * if(rowIndex==3) { if(colIndex>frozenCols &&
				 * !mapIdx2Refnuc.get(colIndex-frozenCols).equals(refnuc) ) {
				 * if(refnuc.equals("-")) refnuc="&#151;"; // "&#151;" return "<div style=\"" +
				 * STYLE_MISMATCH + "\">" + refnuc.replace("null", "").trim() + "</div>"; } else
				 * { if(refnuc.equals("-")) refnuc="&#151;"; // "&#151;" return
				 * "<div align=center>" + refnuc.replace("null", "").trim() + "</div>" ;
				 * //.split("\t")[rowIndex]; } } else { if(refnuc.equals("-")) refnuc="&#151;";
				 * // "&#151;" return "<div align=center>" + refnuc.replace("null", "").trim() +
				 * "</div>" ; //.split("\t")[rowIndex]; }
				 */

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		return "";

	}

	public Component getFirstRow() {
		return firstRow;
	}

}
