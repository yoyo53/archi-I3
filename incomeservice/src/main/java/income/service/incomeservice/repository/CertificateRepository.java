package income.service.incomeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import income.service.incomeservice.model.Certificate;
import income.service.incomeservice.model.Investment;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
