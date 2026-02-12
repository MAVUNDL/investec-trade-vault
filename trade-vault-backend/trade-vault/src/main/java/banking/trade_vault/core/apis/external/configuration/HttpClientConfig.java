package banking.trade_vault.core.apis.external.configuration;


import banking.trade_vault.core.apis.external.authentication.TokenSupplier;
import banking.trade_vault.core.apis.external.system.cib.service.ClientInvestmentBankingInformationService;
import banking.trade_vault.core.apis.external.system.pb.service.PrivateBankingInformationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;



@Configuration
@ImportHttpServices(group = "private-banking", types = {PrivateBankingInformationService.class})
@ImportHttpServices(group = "client-investment-banking", types = {ClientInvestmentBankingInformationService.class})
public class HttpClientConfig {

    @Bean
    public RestClientHttpServiceGroupConfigurer groupConfigurer(TokenSupplier tokenSupplier) {
        return groups -> {
            groups.filterByName("private-banking")
                    .forEachClient((_, builder) -> builder
                            .baseUrl("https://openapisandbox.investec.com")
                            .requestInterceptor((request, body, execution) -> {
                                String token = tokenSupplier.getAccessToken(System.getenv("PB_CLIENT_ID"), System.getenv("PB_SECRET"), System.getenv("PB_API_KEY"));
                                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                                return execution.execute(request, body);
                            }));

            groups.filterByName("client-investment-banking")
                    .forEachClient((_, builder) -> builder
                            .baseUrl("https://openapisandbox.investec.com")
                            .requestInterceptor((request, body, execution) -> {
                                String token = tokenSupplier.getAccessToken(System.getenv("CIB_CLIENT_ID"), System.getenv("CIB_SECRET"), System.getenv("CIB_API_KEY"));
                                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                                request.getHeaders().add("User-Agent", "PostmanRuntime/7.26.8");
                                return execution.execute(request, body);
                            }));
        };
    }

}
