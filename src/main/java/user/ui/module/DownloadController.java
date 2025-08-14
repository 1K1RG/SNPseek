package user.ui.module;

import javax.servlet.http.HttpSession;

import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;

import user.ui.module.util.constants.SessionConstants;

@Controller
@Scope("session")
public class DownloadController extends SelectorComposer<Component> {

	private User user;

	public DownloadController() {
		super();
		HttpSession session = (HttpSession) Sessions.getCurrent().getNativeSession();
		user = (User) session.getAttribute(SessionConstants.USER_CREDENTIAL);
		session.setAttribute("username", user.getUsername());
		
		AppContext.debug("created WorkspaceController:" + this);
	}

	private HttpSession getHttpSession() {
		return (HttpSession) Sessions.getCurrent().getNativeSession();
	}

}
