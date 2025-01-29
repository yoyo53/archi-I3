package notification.service.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import notification.service.notificationservice.model.Investment;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}

