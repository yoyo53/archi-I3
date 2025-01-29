package property.service.propertyservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import property.service.propertyservice.dto.CreatePropertyDTO;
import property.service.propertyservice.dto.UpdatePropertyDTO;
import property.service.propertyservice.kafka.KafkaProducer;
import property.service.propertyservice.model.Investment;
import property.service.propertyservice.model.Investment.InvestmentStatus;
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

    private LocalDate systemDate;

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, KafkaProducer kafkaProducer, InvestmentRepository investmentRepository){
        this.propertyRepository = propertyRepository;
        this.kafkaProducer = kafkaProducer;
        this.userRepository = userRepository;
        this.investmentRepository = investmentRepository;
        this.systemDate = null;
    }

    public Property createProperty (@RequestBody @NotNull @Valid CreatePropertyDTO propertyDTO, @NotNull @Valid Long userID) throws Exception{
        User user = userRepository.findById(userID).orElse(null);
        if(user == null || !user.getRole().equals(UserRole.AGENT.getDescription())){
            logger.error("User does not exist or is not an agent");
            throw new Exception("User does not exist or is not an agent");
        }

        Property property = new Property(propertyDTO);
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

    public Property updateProperty(@NotNull @Valid Long id, @NotNull @Valid UpdatePropertyDTO propertyDTO, @NotNull @Valid Long userID) throws Exception{
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

    public Investment updateInvestmentStatus(@NotNull @Valid Investment investment, Long propertyId){
        Investment updatedInvestment = investmentRepository.save(investment);

        if (updatedInvestment.getStatus().equals(InvestmentStatus.SUCCESS.getDescription())) {
            Property property = propertyRepository.findById(propertyId).orElseThrow();
            Double totalInvested = propertyRepository.sumInvestedAmountById(propertyId);
            if(totalInvested >= property.getPrice()){
                property.setStatus(PropertyStatus.FUNDED.getDescription());
                Property updatedProperty =  propertyRepository.save(property);

                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "PropertyFunded");
                ObjectNode payload = new ObjectMapper().convertValue(updatedProperty, ObjectNode.class);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
        }
        return updatedInvestment;
    }

    public Investment createInvestment(@NotNull @Valid Investment investment, Long propertyId){
        Property property = propertyRepository.findById(propertyId).orElseThrow();
        investment.setProperty(property);
        Investment savedInvestment = investmentRepository.save(investment);
        return savedInvestment;
    }

    public void setDefaultDate(String defaultDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(defaultDate, formatter);
        this.systemDate = date;
    }

    public void changeDate(String date) {
        // Add logic when date changed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = LocalDate.parse(date, formatter);
        this.systemDate = newDate;
    }

}
