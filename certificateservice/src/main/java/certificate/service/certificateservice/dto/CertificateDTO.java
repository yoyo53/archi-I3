package certificate.service.certificateservice.dto;

import jakarta.validation.constraints.NotNull;

public class CertificateDTO {
    
    @NotNull
    private Long investmentId;

    public CertificateDTO() {}

    public Long getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }
}
