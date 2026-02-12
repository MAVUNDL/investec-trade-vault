package banking.trade_vault.core.apis.external.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class TokenSupplier {
    private final RestClient authClient;

    //  (in production, use a proper cache with expiry)
    private String cachedToken;
    private Instant tokenExpiry = Instant.MIN;

    public TokenSupplier(RestClient.Builder builder) {
        // Create a dedicated client just for the Auth endpoint
        this.authClient = builder.baseUrl("https://openapisandbox.investec.com").build();
    }

    public String getAccessToken( String clientId, String clientSecret, String apiKey) {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiry)) {
            return cachedToken;
        }

        System.out.println("DEBUG: Fetching new Investec Token for ClientID: " + clientId);

        // 2. Prepare Basic Auth (StandardCharsets.UTF_8 is safer)
        String authString = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        // Note: Some Investec environments require a scope, e.g., 'accounts'
        // formData.add("scope", "accounts");

        // 3. Execute Request with Error Logging
        OAuthTokenResponse response = authClient.post()
                .uri("/identity/v2/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, basicAuth)
                .header("x-api-key", apiKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                // CAPTURE THE ERROR BODY
                .onStatus(HttpStatusCode::is4xxClientError, (request, resp) -> {
                    String body = new String(resp.getBody().readAllBytes());
                    System.err.println("❌ INVESTEC AUTH FAILED: " + resp.getStatusCode());
                    System.err.println("❌ BODY: " + body);
                    throw new RuntimeException("Investec Auth Failed: " + body);
                })
                .body(OAuthTokenResponse.class);

        if (response == null || response.access_token() == null) {
            throw new IllegalStateException("Received null token from Investec");
        }

        // 4. Update State
        this.cachedToken = response.access_token();
        this.tokenExpiry = Instant.now().plusSeconds(response.expires_in() - 60);

        System.out.println("✅ Token retrieved successfully.");
        return cachedToken;
    }

}
