package time.service.timeservice.service;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import time.service.timeservice.kafka.KafkaProducer;

import time.service.timeservice.dto.TimeDTO;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

@Service
public class timeService {

    @Value("${spring.application.timezone}")
    private String timeZone;

    private LocalDate systemDate;

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public timeService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostConstruct
    public void init() {
        // Initialisation après l'injection
        this.systemDate = LocalDate.now(ZoneId.of(timeZone));
        sendDefaultTime();
    }

    // Method to send the time to the Kafka topic, with TimeDTO as input : date_to_move or number_day_to_skip
    // If both are null, IllegalArgumentException is thrown
    public ResponseEntity<ObjectNode> sendTime(@Valid TimeDTO timeDTO) {
        if (timeDTO.getDate_to_move() == null && timeDTO.getNumber_day_to_skip() == null) {
            throw new IllegalArgumentException("Both date_to_move and number_day_to_skip cannot be null");
        }
        if (timeDTO.getDate_to_move() != null) {
            LocalDate endDate = LocalDate.parse(timeDTO.getDate_to_move());
            while (!systemDate.isAfter(endDate) && !systemDate.isEqual(endDate)) {
                // Move to the next day
                systemDate = systemDate.plusDays(1);

                String enventDate = systemDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

                // Create and send event for each day
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "TimeEvent");
                ObjectNode payload = new ObjectMapper().createObjectNode();
                payload.put("date", enventDate);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);               
            }
        } else if (timeDTO.getNumber_day_to_skip() != null) {
            // Number_day_to_skip is an integer
            for (int i = 0; i < timeDTO.getNumber_day_to_skip(); i++) {
                // Move to the next day
                systemDate = systemDate.plusDays(1);

                String enventDate = systemDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

                // Create and send event for each day
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "TimeEvent");
                ObjectNode payload = new ObjectMapper().createObjectNode();
                payload.put("default_date", enventDate);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
            
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ObjectNode> sendDefaultTime(){
        String enventDate = systemDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Create and send event for each day
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "TimeEvent");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("date", enventDate);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);

        return ResponseEntity.ok().build();
    }
}