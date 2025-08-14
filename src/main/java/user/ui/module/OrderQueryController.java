package user.ui.module;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.VarietyPlus;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.variety.zkui.VarietyListItemRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Window;

import user.ui.module.util.constants.SessionConstants;

public class OrderQueryController extends SelectorComposer<Window> {

	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;

	@Wire
	private Listbox listboxVarieties;

	@Wire
	private Button button_Copy;

	@Wire
	private Hlayout hlayout_message;

	@Wire
	Combobox combobox_userList;

	private Session sess;

	private User user;

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user == null) {
//			Execution exec = Executions.getCurrent();
//		    HttpServletResponse response = (HttpServletResponse)exec.getNativeResponse();
//		    response.sendRedirect(response.encodeRedirectURL("/index.zul")); //assume there is /login
//		    exec.setVoided(true);
			Executions.sendRedirect("index.zul");
		}

		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		combobox_userList.setModel(new ListModelList<>(workspace.getVarietylistNames()));

	}

	@Listen("onChange = #combobox_userList")
	public void onChange$userList() {

		List listTmp = new ArrayList();

		listTmp.addAll(workspace.getVarieties(combobox_userList.getValue()));

		boolean advancedcols = listTmp.iterator().next() instanceof VarietyPlus;
		listboxVarieties.setItemRenderer(new VarietyListItemRenderer(!advancedcols));

		SimpleListModel model = new SimpleListModel(listTmp);
		model.setMultiple(true);
		listboxVarieties.setModel(model);
		listboxVarieties.setVisible(true);

	}

	@Listen("onClick = #button_Copy")
	public void onCopy$accessionList() {

		SimpleListModel model = (SimpleListModel) listboxVarieties.getModel();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < model.getSize(); i++) {
			Variety v = (Variety) model.getElementAt(i);
			if (sb.length() != 0)
				sb.append(",");
			sb.append(v.getAccession());
		}

		StringSelection sl = new StringSelection(sb.toString());

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(sl, null);

		hlayout_message.setVisible(true);

		Notification.show("Accessions copied to Clipboard.");

	}

}