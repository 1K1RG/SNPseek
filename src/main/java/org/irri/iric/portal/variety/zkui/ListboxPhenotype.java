package org.irri.iric.portal.variety.zkui;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.irri.iric.ds.chado.domain.CvTermUniqueValues;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.variety.VarietyFacade;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

@Component("ListboxPhenotype")
@Scope("session")
public class ListboxPhenotype extends Listbox {

	public Listbox listboxPhenValue;
	Listbox listboxComparator;
	VarietyFacade variety;
	private Integer phenotype_type;
	// private String dataset;
	private Set dataset;

	public ListboxPhenotype(Listbox values, VarietyFacade varietyfacade, Listbox comparator, Set set) {
		super();
		
		listboxPhenValue = values;
		variety = varietyfacade;
		listboxComparator = comparator;
		this.dataset = set;

	}

	public void onSelect() {

		setPhenotypeConstraint();
	}

	void setPhenotypeConstraint() {

		List listValues = new java.util.ArrayList();

		AppContext.debug("phenotype selected:" + getSelectedItem().getLabel());

		if (!getSelectedItem().getLabel().trim().isEmpty()) {
			// Iterator<CvTermUniqueValues> itValues = variety.getPhenotypeUniqueValues(
			// getSelectedItem().getLabel() ).iterator();
			Object retobj[] = variety.getPhenotypeUniqueValues(getSelectedItem().getLabel(), dataset);
			phenotype_type = (Integer) retobj[1];
			Iterator<CvTermUniqueValues> itValues = ((Set) retobj[0]).iterator(); // variety.getPhenotypeUniqueValues(
																					// getSelectedItem().getLabel()
																					// ).iterator();
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

	public int getPhenotype_type() {
		return phenotype_type;
	}

	public void setPhenotype_type(Integer phenotype_type) {
		this.phenotype_type = phenotype_type;
	}

}
