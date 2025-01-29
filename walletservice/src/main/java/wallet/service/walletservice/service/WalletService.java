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
    private final ObjectMapper objectMapper;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public WalletService(WalletRepository walletRepository, KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        this.walletRepository = walletRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    public Iterable<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Wallet createWallet(Long userId) {
        if (!walletRepository.existsByUserId(userId)) {

            Wallet wallet = new Wallet(userId);
            Wallet savedWallet = walletRepository.save(wallet);

            ObjectNode payload = objectMapper.createObjectNode()
                .put("walletId", savedWallet.getId())
                .put("userId", savedWallet.getUserId());
            ObjectNode event = objectMapper.createObjectNode()
                .put(EVENT_TYPE, "WalletCreated")
                .set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);

            return savedWallet;
        }else{
            throw new IllegalArgumentException("Wallet already exists");
        }
        
    }

    public void deposit(Long userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow();
    
        amount = Math.abs(amount);
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

    public void withdraw(Long userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow();
    
        amount = Math.abs(amount);

        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

    }

    public void processPayment(Long userId, Double amount, Long paymentId) {
        try {
            if (amount < 0) {
                deposit(userId, -amount);
            } else if (amount > 0) {
                withdraw(userId, amount);
            }

            ObjectNode payload = objectMapper.createObjectNode()
            .put("paymentId", paymentId);
            ObjectNode event = objectMapper.createObjectNode()
            .put(EVENT_TYPE, "WalletOperationSuccessful")
            .set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);
        } catch (Exception e) {
            ObjectNode payload = objectMapper.createObjectNode()
            .put("paymentId", paymentId);
            ObjectNode event = objectMapper.createObjectNode()
            .put(EVENT_TYPE, "WalletOperationFailed")
            .set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);
        }
    }

    public Wallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId).orElse(null);
    }
}