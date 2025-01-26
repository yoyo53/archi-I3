package property.service.propertyservice.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import property.service.propertyservice.service.PropertyService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final PropertyService propertyService;

    public KafkaConsumer(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message));
    
        try {
            // Parse le message JSON en ObjectNode
            ObjectNode event = new ObjectMapper().readValue(message, ObjectNode.class);
    
            // Loggez le contenu complet de l'objet event
            logger.warn("Event: {}", event);
    
            String eventType = event.get("EventType").asText();
    
            switch (eventType) {
                case "UserCreated":
                    logger.warn("User created event received");
                    JsonNode payload = event.get("Payload");
    
                    // Loggez le contenu de payload
                    logger.warn("Payload: {}", payload);
    
                    if (payload != null && payload.hasNonNull("role") && payload.hasNonNull("id")) {
                        String role = payload.get("role").asText();
                        Long userIdCreated = payload.get("id").asLong();
    
                        if ("Agent".equals(role)) {
                            logger.warn("Adding agent in database");
                            propertyService.addAgent(userIdCreated);
                        }
                    } else {
                        logger.warn("Invalid payload: missing required fields 'role' or 'id'");
                    }
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
