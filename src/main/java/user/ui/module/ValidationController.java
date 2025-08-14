package user.ui.module;

import java.util.List;

import org.irri.iric.ds.chado.dao.UserDAO;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;

import user.ui.module.util.constants.SessionConstants;

public class ValidationController extends SelectorComposer<Component> {

	private UserDAO u_serv;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		String token = Executions.getCurrent().getParameter("token");

		if (!token.isEmpty()) {

			u_serv = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");

			List<User> lst_sers = u_serv.getByValidationToken(token);

			if (lst_sers.size() > 0) {
				User user = lst_sers.get(0);
				user.setValidated(true);
				
				u_serv.save(user);
				
				Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
						"Email Validation Successful. You may now login and use SNPseek");
			}else {
				Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
						"Token Not found..");
			}

		

		}

		// Forward to the new page

		Executions.sendRedirect("index.zul");

	}

}
