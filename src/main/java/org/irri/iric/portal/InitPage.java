package org.irri.iric.portal;

import java.util.List;
import java.util.Map;

import org.irri.iric.ds.chado.dao.UserDAO;
import org.irri.iric.ds.chado.domain.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;

import user.ui.module.util.constants.SessionConstants;

public class InitPage implements Initiator, InitiatorExt {

	private UserDAO u_serv;

	/*
	 * Invoked while ZK parsing a zul
	 */
	@Override
	public void doInit(Page page, Map<String, Object> arg) throws Exception {

		u_serv = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");

		String reset = Executions.getCurrent().getParameter("reset");

		if (reset != null) {
			List<User> lst_user = u_serv.getByValidationToken(reset);

			if (lst_user.size() > 0) {
				org.irri.iric.ds.chado.domain.model.User user = lst_user.get(0);

				if (AppContext.checkIfExpired(user.getTokenvalidity(), 30)) {
						
						String message = "The password reset link you requested may have expired or already been used. \n Please request a new link.";
						
						user.setTokenvalidity(null);
						u_serv.save(user);
						
						Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION, message);

						Executions.sendRedirect("index.zul");
					}
				
			}
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