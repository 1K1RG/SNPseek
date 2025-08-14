package user.ui.module;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.irri.iric.ds.chado.dao.SubscriptionDAO;
import org.irri.iric.ds.chado.dao.UserDAO;
import org.irri.iric.ds.chado.dao.UserSubscriptionDAO;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.MailUtils;
import org.irri.iric.portal.RecaptchaVerifier;
import org.irri.iric.portal.config.KeysPropertyConfig;
import org.irri.iric.portal.config.SNPseekEnv;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Button;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import user.ui.module.util.PasswordUtils;
import user.ui.module.util.constants.SessionConstants;

public class ResetPasswordController extends SelectorComposer<Component> {

	@Wire
	private Textbox txtbox_email;

	@Wire
	private Vlayout resetForm;

	@Wire
	private Vlayout resetToken;

	@Wire
	private Textbox txtbox_password;

	@Wire
	private Textbox txbox_confirmpassword;

	@Wire
	private Button submitBtn;

	private String urlMessage = "https://snpseek.irri.org/resetPassword.zul?reset=";

	private UserDAO u_serv;

	private KeysPropertyConfig keyProp;

	@Wire
	private Textbox recaptchaResponse;

	private User user;

	private String errorMsg;

	private boolean formIsValid;

//	private UserService u_serv;

//	private UserService u_serv;
//
//	private UserService userv;

