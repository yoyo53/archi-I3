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

    public boolean createWallet(Long userId) {
        if (walletRepository.findByUserId(userId) != null) {
            ObjectNode payload = objectMapper.createObjectNode()
                .put("walletId", walletRepository.findByUserId(userId).getId())
                .put("userId", userId);
            ObjectNode event = objectMapper.createObjectNode()
                .put(EVENT_TYPE, "WalletAlreadyExists")
                .set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);

            return false;
        } 
        else {
            Wallet wallet = new Wallet(userId);
            Wallet savedWallet = walletRepository.save(wallet);

            ObjectNode payload = objectMapper.createObjectNode()
                .put("walletId", savedWallet.getId())
                .put("userId", savedWallet.getUserId());
            ObjectNode event = objectMapper.createObjectNode()
                .put(EVENT_TYPE, "WalletCreated")
                .set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);

            return true;
        }
    }

    public void deposit(Long userId, Double amount) {
        if (walletRepository.findByUserId(userId) != null) {
            amount = Math.abs(amount);

            Wallet wallet = walletRepository.findByUserId(userId);
            wallet.setBalance(wallet.getBalance() + amount);
            walletRepository.save(wallet);
        }
        else {
            throw new IllegalArgumentException("Wallet does not exist");
        }
    }

    public void withdraw(Long userId, Double amount) {
        if(walletRepository.findByUserId(userId) != null) {
            amount = Math.abs(amount);

            Wallet wallet = walletRepository.findByUserId(userId);
            if (wallet.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            wallet.setBalance(wallet.getBalance() - amount);
            walletRepository.save(wallet);
        }
        else {
            throw new IllegalArgumentException("Wallet does not exist");
        }
    }

    public void processPayment(Long userId, Double amount, Long paymentId) {
        if (amount < 0) {
            try {
                withdraw(userId, -amount);

                ObjectNode payload = objectMapper.createObjectNode()
                    .put("paymentId", paymentId)
                    .put("userId", userId)
                    .put("amount", amount);
                ObjectNode event = objectMapper.createObjectNode()
                    .put(EVENT_TYPE, "WithdrawalSuccessful")
                    .set(PAYLOAD, payload);
                    kafkaProducer.sendMessage(topic, event);
            }
            catch (IllegalArgumentException e) {
                ObjectNode payload = objectMapper.createObjectNode()
                    .put("userId", userId)
                    .put("amount", amount)
                    .put("paymentId", paymentId)
                    .put("reason", e.getMessage());
                ObjectNode event = objectMapper.createObjectNode()
                    .put(EVENT_TYPE, "WithdrawalFailed")
                    .set(PAYLOAD, payload);
                kafkaProducer.sendMessage(topic, event);
            }
        }
        else if (amount > 0) {
            try {
                deposit(userId, amount);

                ObjectNode payload = objectMapper.createObjectNode()
                    .put("paymentId", paymentId)
                    .put("userId", userId)
                    .put("amount", amount);
                ObjectNode event = objectMapper.createObjectNode()
                    .put(EVENT_TYPE, "DepositSuccessful")
                    .set(PAYLOAD, payload);
                kafkaProducer.sendMessage(topic, event);
            }
            catch (IllegalArgumentException e) {
                ObjectNode payload = objectMapper.createObjectNode()
                    .put("userId", userId)
                    .put("amount", amount)
                    .put("paymentId", paymentId)
                    .put("reason", e.getMessage());
                ObjectNode event = objectMapper.createObjectNode()
                    .put(EVENT_TYPE, "DepositFailed")
                    .set(PAYLOAD, payload);
                kafkaProducer.sendMessage(topic, event);
            }
        }
    }
}