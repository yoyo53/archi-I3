package property.service.propertyservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import property.service.propertyservice.model.Property;
import property.service.propertyservice.model.Property.PropertyStatus;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Iterable<Property> findByStatus(PropertyStatus status);

    Integer countByStatus(PropertyStatus status);

    @Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.property.id = :propertyId")
    Double sumInvestedAmountById(Long propertyId);

    Iterable<Property> findByStatusAndFundingDeadlineBeforeOrEqual(PropertyStatus status, String fundingDeadline);
    
}
