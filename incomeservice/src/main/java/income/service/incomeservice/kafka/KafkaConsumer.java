package income.service.incomeservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import income.service.incomeservice.model.Certificate;
import income.service.incomeservice.model.Investment;
import income.service.incomeservice.model.Property;
import income.service.incomeservice.service.IncomeService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ObjectMapper objectMapper; // = new ObjectMapper();
    private final IncomeService incomeService;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(IncomeService incomeService, ObjectMapper objectMapper) {
        this.incomeService = incomeService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "PropertyCreated":
                    Property property = objectMapper.convertValue(message.get(PAYLOAD), Property.class);
                    incomeService.createProperty(property);
                    break;
                case "InvestmentCompleted":
                    ObjectNode investmentPaylaod = (ObjectNode) message.get("payload");

                    Long propertyId = investmentPaylaod.get("property").get("id").asLong();
                    Long userId = investmentPaylaod.get("user").get("id").asLong();
                    Double amountInvested = investmentPaylaod.get("amountInvested").asDouble();
                    Double sharesOwned = investmentPaylaod.get("sharesOwned").asDouble();
                    
                    incomeService.createInvestment(propertyId, userId, amountInvested, sharesOwned);
                    break;
                case "CertificateCreated":
                    Certificate certificate = objectMapper.convertValue(message.get(PAYLOAD), Certificate.class);
                    Long investmentId = message.get(PAYLOAD).get("investment").get("id").asLong();
                    incomeService.createCertificate(certificate, investmentId);
                    break;
                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    if (payloadTime.has("default_date")) {
                        incomeService.setDefaultDate(payloadTime.get("default_date").asText());
                    } else if (payloadTime.has("date")) {
                        incomeService.changeDate(payloadTime.get("date").asText());
                    }
                default:
                    logger.warn("Unknown event received");
                    break;
            }
        }
        catch (Exception e) {
            logger.error("Error parsing message", e);
        }
    }
}
