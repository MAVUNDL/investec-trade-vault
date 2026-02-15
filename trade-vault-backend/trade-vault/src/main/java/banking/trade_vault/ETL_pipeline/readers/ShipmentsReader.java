package banking.trade_vault.ETL_pipeline.readers;

import banking.trade_vault.ETL_pipeline.investec.api.sections.cib.models.Shipment;
import banking.trade_vault.ETL_pipeline.investec.api.sections.cib.service.ClientInvestmentBankingInformationService;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShipmentsReader implements ItemReader<Shipment> {

    private static final Logger log = LoggerFactory.getLogger(ShipmentsReader.class);
    private final ClientInvestmentBankingInformationService cibService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Iterator<Shipment> shipmentsIterator;

    public ShipmentsReader(ClientInvestmentBankingInformationService cibService){
        this.cibService = cibService;
    }

    @Override
    public @Nullable Shipment read() throws Exception {
        if (shipmentsIterator == null) {
            JsonNode root = cibService.getShipments();
            JsonNode shipmentsNode = root.path("data");

            if (shipmentsNode.isMissingNode() || !shipmentsNode.isArray()) {
                log.warn("No shipment data found in API response.");
                return null;
            }

            List<Shipment> allShipments = objectMapper.convertValue(shipmentsNode, new TypeReference<List<Shipment>>() {});

            // FILTERING LOGIC: Remove items with null shipmentNumber
            List<Shipment> validShipments = allShipments.stream()
                    .filter(s -> {
                        boolean isValid = s.shipmentNumber() != null;
                        if (!isValid) {
                            log.warn("Skipping invalid shipment: ID is null. Data: {}", s);
                        }
                        return isValid;
                    })
                    .toList();

            log.info("Fetched {} shipments. {} were valid.", allShipments.size(), validShipments.size());
            shipmentsIterator = validShipments.iterator();
        }

        if (shipmentsIterator.hasNext()){
            return shipmentsIterator.next();
        } else {
            return null;
        }
    }
}