package income.service.incomeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import income.service.incomeservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    Iterable<Investment> findByCertificate_EmissionDateBefore(String date);
}
