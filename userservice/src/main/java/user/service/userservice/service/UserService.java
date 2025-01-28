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
    private final String USER_ID = "id";

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public String createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "User already exists";
        }
    
        if (!user.getRole().equals(UserRole.INVESTOR.getDescription()) && !user.getRole().equals(UserRole.AGENT.getDescription())) {
            return "Role must be either Investor or Agent";
        }

        User savedUser = userRepository.save(user);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, USER_CREATED_EVENT);
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put(USER_ID, savedUser.getId());
        payload.put("role", savedUser.getRole());
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return "User created successfully";
    }

    public String loginUser(User user) {
        if (userRepository.existsByEmailAndPassword(user.getEmail(), user.getPassword()) != null) {
            User userFromDb = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
            return "Login successful : " + userFromDb.getId();
        } else {
            return "User does not exist";
        }
    }
}