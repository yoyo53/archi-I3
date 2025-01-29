package property.service.propertyservice.dto;

import jakarta.validation.constraints.NotNull;

public class CreatePropertyDTO {

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
    private String fundingDeadline;

    public CreatePropertyDTO() {}

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

    public String getFundingDeadline() {
        return fundingDeadline;
    }

    public void setFundingDeadline(String fundingDeadline) {
        this.fundingDeadline = fundingDeadline;
    }


}
