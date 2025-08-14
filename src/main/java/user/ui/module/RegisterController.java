package user.ui.module;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
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
import org.irri.iric.ds.chado.domain.model.Subscription;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.ds.chado.domain.model.UserSubscription;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.MailUtils;
import org.irri.iric.portal.admin.EnvironmentService;
import org.irri.iric.portal.config.KeysPropertyConfig;
import org.irri.iric.portal.config.SNPseekEnv;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.zkoss.zul.Textbox;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceSettings;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;

import user.ui.module.util.PasswordUtils;
import user.ui.module.util.constants.SessionConstants;

public class RegisterController extends SelectorComposer<Component> {

	@Autowired
	private EnvironmentService env;
	
	@Wire
	private Textbox txtbox_fname;

	@Wire
	private Textbox txtbox_lname;

	@Wire
	private Textbox txtbox_email;

	@Wire
	private Textbox txtbox_password;

	@Wire
	private Textbox txbox_confirmpassword;

	@Wire
	private Button buttonSubmit;

	private String urlMessage = "https://snpseek.irri.org/validate.zul?token=";

	private UserDAO u_serv;

	private KeysPropertyConfig keyProp;

	private SubscriptionDAO subs_serv;

	private UserSubscriptionDAO usubs_serv;

	private boolean formIsValid;

	@Wire
	private Textbox recaptchaResponse;

