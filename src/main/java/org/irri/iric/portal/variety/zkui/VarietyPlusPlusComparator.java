package org.irri.iric.portal.variety.zkui;

import java.util.Comparator;
import java.util.Map;

import org.irri.iric.ds.chado.domain.VarietyPlusPlus;

public class VarietyPlusPlusComparator implements Comparator {

	private String field;
	private boolean ascending = true;

	public VarietyPlusPlusComparator(boolean ascending, String field) {
		super();

		this.ascending = ascending;
		this.field = field;
	}

	@Override
	public int compare(Object o1, Object o2) {
		

		Map<String, Comparable> mapValue1 = ((VarietyPlusPlus) o1).getValueMap();
		Map<String, Comparable> mapValue2 = ((VarietyPlusPlus) o2).getValueMap();

		int v = mapValue1.get(field).compareTo(mapValue2.get(field));

		return ascending ? v : -v;
	}

}
