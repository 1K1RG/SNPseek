package user.ui.module;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@RestController
public class FileDownloadController {

	private final AmazonS3 s3Client;
	private final String bucketName = "snp-seek";

	public FileDownloadController() {
		this.s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1) 
				.withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
	}

	@GetMapping("/download-file")
	public void downloadFile(@RequestParam("key") String fileKey, HttpServletResponse response, Principal principal)
			throws IOException {

		if (!userHasAccess(principal, fileKey)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
			return;
		}

		Date expiration = new Date(System.currentTimeMillis() + (1000 * 60 * 30));
		URL presignedUrl = s3Client.generatePresignedUrl(bucketName, fileKey, expiration, HttpMethod.GET);

		response.sendRedirect(presignedUrl.toString());
	}

	private boolean userHasAccess(Principal principal, String fileKey) {
		return principal != null && fileKey.startsWith("files/");
	}
}
