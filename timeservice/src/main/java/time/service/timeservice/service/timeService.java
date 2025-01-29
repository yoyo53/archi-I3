package time.service.timeservice.service;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import time.service.timeservice.kafka.KafkaProducer;

import jakarta.annotation.PostConstruct;

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
        this.systemDate = LocalDate.now(ZoneId.of(timeZone));
        sendTime();
    }

    public String getTime() {
        return systemDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public void addDays(Long nbDays) {
        for (int i = 0; i < nbDays; i++) {
            nextDay();
        }
    }

    public void moveToDate(String date) {
        LocalDate endDate = LocalDate.parse(date);
        while (!systemDate.isAfter(endDate) && !systemDate.isEqual(endDate)) {
            nextDay();
        }
    }

    public void nextDay() {
        systemDate = systemDate.plusDays(1);
        sendTime();
    }

    public void sendTime() {
        String enventDate = systemDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "TimeEvent");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("date", enventDate);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
    }
}