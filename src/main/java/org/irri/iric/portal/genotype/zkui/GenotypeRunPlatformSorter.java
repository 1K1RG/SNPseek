package org.irri.iric.portal.genotype.zkui;

import java.io.Serializable;
import java.util.Comparator;

import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;

public class GenotypeRunPlatformSorter implements Comparator, Serializable {

	private boolean asc = false;
	private int index = -1;
	private String columnname;
	private int v;

	public GenotypeRunPlatformSorter(boolean asc, int index) {
		super();
		this.asc = asc;
		this.index = index;
	}

	public GenotypeRunPlatformSorter(boolean asc, String column) {
		super();
		this.asc = asc;
		this.columnname = column;
	}

	@Override
	public int compare(Object o1, Object o2) {
		

		if (o1 instanceof GenotypeRunPlatform) {
			GenotypeRunPlatform l1 = (GenotypeRunPlatform) o1;
			GenotypeRunPlatform l2 = (GenotypeRunPlatform) o2;
			if (index == 0) {
				v = l1.getDataset().compareTo(l2.getDataset());
			} else if (index == 1) {
				v = l1.getVariantset().compareTo(l2.getVariantset());
			} else if (index == 3) {
				v = l1.getDatePerformed().compareTo(l2.getDatePerformed());
			} else if (index == 4) {
				v = l1.getMethod().compareTo(l2.getMethod());
			}

		} else {
			Comparable l1 = (Comparable) o1;
			Comparable l2 = (Comparable) o2;
			v = l1.compareTo(l2);
		}

		return asc ? v : -v;
	}
}
