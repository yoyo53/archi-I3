package investment.service.investmentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import investment.service.investmentservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
