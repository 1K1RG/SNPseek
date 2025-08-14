package org.irri.iric.portal.genotype.zkui;

import java.util.Comparator;
import java.util.List;

public class GroupMatrixListItemSorter implements Comparator {
	private boolean asc = false;
	private int index = -1;
	private int v;

	public GroupMatrixListItemSorter(boolean asc, int index) {
		super();
		this.asc = asc;
		this.index = index;
	}

	@Override
	public int compare(Object o1, Object o2) {
		
		List l1 = (List) o1;
		List l2 = (List) o2;
		v = ((Comparable) l1.get(index)).compareTo((Comparable) l2.get(index));
		return asc ? v : -v;
	}
}
