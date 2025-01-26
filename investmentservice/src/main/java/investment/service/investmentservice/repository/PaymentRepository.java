package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import investment.service.investmentservice.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
