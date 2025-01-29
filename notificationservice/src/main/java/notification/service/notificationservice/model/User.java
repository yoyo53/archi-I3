package notification.service.notificationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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
    @NotNull
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User() {}

    public User(Long id, String role) {
        this.id = id;
        this.role = UserRole.valueOf(role);
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

}
