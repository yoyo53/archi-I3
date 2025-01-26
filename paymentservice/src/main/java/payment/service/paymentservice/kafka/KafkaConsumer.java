package payment.service.paymentservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public KafkaConsumer() {
        
    }

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.warn(String.format("#### -> Consumed message -> %s", message.toString()));
    }
}
