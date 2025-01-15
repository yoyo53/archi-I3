package user.service.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic}")
    private String topic;

    public UserController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        kafkaProducer.sendMessage(topic, userDTO);
        return ResponseEntity.ok("User sent to Kafka successfully!");
    }
}
