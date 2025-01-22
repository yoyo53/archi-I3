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
            ObjectNode event = new ObjectMapper().readValue(message, ObjectNode.class);
            String eventType = event.get("EventType").asText();
            
            switch (eventType) {
                case "UserCreated":
                    logger.info("User created event received");
                    JsonNode payload = event.get("payload");
                    String role = payload.get("role").asText();
                    Long userIdCreated = payload.get("id").asLong();

                    if (role.equals("Agent")) {
                        logger.info("Adding agent in database for");
                        
                        propertyService.addAgent(userIdCreated);
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
