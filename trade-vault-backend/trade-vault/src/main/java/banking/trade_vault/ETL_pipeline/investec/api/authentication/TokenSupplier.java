package banking.trade_vault.ETL_pipeline.investec.api.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenSupplier {
    private final RestClient authClient;
    private final Map<String, TokenCacheEntry> tokenCache = new ConcurrentHashMap<>();

    record TokenCacheEntry(String token, Instant expiry) {}

    public TokenSupplier(RestClient.Builder builder) {
        this.authClient = builder.baseUrl("https://openapisandbox.investec.com").build();
    }

    public String getAccessToken(String clientId, String clientSecret, String apiKey) {
        // 1. Check Cache for THIS SPECIFIC Client ID
        TokenCacheEntry entry = tokenCache.get(clientId);

        if (entry != null && Instant.now().isBefore(entry.expiry)) {
            // System.out.println("✅ Using Cached Token for Client: " + clientId.substring(0, 5) + "...");
            return entry.token();
        }

        // 2. Cache Miss? Fetch New Token
        System.out.println("🔄 Fetching NEW Token for Client: " + clientId.substring(0, 5) + "...");

        String authString = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        // Optional: Add scope if your CIB needs it
        // formData.add("scope", "accounts balances transactions trade");

        OAuthTokenResponse response = authClient.post()
                .uri("/identity/v2/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, basicAuth)
                .header("x-api-key", apiKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(OAuthTokenResponse.class);

        if (response == null || response.access_token() == null) {
            throw new IllegalStateException("Received null token from Investec");
        }

        // 3. Store in Map (Keyed by Client ID)
        Instant expiry = Instant.now().plusSeconds(response.expires_in() - 60);
        tokenCache.put(clientId, new TokenCacheEntry(response.access_token(), expiry));

        return response.access_token();
    }
}