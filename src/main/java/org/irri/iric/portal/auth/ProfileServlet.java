package org.irri.iric.portal.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irri.iric.ds.chado.dao.SubscriptionDAO;
import org.irri.iric.ds.chado.dao.UserDAO;
import org.irri.iric.ds.chado.dao.UserSubscriptionDAO;
import org.irri.iric.ds.chado.domain.model.Subscription;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.ds.chado.domain.model.UserSubscription;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.config.KeysPropertyConfig;

import com.google.api.services.oauth2.model.Userinfo;

import user.ui.module.util.PasswordUtils;
import user.ui.module.util.constants.SessionConstants;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

	private KeysPropertyConfig keyProp;

	private UserDAO u_serv;

	private SubscriptionDAO subs_serv;

	private UserSubscriptionDAO usubs_serv;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");

		u_serv = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");

		subs_serv = (SubscriptionDAO) AppContext.getApplicationContext().getBean("SubscriptionDAO");

		usubs_serv = (UserSubscriptionDAO) AppContext.getApplicationContext().getBean("UserSubscriptionDAO");

		response.setContentType("text/html;");
		response.getWriter().println("<h1>Redireting...</h1>");

		String sessionId = request.getSession().getId();
		boolean isUserLoggedIn = OAuthUtils.isUserLoggedIn(sessionId);

		if (isUserLoggedIn) {
			Userinfo userInfo = OAuthUtils.getUserInfo(sessionId);

			String pass = "";
			try {
				System.out.println("key >>>>> "+ keyProp.getKey());
				pass = PasswordUtils.encrypt("mykeySnpseek", keyProp.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}

			HttpSession sess = request.getSession();

			User userCred;

			List<User> lst_User = u_serv.getByUserName(userInfo.getEmail());

			if (lst_User.size() == 0) {

				userCred = new User();
				userCred.setUsername(userInfo.getEmail());
				userCred.setEmail(userInfo.getEmail());
				userCred.setPasswordHash(pass);
				userCred.setValidated(true);
				userCred.setFirstname(Objects.isNull(userInfo.getGivenName()) ? "" : userInfo.getGivenName());
				userCred.setLastname(Objects.isNull(userInfo.getFamilyName()) ? "" : userInfo.getFamilyName());

				userCred.setCreatedAt(AppContext.getCurrentTime());

				userCred = u_serv.save(userCred);

				List<Subscription> lst_subs = subs_serv.getByShortName("adm");

				UserSubscription usub = new UserSubscription();
				usub.setUser(userCred);
				usub.setSubscription(lst_subs.get(0));
				usub.setIsActive(true);
				usub.setStartDate(AppContext.getCurrentTime());

				usubs_serv.save(usub);

				lst_User = u_serv.getByUserName(userInfo.getEmail());

			}
			
			userCred = lst_User.get(0);

			if (!pass.equals(userCred.getPasswordHash())) {
				sess.setAttribute(SessionConstants.NOTIFICATION,
						"Use your account with a password instead of Google Authentication. Go to the login page and enter your password.");

			} else {

				String cm = "";
				cm = "/ACCESS_" + userCred.getUserSubscriptions().get(0).getSubscription().getShortname()
						+ ".properties";

				Properties contentProp = new Properties();
				InputStream contentManager = AppContext.class.getResourceAsStream(cm);
				try {

					contentProp.load(contentManager);

				} catch (IOException e) {

					e.printStackTrace();
				}

				sess.setAttribute(SessionConstants.USER_CREDENTIAL, userCred);
				sess.setAttribute(SessionConstants.CONTENT_MANAGER, contentProp);
				sess.setAttribute(SessionConstants.USER_NAME, userInfo.getEmail());

				AppContext.createUserDirectory(userInfo.getEmail());

			}

			response.sendRedirect("/index.zul");

		} else {
			response.getWriter().println("<a href=\"/login\">Login with Google</a>");
		}
	}
}