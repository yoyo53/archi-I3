package property.service.propertyservice.model;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    private Long id;

    @ManyToOne
    @NotNull
    private Property property;

    @NotNull
    private Double amountInvested;

    @NotNull
    private LocalDate investmentDate;

    public Investment() {
    }

    public Investment(Property property, Double amountInvested) {
        this.property = property;
        this.amountInvested = amountInvested;
    }

    public Investment(Property property, Double amountInvested, LocalDate investmentDate) {
        this.property = property;
        this.amountInvested = amountInvested;
        this.investmentDate = investmentDate;
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

    public LocalDate getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(LocalDate investmentDate) {
        this.investmentDate = investmentDate;
    }

}

