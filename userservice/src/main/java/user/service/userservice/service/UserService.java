package user.service.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import user.service.userservice.kafka.KafkaProducer;
import user.service.userservice.model.User;
import user.service.userservice.model.User.UserRole;
import user.service.userservice.repository.UserRepository;

@Service
public class UserService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    private final String USER_CREATED_EVENT = "UserCreated";
    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        User savedUser = userRepository.save(user);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, USER_CREATED_EVENT);
        ObjectNode payload = new ObjectMapper().convertValue(savedUser, ObjectNode.class);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return savedUser;
    }

    public Long loginUser(User user) {
        User savedUser = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).orElseThrow();
        return savedUser.getId();
    }
}