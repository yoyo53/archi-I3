package investment.service.investmentservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "payments")
public class Payment {
    public enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }
    
    @Id
    private Long id;

    private Status status;

    public Payment() {
    }

    public Payment(Long id, Status status) {
        this.id = id;
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
}