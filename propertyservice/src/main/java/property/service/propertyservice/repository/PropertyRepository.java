package property.service.propertyservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import property.service.propertyservice.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Iterable<Property> findByStatus(String status);
}
