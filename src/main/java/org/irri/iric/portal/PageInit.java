package org.irri.iric.portal;

import java.util.Map;

import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.admin.WorkspaceLoadLocal;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;

import user.ui.module.util.constants.SessionConstants;




public class PageInit implements Initiator, InitiatorExt {

	/*
	 * Invoked while ZK parsing a zul
	 */
	@Override
	public void doInit(Page page, Map<String, Object> arg) throws Exception {

		Session sess = Sessions.getCurrent();
		
		if (sess.getAttribute(SessionConstants.USER_CREDENTIAL) == null) {
			Executions.sendRedirect("login.zul");
		}
		
		
		
	}

	@Override
	public void doAfterCompose(Page arg0, Component[] arg1) throws Exception {
		

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