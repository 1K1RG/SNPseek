package org.irri.iric.portal.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/microsoftLogin")
public class MicrosoftLoginServlet extends HttpServlet {
	// Replace with your actual client ID, tenant ID, and redirect URI
    private static final String CLIENT_ID = "8f69a01b-9888-4dab-a974-0806b5d9c90e";
    private static final String TENANT_ID = "6afa0e00-fa14-40b7-8a2e-22a7f8c357d5";
    private static final String REDIRECT_URI = "https://snpseek.irri.org/callback"; // Make sure this matches what you set in Azure
//    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // Prepare the Microsoft login URL
        String authUrl = "https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/authorize?"
                + "client_id=" + CLIENT_ID
                + "&response_type=code"
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
                + "&scope=" + URLEncoder.encode("User.Read", "UTF-8");

        // Generate the HTML login page with a button that redirects to Microsoft's OAuth login
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Microsoft Login</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f4; margin: 0; }");
        out.println(".login-container { background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center; }");
        out.println("h2 { margin-bottom: 20px; }");
        out.println(".login-btn { padding: 10px 20px; background-color: #0078d4; color: white; font-size: 16px; border: none; border-radius: 5px; cursor: pointer; }");
        out.println(".login-btn:hover { background-color: #005a9e; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='login-container'>");
        out.println("<h2>Sign in with Microsoft</h2>");
        out.println("<a href='" + authUrl + "'>");
        out.println("<button class='login-btn'>Login with Microsoft</button>");
        out.println("</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        
        response.sendRedirect(authUrl);
    }
    
   
}
