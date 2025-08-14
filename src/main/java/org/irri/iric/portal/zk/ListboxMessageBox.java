package org.irri.iric.portal.zk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irri.iric.portal.AppContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.impl.MessageboxDlg;

/**
 * Extended messagebox that can show multilined messages. <br>
 * Lines can be breaked with the \n . <br>
 * <br>
 * 
 * @author Stephan Gerth / Forsthaus Datentechnik
 * @email sge(at)forsthaus(dot)de
 * @changes 04.07.2009/sge extended for showing the icons <br>
 *          05.07.2009/sge added an empty line before and after the message.
 *          <br>
 *          08.07.2009/sge added for the EventListener
 * 
 */
public class ListboxMessageBox extends Messagebox implements Composer {

	private static final long serialVersionUID = 1L;
	// path of the messagebox zul-template
	// private static String _templ = "/WEB-INF/pages/util/multiLineMessageBox.zul";
	private static String _templ = "/listbox_msgbox.zul";
	private static String oldTemplate = Messagebox.getTemplate();

	@Wire
	public static Listbox listboxOptions;

	public ListboxMessageBox() {

		super();

	}

	public static void doSetTemplate() {
		setTemplate(_templ);

	}

	public static Object objSel = null;

	public static Object getObjSel() {
		return objSel;
	}

	/*
	 * @Listen("onSelect =#listboxOptions") public void onselectoptions() {
	 * objSel=listboxOptions.getSelectedItem().getValue();
	 * AppContext.debug("onSelect objSel=" + objSel);
	 * Sessions.getCurrent().setAttribute("comboboxvar",objSel); }
	 */

	public static final Object show(String message, String title, int buttons, String icon, List options,
			EventListener listener) throws InterruptedException {
		setTemplate(_templ);
		showInput(message, title, buttons, icon, true, options, listener);
		setTemplate(oldTemplate);
		return objSel;
	}

	public static final Object show(String message, String title, List options, EventListener listener)
			throws InterruptedException {
		setTemplate(_templ);

		showInput(message, title, ListboxMessageBox.CANCEL | ListboxMessageBox.OK, ListboxMessageBox.INFORMATION, true,
				options, listener);

		/*
		 * new org.zkoss.zk.ui.event.EventListener() {
		 * 
		 * @Override public void onEvent(Event e) throws Exception { // TODO
		 * Auto-generated method stub if(e.getName().equals(ListboxMessageBox.ON_OK)) {
		 * //objSel= getObjSel(); //listboxOptions.getSelectedItem().getValue();
		 * //AppContext.debug("objSel=" + objSel); } else objSel=null; }});
		 */
		setTemplate(oldTemplate);
		return objSel;
	}

	public static final int show1(String message, String title, List options) throws InterruptedException {
		setTemplate(_templ);
		objSel = null;
		// listboxOptions.setModel(new SimpleListModel(options));
		// listboxOptions.setSelectedIndex(0);
		return show(message, title, ListboxMessageBox.CANCEL | ListboxMessageBox.OK, INFORMATION, false,
				new org.zkoss.zk.ui.event.EventListener() {
					@Override
					public void onEvent(Event e) throws Exception {
						
						if (e.getName().equals(ListboxMessageBox.ON_OK)) {
							objSel = listboxOptions.getSelectedItem().getValue();
						}

					}
				});
	}

	public static final int showInput(String message, String title, int buttons, String icon, boolean focus,
			List options, EventListener listener) throws InterruptedException {
		final Map params = new HashMap();
		params.put("message", message);
		params.put("title", title != null ? title : Executions.getCurrent().getDesktop().getWebApp().getAppName());
		params.put("icon", icon);
		params.put("buttons",
				new Integer((buttons & (OK | CANCEL | YES | NO | ABORT | RETRY | IGNORE)) != 0 ? buttons : OK));
		if ((buttons & OK) != 0)
			params.put("OK", new Integer(OK));
		if ((buttons & CANCEL) != 0)
			params.put("CANCEL", new Integer(CANCEL));
		if ((buttons & YES) != 0)
			params.put("YES", new Integer(YES));
		if ((buttons & NO) != 0)
			params.put("NO", new Integer(NO));
		if ((buttons & RETRY) != 0)
			params.put("RETRY", new Integer(RETRY));
		if ((buttons & ABORT) != 0)
			params.put("ABORT", new Integer(ABORT));
		if ((buttons & IGNORE) != 0)
			params.put("IGNORE", new Integer(IGNORE));

		final MessageboxDlg dlg = (MessageboxDlg) Executions.createComponents(_templ, null, params);
		listboxOptions = (Listbox) dlg.getFellowIfAny("listboxOptions");
		if (options.size() > 20)
			listboxOptions.setHeight("200px");
		listboxOptions.setModel(new SimpleListModel(options));
		listboxOptions.addEventListener("onSelect", new org.zkoss.zk.ui.event.EventListener() {
			@Override
			public void onEvent(Event ev) throws InterruptedException {
				SelectEvent evt = (SelectEvent) ev;
				if (evt.getName().equals("onSelect")) {
					objSel = ((Listitem) evt.getSelectedItems().iterator().next()).getValue();
					AppContext.debug("onSelectEvent objSel=" + objSel);
				}

			}
		});
		// dlg.set .setButtons(buttons);
		if (listener != null)
			dlg.setEventListener(listener);
		// if (focus > 0) dlg.setFocus(focus);
		dlg.setFocus(focus);

		if (dlg.getDesktop().getWebApp().getConfiguration().isEventThreadEnabled()) {
			try {
				dlg.doModal();
			} catch (Throwable ex) {
				if (ex instanceof InterruptedException)
					throw (InterruptedException) ex;
				try {
					dlg.detach();
				} catch (Throwable ex2) {
					System.err.println("Failed to detach when recovering from an error " + ex2.toString());
				}
				throw UiException.Aide.wrap(ex);
			}
			return dlg.getResult().id; // . .label; //ListboxMessageBox. .id; // .getr .getResult();
		} else {
			dlg.doHighlighted();
			return OK;
		}
	}

