package database.service.dbservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final UserRepository userRepository;

    public KafkaConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "db-service")
    public void consume(User user) {
        //logger.warn("Consumed message: " + message);

        /*try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(message, User.class);
            logger.warn("User: " + user);
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error parsing message: " + message, e);
        }*/

        userRepository.save(user);

    }
}
