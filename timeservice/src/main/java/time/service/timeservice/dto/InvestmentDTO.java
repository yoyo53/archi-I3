package time.service.timeservice.dto;

import jakarta.validation.constraints.NotNull;

public class timeDTO {
    @NotNull
    private Long propertyId;
    @NotNull
    private Double amount;

    public timeDTO() {}

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
