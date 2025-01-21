package user.service.userservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public KafkaConsumer() {
        
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "db-service")
    public void consume(String message) {
    }
}
