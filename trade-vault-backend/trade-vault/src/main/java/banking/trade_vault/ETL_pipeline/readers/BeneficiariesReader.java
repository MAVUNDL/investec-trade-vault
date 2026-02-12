package banking.trade_vault.ETL_pipeline.readers;

import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Beneficiary;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.service.PrivateBankingInformationService;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.List;

@Component
public class BeneficiariesReader implements ItemReader<Beneficiary> {

    private final PrivateBankingInformationService pbService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Iterator<Beneficiary> beneficiaryIterator;

    public BeneficiariesReader(PrivateBankingInformationService pbService) {
        this.pbService = pbService;
    }

    @Override
    public Beneficiary read() throws Exception {
        if(beneficiaryIterator == null){
            tools.jackson.databind.JsonNode root = pbService.getBeneficiaries();
            JsonNode beneficiariesNode = root.path("data");

            if(beneficiariesNode.isMissingNode() || !beneficiariesNode.isArray()){
                return null;
            }

            List<Beneficiary> beneficiaries = objectMapper.convertValue(beneficiariesNode, new TypeReference<List<Beneficiary>>() {});
            beneficiaryIterator = beneficiaries.iterator();
        }

        if(beneficiaryIterator.hasNext()){
            return beneficiaryIterator.next();
        } else {
            return null;
        }
    }
}
