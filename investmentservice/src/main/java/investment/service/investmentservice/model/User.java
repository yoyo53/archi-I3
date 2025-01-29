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
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Investment> investments;

    public User() {
        this.investments = new ArrayList<>();
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

    public List<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }
}
