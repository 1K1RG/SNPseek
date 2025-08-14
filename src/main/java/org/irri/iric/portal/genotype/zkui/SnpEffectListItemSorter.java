package org.irri.iric.portal.genotype.zkui;

import java.util.Comparator;

import org.irri.iric.ds.chado.domain.SnpsEffect;

public class SnpEffectListItemSorter implements Comparator {
	private boolean asc = false;
	private int index = -1;
	private String columnname;
	private int v;

	public SnpEffectListItemSorter(boolean asc, int index) {
		super();
		this.asc = asc;
		this.index = index;
	}

	public SnpEffectListItemSorter(boolean asc, String column) {
		super();
		this.asc = asc;
		this.columnname = column;
	}

	@Override
	public int compare(Object o1, Object o2) {
		

		if (o1 instanceof SnpsEffect) {
			SnpsEffect l1 = (SnpsEffect) o1;
			SnpsEffect l2 = (SnpsEffect) o2;
			if (index == 1) {
				v = l1.getScore().compareTo(l2.getScore());
			}
		} else {
			Comparable l1 = (Comparable) o1;
			Comparable l2 = (Comparable) o2;
			v = l1.compareTo(l2);
		}

		return asc ? v : -v;
	}
}
