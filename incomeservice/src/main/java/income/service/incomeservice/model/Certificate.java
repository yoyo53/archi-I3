package income.service.incomeservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    private Long id;

    @OneToOne
    @NotNull
    private Investment investment;

    @NotNull
    private String emissionDate;

    public Certificate() {}

    public Certificate(Investment investment, String emissionDate) {
        this.investment = investment;
        this.emissionDate = emissionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
    }

    public String getEmissionDate() {
        return emissionDate;
    }

    public void setEmissionDate(String emissionDate) {
        this.emissionDate = emissionDate;
    }
}