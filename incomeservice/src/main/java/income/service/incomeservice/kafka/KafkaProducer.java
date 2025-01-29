package income.service.incomeservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, ObjectNode> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, ObjectNode> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, ObjectNode message) {
        kafkaTemplate.send(topic, message);
    }
}