package investment.service.investmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String role;

    @OneToMany(mappedBy = "user")
    private Investment[] investments;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Investment[] getInvestments() {
        return investments;
    }

    public void setInvestments(Investment[] investments) {
        this.investments = investments;
    }
}
