package user.service.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import user.service.userservice.kafka.KafkaProducer;
import user.service.userservice.model.User;
import user.service.userservice.repository.UserRepository;

@Service
public class UserService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";
    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public String createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "User already exists";
        } else if (!user.getRole().equals("Investor") && !user.getRole().equals("Agent")) {
            return "Role must be either Investor or Agent";
        } else {
            User savedUser = userRepository.save(user);
            

            //Json Object
            ObjectNode event = new ObjectMapper().createObjectNode();
            event.put(EVENT_TYPE, "UserCreated");
            ObjectNode payload = new ObjectMapper().createObjectNode();
            payload.put("id", savedUser.getId());
            event.put(PAYLOAD, EVENT_TYPE);

            kafkaProducer.sendMessage(topic, event);
            return "User created successfully";
        }
    }

    public String loginUser(User user) {
        if (userRepository.existsById(user.getId())) {
            User userFromDb = userRepository.findById(user.getId()).get();
            if (userFromDb.getPassword().equals(user.getPassword())) {
                return "Login successful" + userFromDb.getId();
            } else {
                return "Invalid password";
            }
        } else {
            return "User does not exist";
        }
    }
}