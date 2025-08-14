/* Copied from FakerMatrixModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 23, 2012 2:29:27 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
 */
package org.irri.iric.portal.genotype.zkui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.irri.iric.ds.chado.domain.ConvertposAny2Allrefs;
import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.Organism;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImplAllelePvalue;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantTable;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
import org.irri.iric.portal.genotype.service.VariantTableArraysImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.lang.Objects;
import org.zkoss.zkmax.zul.MatrixModel;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

import user.ui.module.util.Object2StringMatrixComparatorProvider.Object2StringComparator;

/**
 * A faker MatrixModel is used to handle a unlimited big table data.
 * 
 * @author jumperchen
 */
// @Component("Object2StringMatrixModel")
// @Scope("prototype")
public class Object2StringMultirefsMatrixModel<Head extends List, Row extends List, Cell, Header>
		extends AbstractListModel<Row> implements MatrixModel<Row, Head, Cell, Header>, Sortable, ListModelExt {

	private Map contigid2name = new HashMap();
	private int frozenCols = AppContext.getSnpMatrixFrozenCols();// 6;
	private VariantTable data;
	private String message;
	private GenotypeQueryParams params;
	private Map<String, Map<BigDecimal, StockSample>> mapVarid2Variety;
	private Grid gridHeader;
	private int biglistboxRows, lastY;
	private Map<BigDecimal, Object> mapVarid2Phenotype;
	private String sPhenotype;
	private Map<String, Double> mapRef2Match;

	// private Map<BigDecimal, MultiReferencePosition> mapMSU7Pos2ConvertedPos;

	private List<String> listOtherRefs;
	private Map<String, Map<BigDecimal, MultiReferencePosition>> mapOrg2MSU7Pos2ConvertedPos;
	private Map<String, Map<BigDecimal, MultiReferencePosition>> mapOrg2RefPos2ConvertedPos;
	private Map<String, String> mapMSU7Pos2FilterAllele;

	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemdao;

	// @Autowired
	// private ScaffoldDAO scaffolddao;

	// a rendering function
	private interface Fun<T> {
		public T apply(int index);
	}

	// A faker of key list implementation that contains a key to speed up the
	// performance.
	// Because Java Collection framework didn't handle it well for huge data, it
	// will
	// go through whole the list entries to receive the value for those methods,
	// hashCode(), equals(), and toString()
	private class FakerKeyList<T> extends AbstractList<T> {
		final int _size;
		Map<String, T> _updateCache = new HashMap<String, T>();
		final Fun<?> _fn;
		final String _key;

		public FakerKeyList(int size, int key, Fun<?> fn) {
			_size = size;
			_key = key + "_" + size;
			_fn = fn;
		}

		@Override
		public int size() {
			return _size;
		}

		@Override
		public boolean isEmpty() {
			return _size == 0;
		}

		@Override
		public T get(int index) {
			// if changed, returns the changed value
			Object val = _updateCache.get(String.valueOf(index));
			if (val != null)
				return (T) val;
			return (T) _fn.apply(index);
		}

		@Override
		public T set(int index, T element) {
			_updateCache.put(String.valueOf(index), element);
			return element;
		}

		@Override
		public int hashCode() {
			return _key.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof FakerKeyList) {
				return _key.equals(((FakerKeyList) (obj))._key);
			}
			return false;
		}

		@Override
		public String toString() {
			return _key;
		}
	}

	private int _colSize;

	private int _rowSize;

	private Map<String, List<String>> _rowCache;

	// private List<SnpsStringAllvars> _listSnpString;
	private List _listSnpString;

	private List<String> _headerData;
	// private List<String[]> _headerData;
	private List<String> _headerData2;
	private List<String> _headerData3;
	private List<String> _headerData4;

	private List<String> _headerDataAlleles1;
	private List<String> _headerDataAlleles2;
	private List<String> _headerDataAlleles3;
	private List<String> _headerDataAlleles4;

	private List<String> _headerFilterDataAlleles;

	private Comparator<Cell> _sorting;

	private boolean _sortDir = true;

	@SuppressWarnings("unchecked")
	@Override
	public void sort(Comparator cmpr, boolean ascending) {

		// try {
		_sorting = cmpr;
		_sortDir = ascending;

		Collections.sort(_listSnpString, cmpr);

		// AppContext.debug("cmpr=" + cmpr.getClass() + " " + cmpr + " asc=" +
		// ascending);

		Object2StringComparator cmp = (Object2StringComparator) cmpr;

		int col = cmp.getColumn();
		Iterator<Object[]> itMatrix = _listSnpString.iterator();
		while (itMatrix.hasNext()) {
			Object[] obj = itMatrix.next();
			// AppContext.debug( obj[0] + " " + obj[1] + " " + obj[2] + " " +
			// obj[col].toString());
		}
		// AppContext.debug("sorted by column = " + col + ", sortDir=" + _sortDir);
		_rowCache = new HashMap<String, List<String>>();

		// ListDataEvent.
		// fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);

		// }catch(Exception ex) {
		// ex.printStackTrace();
		// throw ex;
		// }

		try {

			// gridBiglistheader.setModel( new SimpleListModel(
			// model.getRowHeaderList(biglistboxRows, lastY) ));
			gridHeader.setModel(new SimpleListModel(getRowHeaderList(biglistboxRows, lastY)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setHeaderRows(int nRows, int lastY) {
		this.biglistboxRows = nRows;
		this.lastY = lastY;
	}

	@Override
	public String getSortDirection(Comparator cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}

	public Object2StringMultirefsMatrixModel(VariantTable data, GenotypeQueryParams params, Map mapVarid2Variety,
			Grid gridHeader,Map mapVarid2Phenotype, String sPhenotype) {
		super();
		// TODO Auto-generated constructor stub
		this.data = data;
		this.params = params;
		this.mapVarid2Variety = mapVarid2Variety;
		this.gridHeader = gridHeader;
		this.mapVarid2Phenotype = mapVarid2Phenotype;

		if (mapVarid2Phenotype != null) {
			frozenCols++;
			if (sPhenotype.length() > 15)
				sPhenotype = sPhenotype.substring(0, 15);
		}

		this.sPhenotype = sPhenotype;

		// mapMSU7Pos2ConvertedPos =
		// data.getVariantStringData().getMapMSU7Pos2ConvertedPos();
		mapOrg2MSU7Pos2ConvertedPos = data.getVariantStringData().getMapOrg2MSU7Pos2ConvertedPos();
		mapOrg2RefPos2ConvertedPos = data.getVariantStringData().getMapOrg2RefPos2ConvertedPos();

		mapRef2Match = data.getVariantStringData().getMapReference2Mismatch();
		AppContext.debug("mapRef2Match=" + mapRef2Match);
		// mapVar2Mismatch = data.getVariantStringData().getMapVariety2Mismatch();

		if (params.isbShowAllRefAlleles()) {
			if (params.getOrganism().getName().equals(AppContext.JAPONICA_NIPPONBARE)) {
				listOtherRefs = new ArrayList();
				if (mapOrg2MSU7Pos2ConvertedPos.size() > 0)
					listOtherRefs.addAll(mapOrg2MSU7Pos2ConvertedPos.keySet());
				listOtherRefs.remove(AppContext.REFERENCE_NIPPONBARE());
			} else {
				listOtherRefs = new ArrayList();
				if (mapOrg2RefPos2ConvertedPos.size() > 0)
					listOtherRefs.addAll(mapOrg2RefPos2ConvertedPos.keySet());
				listOtherRefs.remove(params.getOrganism().getName());
			}
		}

		if (params.isbAlleleFilter() || params.isVarAlleleFilter()) {
			mapMSU7Pos2FilterAllele = new HashMap();
			Iterator<MultiReferencePositionImplAllelePvalue> itPos = params.getPoslist().iterator();
			while (itPos.hasNext()) {
				MultiReferencePositionImplAllelePvalue posallele = itPos.next();
				mapMSU7Pos2FilterAllele.put(posallele.getContig() + "-" + posallele.getPosition(),
						posallele.getAllele());
			}

			AppContext.debug("model mapMSU7Pos2FilterAllele:" + mapMSU7Pos2FilterAllele);
		}

		// AppContext.debug("Object2StringMultirefsMatrixModel
		// getMapMSU7Pos2ConvertedPos=" + mapMSU7Pos2ConvertedPos + ";" + );
		_Object2StringMatrixModel();
	}

	public Object2StringMultirefsMatrixModel(VariantTable data, GenotypeQueryParams params, Map mapVarid2Variety, Grid gridHeader) {
		this(data, params, mapVarid2Variety,gridHeader, null, null);
	}

	String getIrisIdLabel() {
		/*
		 * if(this.params.getDataset().equals("hdra")) return "GSOR ID"; else return
		 * "IRIS ID";
		 */
		return "Assay";
	}

	private void _Object2StringMatrixModel() {

		final DecimalFormat df = new DecimalFormat("0.00");

		Map<BigDecimal, Number> mapVar2Mismatch = data.getVariantStringData().getMapVariety2Mismatch();

		if (data instanceof VariantAlignmentTableArraysImpl) {
			VariantAlignmentTableArraysImpl tabledata = (VariantAlignmentTableArraysImpl) data;
			_colSize = tabledata.getPosition().length + frozenCols;
			_rowSize = tabledata.getVarid().length;
			List listStrArray = new ArrayList();
			Object objtable[][] = tabledata.getVaralleles();

			if (this.mapVarid2Phenotype != null) {
				for (int i = 0; i < objtable.length; i++) {

					BigDecimal varid = BigDecimal.valueOf(tabledata.getVarid()[i]);

					Map<BigDecimal, StockSample> mapVarid2Variety2 = mapVarid2Variety.get(tabledata.getDataset()[i]);
					StockSample var = mapVarid2Variety2.get(varid);
					Object phen = mapVarid2Phenotype.get(var.getVarietyId());

					/*
					 * AppContext.debug( var.toString() ); AppContext.debug(
					 * (phen!=null?phen.toString():"phen=null") ); AppContext.debug("var.getName()="
					 * + var.getName());
					 */

					/*
					 * if(phen!=null) listStrArray.add( ArrayUtils.addAll(new
					 * Object[]{var.getName().toUpperCase(),
					 * (var.getIrisId()!=null?var.getIrisId():var.getAccession()),
					 * var.getSubpopulation(), var.getDataset(),mapVar2Mismatch.get(varid), phen },
					 * objtable[i] )); else listStrArray.add( ArrayUtils.addAll(new
					 * Object[]{var.getName().toUpperCase(),
					 * (var.getIrisId()!=null?var.getIrisId():var.getAccession()),
					 * var.getSubpopulation(), var.getDataset(),mapVar2Mismatch.get(varid) ,""},
					 * objtable[i] ));
					 */
					if (phen != null)
						listStrArray.add(ArrayUtils
								.addAll(new Object[] { (var.getName() == null ? "" : var.getName().toUpperCase()),
										(var.getAssay() != null ? var.getAssay() : ""),
										(var.getAccession() != null ? var.getAccession() : ""), var.getSubpopulation(),
										var.getDataset(), mapVar2Mismatch.get(varid), phen }, objtable[i]));
					else
						listStrArray.add(ArrayUtils
								.addAll(new Object[] { (var.getName() == null ? "" : var.getName().toUpperCase()),
										(var.getAssay() != null ? var.getAssay() : ""),
										(var.getAccession() != null ? var.getAccession() : ""), var.getSubpopulation(),
										var.getDataset(), mapVar2Mismatch.get(varid), "" }, objtable[i]));
				}

			} else {
				BigDecimal newIdx;
				for (int i = 0; i < objtable.length; i++) {
					
					BigDecimal varid = BigDecimal.valueOf(tabledata.getVarid()[i]);
					
					//TODO 2nd reference genome
//					System.out.println(i +"->"+ varid);
//					
//					if (tabledata.getDataset()[i].equals("3k-MH63v15")) {
//						Map<Integer, BigDecimal> idxSample = tabledata.getVariantStringData().getSnpstringdata().getIdxSample();
//						varid = idxSample.get(tabledata.getVarid()[i].intValue());
//					}
					
					//BigDecimal varid = BigDecimal.valueOf(tabledata.getVarid()[i]);

					Map<BigDecimal, StockSample> mapVarid2Variety2 = mapVarid2Variety.get(tabledata.getDataset()[i]);

					StockSample var = mapVarid2Variety2.get(varid);

					if (var != null) {
						listStrArray
								.add(ArrayUtils.addAll(
										new Object[] { (var.getName() == null ? "" : var.getName().toUpperCase()),
												(var.getAssay() != null ? var.getAssay() : ""),
												(var.getAccession() != null ? var.getAccession() : ""),
												var.getSubpopulation(), var.getDataset(), mapVar2Mismatch.get(varid) },
										objtable[i]));

					}

				}

				// }
				// else
				// listStrArray.add(ArrayUtils.addAll(new Object[] { "", "", "", "", "", "" },
				// objtable[i]));

			}
			_listSnpString = listStrArray;

		} else if (data instanceof VariantTableArraysImpl) {
			VariantTableArraysImpl tabledata = (VariantTableArraysImpl) data;
			_colSize = tabledata.getPosition().length + frozenCols;
			_rowSize = tabledata.getVarid().length;
			List listStrArray = new ArrayList();
			Object objtable[][] = tabledata.getVaralleles();

			if (this.mapVarid2Phenotype != null) {
				for (int i = 0; i < objtable.length; i++) {
					BigDecimal varid = BigDecimal.valueOf(tabledata.getVarid()[i]);

					Map<BigDecimal, StockSample> mapVarid2Variety2 = mapVarid2Variety.get(tabledata.getDatasets()[i]);

					StockSample var = mapVarid2Variety2.get(varid);
					Object phen = mapVarid2Phenotype.get(var.getVarietyId());

					if (phen != null)
						listStrArray.add(ArrayUtils.addAll(new Object[] { var.getName().toUpperCase(),
								(var.getAssay() != null ? var.getAssay() : ""),
								(var.getAccession() != null ? var.getAccession() : ""), var.getSubpopulation(),
								var.getDataset(), mapVar2Mismatch.get(varid), phen }, objtable[i]));
					else
						listStrArray.add(ArrayUtils.addAll(new Object[] { var.getName().toUpperCase(),
								(var.getAssay() != null ? var.getAssay() : ""),
								(var.getAccession() != null ? var.getAccession() : ""), var.getSubpopulation(),
								var.getDataset(), mapVar2Mismatch.get(varid), "" }, objtable[i]));
				}

			} else {
				for (int i = 0; i < objtable.length; i++) {

					BigDecimal varid = BigDecimal.valueOf(tabledata.getVarid()[i]);

					Map<BigDecimal, StockSample> mapVarid2Variety2 = mapVarid2Variety.get(tabledata.getDatasets()[i]);
					StockSample var = mapVarid2Variety2.get(varid);

					listStrArray.add(ArrayUtils.addAll(
							new Object[] { var.getName().toUpperCase(), (var.getAssay() != null ? var.getAssay() : ""),
									(var.getAccession() != null ? var.getAccession() : ""), var.getSubpopulation(),
									var.getDataset(), mapVar2Mismatch.get(varid) },
							objtable[i]));

				}
			}
			_listSnpString = listStrArray;
		}

		_rowCache = new HashMap<String, List<String>>();

		_headerData = new FakerKeyList<String>(_colSize, 0, new Fun() {
			@Override
			public Object apply(int index) {

				if (index >= frozenCols) {

					SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
					String out = "";

					DecimalFormat df = new DecimalFormat("0.00");

					if (data.getVariantStringData().isNipponbareReference()) {
						out += df.format(snppos.getPosition()).replace(".00", "");
						// out += df.format( snppos.getPosition() ).replaceAll("\\.0+","");
						if (out.endsWith(".0"))
							out = out.replace(".0", "");

						if (data.getContigs() != null && !out.isEmpty())
							out = data.getContigs()[index - frozenCols] + "-" + out;

						// AppContext.debug( snppos.getPos() + " -> " );
					} else {

						// if(snppos.getPos().toString().contains("\\.")) {
						//
						// out += df.format(
						// data.getVariantStringData().getMapMSU7Pos2ConvertedPos().get(BigDecimal.valueOf(Long.valueOf(
						// snppos.getPos().toString().split("\\.")[0]))).getPosition()
						// ).replace(".00","");
						// if(out.endsWith(".0")) out=out.replace(".0", "");
						// //out = data.getContigs()[index-frozenCols] + "-" + out;
						// }
						// else {
						// out += df.format(
						// data.getVariantStringData().getMapMSU7Pos2ConvertedPos().get(snppos.getPos()).getPosition()
						// ).replace(".00","");
						// if(out.endsWith(".0")) out=out.replace(".0", "");
						// //out = data.getContigs()[index-frozenCols] + "-" + out;
						// }

						/*
						 * if(mapMSU7Pos2ConvertedPos.get(snppos.getPosition())==null) { out =""; } else
						 * { out +=
						 * df.format(mapMSU7Pos2ConvertedPos.get(snppos.getPosition()).getPosition()
						 * ).replace(".00",""); if(out.endsWith(".0")) out=out.replace(".0", "");
						 * 
						 * //AppContext.debug( snppos.getPos() + " -> " +
						 * mapMSU7Pos2ConvertedPos.get(snppos.getPos()).getPosition() ); }
						 * 
						 * if(data.getContigs()!=null && !out.isEmpty()) out =
						 * data.getContigs()[index-frozenCols] + "-" + out;
						 * 
						 */

						if (snppos.getPosition() == null) {
							out = "";
						} else {
							out += df.format(snppos.getPosition()).replace(".00", "");
							if (out.endsWith(".0"))
								out = out.replace(".0", "");
							// out += df.format( snppos.getPosition() ).replaceAll("\\.0+","");
						}

						if (data.getContigs() != null && !out.isEmpty())
							out = data.getContigs()[index - frozenCols] + "-" + out;

						out = out.replaceAll("\\.0+", "");

					}

					// AppContext.debug( "getHeaderModel (" + index + "):" + out);

					return out;

					// VariantTableArraysImpl tabledata = (VariantTableArraysImpl)data;

				}

				else if (index == 5) {
					if (params.isbAlleleFilter() || params.isVarAlleleFilter())
						return "Match";
					else
						return "Mismatch";
				} else if (index == 4)
					return "Dataset";
				else if (index == 3)
					return "Subpopulation";
				else if (index == 1)
					// return "IRIS ID";
					return getIrisIdLabel();
				else if (index == 2)
					return "Accession";

				/*
				 * else if(index==1)
				 * if(params.getDataset().equals(VarietyFacade.DATASET_SNP_HDRA)) return
				 * "Accession"; else
				 * if(params.getDataset().equals(VarietyFacade.DATASET_SNPINDELV2_IUPAC)) return
				 * "IRIS ID"; else return "IRIS ID/Accession";
				 */
				else if (index == 0)
					return "Variety 22";
				else if (mapVarid2Phenotype != null && index == 6)
					return sPhenotype;
				else
					return "";

			}
		});
		_headerData2 = new FakerKeyList<String>(_colSize, 1, new Fun() {
			@Override
			public Object apply(int index) {
				// return "Header x = " + index;
				//
				if (index >= frozenCols) {
					SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
					if (data.getVariantStringData().isNipponbareReference()) {
						return snppos.getRefnuc().substring(0, 1);
					} else {
						String multiref = snppos.getRefnuc().substring(0, 1);
						if (multiref == null) {
							return "";
						} else {
							return multiref;
						}
						/*
						 * MultiReferencePosition multiref
						 * =mapMSU7Pos2ConvertedPos.get(snppos.getPosition()); if(multiref==null) {
						 * return ""; } else { return multiref.getRefnuc(); }
						 */
					}
				} else if (index == 0)
					// return "Reference";
					return params.getOrganism().getName();
				else if (index == 5) {
					// return "mismatch";
					if (mapRef2Match != null)
						return df.format(mapRef2Match.get(params.getOrganism().getName())); // params.getOrganism();
					else
						return "";
				} else
					return "";
			}
		});

		if (params.isbShowNPBPositions()) {

			_headerData3 = new FakerKeyList<String>(_colSize, 2, new Fun() {
				@Override
				public Object apply(int index) {

					if (index >= frozenCols) {

						String out = "";
						SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);

						BigDecimal pos = null;

						if (snppos instanceof ConvertposAny2Allrefs) {
							pos = ((ConvertposAny2Allrefs) snppos).getNbPosition();
							out += ((ConvertposAny2Allrefs) snppos).getNBContigName() + "-" + pos.toString();
							out = out.replaceAll("\\.0+", "");
						}

						// if(snppos instanceof VConvertposAny2allrefs) {
						// pos = ((VConvertposAny2allrefs)snppos).getNbPosition();
						// out += ((VConvertposAny2allrefs)snppos).getNBContigName() + "-" +
						// pos.toString();
						// /*
						//
						// DecimalFormat df = new DecimalFormat("0.00");
						// out += df.format(pos ).replace(".00","");
						// if(out.endsWith(".0")) out=out.replace(".0", "");
						// */
						// }
						return out;
					} else if (index == 5) {
						if (params.isbAlleleFilter() || params.isVarAlleleFilter())
							return "Match";
						else
							return "Mismatch";
					} else if (index == 4)
						return "Dataset";
					else if (index == 3)
						return "Subpopulation";
					else if (index == 2)
						return "Accession";
					else if (index == 1)
						return "IRIS ID";
					else if (index == 0)
						return "Variety";
					else if (mapVarid2Phenotype != null && index == 6)
						return sPhenotype;
					else
						return "";
				}
			});
			_headerData4 = new FakerKeyList<String>(_colSize, 3, new Fun() {
				@Override
				public Object apply(int index) {
					// return "Header x = " + index;
					//
					if (index >= frozenCols) {
						SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
						String ref = "";

						if (snppos instanceof ConvertposAny2Allrefs) {
							ref = ((ConvertposAny2Allrefs) snppos).getNbRefcall();
						}

						/*
						 * if(snppos instanceof VConvertposAny2allrefs) { ref =
						 * ((VConvertposAny2allrefs)snppos).getNbRefcall(); }
						 */

						return ref;
					} else if (index == 0)
						return AppContext.REFERENCE_NIPPONBARE();
					else
						return "";
				}
			});

		}

		if (listOtherRefs != null && !listOtherRefs.isEmpty()) {

			AppContext.debug("preparing model otheralleles for references: " + listOtherRefs);

			// if(params.getOrganism().equals(AppContext.getDefaultOrganism())) {

			if (listOtherRefs.size() > 0) {
				_headerDataAlleles1 = new FakerKeyList<String>(_colSize, 4, new Fun() {
					@Override
					public Object apply(int index) {
						// return "Header x = " + index;
						//
						if (index >= frozenCols) {
							SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);

							// AppContext.debug("mapOrg2MSU7Pos2ConvertedPos="+
							// mapOrg2MSU7Pos2ConvertedPos);
							// AppContext.debug("mapOrg2RefPos2ConvertedPos="+ mapOrg2RefPos2ConvertedPos);

							Map<BigDecimal, MultiReferencePosition> mapPos2Refpos = null;
							if (params.getOrganism().getName().equals(AppContext.JAPONICA_NIPPONBARE))
								mapPos2Refpos = mapOrg2MSU7Pos2ConvertedPos.get(listOtherRefs.get(0));
							else
								mapPos2Refpos = mapOrg2RefPos2ConvertedPos.get(listOtherRefs.get(0));

							MultiReferencePosition multiref = mapPos2Refpos.get(snppos.getPosition());
							if (multiref == null) {
								return "";
							} else {
								return multiref.getRefnuc();
							}
						} else if (index == 0)
							// return "Reference";
							return listOtherRefs.get(0); // params.getOrganism();
						else if (index == 5) {
							// return "mismatch";
							try {
								if (mapRef2Match != null) // && mapRef2Match.get(listOtherRefs.get(0))!=null)
									return df.format(mapRef2Match.get(listOtherRefs.get(0))); // params.getOrganism();
								else
									return "";
							} catch (Exception ex) {
								mapRef2Match.get(listOtherRefs.get(0));
								AppContext.debug("listOtherRefs.get(0)=" + listOtherRefs.get(0) + " , "
										+ mapRef2Match.get(listOtherRefs.get(0)));
							}
							return "";
						} else
							return "";
					}
				});
			}
			if (listOtherRefs.size() > 1) {
				_headerDataAlleles2 = new FakerKeyList<String>(_colSize, 5, new Fun() {
					@Override
					public Object apply(int index) {
						// return "Header x = " + index;
						//
						if (index >= frozenCols) {
							SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
							Map<BigDecimal, MultiReferencePosition> mapPos2Refpos = null;
							if (params.getOrganism().getName().equals(AppContext.JAPONICA_NIPPONBARE))
								mapPos2Refpos = mapOrg2MSU7Pos2ConvertedPos.get(listOtherRefs.get(1));
							else
								mapPos2Refpos = mapOrg2RefPos2ConvertedPos.get(listOtherRefs.get(1));

							MultiReferencePosition multiref = mapPos2Refpos.get(snppos.getPosition());
							if (multiref == null) {
								return "";

							} else {
								return multiref.getRefnuc();
							}
						} else if (index == 0)
							// return "Reference";
							return listOtherRefs.get(1); // params.getOrganism();
						else if (index == 5) {
							// return "Reference";
							if (mapRef2Match != null)
								return df.format(mapRef2Match.get(listOtherRefs.get(1))); // params.getOrganism();
							else
								return "";
						} else
							return "";
					}
				});
			}
			if (listOtherRefs.size() > 2) {
				_headerDataAlleles3 = new FakerKeyList<String>(_colSize, 6, new Fun() {
					@Override
					public Object apply(int index) {
						// return "Header x = " + index;
						//
						if (index >= frozenCols) {
							SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
							Map<BigDecimal, MultiReferencePosition> mapPos2Refpos = null;
							if (params.getOrganism().getName().equals(AppContext.JAPONICA_NIPPONBARE))
								mapPos2Refpos = mapOrg2MSU7Pos2ConvertedPos.get(listOtherRefs.get(2));
							else
								mapPos2Refpos = mapOrg2RefPos2ConvertedPos.get(listOtherRefs.get(2));

							MultiReferencePosition multiref = mapPos2Refpos.get(snppos.getPosition());
							if (multiref == null) {
								return "";
							} else {
								return multiref.getRefnuc();
							}
						} else if (index == 0)
							// return "Reference";
							return listOtherRefs.get(2); // params.getOrganism();
						else if (index == 5) {
							// return "Reference";
							if (mapRef2Match != null)
								return df.format(mapRef2Match.get(listOtherRefs.get(2))); // params.getOrganism();
							else
								return "";
						} else
							return "";
					}
				});
			}
			if (listOtherRefs.size() > 3) {
				_headerDataAlleles4 = new FakerKeyList<String>(_colSize, 7, new Fun() {
					@Override
					public Object apply(int index) {
						// return "Header x = " + index;
						//
						if (index >= frozenCols) {
							SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
							Map<BigDecimal, MultiReferencePosition> mapPos2Refpos = null;
							if (params.getOrganism().getName().equals(AppContext.JAPONICA_NIPPONBARE))
								mapPos2Refpos = mapOrg2MSU7Pos2ConvertedPos.get(listOtherRefs.get(3));
							else
								mapPos2Refpos = mapOrg2RefPos2ConvertedPos.get(listOtherRefs.get(3));
							MultiReferencePosition multiref = mapPos2Refpos.get(snppos.getPosition());

							if (multiref == null) {
								return "";
							} else {
								return multiref.getRefnuc();
							}
						} else if (index == 0)
							// return "Reference";
							return listOtherRefs.get(3); // params.getOrganism();
						else if (index == 5) {
							// return "Reference";
							if (mapRef2Match != null)
								return df.format(mapRef2Match.get(listOtherRefs.get(3))); // params.getOrganism();
							else
								return "";
						} else
							return "";
					}
				});
			}

			// }
		}

		if (this.mapMSU7Pos2FilterAllele != null) {
			this._headerFilterDataAlleles = new FakerKeyList<String>(_colSize, 8, new Fun() {
				@Override
				public Object apply(int index) {
					// return "Header x = " + index;
					//
					if (index >= frozenCols) {
						SnpsAllvarsPos snppos = data.getVariantStringData().getListPos().get(index - frozenCols);
						return mapMSU7Pos2FilterAllele.get(snppos.getContig() + "-" + snppos.getPosition());
					}
					/*
					 * else if(index==0) //return "Reference"; return "Query Alleles"; //
					 * params.getOrganism();
					 * 
					 */
					else
						return "";
				}
			});
		}
	}

	public void update(Integer[] axis, String value) {
		List<String> list = _rowCache.get(String.valueOf(axis[1]));
		list.set(axis[0], value);
		this.fireEvent(ListDataEvent.CONTENTS_CHANGED, axis[0], axis[1]);
	}

	public void setSize(int colSize, int rowSize) {
		_colSize = colSize;
		_rowSize = rowSize;
		this.fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Row getElementAt(int index) {

		final int rowIndex = index; // _listSnpString already reversed in sort();
		final String key = String.valueOf(rowIndex);
		List<String> value = _rowCache.get(key);

		if (value == null) {
			value = new FakerKeyList<String>(_colSize, rowIndex, new Fun() {
				@Override
				public Object apply(int index) {

					return _listSnpString.get(rowIndex);

				}
			});
			_rowCache.put(key, value);
		}
		return (Row) value;
	}

	@Override
	public int getSize() {
		return _rowSize;
	}

	@Override
	public int getColumnSize() {
		return _colSize;
	}

	@Override
	public int getHeadSize() {

		int heads = 2;
		if (params.isbShowNPBPositions())
			heads++;
		if (params.isbShowAllRefAlleles())
			heads += 4;
		if (params.isbAlleleFilter() || params.isVarAlleleFilter())
			heads++;

		return heads;
		/*
		 * if(params.isbShowNPBPositions()) { if(params.isbShowAllRefAlleles()) {
		 * if(params.isbAlleleFilter()) return 8; else return 7; } else {
		 * if(params.isbAlleleFilter()) return 5; else return 4; } } else {
		 * if(params.isbShowAllRefAlleles()) { if(params.isbAlleleFilter()) return 7;
		 * else return 6; } else { if(params.isbAlleleFilter()) return 3; else return 2;
		 * } }
		 */

		/*
		 * if(params.isbShowNPBPositions()) { if(params.isbShowAllRefAlleles()) {
		 * if(params.isbAlleleFilter()) return 8; else return 7; } else {
		 * if(params.isbAlleleFilter()) return 5; else return 4; } } else {
		 * if(params.isbShowAllRefAlleles()) { if(params.isbAlleleFilter()) return 7;
		 * else return 6; } else { if(params.isbAlleleFilter()) return 3; else return 2;
		 * } }
		 */
	}

	@SuppressWarnings("unchecked")
	@Override
	public Head getHeadAt(int rowIndex) {
		/*
		 * if(params.isbShowNPBPositions()) { if(params.isbShowAllRefAlleles()) {
		 * if(params.isbAlleleFilter()) return 8; else return 7; } else {
		 * if(params.isbAlleleFilter()) return 5; else return 4; } } else {
		 * if(params.isbShowAllRefAlleles()) { if(params.isbAlleleFilter()) return 7;
		 * else return 6; } else { if(params.isbAlleleFilter()) return 3; else return 2;
		 * } }
		 */

		if (params.isbShowAllRefAlleles()) {
			if (rowIndex == 0)
				return (Head) _headerData;
			else if (rowIndex == 1)
				return (Head) _headerData2;

			if (params.isbShowNPBPositions()) {
				if (rowIndex == 2)
					return (Head) _headerData3;
				else if (rowIndex == 3)
					return (Head) _headerData4;
				else if (rowIndex == 4)
					return (Head) _headerDataAlleles1;
				else if (rowIndex == 5)
					return (Head) _headerDataAlleles2;
				else if (rowIndex == 6)
					return (Head) _headerDataAlleles3;
				else if (rowIndex == 7)
					return (Head) _headerDataAlleles4;

				if ((params.isbAlleleFilter() || params.isVarAlleleFilter()) && rowIndex == 8)
					return (Head) this._headerFilterDataAlleles;

			} else {
				if (rowIndex == 2)
					return (Head) _headerDataAlleles1;
				else if (rowIndex == 3)
					return (Head) _headerDataAlleles2;
				else if (rowIndex == 4)
					return (Head) _headerDataAlleles3;
				else if (rowIndex == 5)
					return (Head) _headerDataAlleles4;
				if ((params.isbAlleleFilter() || params.isVarAlleleFilter()) && rowIndex == 6)
					return (Head) this._headerFilterDataAlleles;

			}

		} else {
			if (rowIndex == 0)
				return (Head) _headerData;
			else if (rowIndex == 1)
				return (Head) _headerData2;

			if (params.isbShowNPBPositions()) {
				if (rowIndex == 2)
					return (Head) _headerData3;
				else if (rowIndex == 3)
					return (Head) _headerData4;
				if ((params.isbAlleleFilter() || params.isVarAlleleFilter()) && rowIndex == 4)
					return (Head) this._headerFilterDataAlleles;

			} else {
				if ((params.isbAlleleFilter() || params.isVarAlleleFilter()) && rowIndex == 2)
					return (Head) this._headerFilterDataAlleles;
			}
			/*
			 * return (Head) _headerData3; else if(rowIndex==3) return (Head) _headerData4;
			 */

		}

		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Cell getCellAt(Row rowData, int columnIndex) {

		// return (Cell) rowData.get(columnIndex);
		return (Cell) rowData.get(columnIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Header getHeaderAt(Head headData, int columnIndex) {

		// AppContext.debug("Model columnIndex=" + columnIndex);

		return (Header) headData.get(columnIndex);
	}

	public VariantTable getData() {
		return data;
	}

	public List getRowHeaderList(int nRows) {
		return getRowHeaderList(nRows, 0);
	}

	public List getRowHeaderList(int nRows, int firstRow) {

		listitemdao = (ListItemsDAO) AppContext.checkBean(listitemdao, "ListItems");
		// Map<BigDecimal,Variety> mapVarId2Var = listitemsdao.getMapId2Variety();
		// Map<String,Variety> mapVarname2Var =
		// listitemdao.getMapVarname2Variety(params.getDataset());
		// Map<BigDecimal,Variety> mapVarid2Var =
		// listitemdao.getMapId2Variety(params.getDataset());
		List list = new ArrayList();

		int lastIdx = firstRow + nRows;
		if (lastIdx > getSize()) {
			lastIdx = getSize();
		}

		for (int i = firstRow; i < lastIdx; i++) {
			Object[] rowarr = (Object[]) _listSnpString.get(i);
			list.add(rowarr);
		}

		// if(this.mapVarid2Phenotype==null) {
		// for(int i=firstRow; i<lastIdx; i++) {
		// Object[] rowarr = (Object[])_listSnpString.get(i);
		//
		// //Variety var = mapVarname2Var.get( rowarr[0].toString() );
		//
		// //list.add( new Object[]{var.getName()
		// ,(var.getIrisId()!=null?var.getIrisId():"") ,
		// (var.getAccession()!=null?var.getAccession():"") , var.getSubpopulation(),
		// var.getDataset(),rowarr[4] });
		// //list.add( new Object[]{var.getName() ,
		// (var.getIrisId()!=null?var.getIrisId():var.getAccession()) ,
		// var.getSubpopulation(), var.getDataset(),rowarr[4] });
		// list.add( rowarr );
		// }
		// } else {
		// for(int i=firstRow; i<lastIdx; i++) {
		// Object[] rowarr = (Object[])_listSnpString.get(i);
		// Variety var = mapVarname2Var.get( rowarr[0].toString() );
		// Object phen=mapVarid2Phenotype.get(var.getVarietyId());
		// /*
		// if(phen!=null)
		// list.add( new Object[]{var.getName() ,
		// (var.getIrisId()!=null?var.getIrisId():var.getAccession()) ,
		// var.getSubpopulation(), var.getDataset(), rowarr[4], phen });
		// else list.add( new Object[]{var.getName() ,
		// (var.getIrisId()!=null?var.getIrisId():var.getAccession()) ,
		// var.getSubpopulation(), var.getDataset(),rowarr[4], "" });
		// */
		// if(phen!=null)
		// list.add( new
		// Object[]{rowarr[0],rowarr[1],rowarr[2],rowarr[3],rowarr[4],rowarr[5], phen
		// });
		// else list.add(new
		// Object[]{rowarr[0],rowarr[1],rowarr[2],rowarr[3],rowarr[4],rowarr[5], ""});
		// }
		// }

		return list;
	}

	public List getRowHeaderHeaderList() {

		List listHeads = new ArrayList();
		int headsize = this.getHeadSize();
		for (int i = 0; i < headsize; i++) {
			Head ihead = getHeadAt(i);
			listHeads.add(ihead.get(0));
		}
		return listHeads;
	}

}

// PAST CODE TERAINED

/*
 * public List getRowHeaderList(Long[] varid, int nRows, int firstRow) {
 * 
 * listitemsdao = (ListItemsDAO)AppContext.checkBean(listitemsdao,"ListItems");
 * Map<BigDecimal,Variety> mapVarId2Var = listitemsdao.getMapId2Variety();
 * 
 * int lastIdx=firstRow + nRows; if(lastIdx>varid.length) {
 * lastIdx=varid.length; }
 * 
 * List list = new ArrayList(); for(int i=firstRow; i<lastIdx; i++) { Variety
 * var = mapVarId2Var.get(BigDecimal.valueOf(varid[i])); //list.add( new
 * Object[]{varnames[i], var.getIrisId(), var.getSubpopulation(), varmismatch[i]
 * }); list.add( new Object[]{varnames[i], var.getIrisId(),
 * var.getSubpopulation(), varmismatch[i] }); } return list; }
 */

// @Override
// public void setVariantStringData(VariantStringData data) {
// 
// this.data=data;
// _FakerMatrixModel(data.getListVariantsString(), data.getListPos());
// }
//
// @Override
// public String getMessage() {
// 
// return message;
// }
//
// @Override
// public void setMessage(String message) {
// 
// this.message=message;
// }
//
// @Override
// public VariantStringData getVariantStringData() {
// 
// return data;
// }
