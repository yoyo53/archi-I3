package certificate.service.certificateservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import certificate.service.certificateservice.service.CertificateService;
import certificate.service.certificateservice.model.Investment;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final CertificateService certificateService;
    private final ObjectMapper objectMapper;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(CertificateService certificateService, ObjectMapper objectMapper) {
        this.certificateService = certificateService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("Consuming message: %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "InvestmentCreated":
                    ObjectNode payload = (ObjectNode) message.get(PAYLOAD);
                    Investment investment = new Investment(
                            payload.get("id").asLong(),
                            payload.get("user").get("id").asLong(),
                            payload.get("property").get("id").asLong());
                    certificateService.createInvestment(investment);
                    break;
                
                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    if (payloadTime.has("default_date")) {
                        certificateService.setDefaultDate(payloadTime.get("default_date").asText());
                    } else if (payloadTime.has("date")) {
                        certificateService.changeDate(payloadTime.get("date").asText());
                    }
                    break;

                case "InvestmentFund":
                    ObjectNode payloadFund = (ObjectNode) message.get(PAYLOAD);
                    certificateService.InvestmentFulfilled(payloadFund.get("id").asLong());

                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }

    }
}
