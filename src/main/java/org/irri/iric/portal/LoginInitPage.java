package org.irri.iric.portal;

import java.util.List;
import java.util.Map;

import org.irri.iric.ds.chado.dao.UserDAO;
import org.irri.iric.ds.chado.domain.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;

import user.ui.module.util.constants.SessionConstants;

public class LoginInitPage implements Initiator, InitiatorExt {

	
	private User user;

	/*
	 * Invoked while ZK parsing a zul
	 */
	@Override
	public void doInit(Page page, Map<String, Object> arg) throws Exception {

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user != null) {
			Executions.sendRedirect("index.zul");
		}
		
		

	}

	@Override
	public void doAfterCompose(Page arg0, Component[] arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doCatch(Throwable arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doFinally() throws Exception {
		// TODO Auto-generated method stub

	}
}