package org.irri.iric.portal.genomics.zkui;

import java.util.Comparator;

import org.irri.iric.ds.chado.domain.Locus;

public class LocusListItemComparator implements Comparator {

	private boolean asc = false;
	private int index = 0;
	private int type = 0;
	private int v;

	public LocusListItemComparator(boolean asc, int index, int type) {
		super();
		this.asc = asc;
		this.index = index;
		this.type = type;
	}

	@Override
	public int compare(Object o1, Object o2) {
		

		Locus l1 = (Locus) o1;
		Locus l2 = (Locus) o2;

		Object temp1 = null;
		Object temp2 = null;
		switch (index) {
		case 0:
			temp1 = l1.getUniquename();
			temp2 = l2.getUniquename();
			break;
		case 1:
			temp1 = l1.getContig();
			temp2 = l2.getContig();
			break;
		case 2:
			temp1 = l1.getFmin();
			temp2 = l2.getFmin();
			break;
		case 3:
			temp1 = l1.getFmax();
			temp2 = l2.getFmax();
			break;
		case 4:
			temp1 = l1.getStrand();
			temp2 = l2.getStrand();
			break;
		case 6:
			temp1 = l1.getDescription();
			temp2 = l2.getDescription();
			break;

		}

		if (type == 0) {
			String s1 = (String) temp1;
			String s2 = (String) temp2;
			v = s1.compareTo(s2);
		} else if (type == 1) {
			Integer s1 = (Integer) temp1;
			Integer s2 = (Integer) temp2;
			v = s1.compareTo(s2);
		}

		return asc ? v : -v;

	}

}
