package banking.trade_vault.ETL_pipeline.investec.api.authentication;

public record OAuthTokenResponse(
        String access_token,
        String token_type,
        int expires_in,
        String scope
) {}