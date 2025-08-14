package org.irri.iric.portal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.irri.iric.ds.chado.dao.UserDAO;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.ds.chado.domain.model.UserSubscription;
import org.irri.iric.portal.config.KeysPropertyConfig;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.PublicClientApplication;

import user.ui.module.util.PasswordUtils;
import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;

public class LoginController extends SelectorComposer<Window> {

	@Wire
	private Button btn_login;

	@Wire
	private Div loginfo;

	@Wire
	private Div logform;

	@Wire
	private Label lbl_id;

	@Wire
	private Textbox txtbox_username;

	@Wire
	private Textbox txtbox_password;

	@Wire
	private Label lbl_email;

	@Wire
	private Label lbl_firstname;

	@Wire
	private Label lbl_lastname;

	@Wire
	private Label lbl_name;

	@Wire
	private Label dsNumber;

	@Wire
	private Image image_Id;

	private User user;

	private UserDAO user_service;

	private Session sess;

	private KeysPropertyConfig keyProp;

	private static final String CLIENT_ID = "8f69a01b-9888-4dab-a974-0806b5d9c90e"; // Your Azure App Registration's
																					// Client ID
	private static final String TENANT_ID = "6afa0e00-fa14-40b7-8a2e-22a7f8c357d5"; // Your Azure AD Tenant ID
	private static final String REDIRECT_URI = "https://localhost:44368/"; // Redirect URI
	private static final String AUTHORITY = "https://login.microsoftonline.com/" + TENANT_ID;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		sess = Sessions.getCurrent();

		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		user_service = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");

		keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");

		System.out.println("KEY >>>" + keyProp.getKey());
	}

	@Listen("onClick=#btn_mlogin")
	public void microsoftLogin() {
		try {
			// Build the PublicClientApplication
			PublicClientApplication publicClientApplication = PublicClientApplication.builder(CLIENT_ID)
					.authority(AUTHORITY).build();

			// Define the required scope(s)
			Set<String> scopes = Collections.singleton("User.Read"); // Using Set instead of List

			// Acquire the token interactively (browser-based login)
			InteractiveRequestParameters requestParameters = InteractiveRequestParameters.builder(new URI(REDIRECT_URI))
					.scopes(scopes) // Using Set of scopes correctly
					.build();

			IAuthenticationResult result = publicClientApplication.acquireToken(requestParameters).join(); // This is a
																											// blocking
																											// call for
																											// simplicity

			// Successfully authenticated, retrieve the access token
			String accessToken = result.accessToken();
			System.out.println("Access token: " + accessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Listen("onClick=#btn_login")
	public void queryVariants() {

		String password = "";

		String dbPass = "";

		List<org.irri.iric.ds.chado.domain.model.User> lst_users = user_service
				.getByUserName(txtbox_username.getValue());

		if (lst_users.size() == 1) {

			user = lst_users.get(0);

			String subscription = "/ACCESS_" + UserConstants.ANONYMOUS + ".properties";

			if (user.getValidated()) {

				try {
					dbPass = PasswordUtils.decrypt(user.getPasswordHash(), keyProp.getKey());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				AppContext.debug(dbPass + "+" + txtbox_password.getValue());
				if (!dbPass.equals(txtbox_password.getValue())) {
					Notification.show("Username password does not match");
					return;
				}

				for (UserSubscription userSub : user.getUserSubscriptions()) {
					if (userSub.getIsActive())
						subscription = "/ACCESS_" + userSub.getSubscription().getShortname() + ".properties";
				}

				Properties contentProp = new Properties();
				InputStream contentManager = AppContext.class.getResourceAsStream(subscription);
				try {

					contentProp.load(contentManager);

				} catch (IOException e) {

					e.printStackTrace();
				}

				// AppContext.setUserContent();

				sess.setAttribute(SessionConstants.USER_CREDENTIAL, user);
				sess.setAttribute(SessionConstants.CONTENT_MANAGER, contentProp);

				Executions.sendRedirect("index.zul");
			} else {
				Notification.show("Go to your registered email address and click the validation link. ", true);
			}

		} else {
			Notification.show("The email address is not registered. Please click \"Sign Up\" to create an account.",
					true);
		}

	}

}
