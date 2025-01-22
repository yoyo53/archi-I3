package certificate.service.certificateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import certificate.service.certificateservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}
