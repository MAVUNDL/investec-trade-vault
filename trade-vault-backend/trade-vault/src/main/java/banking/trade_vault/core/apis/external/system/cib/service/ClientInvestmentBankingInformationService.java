package banking.trade_vault.core.apis.external.system.cib.service;

import banking.trade_vault.core.apis.external.system.cib.models.Shipment;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("za/bb/v1")
public interface ClientInvestmentBankingInformationService {
    @GetExchange("companies/SANDBOXRACINGIMPORTS/currentOrdersAndShipmentStatusLineItems")
    List<Shipment> getShipments();
}
