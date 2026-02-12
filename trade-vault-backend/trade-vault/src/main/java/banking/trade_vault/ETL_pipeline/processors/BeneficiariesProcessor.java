package banking.trade_vault.ETL_pipeline.processors;

import banking.trade_vault.ETL_pipeline.entities.BeneficiaryEntity;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Beneficiary;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class BeneficiariesProcessor implements ItemProcessor<Beneficiary, BeneficiaryEntity> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public @Nullable BeneficiaryEntity process(Beneficiary beneficiary) throws Exception {

        // --- Safe parsing for lastPaymentDate ---
        LocalDate lastPaymentDate = parseDateSafe(beneficiary.lastPaymentDate());

        // --- Safe parsing for lastPaymentAmount (removing non-digit characters) ---
        BigDecimal lastPaymentAmount = null;
        if (beneficiary.lastPaymentAmount() != null && !beneficiary.lastPaymentAmount().isBlank()) {
            String sanitizedAmount = beneficiary.lastPaymentAmount().replaceAll("[^0-9.]", "");
            if (!sanitizedAmount.isBlank()) {
                lastPaymentAmount = new BigDecimal(sanitizedAmount);
            }
        }

        return new BeneficiaryEntity(
                beneficiary.beneficiaryId(),
                beneficiary.accountNumber(),
                beneficiary.code(),
                beneficiary.bank(),
                beneficiary.beneficiaryName(),
                lastPaymentAmount,
                lastPaymentDate,
                beneficiary.cellNo(),
                beneficiary.emailAddress(),
                beneficiary.name(),
                beneficiary.referenceAccountNumber(),
                beneficiary.referenceName(),
                beneficiary.categoryId(),
                beneficiary.profileId(),
                beneficiary.fasterPaymentAllowed(),
                LocalDateTime.now()
        );
    }

    /**
     * Safely parse a date string. Returns null if invalid or non-date text.
     */
    private LocalDate parseDateSafe(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }
        rawDate = rawDate.trim();

        try {
            // Only attempt parsing if it matches dd/MM/yyyy
            if (rawDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(rawDate, DATE_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            // Ignore invalid dates
        }

        return null; // fallback for invalid / non-date strings
    }
}
