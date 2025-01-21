package certificate.service.certificateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import certificate.service.certificateservice.model.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
