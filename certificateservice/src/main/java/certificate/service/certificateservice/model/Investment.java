package certificate.service.certificateservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "investments")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long propertyId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvestmentStatus status;

    public Investment() {}

    public Investment(Long id, Long userId, Long propertyId, String status) {
        this.id = id;
        this.userId = userId;
        this.propertyId = propertyId;
        this.status = InvestmentStatus.valueOf(status);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getStatus() {
        return status.getDescription();
    }

    public void setStatus(String status) {
        this.status = InvestmentStatus.valueOf(status);
    }
}
