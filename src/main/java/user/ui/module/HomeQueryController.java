package user.ui.module;

import java.util.Properties;

import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.zkoss.chart.Position;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import user.ui.module.util.constants.SessionConstants;

/**
 * A demo of Big listbox to handle 1 trillion data.
 * 
 * @author jumperchen
 */
public class HomeQueryController extends SelectorComposer<Window> {

	private User user;
	private Properties contentProp;

	@Wire
	private Label dsNumber;

	/**
	 * Initializes Controller to Genotype Module (GenotypeContent.zul)
	 * 
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);
		contentProp = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		dsNumber.setValue("1");
		
		Notification.show("Announcement", "info", comp, "overlap_after", 3000);
		

		if (user != null) {
			if (user.getEmail().contains(AppContext.EMAIL_IRRI))
				dsNumber.setValue("14");
		}

	}

}