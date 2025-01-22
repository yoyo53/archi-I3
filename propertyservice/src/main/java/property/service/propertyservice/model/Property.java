package property.service.propertyservice.model;


import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private double price;
    private double annualRentalIncomeRate;
    private double appreciationRate;
    private String status;
    private String fundingDeadline;
    private double fundedAmount;
    private ArrayList<Investment> investors;

    public Property() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getFundingDeadline() {
        return fundingDeadline;
    }
    public void setFundingDeadline(String fundingDeadline) {
        this.fundingDeadline = fundingDeadline;
    }
    public double getFundedAmount() {
        return fundedAmount;
    }
    public void setFundedAmount(double fundedAmount) {
        this.fundedAmount = fundedAmount;
    }
    public ArrayList<Investment> getInvestors() {
        return investors;
    }
    public void setInvestors(ArrayList<Investment> investors) {
        this.investors = investors;
    }

    

    
}
