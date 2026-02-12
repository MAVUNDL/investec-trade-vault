package banking.trade_vault.ETL_pipeline.investec.api.sections.cib.service;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;


@HttpExchange("/za/bb/v1")
public interface ClientInvestmentBankingInformationService {
    @GetExchange("/companies/SANDBOXRACINGIMPORTS/currentOrdersAndShipmentStatusLineItems")
    JsonNode getShipments();
}
