package investment.service.investmentservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Model
import investment.service.investmentservice.model.User;
import investment.service.investmentservice.model.Property;
import investment.service.investmentservice.model.Payment;
import investment.service.investmentservice.model.Certificat;

// Service
import investment.service.investmentservice.service.InvestmentService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ObjectMapper objectMapper; // = new ObjectMapper();
    private final InvestmentService investmentService; // = new InvestmentService();

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(ObjectMapper objectMapper, InvestmentService investmentService) {
        this.objectMapper = objectMapper;
        this.investmentService = investmentService;
    }
    

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("Consuming message: %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "UserCreated":
                    User user = objectMapper.convertValue(message.get(PAYLOAD), User.class);
                    investmentService.createUser(user);
                    break;
                
                case "PropertyCreated":
                    Property property = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    investmentService.createProperty(property);
                    break;

                case "PaymentCreated":
                    Payment payment = objectMapper.convertValue(message.get(PAYLOAD), Payment.class);
                    Long InvestmentId = message.get(PAYLOAD).get("InvestmentID").asLong();
                    investmentService.createPayment(payment, InvestmentId);
                    break;
                
                case "CertificatCreated":
                    Certificat certificat = objectMapper.convertValue(message.get(PAYLOAD), Certificat.class);
                    investmentService.createCertificat(certificat);
                    break;

                case "PaymentSuccessful":
                case "PaymentFailed":
                    Payment paymentUpdated = objectMapper.convertValue(message.get(PAYLOAD), Payment.class);
                    investmentService.updatePaymentStatus(paymentUpdated);
                    break;
                case "PropertyUpdated":
                case "PropertyFunded":
                    Property propertyUpdated = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    investmentService.updatePropertyStatus(propertyUpdated);
                    break;
                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    if (payloadTime.has("default_date")) {
                        investmentService.setDefaultDate(payloadTime.get("default_date").asText());
                    } else if (payloadTime.has("date")) {
                        investmentService.changeDate(payloadTime.get("date").asText());
                    }
                    break;
                case "PropertyClosed":
                    Property propertyClosed = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    investmentService.updatePropertyStatus(propertyClosed);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }

    }
}
