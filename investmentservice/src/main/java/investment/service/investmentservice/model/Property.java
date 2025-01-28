package investment.service.investmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;

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

    private Long price;

    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @OneToMany(mappedBy = "property")
    private Investment[] investments;

    public Property() {
    }

    public Property(Long id, Long price, PropertyStatus status, Long[] investors) {
        this.id = id;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getStatus() {
        return status.getDescription();
    }

    public void setStatus(String status) {
        this.status = PropertyStatus.valueOf(status);
    }

    public Investment[] getInvestments() {
        return investments;
    }

    public void setInvestments(Investment[] investments) {
        this.investments = investments;
    }
}
