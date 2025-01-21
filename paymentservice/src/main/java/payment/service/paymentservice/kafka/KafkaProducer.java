package payment.service.paymentservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import payment.service.paymentservice.model.Payment;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, Payment> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Payment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Payment payment) {
        kafkaTemplate.send(topic, payment);
    }
}