	// The regular expressions for password strength
	private static final String STRONG_PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}";

	private static final String WEAK_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{6,}";

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		user = null;

		u_serv = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");

		keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");

		
		String reset = Executions.getCurrent().getParameter("reset");

		if (reset != null)
			if (!reset.isEmpty()) {

				List<User> lst_user = u_serv.getByValidationToken(reset);

				if (lst_user.size() > 0) {
					resetForm.setVisible(true);
					resetToken.setVisible(false);
					submitBtn.setDisabled(false);

					user = lst_user.get(0);

				} else {
					Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
							"AN ERROR OCCUR: SNPSEEK ERR 1. Contact Admin");

					Executions.sendRedirect("index.zul");

				}

			} else {
				Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION, "Token Not found..");

				Executions.sendRedirect("index.zul");
			}

	}

	@Listen("onUserRespond = #recaptcha")
	public void verify(Event event) throws Exception {
		
		String SECRET = System.getenv(SNPseekEnv.RECAPTCHA_RESET);
		
		JSONObject result = RecaptchaVerifier.verifyResponse(SECRET,
				((JSONObject) event.getData()).get("response").toString());

		Notification.show("recapcha");
		if (Boolean.parseBoolean(result.get("success").toString())) {
			submitBtn.setDisabled(false);
			submitBtn.invalidate();
		} else {
			String errorCode = result.get("error-codes").toString();
			// log or show error
		}

		System.out.println("recaptcha update BUtton Submit ");
	}

	@Listen("onClick =#submitBtn")
	public void onbutton_addUser() {

		if (resetForm.isVisible()) {

			errorMsg = "";
			
			if (validateResetForm()) {

				String pass = "";

				try {
					pass = PasswordUtils.encrypt(txtbox_password.getValue(), keyProp.getKey());
				} catch (Exception e) {
					e.printStackTrace();
				}

				user.setTokenvalidity(null);
				user.setPasswordHash(pass);

				u_serv.save(user);

				Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
						"User password successfully changed. ");

				Executions.sendRedirect("index.zul");
			}
		} else {
			List<User> lst_result = u_serv.getByUserEmail(txtbox_email.getValue());

			if (lst_result.size() > 0) {

				user = lst_result.get(0);

				String gpass = "";
				try {
					gpass = PasswordUtils.encrypt("mykeySnpseek", keyProp.getKey());
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Check if user uses SSO
				if (!gpass.equals(user.getPasswordHash())) {

					// if (AppContext.checkIfExpired(user.getTokenvalidity(), 60)) {
					if (user.getTokenvalidity() != null) {
						if (AppContext.checkIfExpired(user.getTokenvalidity(), 60)) {
							user.setTokenvalidity(AppContext.getCurrentTime());
							u_serv.save(user);
							sendTokenToEmail();
						} else {
							Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
									"A token has already been sent to this email. Please try again after some time.");

							Executions.sendRedirect("index.zul");
						}

					} else
						sendTokenToEmail();
				} else {
					Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
							"The user account is authenticated via Google Authentication. Please click \"Login with Google\" instead.");

					Executions.sendRedirect("index.zul");
				}

			}else {
				Notification.show("No user found with that email.");
			}

		}

	}

	private boolean validateResetForm() {
		
		formIsValid = true;
		
		checkPassword();
		
		checkConfirmPassword();
		
		
		return formIsValid;

	}

	private void checkConfirmPassword() {
		
		String value = txbox_confirmpassword.getValue();
		
		if (value == null || value.toString().trim().isEmpty()) {
			formIsValid = false;
			throw new WrongValueException(txbox_confirmpassword, "This field cannot be empty.");
		}

		String password = txtbox_password.getValue();

		// Compare the value of confirm password with the password
		if (value == null || !value.toString().equals(password)) {
			formIsValid = false;
			throw new WrongValueException(txbox_confirmpassword, "Passwords do not match.");
		}
		
	}

	private void checkPassword() {
		String value = txtbox_password.getValue();
		
		if (value == null || value.toString().trim().isEmpty()) {
			formIsValid = false;
			throw new WrongValueException(txtbox_password, "This field cannot be empty.");
		}

		String strength = "Weak";

		String password = (String) value;

		if (Pattern.matches(STRONG_PASSWORD_REGEX, password)) {
			strength = "Strong";
		} else if (Pattern.matches(WEAK_PASSWORD_REGEX, password)) {
			strength = "Medium";
		}

		// Display password strength feedback
		if (txtbox_password instanceof Textbox) {
			Textbox textbox = (Textbox) txtbox_password;
			textbox.setTooltip("Password Strength: " + strength);
		}

		// You can also handle error display here
		if (strength.equals("Weak")) {
			formIsValid = false;
			throw new WrongValueException(txtbox_password,
					"Password is too weak. Use a mix of letters, numbers, and special characters.");
		}

		
	}

	private void checkEmail() {
		
		String value = txtbox_email.getValue();

		Pattern regexPattern;

		Matcher regMatcher;

		if (value == null || value.toString().trim().isEmpty()) {
			formIsValid = false;
			throw new WrongValueException(txtbox_email, "This field cannot be empty.");
		}

		List<User> lst_User = u_serv.getByUserName(value.toString());

		if (lst_User.size() > 0) {
			formIsValid = false;
			throw new WrongValueException(txtbox_email, "Email already registered");
		}

		regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
		regMatcher = regexPattern.matcher(value.toString());
		if (!regMatcher.matches()) {
			formIsValid = false;
			throw new WrongValueException(txtbox_email, "Invalid Email Address");
		}

	}

	

	private void sendTokenToEmail() {
		String generatedString = AppContext.getToken();

		user.setTokenvalidity(AppContext.getCurrentTime());
		user.setValidationtoken(generatedString);

		u_serv.save(user);

		try {
			StringBuilder message = new StringBuilder();
			message.append("<html><body>");
			message.append("<p>Hello,</p>");
			message.append(
					"<p>We received a request to change the password for your account on <strong>SNPseek Database</strong>. "
							+ "If you made this request, you can reset your password by clicking the link below. Please note that the link will expire in 30 minutes:</p>");
			message.append(
					"<p><a href=\"" + urlMessage + generatedString + "\">Click here to reset your password</a></p>");
			message.append(
					"<p>If you did not request a password change, please ignore this message. For your security, we recommend "
							+ "changing your password if you suspect any unauthorized activity on your account.</p>");
			message.append("<br>");
			message.append("<p>Kind Regards,<br>");
			message.append("The SNPseek Team</p>");
			message.append("</body></html>");

			MailUtils.simpleSendMail(txtbox_email.getValue(), message.toString());

			Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
					"A password reset link has been sent to " + txtbox_email.getValue());

			Executions.sendRedirect("index.zul");
		} catch (WrongValueException | UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
