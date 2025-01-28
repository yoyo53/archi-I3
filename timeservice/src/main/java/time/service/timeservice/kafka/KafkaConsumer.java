package time.service.timeservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Model
// import time.service.timeservice.model.User;
// import time.service.timeservice.model.Property;
// import time.service.timeservice.model.Payment;
// import time.service.timeservice.model.Certificat;

// Service
import time.service.timeservice.service.timeService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ObjectMapper objectMapper; // = new ObjectMapper();
    private final timeService timeService; // = new timeService();

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(ObjectMapper objectMapper, timeService timeService) {
        this.objectMapper = objectMapper;
        this.timeService = timeService;
    }
    

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("Consuming message: %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "UserCreated":
                    User user = objectMapper.convertValue(message.get(PAYLOAD), User.class);
                    User usercreated = timeService.createUser(user);
                    System.out.println(usercreated);
                    break;
                
                case "PropertyCreated":
                    Property property = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    Property propertycreated = timeService.createProperty(property);
                    System.out.println(propertycreated);
                    break;

                case "PropertyUpdated":
                    Property propertyUpdated = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    Property propertyupdatednew = timeService.updatePropertyStatus(propertyUpdated);
                    System.out.println(propertyupdatednew);
                    break;

                case "PaymentCreated":
                    Payment payment = objectMapper.convertValue(message.get(PAYLOAD), Payment.class);
                    Payment paymentcreated = timeService.createPayment(payment);
                    System.out.println(paymentcreated);
                    break;
                
                case "CertificatCreated":
                    Certificat certificat = objectMapper.convertValue(message.get(PAYLOAD), Certificat.class);
                    Certificat certificatcreated = timeService.createCertificat(certificat);
                    System.out.println(certificatcreated);
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }

    }
}
