package org.irri.iric.portal.variety.zkui;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.irri.iric.ds.chado.domain.CvTermUniqueValues;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.variety.VarietyFacade;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

public class ListboxPassport extends Listbox {

	Listbox listboxPhenValue;
	VarietyFacade variety;
	private Set dataset;

	public ListboxPassport(Listbox values, VarietyFacade varietyfacade, Set set) {
		super();
		
		listboxPhenValue = values;
		variety = varietyfacade;
		this.dataset = set;
	}

	public void onSelect() {

		setPhenotypeConstraint();
	}

	private void setPhenotypeConstraint() {

		List listValues = new java.util.ArrayList();

		// AppContext.debug("phenotype selected:" + getSelectedItem().getLabel());

		if (!getSelectedItem().getLabel().trim().isEmpty()) {
			Iterator<CvTermUniqueValues> itValues = variety
					.getPassportUniqueValues(getSelectedItem().getLabel(), dataset).iterator();
			while (itValues.hasNext()) {
				CvTermUniqueValues value = itValues.next();

				if (value == null) {
					AppContext.debug("null value");
					continue;
				}

				// AppContext.debug(value.toString());

				listValues.add(value.getValue());

				// AppContext.debug(value.getValue());
			}
		}

		listboxPhenValue.setModel(new SimpleListModel(listValues));
		if (listValues.size() > 0)
			listboxPhenValue.setSelectedIndex(0);
	}

}
