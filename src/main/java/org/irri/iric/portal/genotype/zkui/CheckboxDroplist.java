package org.irri.iric.portal.genotype.zkui;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class CheckboxDroplist extends GenericForwardComposer {

	private Component comp;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.comp = comp;

	}

	public Listbox getListbox() {
		return (Listbox) comp.getFellow("list");
	}

	public void onSelect$list(Event e) throws InterruptedException {
		Listbox listbox = (Listbox) comp.getFellow("checkboxdroplistGenotyperun");

		String str = "";

		for (Listitem li : listbox.getItems()) {
			if (!li.isSelected()) {
				continue;
			}
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += li.getLabel();
		}
		Bandbox bandbox = (Bandbox) comp.getFellow("bandboxVarietyset");
		bandbox.setValue(str);
	}
}
