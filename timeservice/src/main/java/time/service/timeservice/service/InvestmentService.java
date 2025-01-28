package time.service.timeservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import time.service.timeservice.kafka.KafkaProducer;

import time.service.timeservice.dto.timeDTO;

// time
import time.service.timeservice.model.time;
import time.service.timeservice.repository.timeRepository;

// User
import time.service.timeservice.model.User;
import time.service.timeservice.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
// Property
import time.service.timeservice.model.Property;
import time.service.timeservice.repository.PropertyRepository;

// Payment
import time.service.timeservice.model.Payment;
import time.service.timeservice.repository.PaymentRepository;

// Certificat
import time.service.timeservice.model.Certificat;
import time.service.timeservice.repository.CertificatRepository;

@Service
public class timeService {

    private static final int time_LIMIT_PER_YEAR = 100000;

    @Value("${spring.kafka.topic}")
    private String topic;

    // Repository
    private final timeRepository timeRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PaymentRepository paymentRepository;
    private final CertificatRepository certificatRepository;

    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public timeService(timeRepository timeRepository, UserRepository userRepository, PropertyRepository propertyRepository, KafkaProducer kafkaProducer, PaymentRepository paymentRepository, CertificatRepository certificatRepository) {
        this.timeRepository = timeRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.paymentRepository = paymentRepository;
        this.certificatRepository = certificatRepository;
        this.kafkaProducer = kafkaProducer;
    }

    // time
    public time createtime(@NotNull @Valid timeDTO timeDTO, @NotNull Long userID) {
        if (timeDTO.getAmount() <= 500) {
            throw new IllegalArgumentException("time amount must be greater than zero");
        }
        
        if (timeRepository.findtimeTotalBytime_UserId(userID).compareTo(BigDecimal.valueOf(time_LIMIT_PER_YEAR)) > 0) {
            throw new IllegalArgumentException("time amount exceeds maximum limit for this year");
        }

        Property property = propertyRepository.findById(timeDTO.getPropertyId()).orElseThrow();
        if (!canInvest(property, timeDTO.getAmount())) {
            throw new IllegalArgumentException("time exceeds property price");
        }
        User user = userRepository.findById(userID).orElseThrow();
        

        time time = new time(property, user, timeDTO.getAmount());
        
        time savedtime = timeRepository.save(time);

        //Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "timeCreated");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", savedtime.getId());
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return savedtime;
    }

    public Iterable<time> gettimes() {
        return timeRepository.findAll();
    }

    public time gettime(Long id) {
        return timeRepository.findById(id).orElse(null);
    }

    public Iterable<time> gettimesByUser(Long userID) {
        return timeRepository.findByUser_id(userID);
    }

    public Boolean canInvest(@NotNull @Valid Property property, Double amount) {

        BigDecimal totalInvested = timeRepository.findTotalInvestedBytime_PropertyId(property.getId());
        if (totalInvested == null) {
            totalInvested = BigDecimal.ZERO;
        }

        if (totalInvested.add(BigDecimal.valueOf(amount)).compareTo(BigDecimal.valueOf(property.getPrice().longValue())) > 0) {
            return false; //"time exceeds property price";
        }

        return true; //"time is possible";
    }

    // User
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    // Property
    public Property createProperty(Property property) {
        Property savedProperty = propertyRepository.save(property);
        return savedProperty;
    }

    public Property updatePropertyStatus(Property property) {
        Property updatedProperty = propertyRepository.save(property);
        return updatedProperty;
    }

    // Payment
    public Payment createPayment(Payment payment) {
        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment;
    }

    // Certificat
    public Certificat createCertificat(Certificat certificat) {
        Certificat savedCertificat = certificatRepository.save(certificat);
        return savedCertificat;
    }
}