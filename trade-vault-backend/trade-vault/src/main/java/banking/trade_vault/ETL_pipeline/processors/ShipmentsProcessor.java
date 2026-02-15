package banking.trade_vault.ETL_pipeline.processors;

import banking.trade_vault.ETL_pipeline.entities.ShipmentEntity;
import banking.trade_vault.ETL_pipeline.investec.api.sections.cib.models.Shipment;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ShipmentsProcessor implements ItemProcessor<Shipment, ShipmentEntity> {
    @Override
    public ShipmentEntity process(Shipment shipment) throws Exception {

        BigDecimal estimatedLandedCost = null;
        if (shipment.estimatedLandedCost()!= null && !shipment.estimatedLandedCost().isBlank()) {
            String sanitizedAmount = shipment.estimatedLandedCost().replaceAll("[^0-9.]", "");
            if (!sanitizedAmount.isBlank()) {
                estimatedLandedCost = new BigDecimal(sanitizedAmount);
            }
        }
        return new ShipmentEntity(
                shipment.shipmentNumber(),
                shipment.indentNumber(),
                shipment.ifbReference(),
                shipment.customerName(),
                shipment.buyerFullName(),
                shipment.supplierName(),
                shipment.portOfLoad(),
                shipment.portOfDischarge(),
                shipment.shipOnBoard(),
                shipment.eta(),
                shipment.deliveryDate(),
                shipment.currencyCode(),
                shipment.orderValue(),
                shipment.shippedValue(),
                shipment.paidAmount(),
                shipment.incoterm(),
                shipment.status(),
                shipment.movementType(),
                shipment.shipmentMode(),
                shipment.mvStartDate(),
                shipment.mvEndDate(),
                shipment.vesselName(),
                shipment.containerNumber(),
                Integer.valueOf(shipment.containerCount()),
                shipment.containerType(),
                shipment.loadType(),
                shipment.pallets(),
                shipment.cartons(),
                shipment.deliveryContact(),
                shipment.deliveryMonth(),
                shipment.deliveryYear(),
                shipment.deliveryAddress(),
                shipment.unitPrice(),
                shipment.itemReference(),
                Integer.valueOf(shipment.quantity()),
                shipment.description(),
                estimatedLandedCost,
                LocalDateTime.now()
        );
    }
}
