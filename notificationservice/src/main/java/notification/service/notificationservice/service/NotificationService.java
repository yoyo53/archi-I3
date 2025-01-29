package notification.service.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import notification.service.notificationservice.kafka.KafkaProducer;
import notification.service.notificationservice.model.Investment;
import notification.service.notificationservice.model.Property;
import notification.service.notificationservice.model.User;
import notification.service.notificationservice.repository.InvestmentRepository;
import notification.service.notificationservice.repository.PropertyRepository;
import notification.service.notificationservice.repository.UserRepository;

@Service
public class NotificationService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final InvestmentRepository investmentRepository;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public NotificationService(
            KafkaProducer kafkaProducer,
            ObjectMapper objectMapper, 
            UserRepository userRepository, 
            PropertyRepository propertyRepository,
            InvestmentRepository investmentRepository) {
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.investmentRepository = investmentRepository;
    }

    public User createAgent(@NotNull @Valid User user) {
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public Property createProperty(@NotNull @Valid Property property) {
        Property savedProperty = propertyRepository.save(property);
        return savedProperty;
    }

    public Investment createInvestment(@NotNull @Valid Investment investment, Long propertyId){
        Property property = propertyRepository.findById(propertyId).orElseThrow();
        investment.setProperty(property);
        Investment savedInvestment = investmentRepository.save(investment);
        return savedInvestment;
    }

    public void sendNotification(String subject, ObjectNode payload) {
        ObjectNode message = objectMapper.createObjectNode();
        message.put(EVENT_TYPE, "EmailSend");
        message.put(PAYLOAD, subject);
        message.set(PAYLOAD, payload);
        kafkaProducer.sendMessage(topic, message);
    }
}