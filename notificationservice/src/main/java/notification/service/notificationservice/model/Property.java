package notification.service.notificationservice.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import notification.service.notificationservice.dto.CreatePropertyDTO;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "properties")
public class Property {

    public enum PropertyStatus {
        DRAFT("DRAFT"),
        OPENED("OPENED"),
        FUNDED("FUNDED"),
        CLOSED("CLOSED");
    
        private final String description;
    
        PropertyStatus(String description) {
            this.description = description;
        }
    
        public String getDescription() {
            return description;
        }
    }

    @Id
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @NotNull
    private double price;

    @NotNull
    private double annualRentalIncomeRate;

    @NotNull
    private double appreciationRate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @NotNull
    private String fundingDeadline;
    
    @JsonIgnore
    @OneToMany(mappedBy = "property")
    private List<Investment> investments;
    


    public Property() {
        this.status = PropertyStatus.DRAFT;
    }

    public Property(CreatePropertyDTO propertyDTO){
        this.name = propertyDTO.getName();
        this.type = propertyDTO.getType();
        this.price = propertyDTO.getPrice();
        this.annualRentalIncomeRate = propertyDTO.getAnnualRentalIncomeRate();
        this.appreciationRate = propertyDTO.getAppreciationRate();
        this.status = PropertyStatus.DRAFT;
        this.fundingDeadline = propertyDTO.getFundingDeadline();

    }
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
        return status.getDescription();
    }
    public void setStatus(String status) {
        this.status = PropertyStatus.valueOf(status);
    }
    public String getFundingDeadline() {
        return fundingDeadline;
    }
    public void setFundingDeadline(String fundingDeadline) {
        this.fundingDeadline = fundingDeadline;
    }
    public List<Investment> getInvestments() {
        return investments;
    }
    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }
    
}
