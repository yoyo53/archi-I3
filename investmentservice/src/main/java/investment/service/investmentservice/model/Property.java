package investment.service.investmentservice.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "properties")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

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
    private Long price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "property")
    private List<Investment> investments;

    public Property() {
        this.investments = new ArrayList<>();
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

    public List<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }
}
