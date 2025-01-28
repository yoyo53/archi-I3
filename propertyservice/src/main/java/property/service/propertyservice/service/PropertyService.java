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
import property.service.propertyservice.model.Investment;
import property.service.propertyservice.model.Property;
import property.service.propertyservice.model.User;
import property.service.propertyservice.model.User.UserRole;
import property.service.propertyservice.model.Property.PropertyStatus;
import property.service.propertyservice.repository.InvestmentRepository;
import property.service.propertyservice.repository.PropertyRepository;
import property.service.propertyservice.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PropertyService {
    @Value("${spring.kafka.topic}")
    private String topic;

    private final PropertyRepository propertyRepository;
    private final InvestmentRepository investmentRepository;
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";
    private final String PROPERTY_CREATED_EVENT = "PropertyCreated";
    private final String PROPEERTY_DELETED_EVENT = "PropertyDeleted";
    private final String PROPERTY_UPDATED_EVENT = "PropertyUpdated";

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, KafkaProducer kafkaProducer, InvestmentRepository investmentRepository){
        this.propertyRepository = propertyRepository;
        this.kafkaProducer = kafkaProducer;
        this.userRepository = userRepository;
        this.investmentRepository = investmentRepository;
    }

    public Property createProperty (@RequestBody @NotNull @Valid Property property, @NotNull @Valid Long userID) throws Exception{
        User user = userRepository.findById(userID).orElse(null);
        if(user == null || !user.getRole().equals(UserRole.AGENT.getDescription())){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");
        }

        if(!property.getStatus().equals(PropertyStatus.DRAFT.getDescription())){
            logger.error("Property status must be DRAFT when creating a new property");
            throw new Exception("Property status must be DRAFT when creating a new property");
        }

        Property savedProperty = propertyRepository.save(property);
        
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, PROPERTY_CREATED_EVENT);
        ObjectNode payload = new ObjectMapper().convertValue(savedProperty, ObjectNode.class);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);

        return savedProperty;
    }

    public Long deleteProperty(@NotNull @Valid Long id, @NotNull @Valid Long userID) throws Exception{
        User user = userRepository.findById(userID).orElse(null);
        if(user == null || !user.getRole().equals(UserRole.AGENT.getDescription())){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");

        }
        if(propertyRepository.findById(id).isEmpty()){
            logger.error("Property does not exist");
            return null;

        }
        propertyRepository.deleteById(id);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, PROPEERTY_DELETED_EVENT);
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", id);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);

        return id;
        
    }

    public Iterable<Property> getProperties(){
        return propertyRepository.findAll();
    }
    
    public Property getPropertyById(@NotNull @Valid Long id){
        return propertyRepository.findById(id).get();
    }

    public Property updateProperty(@NotNull @Valid Long id, @NotNull @Valid PropertyDTO propertyDTO, @NotNull @Valid Long userID) throws Exception{
        User user = userRepository.findById(userID).orElse(null);
        if(user == null || !user.getRole().equals(UserRole.AGENT.getDescription())){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");
        }

        Property oldProperty = propertyRepository.findById(id).orElse(null);
        
        if(oldProperty == null){
            logger.error("Property does not exist");
            return null;
        }

        if(!checkMaximumOpenProperties() && propertyDTO.getStatus().equals(PropertyStatus.OPENED.getDescription())){
            logger.error("Maximum number of open properties reached");
            throw new Exception("Maximum number of open properties reached");
        }


       
        oldProperty.setStatus(propertyDTO.getStatus());
        Property newProperty = propertyRepository.save(oldProperty);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, PROPERTY_UPDATED_EVENT);
        ObjectNode payload = new ObjectMapper().convertValue(newProperty, ObjectNode.class);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);

        return newProperty;
    }

    public Iterable<Property> getOpenProperties(){
        return propertyRepository.findByStatus(PropertyStatus.OPENED);
    }

    private boolean checkMaximumOpenProperties(){
        return propertyRepository.countByStatus(PropertyStatus.OPENED) <= 5;
    }
        

    public User createAgent(@NotNull @Valid User user){
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public Investment createInvestment(@NotNull @Valid Investment investment){
        Investment savedInvestment = investmentRepository.save(investment);
        return savedInvestment;
    }

}
