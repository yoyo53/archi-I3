package income.service.incomeservice.model;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Property property;

    @NotNull
    private Long userId;

    @NotNull
    private Double amountInvested;

    @NotNull
    private Double sharesOwned;

    @OneToOne
    private Certificate certificat;

    // Constructeurs
    public Investment() {
    }

    public Investment(Property property, Long userId, Double amountInvested, Double sharesOwned) {
        this.property = property;
        this.userId = userId;
        this.amountInvested = amountInvested;
        this.sharesOwned = sharesOwned;
    }   

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmountInvested() {
        return amountInvested;
    }

    public void setAmountInvested(Double amountInvested) {
        this.amountInvested = amountInvested;
    }

    public Double getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(Double sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Certificate getCertificat() {
        return certificat;
    }

    public void setCertificat(Certificate certificat) {
        this.certificat = certificat;
    }
    
}