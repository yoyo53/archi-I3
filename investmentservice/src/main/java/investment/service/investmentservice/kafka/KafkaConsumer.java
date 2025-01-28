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
                    User usercreated = investmentService.createUser(user);
                    System.out.println(usercreated);
                    break;
                
                case "PropertyCreated":
                    Property property = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    Property propertycreated = investmentService.createProperty(property);
                    System.out.println(propertycreated);
                    break;

                case "PropertyUpdated":
                    Property propertyUpdated = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    Property propertyupdatednew = investmentService.updatePropertyStatus(propertyUpdated);
                    System.out.println(propertyupdatednew);
                    break;

                case "PaymentCreated":
                    Payment payment = objectMapper.convertValue(message.get(PAYLOAD), Payment.class);
                    Payment paymentcreated = investmentService.createPayment(payment);
                    System.out.println(paymentcreated);
                    break;
                
                case "CertificatCreated":
                    Certificat certificat = objectMapper.convertValue(message.get(PAYLOAD), Certificat.class);
                    Certificat certificatcreated = investmentService.createCertificat(certificat);
                    System.out.println(certificatcreated);
                    break;

                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    if (payloadTime.has("default_date")) {
                        investmentService.setDefaultDate(payloadTime.get("default_date").asText());
                    } else if (payloadTime.has("date")) {
                        investmentService.changeDate(payloadTime.get("date").asText());
                    }
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }

    }
}
