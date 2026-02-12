package banking.trade_vault.core.apis.external.authentication;

public record OAuthTokenResponse(
        String access_token,
        String token_type,
        int expires_in,
        String scope
) {}