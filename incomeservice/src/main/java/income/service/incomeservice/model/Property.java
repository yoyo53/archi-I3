package income.service.incomeservice.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    private Long id;

    @NotNull
    private double price;

    @NotNull
    private double annualRentalIncomeRate;

    @NotNull
    private double appreciationRate;
    
    @JsonIgnore
    @OneToMany(mappedBy = "property")
    private List<Investment> investments;
    


    public Property() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getAnnualRentalIncomeRate() {
        return annualRentalIncomeRate;
    }
    public void setAnnualRentalIncomeRate(double annualRentalIncomeRate) {
        this.annualRentalIncomeRate = annualRentalIncomeRate;
    }
    public double getAppreciationRate() {
        return appreciationRate;
    }
    public void setAppreciationRate(double appreciationRate) {
        this.appreciationRate = appreciationRate;
    }
    public List<Investment> getInvestments() {
        return investments;
    }
    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }
    
}