	/**
	 * Shows a message box and returns what button is pressed. A shortcut to
	 * show(message, null, OK, INFORMATION). <br>
	 * <br>
	 * Simple MessageBox with customizable message and title. <br>
	 * 
	 * @param message
	 *            The message to display.
	 * @param title
	 *            The title to display.
	 * @param icon
	 *            The icon to display. <br>
	 *            QUESTION = "z-msgbox z-msgbox-question"; <br>
	 *            EXCLAMATION = "z-msgbox z-msgbox-exclamation"; <br>
	 *            INFORMATION = "z-msgbox z-msgbox-imformation"; <br>
	 *            ERROR = "z-msgbox z-msgbox-error"; <br>
	 * @param buttons
	 *            MultiLineMessageBox.CANCEL<br>
	 *            MultiLineMessageBox.YES<br>
	 *            MultiLineMessageBox.NO<br>
	 *            MultiLineMessageBox.ABORT<br>
	 *            MultiLineMessageBox.RETRY<br>
	 *            MultiLineMessageBox.IGNORE<br>
	 * @param padding
	 *            true = Added an empty line before and after the message.<br>
	 * 
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static final int show(String message, String title, int buttons, String icon, boolean padding)
			throws InterruptedException {

		String msg = message;

		if (padding == true) {
			msg = "\n" + message + "\n\n";
		}

		if (icon.equals("QUESTION")) {
			icon = "z-msgbox z-msgbox-question";
		} else if (icon.equals("EXCLAMATION")) {
			icon = "z-msgbox z-msgbox-exclamation";
		} else if (icon.equals("INFORMATION")) {
			icon = "z-msgbox z-msgbox-imformation";
		} else if (icon.equals("ERROR")) {
			icon = "z-msgbox z-msgbox-error";
		}

		return show(msg, title, buttons, icon, 0, null);
	}

	/**
	 * Shows a message box and returns what button is pressed. A shortcut to
	 * show(message, null, OK, INFORMATION). <br>
	 * <br>
	 * Simple MessageBox with customizable message and title. <br>
	 * 
	 * @param message
	 *            The message to display.
	 * @param title
	 *            The title to display.
	 * @param icon
	 *            The icon to display. <br>
	 *            QUESTION = "z-msgbox z-msgbox-question"; <br>
	 *            EXCLAMATION = "z-msgbox z-msgbox-exclamation"; <br>
	 *            INFORMATION = "z-msgbox z-msgbox-imformation"; <br>
	 *            ERROR = "z-msgbox z-msgbox-error"; <br>
	 * @param buttons
	 *            MultiLineMessageBox.CANCEL<br>
	 *            MultiLineMessageBox.YES<br>
	 *            MultiLineMessageBox.NO<br>
	 *            MultiLineMessageBox.ABORT<br>
	 *            MultiLineMessageBox.RETRY<br>
	 *            MultiLineMessageBox.IGNORE<br>
	 * @param padding
	 *            true = Added an empty line before and after the message.<br>
	 * 
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static final int show(String message, String title, int buttons, String icon, boolean padding,
			EventListener listener) throws InterruptedException {

		String msg = message;

		if (padding == true) {
			msg = "\n" + message + "\n\n";
		}

		if (icon.equals("QUESTION")) {
			icon = "z-msgbox z-msgbox-question";
		} else if (icon.equals("EXCLAMATION")) {
			icon = "z-msgbox z-msgbox-exclamation";
		} else if (icon.equals("INFORMATION")) {
			icon = "z-msgbox z-msgbox-imformation";
		} else if (icon.equals("ERROR")) {
			icon = "z-msgbox z-msgbox-error";
		}

		return show(msg, title, buttons, icon, 0, listener);
	}

	@Override
	public void doAfterCompose(Component arg0) throws Exception {
		

		if (listboxOptions == null)
			listboxOptions = (Listbox) arg0.getFellowIfAny("listboxOptions");
	}

}