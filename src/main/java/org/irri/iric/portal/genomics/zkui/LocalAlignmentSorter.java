package org.irri.iric.portal.genomics.zkui;

import java.io.Serializable;
import java.util.Comparator;

import org.irri.iric.ds.chado.domain.LocalAlignment;

public class LocalAlignmentSorter implements Comparator , Serializable {

	private boolean asc = false;
	private int index = 0;
	private int type = 0;
	private int v;

	public LocalAlignmentSorter(boolean asc, int index, int type) {
		super();
		this.asc = asc;
		this.index = index;
		this.type = type;
	}

	@Override
	public int compare(Object o1, Object o2) {
		

		LocalAlignment l1 = (LocalAlignment) o1;
		LocalAlignment l2 = (LocalAlignment) o2;

		Object temp1 = null;
		Object temp2 = null;

		/*
		 * <column label="QUERY" width="150px" sort="auto" /> <column label="Q START"
		 * width="100px" sort="auto" /> <column label="Q END" width="100px" sort="auto"
		 * /> <column label="SUBJECT" width="180px" sort="auto" /> <column
		 * label="S START" width="100px" sort="auto" /> <column label="S END"
		 * width="100px" sort="auto" /> <column label="S STRAND" width="100px"
		 * sort="auto" /> <column label="MATCHES" width="150px" sort="auto" /> <column
		 * label="E VALUE" width="100px" sort="auto" />
		 */
		switch (index) {
		case 0:
			temp1 = l1.getQacc();
			temp2 = l2.getQacc();
			break;
		case 1:
			temp1 = l1.getQstart();
			temp2 = l2.getQstart();
			break;
		case 2:
			temp1 = l1.getQend();
			temp2 = l2.getQend();
			break;
		case 3:
			temp1 = l1.getSacc();
			temp2 = l2.getSacc();
			break;
		case 4:
			temp1 = l1.getSstart();
			temp2 = l2.getSstart();
			break;
		case 5:
			temp1 = l1.getSend();
			temp2 = l2.getSend();
			break;
		case 6:
			temp1 = l1.getSstrand();
			temp2 = l2.getSstrand();
			break;
		case 7:
			temp1 = l1.getMatches();
			temp2 = l2.getMatches();
			break;
		case 8:
			temp1 = l1.getEvalue();
			temp2 = l2.getEvalue();
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
		} else if (type == 2) {
			Long s1 = (Long) temp1;
			Long s2 = (Long) temp2;
			v = s1.compareTo(s2);
		} else if (type == 3) {
			Double s1 = (Double) temp1;
			Double s2 = (Double) temp2;
			v = s1.compareTo(s2);
		}

		return asc ? v : -v;
	}

}
