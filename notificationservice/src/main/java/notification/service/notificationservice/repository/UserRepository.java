package notification.service.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import notification.service.notificationservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
