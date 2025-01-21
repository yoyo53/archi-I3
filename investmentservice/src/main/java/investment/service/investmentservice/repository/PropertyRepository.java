package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import investment.service.investmentservice.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
}
