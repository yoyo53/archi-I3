package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

import investment.service.investmentservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    // Requête corrigée pour utiliser property.id
    @Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.property.id = :propertyID AND (i.status = 'PENDING' OR i.status = 'SUCCESS')")
    BigDecimal findTotalInvestedByInvestment_PropertyId(@Param("propertyID") Long propertyID);

    // Méthode standard pour trouver les investissements par utilisateur
    Iterable<Investment> findByUser_id(Long userId);

    // Requête corrigée pour utiliser user.id
    @Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.user.id = :userId")
    BigDecimal findInvestmentTotalByInvestment_UserId(@Param("userId") Long userId);

    Optional<Investment> findByPayment_id(Long paymentId);

    Optional<Iterable<Investment>> findByProperty_id(Long propertyId);
}