	// The regular expressions for password strength
	private static final String STRONG_PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}";

	private static final String WEAK_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{6,}";

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		u_serv = (UserDAO) AppContext.getApplicationContext().getBean("UserDAO");
		subs_serv = (SubscriptionDAO) AppContext.getApplicationContext().getBean("SubscriptionDAO");
		usubs_serv = (UserSubscriptionDAO) AppContext.getApplicationContext().getBean("UserSubscriptionDAO");

		keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");


	}

	@Listen("onRecaptchaValidated = #recaptchaDiv")
	public void verify(Event event) throws Exception {
		// Get the event data which contains the response and action
		Map<String, Object> eventData = (Map<String, Object>) event.getData();

		// Extract the reCAPTCHA response and action
		String recaptchaResponse = (String) eventData.get("response");
		String recaptchaAction = (String) eventData.get("action");
		String token = (String) eventData.get("token");

		String projectID = "snp-seek";
		String recaptchaKey = System.getenv(SNPseekEnv.RECAPTCHA_KEY);


		GoogleCredentials credentials = GoogleCredentials
				.fromStream(new FileInputStream(AppContext.getFlatfilesDir()+"snp-seek-a2082ae982eb.json"));

		createAssessment(projectID, recaptchaKey, token, recaptchaAction, credentials);

	}

	private void createAssessment(String projectID, String recaptchaKey, String token, String recaptchaAction,
			GoogleCredentials credentials) throws IOException {

		try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient
				.create(RecaptchaEnterpriseServiceSettings.newBuilder()
						.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build());) {

			// Set the properties of the event to be tracked.
			com.google.recaptchaenterprise.v1.Event event = com.google.recaptchaenterprise.v1.Event.newBuilder()
					.setSiteKey(recaptchaKey).setToken(token).build();

			// Build the assessment request.
			CreateAssessmentRequest createAssessmentRequest = CreateAssessmentRequest.newBuilder()
					.setParent(ProjectName.of(projectID).toString())
					.setAssessment(Assessment.newBuilder().setEvent(event).build()).build();
			Assessment response = client.createAssessment(createAssessmentRequest);

			// Check if the token is valid.
			if (!response.getTokenProperties().getValid()) {
				System.out.println("The CreateAssessment call failed because the token was: "
						+ response.getTokenProperties().getInvalidReason().name());
				return;
			}

			// Check if the expected action was executed.
//			if (!response.getTokenProperties().getAction().trim().equals(recaptchaAction)) {
//				System.out.println(
//						"The action attribute in reCAPTCHA tag is: " + response.getTokenProperties().getAction());
//				System.out.println("The action attribute in the reCAPTCHA tag " + "does not match the action ("
//						+ recaptchaAction + ") you are expecting to score");
//				return;
//			}
			// Get the risk score and the reason(s).
			// For more information on interpreting the assessment, see:
			// https://cloud.google.com/recaptcha-enterprise/docs/interpret-assessment
			for (ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
				System.out.println(reason);
			}

			float recaptchaScore = response.getRiskAnalysis().getScore();
			System.out.println("The reCAPTCHA score is: " + recaptchaScore);

			// Get the assessment name (id). Use this to annotate the assessment.
			String assessmentName = response.getName();
			System.out.println("Assessment name: " + assessmentName.substring(assessmentName.lastIndexOf("/") + 1));
		}
	}

	

	@Listen("onClick =#buttonSubmit")
	public void onbutton_addUser() {

		if (checkForm()) {

			List<Subscription> lst_subs = subs_serv.getByShortName("adm");

			String generatedString = AppContext.getToken();

			String pass = "";
			try {
				pass = PasswordUtils.encrypt(txtbox_password.getValue(), keyProp.getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}

			org.irri.iric.ds.chado.domain.model.User user = new User();
			user = new User();
			user.setEmail(txtbox_email.getValue());
			user.setUsername(txtbox_email.getValue());
			user.setPasswordHash(pass);
			user.setValidationtoken(generatedString);
			user.setValidated(false);
			user.setCreatedAt(AppContext.getCurrentTime());

			user = u_serv.save(user);

			UserSubscription usub = new UserSubscription();
			usub.setUser(user);
			usub.setSubscription(lst_subs.get(0));
			usub.setIsActive(true);
			usub.setStartDate(AppContext.getCurrentTime());

			usubs_serv.save(usub);

			StringBuilder message = new StringBuilder();
			message.append("<html><body>");
			message.append("<p>Hello,</p>");
			message.append(
					"<p>You registered an account on <strong>SNPseek Database</strong>. Before being able to use your account, "
							+ "you need to verify that this is your email address by clicking the link below:</p>");
			message.append(
					"<p><a href=\"" + urlMessage + generatedString + "\">Click here to verify your email</a></p>");
			message.append("<br>");
			message.append("<p>Kind Regards,<br>");
			message.append("The SNPseek Team</p>");
			message.append("</body></html>");

			try {
				MailUtils.simpleSendMail(txtbox_email.getValue(), message.toString());
			} catch (WrongValueException | UnsupportedEncodingException | MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Set a session or request attribute with the message
			Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
					"A validation email has been sent to " + txtbox_email.getValue());

			// Forward to the new page

			Executions.sendRedirect("index.zul");
		}

	}

	private boolean checkForm() {
		formIsValid = true;

		checkEmptyValue();
		
		checkEmail();
		
		checkPassword();
		
		checkConfirmPassword();

		return formIsValid;
	}

	private void checkEmptyValue() {
		String fvalue = txtbox_fname.getValue();
		String lvalue = txtbox_lname.getValue();
		
		if (fvalue == null || fvalue.toString().trim().isEmpty()) {
			formIsValid = false;
			throw new WrongValueException(txtbox_fname, "This field cannot be empty.");
		}
		
		if (lvalue == null || lvalue.toString().trim().isEmpty()) {
			formIsValid = false;
			throw new WrongValueException(txtbox_lname, "This field cannot be empty.");
		}

		
		
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
			throw new IllegalArgumentException(
					"Your password is too weak. For better security, please create a password that includes:\n"
					+ "\n"
					+ "At least 8 characters\n"
					+ "A mix of uppercase and lowercase letters\n"
					+ "At least one number (0-9)\n"
					+ "At least one special character (e.g., !, @, #, $, %, &)\n"
					+ "Avoid using easily guessable information like names, birthdates, or common words");
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

		regexPattern = Pattern.compile("^([a-zA-Z0-9._\\+-]+)@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
									

		regMatcher = regexPattern.matcher(value.toString());
		if (!regMatcher.matches()) {
			formIsValid = false;
			throw new WrongValueException(txtbox_email, "Invalid Email Address");
		}

	}

	private boolean validPassword() {
		if (txtbox_password.getValue().isEmpty() || txbox_confirmpassword.getValue().isEmpty()) {
			Notification.show("password is empty");
			return false;
		}

		if (!txtbox_password.getValue().equals(txbox_confirmpassword.getValue())) {
			Notification.show("password do not match");
			return false;
		}

		return true;

	}

	private void simpleSendMail(String email, String generatedString)
			throws MessagingException, UnsupportedEncodingException {

		String FROM = keyProp.getFrom();
		String FROMNAME = keyProp.getFromName();

		String HOST = keyProp.getHost();
		String SMTP_USERNAME = keyProp.getUsername();
		String SMTP_PASSWORD = keyProp.getPassword();

		InternetAddress[] recipients = new InternetAddress[1];
		recipients[0] = new InternetAddress(email);

		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", keyProp.getPort());
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Ensure TLS 1.2 is enabled

		Session session = Session.getDefaultInstance(props);

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM, FROMNAME));
		msg.setRecipients(Message.RecipientType.TO, recipients);
		msg.setSubject("SNPSEEK : Account Verification");

		StringBuilder message = new StringBuilder();
		message.append("<html><body>");
		message.append("<p>Hello,</p>");
		message.append(
				"<p>You registered an account on <strong>SNPseek Database</strong>. Before being able to use your account, "
						+ "you need to verify that this is your email address by clicking the link below:</p>");
		message.append("<p><a href=\"" + urlMessage + generatedString + "\">Click here to verify your email</a></p>");
		message.append("<br>");
		message.append("<p>Kind Regards,<br>");
		message.append("The SNPseek Team</p>");
		message.append("</body></html>");

		msg.setContent(message.toString(), "text/html");

		Transport transport = session.getTransport();

		try {
			AppContext.debug("Sending...");

			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

			transport.sendMessage(msg, msg.getAllRecipients());
			AppContext.debug("Email sent!");
		} finally {
			// Close and terminate the connection.
			transport.close();
		}

	}

}
