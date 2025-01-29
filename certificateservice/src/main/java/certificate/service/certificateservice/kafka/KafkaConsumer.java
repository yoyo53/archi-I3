package certificate.service.certificateservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import certificate.service.certificateservice.service.CertificateService;
import certificate.service.certificateservice.model.Investment;
import certificate.service.certificateservice.model.Investment.InvestmentStatus;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final CertificateService certificateService;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.info(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "InvestmentCreated":
                    Investment investment = new Investment(
                        message.get(PAYLOAD).get("id").asLong(),
                        message.get(PAYLOAD).get("user").get("id").asLong(),
                        message.get(PAYLOAD).get("property").get("id").asLong(),
                        message.get(PAYLOAD).get("status").asText());
                        certificateService.createInvestment(investment);
                    break;
                case "InvestmentSuccessful":
                    Long investmentSuccessfulId = message.get(PAYLOAD).get("id").asLong();
                    certificateService.updateInvestmentStatus(investmentSuccessfulId, InvestmentStatus.SUCCESS.getDescription());
                    break;
                case "InvestmentFailed":
                    Long investmentFailedId = message.get(PAYLOAD).get("id").asLong();
                    certificateService.updateInvestmentStatus(investmentFailedId, InvestmentStatus.FAILED.getDescription());
                    break;
                case "InvestmentCompleted":
                    Long investmentCompletedId = message.get(PAYLOAD).get("id").asLong();
                    certificateService.updateInvestmentStatus(investmentCompletedId, InvestmentStatus.COMPLETED.getDescription());
                    break;
                case "InvestmentCancelled":
                    Long investmentCancelledId = message.get(PAYLOAD).get("id").asLong();
                    certificateService.updateInvestmentStatus(investmentCancelledId, InvestmentStatus.CANCELLED.getDescription());
                    break;
                
                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    certificateService.changeDate(payloadTime.get("date").asText());
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }

    }
}
