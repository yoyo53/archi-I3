package payment.service.paymentservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import payment.service.paymentservice.model.Payment;
import payment.service.paymentservice.service.PaymentService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(ObjectMapper objectMapper, PaymentService paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "InvestmentCreated":
                    ObjectNode payload = (ObjectNode) message.get(PAYLOAD);
                    paymentService.createPayment(payload.get("user").get("id").asLong(),
                            payload.get("amountInvested").asDouble(), 
                            payload.get("id").asLong());
                    break;

                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    if (payloadTime.has("default_date")) {
                        paymentService.setDefaultDate(payloadTime.get("default_date").asText());
                    } else if (payloadTime.has("date")) {
                        paymentService.changeDate(payloadTime.get("date").asText());
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
