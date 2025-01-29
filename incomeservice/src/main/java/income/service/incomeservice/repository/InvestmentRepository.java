package income.service.incomeservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import income.service.incomeservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    @Query("SELECT i FROM Investment i WHERE i.certificate.emissionDate < :paramDate")
    Optional<Iterable<Investment>> findByCertificate_EmissionDateBefore(@Param("paramDate") String date);
}
