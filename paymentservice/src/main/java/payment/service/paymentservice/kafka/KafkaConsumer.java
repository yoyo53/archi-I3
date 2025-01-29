package payment.service.paymentservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import payment.service.paymentservice.model.Payment.PaymentStatus;
import payment.service.paymentservice.service.PaymentService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final PaymentService paymentService;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            switch (eventType) {
                case "InvestmentCreated":
                    ObjectNode investmentPayload = (ObjectNode) message.get(PAYLOAD);
                    paymentService.createPayment(investmentPayload.get("user").get("id").asLong(),
                            investmentPayload.get("amountInvested").asDouble(), 
                            investmentPayload.get("id").asLong());
                    break;
                case "WalletOperationSuccessful":
                    ObjectNode walletSuccessPayload = (ObjectNode) message.get(PAYLOAD);
                    paymentService.updatePaymentStatus(walletSuccessPayload.get("paymentId").asLong(), PaymentStatus.SUCCESS.getDescription());
                    break;
                case "WalletOperationFailed":
                    ObjectNode walletFailedPayload = (ObjectNode) message.get(PAYLOAD);
                    paymentService.updatePaymentStatus(walletFailedPayload.get("paymentId").asLong(), PaymentStatus.FAILED.getDescription());
                    break;
                case "IncomeCreated":
                    ObjectNode incomePayload = (ObjectNode) message.get(PAYLOAD);
                    paymentService.createPayment(incomePayload.get("investment").get("userId").asLong(), 
                            -incomePayload.get("amount").asDouble());
                    break;
                case "TimeEvent":
                    ObjectNode payloadTime = (ObjectNode) message.get(PAYLOAD);
                    if (payloadTime.has("default_date")) {
                        paymentService.setDefaultDate(payloadTime.get("default_date").asText());
                    } else if (payloadTime.has("date")) {
                        paymentService.changeDate(payloadTime.get("date").asText());
                    }
                    break;
                case "InvestmentCancelled":
                    ObjectNode investmentCancelledPayload = (ObjectNode) message.get(PAYLOAD);
                    paymentService.createPayment(investmentCancelledPayload.get("user").get("id").asLong(),
                            -investmentCancelledPayload.get("amountInvested").asDouble(), 
                            investmentCancelledPayload.get("id").asLong());
                    
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            logger.error("Error consuming message", e);
        }
    }
}
