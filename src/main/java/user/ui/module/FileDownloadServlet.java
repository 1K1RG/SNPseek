package user.ui.module;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irri.iric.ds.chado.domain.model.User;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@WebServlet("/download-file")
public class FileDownloadServlet extends HttpServlet {

    private AmazonS3 s3Client;
    private final String bucketName = "snp-seek";


    @Override
    public void init() throws ServletException {
        super.init();
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_1)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileKey = request.getParameter("key");

        if (fileKey == null || fileKey.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file key");
            return;
        }

        HttpSession session = request.getSession(false);
        String username = null;

        if (session != null) {
            username = (String) session.getAttribute("username"); // or "user", depending on what you set
        }
        // Get logged-in user (from session or security context)
//        Principal principal = request.getUserPrincipal();
        if (userNoHasAccess(username, fileKey)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // Generate S3 presigned URL
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 30); // 30 minutes
        URL presignedUrl = s3Client.generatePresignedUrl(bucketName, fileKey, expiration, HttpMethod.GET);

        // Redirect to presigned S3 URL
        response.sendRedirect(presignedUrl.toString());
    }

    private boolean userNoHasAccess(String username, String fileKey) {
        return username == null || fileKey == null;
    }
}
