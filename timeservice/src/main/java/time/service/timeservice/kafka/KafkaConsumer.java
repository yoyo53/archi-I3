package time.service.timeservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
@Service
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    // private final timeService timeService; // = new timeService();

    // private final String EVENT_TYPE = "EventType";
    // private final String PAYLOAD = "Payload";

    @Autowired
    public KafkaConsumer() {
    }
    

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ObjectNode message) {
        logger.info(String.format("#### -> Consumed message -> %s", message.toString()));
    }
}
