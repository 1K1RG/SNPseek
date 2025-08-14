package org.irri.iric.portal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.irri.iric.ds.chado.domain.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

import user.ui.module.util.constants.ContentConstants;
import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;

public class SidebarController extends SelectorComposer<Component> {

	@Wire
	private Div div_download;

	@Wire
	private Div div_myList;

	@Wire
	private Div div_gwas;

	private Session sess;
	private User user;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		Properties prop = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		if (prop == null) {
			prop = new Properties();
			InputStream contentManager = AppContext.class
					.getResourceAsStream("/ACCESS_" + UserConstants.ANONYMOUS + ".properties");
			try {

				prop.load(contentManager);

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		div_download.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_DOWNLOAD).toString()));
		div_gwas.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_GWAS).toString()));
		div_myList.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_MYLIST).toString()));

	}

	@Listen("onClick =#menuitem_user")
	public void onbutton_addUser() {

		sess.removeAttribute(SessionConstants.CONTENT_MANAGER);
		sess.removeAttribute(SessionConstants.USER_CREDENTIAL);

		Executions.sendRedirect("index.zul");

	}

}
