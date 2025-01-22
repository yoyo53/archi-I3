package property.service.propertyservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import property.service.propertyservice.kafka.KafkaProducer;
import property.service.propertyservice.model.Property;
import property.service.propertyservice.model.User;
import property.service.propertyservice.repository.PropertyRepository;
import property.service.propertyservice.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PropertyService {
    @Value("${spring.kafka.topic}")
    private String topic;

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.propertyRepository = propertyRepository;
        this.kafkaProducer = kafkaProducer;
        this.userRepository = userRepository;
    }

    public Property createProperty (@RequestBody Property property, String userID){
        //TODO check if user is agent before creating property
        Property savedProperty = propertyRepository.save(property);

        return savedProperty;
    }

    public void addAgent(Long userID){
        User user = new User(userID, "Agent");
        userRepository.save(user);
    }


}
