package user.ui.module.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantTable;
//import org.zkoss.addon.MatrixModel;
import org.zkoss.lang.Objects;
import org.zkoss.zkmax.zul.MatrixModel;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

/**
 * A faker MatrixModel is used to handle a unlimited big table data.
 * 
 * @author jumperchen
 */
public class FakerMatrixModel<Head extends List, Row extends List, Cell, Header> extends AbstractListModel<Row>
		implements MatrixModel<Row, Head, Cell, Header>, Sortable, VariantTable {

	private int frozenCols = AppContext.getSnpMatrixFrozenCols(); // 3;
	private VariantStringData data;
	private String message;
	private Set<Position> setGapPos;
	private Map<Integer, Position> mapIdx2Pos;
	private Map<Integer, String> mapDeletionIdx2Refnuc;

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

	private Comparator<Cell> _sorting;

	private boolean _sortDir = true;

	@SuppressWarnings("unchecked")
	@Override
	public void sort(Comparator cmpr, boolean ascending) {
		_sorting = cmpr;
		_sortDir = ascending;
		fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}

	@Override
	public String getSortDirection(Comparator cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}

	public FakerMatrixModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FakerMatrixModel(VariantStringData data) {
		super();
		// TODO Auto-generated constructor stub
		this.data = data;
		if (data.hasIndel())
			setGapPos = data.getIndelstringdata().getSetPosGapRegion(); // .getSetGapIdx();

		mapIdx2Pos = data.getMapIdx2Pos();

		_FakerMatrixModel(data.getListVariantsString(), data.getListPos());
	}

	// public FakerMatrixModel(VariantTableRandomImpl table) {
	// super();
	// // TODO Auto-generated constructor stub
	// // _FakerMatrixModel(data.getListVariantsString(), data.getListPos());
	// }

	public FakerMatrixModel(int colSize, int rowSize) {
		_colSize = colSize;
		_rowSize = rowSize;
		_listSnpString = new ArrayList();
		_rowCache = new HashMap<String, List<String>>();
		_headerData = new FakerKeyList<String>(colSize, 0, new Fun() {
			@Override
			public Object apply(int index) {
				return "Header x = " + index;
			}
		});
		_headerData2 = new FakerKeyList<String>(colSize, 1, new Fun() {
			@Override
			public Object apply(int index) {
				return "Header x = " + index;
			}
		});
	}
	//

	// public FakerMatrixModel(List listSnpString, final String reference) {
	// _colSize = reference.length()+3;
	// _rowSize = listSnpString.size();
	// _rowCache = new HashMap<String, List<String>>();
	// _listSnpString = listSnpString;
	// _headerData = new FakerKeyList<String>( reference.length()+3, 0, new Fun() {
	// @Override
	// public Object apply(int index) {
	// //return "Header x = " + index;
	// if(index>1)
	// return reference.substring(index-3, index-2);
	// else if(index==0)
	// return "VarietyID";
	// else
	// return "Mismatch";
	// }});
	// }

	public FakerMatrixModel(List listSnpString, final List<SnpsAllvarsPos> listSnpsAllvarsPos) {
		_FakerMatrixModel(listSnpString, listSnpsAllvarsPos);
	}

	private void _FakerMatrixModel(List listSnpString, final List<SnpsAllvarsPos> listSnpsAllvarsPos) {
		_colSize = listSnpsAllvarsPos.size() + frozenCols;
		_rowSize = listSnpString.size();
		_rowCache = new HashMap<String, List<String>>();
		_listSnpString = listSnpString;

		_headerData = new FakerKeyList<String>(listSnpsAllvarsPos.size() + frozenCols, 0, new Fun() {
			@Override
			public Object apply(int index) {
				// return "Header x = " + index;
				//
				if (index >= frozenCols) {
					SnpsAllvarsPos snppos = listSnpsAllvarsPos.get(index - frozenCols);
					return snppos.getPosition().toString();
				} else if (index == 2)
					return "Mismatch";
				else if (index == 1)
					return "IRIS ID";
				else // if(index==0)
					return "Variety Name";
			}
		});
		_headerData2 = new FakerKeyList<String>(listSnpsAllvarsPos.size() + frozenCols, 1, new Fun() {
			@Override
			public Object apply(int index) {
				// return "Header x = " + index;
				//
				if (index >= frozenCols) {
					SnpsAllvarsPos snppos = listSnpsAllvarsPos.get(index - frozenCols);
					return snppos.getRefnuc();
				} else if (index == 0)
					return "Reference";
				else
					return "";
			}
		});
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
		final int rowIndex = _sortDir ? index : getSize() - index - 1; // handle the sorting
		final String key = String.valueOf(rowIndex);
		List<String> value = _rowCache.get(key);
		if (value == null) {
			value = new FakerKeyList<String>(_colSize, rowIndex, new Fun() {
				@Override
				public Object apply(int index) {

					return _listSnpString.get(rowIndex);

					// return "y = " + rowIndex;
					// return _listSnpString.get(rowIndex).getVarnuc();
					/*
					 * List<String> extlist = new ArrayList(); SnpsStringAllvars snpstr =
					 * _listSnpString.get(rowIndex); extlist.add(snpstr.getVar().toString());
					 * extlist.add(snpstr.getMismatch().toString());
					 * extlist.add(snpstr.getVarnuc()); return extlist;
					 */
					// return _listSnpString.get(rowIndex).getVarnuc();

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
		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Head getHeadAt(int rowIndex) {
		if (rowIndex == 0)
			return (Head) _headerData;
		else
			return (Head) _headerData2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cell getCellAt(Row rowData, int columnIndex) {

		if (setGapPos == null)
			return (Cell) rowData.get(columnIndex);
		else {
			// if(setGapIdx.contains(columnIndex)) return (Cell)"-";
			if (setGapPos.contains(columnIndex))
				return (Cell) "-";
		}
		return (Cell) "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Header getHeaderAt(Head headData, int columnIndex) {
		return (Header) headData.get(columnIndex);
	}

	@Override
	public void setVariantStringData(VariantStringData data, GenotypeQueryParams params, List listCDS)
			throws Exception {

	}

	@Override
	public void setVariantStringData(VariantStringData data, GenotypeQueryParams params) {

		this.data = data;
		if (data.hasIndel())
			setGapPos = data.getIndelstringdata().getSetPosGapRegion(); // .getSetGapPos();
		mapIdx2Pos = data.getMapIdx2Pos();
		_FakerMatrixModel(data.getListVariantsString(), data.getListPos());
	}

	@Override
	public String getMessage() {

		return message;
	}

	@Override
	public void setMessage(String message) {

		this.message = message;
	}

	@Override
	public VariantStringData getVariantStringData() {

		return data;
	}

	@Override
	public String[] getContigs() {
		return null;
	}

}
