package investment.service.investmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "users")
public class User {

    public enum UserRole {
        AGENT("AGENT"),
        INVESTOR("INVESTOR");
    
        private final String description;
    
        UserRole(String description) {
            this.description = description;
        }
    
        public String getDescription() {
            return description;
        }
    }

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

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
        return role.getDescription();
    }

    public void setRole(String role) {
        this.role = UserRole.valueOf(role);
    }

    public Investment[] getInvestments() {
        return investments;
    }

    public void setInvestments(Investment[] investments) {
        this.investments = investments;
    }
}
