package org.irri.iric.portal;

import org.irri.iric.ds.chado.domain.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;

import user.ui.module.util.constants.SessionConstants;

public class BannerController extends SelectorComposer<Component> {

	@Wire
	private Menu menuLogout;
	
	@Wire
	private Menuitem mi_login;
	

//	@Wire
//	private Menuitem menuitem_order;

	private Session sess;
	
	private User user;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user != null) {
			menuLogout.setLabel(user.getUsername());
			menuLogout.setVisible(true);
			mi_login.setVisible(false);

		} else {
			menuLogout.setVisible(false);
			mi_login.setVisible(true);
		}
		

	}

//	@Listen("onClick =#menuitem_user")
//	public void onbutton_addUser() {
//
//		sess.removeAttribute(SessionConstants.CONTENT_MANAGER);
//		sess.removeAttribute(SessionConstants.USER_CREDENTIAL);
//
//		Executions.sendRedirect("index.zul");
//
//	}

}
