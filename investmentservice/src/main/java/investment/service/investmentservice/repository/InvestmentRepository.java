package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

import investment.service.investmentservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
@Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.propertyID = :propertyID")
BigDecimal findTotalInvestedByPropertyID(@Param("propertyID") Long propertyID);
}
