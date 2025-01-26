package property.service.propertyservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import property.service.propertyservice.dto.PropertyDTO;
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
    private final String PROPERTY_CREATED_EVENT = "PropertyCreated";
    private final String PROPEERTY_DELETED_EVENT = "PropertyDeleted";
    private final String PROPERTY_UPDATED_EVENT = "PropertyUpdated";

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.propertyRepository = propertyRepository;
        this.kafkaProducer = kafkaProducer;
        this.userRepository = userRepository;
    }

    public void createProperty (@RequestBody Property property, String userID) throws Exception{
        if(userID == null){
            logger.error("User ID is null");
            throw new Exception("User ID is null");
        }

        User user = userRepository.findById(Long.parseLong(userID)).orElse(null);
        if(user == null || !user.getRole().equals("Agent")){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");
        }

        if(!property.getStatus().equals("DRAFT")){
            logger.error("Property status must be DRAFT when creating a new property");
            throw new Exception("Property status must be DRAFT when creating a new property");
        }

        Property savedProperty = propertyRepository.save(property);
        
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, PROPERTY_CREATED_EVENT);
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", savedProperty.getId());
        payload.put("name", savedProperty.getName());
        payload.put("type", savedProperty.getType());
        payload.put("price", savedProperty.getPrice());
        payload.put("annualRentalIncomeRate", savedProperty.getAnnualRentalIncomeRate());
        payload.put("appreciationRate", savedProperty.getAppreciationRate());
        payload.put("status", savedProperty.getStatus());
        payload.put("fundingDeadline", savedProperty.getFundingDeadline());
        payload.put("fundedAmount", savedProperty.getFundedAmount());
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
    }

    public void deleteProperty(@NotNull @Valid Long id, @NotNull @Valid String userID) throws Exception{
        if(userID == null){
            logger.error("User ID is null");
            throw new Exception("User ID is null");
        }
        User user = userRepository.findById(Long.parseLong(userID)).orElse(null);
        if(user == null || !user.getRole().equals("Agent")){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");

        }
        if(propertyRepository.findById(id).isEmpty()){
            logger.error("Property does not exist");
            throw new Exception("Property does not exist");

        }
        propertyRepository.deleteById(id);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, PROPEERTY_DELETED_EVENT);
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", id);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);

        
    }

    public Iterable<Property> getProperties(){
        return propertyRepository.findAll();
    }
    
    public Property getPropertyById(@NotNull @Valid Long id){
        return propertyRepository.findById(id).get();
    }

    public void updateProperty(@NotNull @Valid Long id, @NotNull @Valid PropertyDTO propertyDTO, @NotNull @Valid String userID) throws Exception{
        if(userID == null){
            logger.error("User ID is null");
            throw new Exception("User ID is null");
        }
        User user = userRepository.findById(Long.parseLong(userID)).orElse(null);
        if(user == null || !user.getRole().equals("Agent")){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");
        }
        if(propertyRepository.findById(id).isEmpty()){
            logger.error("Property does not exist");
            throw new Exception("Property does not exist");
        }

        if(!checkMaximumOpenProperties()){
            logger.error("Maximum number of open properties reached");
            throw new Exception("Maximum number of open properties reached");
        }

        if(!checkPropertyStatus(propertyDTO.getStatus())){
            logger.error("Invalid property status");
            throw new Exception("Invalid property status, must be DRAFT, OPENED, FUNDED or CLOSED");
        }


        Property oldProperty = propertyRepository.findById(id).orElse(null);
        oldProperty.setStatus(propertyDTO.getStatus());
        propertyRepository.save(oldProperty);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, PROPERTY_UPDATED_EVENT);
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", oldProperty.getId());
        payload.put("status", oldProperty.getStatus());
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
    }

    public Iterable<Property> getOpenProperties(){
        return propertyRepository.findByStatus("OPEN");
    }

    private boolean checkMaximumOpenProperties(){
        int count = 0;
        for(Property property : propertyRepository.findAll()){
            if(property.getStatus().equals("OPEN")){
                count++;
            }
        }
        if(count >= 6){
            return false;
        }
        return true;
    }

    private boolean checkPropertyStatus(String status){
        if(status.equals("DRAFT") || status.equals("OPEN") || status.equals("CLOSED")){
            return true;
        }
        return false;
    }
        

    public void addAgent(Long userID){
        User user = new User(userID, "Agent");
        userRepository.save(user);
    }


}
