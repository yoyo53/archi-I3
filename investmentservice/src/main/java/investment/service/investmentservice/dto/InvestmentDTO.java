package investment.service.investmentservice.dto;

import jakarta.validation.constraints.NotNull;

public class InvestmentDTO {
    @NotNull
    private Long propertyId;
    @NotNull
    private Double amount;

    public InvestmentDTO() {}

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
