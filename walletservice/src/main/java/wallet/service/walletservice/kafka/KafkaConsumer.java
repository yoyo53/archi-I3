package wallet.service.walletservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import wallet.service.walletservice.service.WalletService;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final WalletService walletService;
    private final ObjectMapper objectMapper;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer(WalletService walletService, ObjectMapper objectMapper) {
        this.walletService = walletService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));

        try {
            String eventType = message.get(EVENT_TYPE).asText();
            
            switch (eventType) {
                case "UserCreated":
                    logger.info("Wallet created event received");
                    JsonNode userPayload = message.get(PAYLOAD);

                    if (userPayload.get("role").asText().equals("Investor")) {
                        logger.info("Creating wallet for investor");
                        walletService.createWallet(userPayload.get("id").asLong());
                    } 
                    break;
                case "ProcessPayment":
                    logger.info("Payment processed event received");
                    JsonNode paymentPayload = message.get(PAYLOAD);
                    Long paymentId = paymentPayload.get("paymentId").asLong();
                    Long userId = paymentPayload.get("userId").asLong();
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
