package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.model.Investment.InvestmentStatus;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    @Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.property.id = :propertyID AND (i.status = 'PENDING' OR i.status = 'SUCCESS')")
    Optional<BigDecimal> sumAmountInvestedByInvestment_PropertyId(@Param("propertyID") Long propertyID);

    Iterable<Investment> findByUser_id(Long userId);

    @Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.investmentDate > :date AND i.user.id = :userId AND (i.status = 'PENDING' OR i.status = 'SUCCESS')")
    Optional<BigDecimal> sumAmountInvestedByDateAfterInvestment_UserId(@Param("date") String date, @Param("userId") Long userId);

    Optional<Investment> findByPayment_id(Long paymentId);

    Optional<Iterable<Investment>> findByStatusAndProperty_id(InvestmentStatus status, Long propertyId);

    Iterable<Investment> findByInvestmentDateBeforeAndStatus(String date, InvestmentStatus status);
}
