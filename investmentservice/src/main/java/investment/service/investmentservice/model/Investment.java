package investment.service.investmentservice.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long propertyID;

    private Long userID;

    private Long amountInvested;

    private LocalDate investmentDate;

    private Long sharesOwned;

    private Long certificatID = null;

    private Long paymentID = null;

    // Constructeurs
    public Investment() {
    }

    public Investment(Long propertyID, Long userID, Long amountInvested) {
        this.propertyID = propertyID;
        this.userID = userID;
        this.amountInvested = amountInvested;
    }

    public Investment(Long propertyID, Long userID, Long amountInvested, LocalDate investmentDate, Long sharesOwned) {
        this.propertyID = propertyID;
        this.userID = userID;
        this.amountInvested = amountInvested;
        this.investmentDate = investmentDate;
        this.sharesOwned = sharesOwned;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(Long propertyID) {
        this.propertyID = propertyID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getAmountInvested() {
        return amountInvested;
    }

    public void setAmountInvested(Long amountInvested) {
        this.amountInvested = amountInvested;
    }

    public LocalDate getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(LocalDate investmentDate) {
        this.investmentDate = investmentDate;
    }

    public Long getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(Long sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    public Long getCertificatID() {
        return certificatID;
    }

    public void setCertificatID(Long certificatID) {
        this.certificatID = certificatID;
    }

    public Long getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }
}
