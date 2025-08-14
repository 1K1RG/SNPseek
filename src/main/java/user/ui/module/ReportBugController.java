package user.ui.module;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.apache.poi.hpsf.Array;
import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.MailUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceSettings;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;

import user.ui.module.util.constants.SessionConstants;

/**
 * A demo of Big listbox to handle 1 trillion data.
 * 
 * @author jumperchen
 */
public class ReportBugController extends SelectorComposer<Div> {

	private User user;
	private Properties contentProp;

	@Wire
	private Vbox attachmentVb;

	@Wire
	private Button buttonSubmit;

	@Wire
	private Textbox textboxDesc;

	@Wire
	private Textbox textboxEmail;

	@Wire
	private Textbox textboxName;

	// Max file size in bytes (e.g., 5 MB)
	final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

	// Allowed extensions (lowercase for simplicity)
	Set<String> allowedExtensions = new HashSet<>(Arrays.asList("jpg", "png", "pdf", "jpeg"));

	private List<Media> mediaList;

	/**
	 * Initializes Controller to Genotype Module (GenotypeContent.zul)
	 * 
	 */
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);
		contentProp = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

	}

	@Listen("onUpload = #uploadBtn")
	public void handleUpload(UploadEvent event) {
		Media[] medias = event.getMedias(); // 🔥 handle multiple uploads

		mediaList = new ArrayList<Media>();

		if (medias != null && medias.length > 0) {

			int i = 1;
			if (medias.length > 5) {
				for (Media media : medias) {
					String fileName = media.getName();

					String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

					// Check file extension
					if (!allowedExtensions.contains(extension)) {
						Messagebox.show(
								"File " + fileName + " has an invalid file type. Allowed types are .jpg, .png, .pdf.");
						continue; // Skip this file
					}

					if (media.getByteData().length > MAX_FILE_SIZE) {
						Messagebox.show("The file is too large. Max size is 5 MB.");
						return;
					}

					Hbox hbox = new Hbox();

					Label lbl = new Label(fileName);
					lbl.setId("lbl" + i);
					lbl.setParent(attachmentVb);

					// Remove button
					Label removeBtn = new Label("Remove");
					removeBtn.setStyle(
							"padding-left:5px;color: red; background: transparent; border: none; cursor: pointer;");
					removeBtn.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							attachmentVb.removeChild(hbox);
						}

					});

					hbox.appendChild(lbl);
					hbox.appendChild(removeBtn);

					attachmentVb.appendChild(hbox);
					mediaList.add(media);
					i++;
				}
			} else {
				Messagebox.show("Maximum of 5 attachments only.");
			}

			attachmentVb.invalidate();

		} else {
			Messagebox.show("No files were uploaded.");
		}
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
		String recaptchaKey = "6Lde5MEqAAAAAPfUDTkpvARIpETwSsiT-c0ne4eh";

		GoogleCredentials credentials = GoogleCredentials
				.fromStream(new FileInputStream(AppContext.getFlatfilesDir() + "snp-seek-a2082ae982eb.json"));

		createAssessment(projectID, recaptchaKey, token, recaptchaAction, credentials);

		buttonSubmit.setDisabled(false);

	}

	@Listen("onClick =#buttonSubmit")
	public void onbutton_addUser() {

		if (formIsValid()) {
			try {
				MailUtils.sendEmailWithAttachments(textboxName.getName(), textboxEmail.getValue(), "SNPSEEK BUG REPORT",
						textboxDesc.getValue(), mediaList);
			} catch (WrongValueException | UnsupportedEncodingException | MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Sessions.getCurrent().setAttribute(SessionConstants.NOTIFICATION,
					"A bug report has been submitted to the developer. Please wait for their reply at the email address you provided.");

			Executions.sendRedirect("index.zul");
		}

	}

	private boolean formIsValid() {

		String name = textboxName.getValue();
		String value = textboxEmail.getValue();

		Pattern regexPattern;

		Matcher regMatcher;

		if (value == null || value.toString().trim().isEmpty()) {
			Messagebox.show("Email address cannot be empty.");
			return false;
		}

		if (name == null || name.toString().trim().isEmpty()) {
			Messagebox.show("Name should not be empty");
			return false;
		}

		regexPattern = Pattern.compile("^([a-zA-Z0-9._\\+-]+)@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");

		regMatcher = regexPattern.matcher(value.toString());
		if (!regMatcher.matches()) {
			Messagebox.show("Invalid Email Address.");
			return false;
		}

		return true;

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

}