package wallet.service.walletservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.node.ObjectNode;

import wallet.service.walletservice.service.WalletService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final WalletService walletService;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(WalletService walletService) {
        this.walletService = walletService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.info(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            
            switch (eventType) {
                case "UserCreated":
                    ObjectNode userPayload = (ObjectNode) message.get(PAYLOAD);
                    if (userPayload.get("role").asText().equals("INVESTOR")) {
                        walletService.createWallet(userPayload.get("id").asLong());
                    } 
                    break;
                case "PaymentCreated":
                    ObjectNode paymentPayload = (ObjectNode) message.get(PAYLOAD);
                    Long paymentId = paymentPayload.get("id").asLong();
                    Long userId = paymentPayload.get("userID").asLong();
                    Double amount = paymentPayload.get("amount").asDouble();
                    walletService.processPayment(userId, amount, paymentId);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            logger.error("Error consuming message", e);
        }
    }
}
