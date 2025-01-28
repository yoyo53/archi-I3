package payment.service.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import payment.service.paymentservice.kafka.KafkaProducer;
import payment.service.paymentservice.model.Payment;
import payment.service.paymentservice.repository.PaymentRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {
    @Value("${spring.kafka.topic}")
    private String topic;

    private final PaymentRepository paymentRepository;
    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Value("${spring.application.timezone}")
    private String timeZone;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, KafkaProducer kafkaProducer) {
        this.paymentRepository = paymentRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public Payment createPayment(Long userID, Double amount, Long InvestmentID) {
        Payment payment = new Payment(userID, getISOdate(), amount);
        Payment savedPayment = paymentRepository.save(payment);

        //Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "PaymentCreated");
        ObjectNode payload = new ObjectMapper().convertValue(savedPayment, ObjectNode.class);
        payload.put("InvestmentID", InvestmentID);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return savedPayment;
    }

    public Iterable<Payment> getPayments(){
        return paymentRepository.findAll();
    }
    
    public Payment getPaymentById(Long id){
        return paymentRepository.findById(id).get();
    }

    private String getISOdate() {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        logger.warn(timeZone);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(new Date());

    }
}
