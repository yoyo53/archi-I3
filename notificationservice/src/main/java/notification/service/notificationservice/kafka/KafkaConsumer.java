package notification.service.notificationservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import notification.service.notificationservice.model.Investment;
import notification.service.notificationservice.model.Property;
import notification.service.notificationservice.model.User;
import notification.service.notificationservice.model.User.UserRole;
import notification.service.notificationservice.service.NotificationService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            ObjectNode payload = (ObjectNode) message.get(PAYLOAD);
            switch (eventType) {
                case "UserCreated":
                    User user = objectMapper.convertValue(message.get(PAYLOAD), User.class);
                    if (user.getRole().equals(UserRole.AGENT.getDescription())) {
                        notificationService.createAgent(user);
                    }
                    break;
                case "PropertyCreated":
                    Property property = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    notificationService.createProperty(property);
                    break;
                case "InvestmentCreated":
                    Investment investment = objectMapper.convertValue(message.get(PAYLOAD), Investment.class);
                    Long propertyId = message.get(PAYLOAD).get("property").get("id").asLong();
                    notificationService.createInvestment(investment, propertyId);
                    break;
                case "PaymentSuccessful":
                    notificationService.sendNotification("Payment Successful", payload);
                    break;
                case "PaymentFailed":
                    notificationService.sendNotification("Payment Failed", payload);
                    break;
                case "PropertyFunded":
                    notificationService.sendNotification("Property Funded", payload);
                    break;
                case "CertificateDelivery":
                    notificationService.sendNotification("Certificate Delivery", payload);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }

    }
}
