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

    public boolean createWallet(Long userId) {
        if (walletRepository.findByUserId(userId) != null) {
            return false;
        } 
        else {
            Wallet wallet = new Wallet(userId);
            Wallet savedWallet = walletRepository.save(wallet);
            
            //Json Object
            ObjectNode event = new ObjectMapper().createObjectNode();
            event.put(EVENT_TYPE, "WalletCreated");
            ObjectNode payload = new ObjectMapper().createObjectNode();
            payload.put("id", savedWallet.getId());
            event.set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);

            return true;
        }
    }

    public void deposit(Long userId, Double amount) {
        if (walletRepository.findByUserId(userId) != null) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Invalid amount");
            }
            Wallet wallet = walletRepository.findByUserId(userId);
            wallet.setBalance(wallet.getBalance() + amount);
            walletRepository.save(wallet);

            //Json Object
            ObjectNode event = new ObjectMapper().createObjectNode();
            event.put(EVENT_TYPE, "DepositMade");
            ObjectNode payload = new ObjectMapper().createObjectNode();
            payload.put("id", wallet.getId());
            payload.put("amount", amount);
            event.set(PAYLOAD, payload);

            kafkaProducer.sendMessage(topic, event);
        }
        else {
            throw new IllegalArgumentException("Wallet does not exist");
        }
    }

    public void withdraw(Long userId, Double amount) {
        if(walletRepository.findByUserId(userId) != null) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Invalid amount");
            }
            Wallet wallet = walletRepository.findByUserId(userId);
            if (wallet.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            wallet.setBalance(wallet.getBalance() - amount);
            walletRepository.save(wallet);

            //Json Object
            ObjectNode event = new ObjectMapper().createObjectNode();
            event.put(EVENT_TYPE, "WithdrawalMade");
            ObjectNode payload = new ObjectMapper().createObjectNode();
            payload.put("id", wallet.getId());
            payload.put("amount", amount);
            event.set(PAYLOAD, payload);

            kafkaProducer.sendMessage(topic, event);
        }
        else {
            throw new IllegalArgumentException("Wallet does not exist");
        }
    }

    public void processPayment(Long userId, Double amount, Long paymentId) {
        if (amount < 0) {
            try {
                withdraw(userId, -amount);
                //Json Object
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "ProcessPaymentSuccessful");
                ObjectNode payload = new ObjectMapper().createObjectNode();
                payload.put("userId", userId);
                payload.put("paymentId", paymentId);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
            catch (IllegalArgumentException e) {
                //Json Object
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "ProcessPaymentFailed");
                ObjectNode payload = new ObjectMapper().createObjectNode();
                payload.put("userId", userId);
                payload.put("paymentId", paymentId);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
                throw new IllegalArgumentException("Payment failed");
            }
        }
        else if (amount > 0) {
            try {
                deposit(userId, amount);
                //Json Object
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "ProcessPaymentSuccessful");
                ObjectNode payload = new ObjectMapper().createObjectNode();
                payload.put("userId", userId);
                payload.put("paymentId", paymentId);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
            catch (IllegalArgumentException e) {
                //Json Object
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "ProcessPaymentFailed");
                ObjectNode payload = new ObjectMapper().createObjectNode();
                payload.put("userId", userId);
                payload.put("paymentId", paymentId);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
                throw new IllegalArgumentException("Payment failed");
            }
        }
    }
}