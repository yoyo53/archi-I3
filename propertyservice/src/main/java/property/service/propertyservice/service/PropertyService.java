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
        if(userID == null){
            logger.error("User ID is null");
            return null;
        }
        User user = userRepository.findById(Long.parseLong(userID)).get();
        if(user == null || !user.getRole().equals("Agent")){
            logger.error("User does not exist or is not an agent");
            return null;
        }
        Property savedProperty = propertyRepository.save(property);

        return savedProperty;
    }

    public void deleteProperty(Long id, String userID){
        if(userID == null){
            logger.error("User ID is null");
            return;
        }
        User user = userRepository.findById(Long.parseLong(userID)).get();
        if(user == null || !user.getRole().equals("Agent")){
            logger.error("User does not exist or is not an agent");
            return;
        }
        if(propertyRepository.findById(id).isEmpty()){
            logger.error("Property does not exist");
            return;
        }
        propertyRepository.deleteById(id);
    }

    public Iterable<Property> getProperties(){
        return propertyRepository.findAll();
    }
    
    public Property getPropertyById(Long id){
        return propertyRepository.findById(id).get();
    }

    public void updateProperty(Long id, Property updatedProperty, String userID){
        if(userID == null){
            logger.error("User ID is null");
            return;
        }
        User user = userRepository.findById(Long.parseLong(userID)).get();
        if(user == null || !user.getRole().equals("Agent")){
            logger.error("User does not exist or is not an agent");
            return;
        }
        if(propertyRepository.findById(id).isEmpty()){
            logger.error("Property does not exist");
            return;
        }
        //TODO Check if every field from updatedProperty is valid
        Property oldProperty = propertyRepository.findById(id).get();
        oldProperty.setName(updatedProperty.getName());
        oldProperty.setType(updatedProperty.getType());
        oldProperty.setPrice(updatedProperty.getPrice());
        oldProperty.setAnnualRentalIncomeRate(updatedProperty.getAnnualRentalIncomeRate());
        oldProperty.setAppreciationRate(updatedProperty.getAppreciationRate());
        oldProperty.setStatus(updatedProperty.getStatus());
        oldProperty.setFundingDeadline(updatedProperty.getFundingDeadline());
        oldProperty.setFundedAmount(updatedProperty.getFundedAmount());
        oldProperty.setInvestors(updatedProperty.getInvestors());
        propertyRepository.save(oldProperty);
    }
        

    public void addAgent(Long userID){
        User user = new User(userID, "Agent");
        userRepository.save(user);
    }


}
