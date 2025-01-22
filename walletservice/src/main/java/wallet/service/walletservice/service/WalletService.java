package wallet.service.walletservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import wallet.service.walletservice.kafka.KafkaProducer;
import wallet.service.walletservice.model.Wallet;
import wallet.service.walletservice.repository.WalletRepository;

@Service
public class WalletService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final WalletRepository walletRepository;
    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public WalletService(WalletRepository walletRepository, KafkaProducer kafkaProducer) {
        this.walletRepository = walletRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public String createWallet(Wallet wallet) {
        if (walletRepository.findByUserId(wallet.getUserId()) != null) {
            return "Wallet already exists";
        } 
        else {
            Wallet savedWallet = walletRepository.save(wallet);
            
            //Json Object
            ObjectNode event = new ObjectMapper().createObjectNode();
            event.put(EVENT_TYPE, "WalletCreated");
            ObjectNode payload = new ObjectMapper().createObjectNode();
            payload.put("id", savedWallet.getId());
            event.set(PAYLOAD, payload);

            kafkaProducer.sendMessage(topic, event);
            return "Wallet created successfully";
        }
    }
}