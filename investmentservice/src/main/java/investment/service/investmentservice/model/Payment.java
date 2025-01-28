package investment.service.investmentservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;


@Entity
@Table(name = "payments")
public class Payment {
    public enum PaymentStatus {
        PENDING("PENDING"),
        SUCCESS("SUCCESS"),
        FAILED("FAILED");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
    
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @JsonIgnore
    @OneToOne(mappedBy = "payment")
    private Investment investment;

    public Payment() {
    }

    public Payment(Long id, String status, Investment investment) {
        this.id = id;
        this.status = PaymentStatus.valueOf(status);
        this.investment = investment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status.getDescription();
    }

    public void setStatus(String status) {
        this.status = PaymentStatus.valueOf(status);
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
    }
}