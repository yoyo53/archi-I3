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
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            
            switch (eventType) {
                case "UserCreated":
                    logger.info("Wallet created event received");
                    ObjectNode userPayload = (ObjectNode) message.get(PAYLOAD);
                    if (userPayload.get("role").asText().equals("INVESTOR")) {
                        logger.info("Creating wallet for investor");
                        walletService.createWallet(userPayload.get("id").asLong());
                    } 
                    break;
                case "PaymentCreated":
                    logger.info("Payment processed event received");
                    ObjectNode paymentPayload = (ObjectNode) message.get(PAYLOAD);
                    Long paymentId = paymentPayload.get("id").asLong();
                    Long userId = paymentPayload.get("userID").asLong();
                    Double amount = paymentPayload.get("amount").asDouble();
                    walletService.processPayment(userId, amount, paymentId);
                    break;
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
