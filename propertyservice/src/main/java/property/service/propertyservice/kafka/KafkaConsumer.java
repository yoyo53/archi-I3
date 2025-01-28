package property.service.propertyservice.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import property.service.propertyservice.model.Investment;
import property.service.propertyservice.model.User;
import property.service.propertyservice.model.User.UserRole;
import property.service.propertyservice.service.PropertyService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;
    private final String PAYLOAD = "Payload";

    public KafkaConsumer(PropertyService propertyService, ObjectMapper objectMapper) {
        this.propertyService = propertyService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));
    
        try {
            ObjectNode event = new ObjectMapper().readValue(message.toString(), ObjectNode.class);
            String eventType = event.get("EventType").asText();
    
            switch (eventType) {
                case "UserCreated":
                    User user = objectMapper.convertValue(message.get(PAYLOAD), User.class);
                    if (user.getRole().equals(UserRole.AGENT.getDescription())) {
                        logger.warn("Adding agent in database");
                        propertyService.createAgent(user);
                    }
                    break;
                case "InvestmentCreated":
                    Investment investment = objectMapper.convertValue(message.get(PAYLOAD), Investment.class);
                    Long propertyId = message.get(PAYLOAD).get("property").get("id").asLong();
                    propertyService.createInvestment(investment, propertyId);
                    break;
                case "InvestmentSuccessful":
                    Investment investmentSuccess = objectMapper.convertValue(message.get(PAYLOAD), Investment.class);
                    propertyService.updateInvestmentStatus(investmentSuccess, "SUCCESS");
                    break;
                case "InvestmentFailed":
                    Investment investmentFailed = objectMapper.convertValue(message.get(PAYLOAD), Investment.class);
                    propertyService.updateInvestmentStatus(investmentFailed, "FAILED");
                    break;
    
                default:
                    logger.warn("Unknown event received");
                    break;
            }
        } catch (IOException e) {
            logger.error("Error parsing message", e);
        }
    }

}
