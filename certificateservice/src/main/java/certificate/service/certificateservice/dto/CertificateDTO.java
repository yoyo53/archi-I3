package certificate.service.certificateservice.dto;

import jakarta.validation.constraints.NotNull;

public class CertificateDTO {
    
    @NotNull
    private Long investmentId;

    @NotNull
    @jakarta.validation.constraints.Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in the format YYYY-MM-DD")
    private String emissionDate;

    public CertificateDTO() {}

    public Long getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }

    public String getEmissionDate() {
        return emissionDate;
    }

    public void setEmissionDate(String emissionDate) {
        this.emissionDate = emissionDate;
    }
}
