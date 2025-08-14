package org.irri.iric.portal.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.ServletException;
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
import org.irri.iric.portal.config.SNPseekEnv;
import org.json.JSONObject;
import org.zkoss.zk.ui.Sessions;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IConfidentialClientApplication;

import user.ui.module.util.PasswordUtils;
import user.ui.module.util.constants.SessionConstants;

@WebServlet("/callback")
public class OAuth2CallbackServlet extends HttpServlet {

	private static final String REDIRECT_URI = "https://snpseek.irri.org/callback"; // Make sure this matches what you set
	private static final String AUTHORITY = "https://login.microsoftonline.com/";

	private UserDAO u_serv;

	private KeysPropertyConfig keyProp;
	private SubscriptionDAO subs_serv;
	private UserSubscriptionDAO usubs_serv;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String code = request.getParameter("code");
		String state = request.getParameter("session_state");

		if (code == null || state == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid callback parameters");
			return;
		}

		AppContext.debug("Getting User Token");
		String token = exchangeCodeForToken2(code);

		AppContext.debug("Getting User Info");

		JSONObject userInfo = getUserInfo(token);

		if (userInfo != null) {

			u_serv = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");

			keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");

			subs_serv = (SubscriptionDAO) AppContext.getApplicationContext().getBean("SubscriptionDAO");

			usubs_serv = (UserSubscriptionDAO) AppContext.getApplicationContext().getBean("UserSubscriptionDAO");

			String pass = "";
			try {
				System.out.println("key >>>>> "+ keyProp.getKey());
				pass = PasswordUtils.encrypt("mykeySnpseek", keyProp.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}

			HttpSession sess = request.getSession();

			User userCred = null;

			UserSubscription usub = null;

			List<User> lst_User = u_serv.getByUserName(userInfo.optString("userPrincipalName"));

			if (lst_User.size() == 0) {

				userCred = new User();
				userCred.setUsername(userInfo.optString("userPrincipalName"));
				userCred.setEmail(userInfo.optString("userPrincipalName"));
				userCred.setPasswordHash(pass);
				userCred.setValidated(true);
				userCred.setFirstname(
						Objects.isNull(userInfo.optString("givenName")) ? "" : userInfo.optString("givenName"));
				userCred.setLastname(
						Objects.isNull(userInfo.optString("surname")) ? "" : userInfo.optString("surname"));

				userCred.setCreatedAt(AppContext.getCurrentTime());

				userCred = u_serv.save(userCred);

				List<Subscription> lst_subs = subs_serv.getByShortName("adm");

				usub = new UserSubscription();
				usub.setUser(userCred);
				usub.setSubscription(lst_subs.get(0));
				usub.setIsActive(true);
				usub.setStartDate(AppContext.getCurrentTime());

				usubs_serv.save(usub);

				lst_User = u_serv.getByUserName(userInfo.optString("userPrincipalName"));

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
				sess.setAttribute(SessionConstants.USER_NAME, userCred.getEmail());

				AppContext.createUserDirectory(userCred.getEmail());

			}

		} else {
			Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
					"An error occurred while attempting to log in to your CGIAR account.");
		}

		response.sendRedirect("/index.zul");

	}

	private String exchangeCodeForToken2(String code) {

		String[] SCOPES = new String[] { "User.Read" };
		
		String CLIENT_ID = System.getenv(SNPseekEnv.MICROSOFT_CLIENT_ID);
		String SECRET = System.getenv(SNPseekEnv.MICROSOFT_SECRET);
		String TENANT_ID = System.getenv(SNPseekEnv.MICROSOFT_TENANT_ID);

		IConfidentialClientApplication app;
		try {
			app = ConfidentialClientApplication.builder(CLIENT_ID, ClientCredentialFactory.createFromSecret(SECRET))
					.authority(AUTHORITY + "" + TENANT_ID).build();
			// Acquire token by providing the authorization code
			AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(code, new URI(REDIRECT_URI))
					.scopes((new HashSet<>(Arrays.asList(SCOPES)))).build();

			IAuthenticationResult result = app.acquireToken(parameters).join();

			return result.accessToken();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private JSONObject getUserInfo(String accessToken) throws IOException {
		String graphApiUrl = "https://graph.microsoft.com/v1.0/me";
		URL url = new URL(graphApiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization", "Bearer " + accessToken.trim());
		connection.setRequestProperty("Accept", "application/json");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuilder responseString = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			responseString.append(inputLine);
		}
		in.close();

		return (new JSONObject(responseString.toString()));

//        String displayName = userInfo.optString("displayName");
//        String email = userInfo.optString("userPrincipalName");
//        String id = userInfo.optString("id");

	}
}
