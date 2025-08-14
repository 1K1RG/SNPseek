package org.irri.iric.portal.genomics.zkui;

import java.util.Comparator;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MarkerAnnotation;

public class MarkerAnnotationSorter implements Comparator {

	private boolean asc = false;
	private int index = -1;
	private String columnname;
	private int v;

	public MarkerAnnotationSorter(boolean asc, int index) {
		super();
		this.asc = asc;
		this.index = index;
	}

	public MarkerAnnotationSorter(boolean asc, String column) {
		super();
		this.asc = asc;
		this.columnname = column;
	}

	@Override
	public int compare(Object o1, Object o2) {
		

		if (o1 instanceof MarkerAnnotation) {
			MarkerAnnotation l1 = (MarkerAnnotation) o1;
			MarkerAnnotation l2 = (MarkerAnnotation) o2;
			if (columnname != null) {
				String s1 = "";
				String s2 = "";
				if (l1.getAnnotation(columnname) != null && !l1.getAnnotation(columnname).isEmpty())
					s1 = ((Locus) l1.getAnnotation(columnname).iterator().next()).getUniquename();
				if (l2.getAnnotation(columnname) != null && !l2.getAnnotation(columnname).isEmpty())
					s2 = ((Locus) l2.getAnnotation(columnname).iterator().next()).getUniquename();
				v = s1.compareTo(s2);
			} else if (index == 0) { // || index==1) {
				v = l1.getContig().compareTo(l2.getContig());
				if (v == 0)
					v = l1.getPosition().compareTo(l2.getPosition());
			} else if (index == 1) { // || index==1) {
				if (l1.getMinusLogPvalue() == null || l2.getMinusLogPvalue() == null)
					return 0;
				v = l1.getMinusLogPvalue().compareTo(l2.getMinusLogPvalue());
			}
		} else {
			Comparable l1 = (Comparable) o1;
			Comparable l2 = (Comparable) o2;
			v = l1.compareTo(l2);
		}

		return asc ? v : -v;
	}

}
