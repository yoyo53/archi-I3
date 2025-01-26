package investment.service.investmentservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;


@Entity
@Table(name = "payments")
public class Payment {
    private enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }
    
    @Id
    private Long id;

    private Status status;

    @OneToOne(mappedBy = "payment")
    private Investment investment;

    public Payment() {
    }

    public Payment(Long id, Status status, Investment investment) {
        this.id = id;
        this.status = status;
        this.investment = investment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
    }
}