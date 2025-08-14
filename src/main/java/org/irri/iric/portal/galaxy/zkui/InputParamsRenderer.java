package org.irri.iric.portal.galaxy.zkui;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

public class InputParamsRenderer implements ListitemRenderer{

	
	
	public InputParamsRenderer() {
		super();
	
	}

	@Override
	public void render(Listitem listitem, Object arg1, int arg2) throws Exception {
		String[] ar=(String[])arg1;		
		addListcell(listitem,ar[0]);
		Textbox tb=addTextcell(listitem,"");
		addListcell(listitem,ar[1]);
		listitem.setValue( new Object[] {ar[0], tb} );
	}
	
	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";

	private void addListcell(Listitem listitem, String value) {
		addListcell(listitem, value, STYLE_BORING);
	}

	private Textbox addTextcell(Listitem listitem, String value) {
		Listcell lc = new Listcell();
		Combobox lb = new Combobox(value);
		lb.setReadonly(false);
		lb.setInplace(true);
		lb.setParent(lc);
		lc.setParent(listitem);
		return lb;
	}

	private void addListcell(Listitem listitem, String value, String style) {
		Listcell lc = new Listcell();
		Label lb = new Label(value);
		if (!style.isEmpty())
			lb.setStyle(style);
		lb.setParent(lc);
		lc.setParent(listitem);
	}

}
