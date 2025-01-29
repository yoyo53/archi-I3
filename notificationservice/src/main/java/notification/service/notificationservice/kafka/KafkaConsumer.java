package notification.service.notificationservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
