package property.service.propertyservice.dto;

import jakarta.validation.constraints.NotNull;

public class PropertyDTO {
    
    @NotNull
    private String status;

    public PropertyDTO() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}