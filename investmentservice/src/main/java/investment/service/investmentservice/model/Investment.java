package investment.service.investmentservice.model;

import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Property property;

    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private Double amountInvested;

    @NotNull
    private String investmentDate;

    @NotNull
    private Double sharesOwned;

    @OneToOne
    private Certificat certificat;

    @OneToOne
    private Payment payment;

    // Constructeurs
    public Investment() {
    }

    public Investment(Property property, User user, String investmentDate, Double amountInvested) {
        this.property = property;
        this.user = user;
        this.amountInvested = amountInvested;
        this.investmentDate = investmentDate;
        this.sharesOwned = amountInvested / property.getPrice();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAmountInvested() {
        return amountInvested;
    }

    public void setAmountInvested(Double amountInvested) {
        this.amountInvested = amountInvested;
    }

    public String getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(String investmentDate) {
        this.investmentDate = investmentDate;
    }

    public Double getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(Double sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    public Certificat getCertificat() {
        return certificat;
    }

    public void setCertificat(Certificat certificat) {
        this.certificat = certificat;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
