package income.service.incomeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import income.service.incomeservice.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
}
