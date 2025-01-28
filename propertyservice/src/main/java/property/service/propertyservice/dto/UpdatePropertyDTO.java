package property.service.propertyservice.dto;

import jakarta.validation.constraints.NotNull;

public class UpdatePropertyDTO {
    
    @NotNull
    private String status;

    public UpdatePropertyDTO() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}