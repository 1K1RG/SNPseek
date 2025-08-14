package org.irri.iric.portal.genotype.zkui;

import java.util.List;

import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.portal.AppContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Window;

//public class VarietySelectorDialog extends SelectorComposer<Component>, GenericForwardComposer {
public class VarietySelectorDialog extends GenericForwardComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Listbox listboxVariety;
	@Wire
	private Button buttonClose;
	@Wire
	private Window windowSelectVariety;

	private int selected = -1;

	/*
	 * public VarietySelectorDialog(List listVars) { super(); // TODO Auto-generated
	 * constructor stub listboxVariety.setModel(new SimpleListModel(listVars)); }
	 */

	@Listen("onClick = #buttonClose")
	public void showModal(Event e) {
		AppContext.debug("buttonClose");
		Combobox combovar = (Combobox) Sessions.getCurrent().getAttribute("comboboxvar");
		Sessions.getCurrent().removeAttribute("comboboxvar");
		Variety varsel = (Variety) listboxVariety.getSelectedItem().getValue();
		combovar.setValue(varsel.getName() + " [" + varsel.getAccession() + "]");
		windowSelectVariety.setVisible(false);
		windowSelectVariety.detach();
	}

	public int getSelected() {
		return selected;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);

		List listvars = (List) Sessions.getCurrent().getAttribute("varlist");
		listboxVariety.setModel(new SimpleListModel(listvars));
		Sessions.getCurrent().removeAttribute("varlist");
		/*
		 * if (arg.containsKey("varlist")) { listboxVariety.setModel(new
		 * SimpleListModel(listvars)); }
		 */
	}

}