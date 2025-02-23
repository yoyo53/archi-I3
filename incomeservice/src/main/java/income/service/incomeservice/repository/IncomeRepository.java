package income.service.incomeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import income.service.incomeservice.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    Iterable<Income> findByInvestment_UserId(Long userId);
}
