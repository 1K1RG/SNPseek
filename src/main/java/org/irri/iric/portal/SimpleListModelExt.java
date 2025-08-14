package org.irri.iric.portal;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;


/**
 * 
 * @author javiut 
 * http://forum.zkoss.org/users/63973/javiut/
 * 
 *
 */
public class SimpleListModelExt extends SimpleListModel {

	public SimpleListModelExt(List data) {
		super(data);
	}

	@Override
	public ListModel getSubModel(Object value, int nRows) {
		final String idx = value == null ? "" : objectToString(value);
		if (nRows < 0)
			nRows = 10;
		final LinkedList data = new LinkedList();
		for (int i = 0; i < getSize(); i++) {
			if (idx.equals("") || entryMatchesText(getElementAt(i).toString(), idx)) {
				data.add(getElementAt(i));
				if (--nRows <= 0)
					break; // done
			}
		}
		return new SimpleListModelExt(data);
	}

	public boolean entryMatchesText(String entry, String text) {
		return entry.toLowerCase().startsWith(text.toLowerCase());
	}

	protected String objectToString(Object value) {
		return value != null ? value.toString() : "";
	}

}
