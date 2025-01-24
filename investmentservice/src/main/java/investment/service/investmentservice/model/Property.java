package investment.service.investmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    private Long id;

    private Long price;

    private Long[] investors; // List of investors if there are Long

    public Property() {
    }

    public Property(Long id, Long price) {
        this.id = id;
        this.price = price;
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

    public Long[] getInvestors() {
        return investors;
    }

    public void setInvestors(Long[] investors) {
        this.investors = investors;
    }
}
