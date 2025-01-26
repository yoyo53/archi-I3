package notification.service.notificationservice.dto;

import jakarta.validation.constraints.NotNull;

public class NotificationDTO {
    
    @NotNull
    private Long investmentId;

    public NotificationDTO() {}

    public Long getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }
}
