package investment.service.investmentservice.model;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "investments")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Property property;

    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private Double amountInvested;

    @NotNull
    private String investmentDate;

    @NotNull
    private Double sharesOwned;

    @OneToOne
    private Certificate certificate;

    @OneToOne
    private Payment payment;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvestmentStatus status;

    // Constructeurs
    public Investment() {
    }

    public Investment(Property property, User user, String investmentDate, Double amountInvested) {
        this.property = property;
        this.user = user;
        this.amountInvested = amountInvested;
        this.investmentDate = investmentDate;
        this.sharesOwned = amountInvested / property.getPrice();
        this.status = InvestmentStatus.PENDING;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Double getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(Double sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status.getDescription();
    }

    public void setStatus(String status) {
        this.status = InvestmentStatus.valueOf(status);
    }

    
}
