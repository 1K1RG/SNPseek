package user.ui.module;

import com.microsoft.aad.msal4j.*;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class MicrosoftAuth {

    private static final String CLIENT_ID = "8f69a01b-9888-4dab-a974-0806b5d9c90e";  // Your Azure App Registration's Client ID
    private static final String TENANT_ID = "6afa0e00-fa14-40b7-8a2e-22a7f8c357d5";  // Your Azure AD Tenant ID
    private static final String REDIRECT_URI = "http://localhost:8080"; // Redirect URI
    private static final String AUTHORITY = "https://login.microsoftonline.com/" + TENANT_ID;

    public static void main(String[] args) {
        try {
            // Build the PublicClientApplication
            PublicClientApplication publicClientApplication = PublicClientApplication
                    .builder(CLIENT_ID)
                    .authority(AUTHORITY)
                    .build();

            // Define the required scope(s)
            Set<String> scopes = Collections.singleton("User.Read"); // Using Set instead of List

            // Acquire the token interactively (browser-based login)
            InteractiveRequestParameters requestParameters = InteractiveRequestParameters
                    .builder(new URI(REDIRECT_URI))
                    .scopes(scopes)  // Using Set of scopes correctly
                    .build();

            IAuthenticationResult result = publicClientApplication
                    .acquireToken(requestParameters)
                    .join();  // This is a blocking call for simplicity

            // Successfully authenticated, retrieve the access token
            String accessToken = result.accessToken();
            System.out.println("Access token: " + accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
