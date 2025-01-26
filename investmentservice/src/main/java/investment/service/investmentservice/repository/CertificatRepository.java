package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import investment.service.investmentservice.model.Certificat;

@Repository
public interface CertificatRepository extends JpaRepository<Certificat, Long> {
}
