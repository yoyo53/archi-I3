package database.service.dbservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final UserRepository userRepository;

    public KafkaConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "db-service")
    public void consume(User user) {
        userRepository.save(user);
    }
}
