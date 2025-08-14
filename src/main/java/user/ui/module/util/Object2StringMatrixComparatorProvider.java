package user.ui.module.util;

import java.util.Comparator;

import org.zkoss.zkmax.zul.MatrixComparatorProvider;

public class Object2StringMatrixComparatorProvider<T> implements MatrixComparatorProvider<Object[]> {
	private int _x = -1;

	private boolean _acs;

	private Object2StringComparator _cmpr;

	public Object2StringMatrixComparatorProvider(boolean asc) {
		_acs = asc;
		_cmpr = new Object2StringComparator(this);
	}

	@Override
	public Comparator<Object[]> getColumnComparator(int columnIndex) {
		this._x = columnIndex;

		// AppContext.debug("sort by column " + columnIndex);

		return _cmpr;

	}

	// a real String comparator
	public class Object2StringComparator implements Comparator<Object[]> {
		private Object2StringMatrixComparatorProvider _mmc;

		public int getColumn() {
			return _mmc._x;
		}

		public Object2StringComparator(Object2StringMatrixComparatorProvider mmc) {
			_mmc = mmc;
		}

		@Override
		public int compare(Object[] o1, Object[] o2) {

			// try {
			// AppContext.debug( "o1_0=" + o1.get(0) + " o2_0=" + o2.get(0));
			// AppContext.debug( "o1[" + _mmc._x + "]=" + o1[_mmc._x] + ", o2[" + _mmc._x +
			// "]=" + o2[_mmc._x]);
			// AppContext.debug( "o1[" + _mmc._x + "]=" + o1.get(_mmc._x) + ", o2[" +
			// _mmc._x + "]=" + o2.get(_mmc._x));

			// return o1.get(_mmc._x).compareTo(o2.get(_mmc._x)) * (_acs ? 1 : -1);

			Object o1x = o1[_mmc._x];
			Object o2x = o2[_mmc._x];
			// returno1x.toString().compareTo( o2x.toString() ) * (_acs ? 1 : -1);

			String s1 = "";
			if (o1x != null)
				s1 = o1x.toString();
			String s2 = "";
			if (o2x != null)
				s2 = o2x.toString();

			// AppContext.debug(s1 + "\t" + s2);
			/*
			 * if(o1x==null && o2x==null) return 0; if(o1x==null) return -1*(_acs ? 1 : -1);
			 * if(o2x==null) return (_acs ? 1 : -1);
			 */

			if (s1.isEmpty() && s2.isEmpty())
				return 0;
			if (s1.isEmpty())
				return -1 * (_acs ? 1 : -1);
			if (s2.isEmpty())
				return (_acs ? 1 : -1);

			try {
				return ((Comparable) o1x).compareTo(o2x) * (_acs ? 1 : -1);
			} catch (Exception ex) {
				// ex.printStackTrace();
				try {
					return Double.valueOf(s1).compareTo(Double.valueOf(s2)) * (_acs ? 1 : -1);
				} catch (Exception ex2) {
					// ex2.printStackTrace();
					try {
						return s1.compareTo(s2) * (_acs ? 1 : -1);
					} catch (Exception ex3) {
						// ex3.printStackTrace();
						return 0;
					}
				}
			}
		}
	}
}