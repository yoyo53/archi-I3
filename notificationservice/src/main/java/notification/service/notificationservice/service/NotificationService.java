package notification.service.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import notification.service.notificationservice.kafka.KafkaProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    private static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    public NotificationService(
            KafkaProducer kafkaProducer,
            ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;

    }


    public void sendNotification(String subject, ObjectNode payload) {
        ObjectNode message = objectMapper.createObjectNode();
        message.put(EVENT_TYPE, "EmailSend");
        message.put(PAYLOAD, subject);
        message.set(PAYLOAD, payload);
        kafkaProducer.sendMessage(topic, message);
        logger.warn(String.format("#### -> Sent message -> %s", message.toString()));
    }
}