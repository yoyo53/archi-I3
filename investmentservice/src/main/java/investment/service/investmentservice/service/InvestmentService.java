package investment.service.investmentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;

import investment.service.investmentservice.kafka.KafkaProducer;

// Investment
import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.repository.InvestmentRepository;

// User
import investment.service.investmentservice.model.User;
import investment.service.investmentservice.repository.UserRepository;

// Property
import investment.service.investmentservice.model.Property;
import investment.service.investmentservice.repository.PropertyRepository;

@Service
public class InvestmentService {

    @Value("${spring.kafka.topic}")
    private String topic;

    // Repository
    private final InvestmentRepository investmentRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;


    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";
    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository, UserRepository userRepository, PropertyRepository propertyRepository, KafkaProducer kafkaProducer) {
        this.investmentRepository = investmentRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.kafkaProducer = kafkaProducer;
    }

    // Investment
    public String createInvestment(Investment investment) {
        Investment savedInvestment = investmentRepository.save(investment);

        //Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "InvestmentCreated");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", savedInvestment.getId());
        event.put(PAYLOAD, payload);
        // ObjectNode objectNode = new ObjectMapper().createObjectNode();

        // kafkaProducer.sendMessage(topic, objectNode);
        kafkaProducer.sendMessage(topic, event);
        return "Investment created successfully";
    }


    // User
    public String createUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return "User already exists";
        } else if (!user.getRole().equals("Investor")) {
            return "Role must be either Investor";
        } else {
            userRepository.save(user);
            return "User created successfully";
        }
    }

    // Property
    public String createProperty(Property property) {
        if (propertyRepository.existsById(property.getId())) {
            return "Property already exists";
        } else {
            propertyRepository.save(property);

            return "Property created successfully";
        }
    }

    // public String createUser(User user) {
    //     if (userRepository.existsById(user.getId())) {
    //         return "User already exists";
    //     } else if (!user.getRole().equals("Investor") && !user.getRole().equals("Agent")) {
    //         return "Role must be either Investor or Agent";
    //     } else {
    //         userRepository.save(user);
    //         //Json Object
    //         ObjectNode objectNode = new ObjectMapper().createObjectNode();

    //         kafkaProducer.sendMessage("user-topic", "User created: " + user.getId());
    //         return "User created successfully";
    //     }
    // }

    // public String loginUser(User user) {
    //     if (userRepository.existsById(user.getId())) {
    //         User userFromDb = userRepository.findById(user.getId()).get();
    //         if (userFromDb.getPassword().equals(user.getPassword())) {
    //             return "Login successful" + userFromDb.getId();
    //         } else {
    //             return "Invalid password";
    //         }
    //     } else {
    //         return "User does not exist";
    //     }
    // }
}