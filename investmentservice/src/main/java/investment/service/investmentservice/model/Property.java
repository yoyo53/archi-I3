package investment.service.investmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "properties")
public class Property {
    public enum Status {
        DRAFT("Draft"),
        OPENED("Opened"),
        FUNDED("Funded"),
        CLOSED("Closed");

        private final String status;

        Status(String displayName) {
            this.status = displayName;
        }

        public String getStatus() {
            return status;
        }
    }

    @Id
    private Long id;

    private Long price;

    private Status status;

    @OneToMany(mappedBy = "property")
    private Investment[] investments;

    public Property() {
    }

    public Property(Long id, Long price, Status status, Long[] investors) {
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
        return status.getStatus();
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public Investment[] getInvestments() {
        return investments;
    }

    public void setInvestments(Investment[] investments) {
        this.investments = investments;
    }
}
