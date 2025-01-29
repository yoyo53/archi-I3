package payment.service.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import payment.service.paymentservice.kafka.KafkaProducer;
import payment.service.paymentservice.model.Payment;
import payment.service.paymentservice.model.Payment.PaymentStatus;
import payment.service.paymentservice.repository.PaymentRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private LocalDate systemDate;

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Value("${spring.application.timezone}")
    private String timeZone;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, KafkaProducer kafkaProducer) {
        this.paymentRepository = paymentRepository;
        this.kafkaProducer = kafkaProducer;
        this.systemDate = null;
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

    public Payment updatePaymentStatus(Long id, String status){
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);

        ObjectNode event = new ObjectMapper().createObjectNode();

        if(updatedPayment.getStatus().equals(PaymentStatus.SUCCESS.getDescription())){
            event.put(EVENT_TYPE, "PaymentSuccessful");
        }else{
            event.put(EVENT_TYPE, "PaymentFailed");
        }
        ObjectNode payload = new ObjectMapper().convertValue(updatedPayment, ObjectNode.class);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        
        return updatedPayment;
    }

    private String getISOdate() {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        logger.warn(timeZone);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(new Date());

    }

    public void setDefaultDate(String defaultDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(defaultDate, formatter);
        this.systemDate = date;
    }

    public void changeDate(String date) {
        // Add logic when date changed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = LocalDate.parse(date, formatter);
        this.systemDate = newDate;
        String previousDate = systemDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Check if there are certificates with the new date and send a message
        Iterable<Payment> payments = paymentRepository.findByDateBeforeAndStatus(previousDate, PaymentStatus.PENDING);
        for (Payment payment : payments) {
            updatePaymentStatus(payment.getId(), PaymentStatus.FAILED.getDescription());
        }
    }
}
