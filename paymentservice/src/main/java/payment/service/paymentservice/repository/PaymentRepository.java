package payment.service.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import payment.service.paymentservice.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
