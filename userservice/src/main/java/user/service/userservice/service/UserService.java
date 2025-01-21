package user.service.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import user.service.userservice.kafka.KafkaProducer;
import user.service.userservice.model.User;
import user.service.userservice.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public String createUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return "User already exists";
        } else if (!user.getRole().equals("Investor") && !user.getRole().equals("Agent")) {
            return "Role must be either Investor or Agent";
        } else {
            userRepository.save(user);
            //Json Object
            ObjectNode objectNode = new ObjectMapper().createObjectNode();

            kafkaProducer.sendMessage("user-topic", "User created: " + user.getId());
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