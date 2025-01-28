package property.service.propertyservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import property.service.propertyservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}

