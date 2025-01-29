package notification.service.notificationservice.model;

import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "investments")

public class Investment {

    public enum InvestmentStatus {
        PENDING("PENDING"),
        SUCCESS("SUCCESS"),
        FAILED("FAILED"),
        CANCELLED("CANCELLED"),
        COMPLETED("COMPLETED");
    
        private final String description;
    
        InvestmentStatus(String description) {
            this.description = description;
        }
    
        public String getDescription() {
            return description;
        }
    }

    @Id
    private Long id;

    @ManyToOne
    @NotNull
    private Property property;

    @NotNull
    private Double amountInvested;

    @NotNull
    private String investmentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvestmentStatus status;

    public Investment() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
    public Double getAmountInvested() {
        return amountInvested;
    }

    public void setAmountInvested(Double amountInvested) {
        this.amountInvested = amountInvested;
    }

    public String getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(String investmentDate) {
        this.investmentDate = investmentDate;
    }

    public String getStatus() {
        return status.getDescription();
    }
    public void setStatus(String status) {
        this.status = InvestmentStatus.valueOf(status);
    }
}

