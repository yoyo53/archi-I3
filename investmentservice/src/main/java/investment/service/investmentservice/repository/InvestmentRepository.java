package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import investment.service.investmentservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}
