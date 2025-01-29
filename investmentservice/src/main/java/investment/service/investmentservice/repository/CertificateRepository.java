package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import investment.service.investmentservice.model.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
