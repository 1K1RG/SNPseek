package org.irri.iric.portal;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.irri.iric.portal.config.KeysPropertyConfig;
import org.zkoss.util.media.Media;

public class MailUtils {

	private static KeysPropertyConfig keyProp;

	public static void simpleSendMail(String email, String message)
			throws UnsupportedEncodingException, MessagingException {

		keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");

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

		msg.setContent(message, "text/html");

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

	public static void sendEmailWithAttachments(String fromName, String fromEmail, String subject, String messageText,
			List<Media> attachments) throws Exception {

		keyProp = (KeysPropertyConfig) AppContext.checkBean(keyProp, "keysPropertyConfig");

		StringBuilder composeMessage = new StringBuilder();

		composeMessage.append(messageText);

		String FROM = keyProp.getFrom();
		String FROMNAME = keyProp.getFromName();

		String HOST = keyProp.getHost();
		String SMTP_USERNAME = keyProp.getUsername();
		String SMTP_PASSWORD = keyProp.getPassword();

		InternetAddress[] recipients = new InternetAddress[1];
		recipients[0] = new InternetAddress("l.h.barboza@cgiar.org");

		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", keyProp.getPort());
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Ensure TLS 1.2 is enabled

		Session session = Session.getDefaultInstance(props);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmail));
		message.setRecipients(Message.RecipientType.TO, recipients);
		message.setSubject(subject);

		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(composeMessage.toString());

		if (attachments.size() > 0) {
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(textPart);

			for (Media media : attachments) {
				MimeBodyPart attachmentPart = new MimeBodyPart();
				InputStream is = media.getStreamData();
				String contentType = media.getContentType();
				String fileName = media.getName();

				ByteArrayDataSource dataSource = new ByteArrayDataSource(is, contentType);
				attachmentPart.setDataHandler(new DataHandler(dataSource));
				attachmentPart.setFileName(fileName);

				multipart.addBodyPart(attachmentPart);
			}

			message.setContent(multipart);
		}
		Transport transport = session.getTransport();

		try {
			AppContext.debug("Sending...");

			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

			transport.sendMessage(message, message.getAllRecipients());
			AppContext.debug("Email sent!");
		} finally {
			// Close and terminate the connection.
			transport.close();
		}
	}

}
