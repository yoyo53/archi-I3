package certificate.service.certificateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import certificate.service.certificateservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
