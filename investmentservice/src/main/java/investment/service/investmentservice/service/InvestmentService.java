package investment.service.investmentservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import investment.service.investmentservice.kafka.KafkaProducer;

// Investment
import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.repository.InvestmentRepository;

// User
import investment.service.investmentservice.model.User;
import investment.service.investmentservice.repository.UserRepository;

// Property
import investment.service.investmentservice.model.Property;
import investment.service.investmentservice.repository.PropertyRepository;

// Payment
import investment.service.investmentservice.model.Payment;
import investment.service.investmentservice.repository.PaymentRepository;

// Certificat
import investment.service.investmentservice.model.Certificat;
import investment.service.investmentservice.repository.CertificatRepository;

@Service
public class InvestmentService {

    @Value("${spring.kafka.topic}")
    private String topic;

    // Repository
    private final InvestmentRepository investmentRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PaymentRepository paymentRepository;
    private final CertificatRepository certificatRepository;

    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository, UserRepository userRepository, PropertyRepository propertyRepository, KafkaProducer kafkaProducer, PaymentRepository paymentRepository, CertificatRepository certificatRepository) {
        this.investmentRepository = investmentRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.paymentRepository = paymentRepository;
        this.certificatRepository = certificatRepository;
        this.kafkaProducer = kafkaProducer;
    }

    // Investment
    public String createInvestment(Investment investment) {
        Investment savedInvestment = investmentRepository.save(investment);

        //Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "InvestmentCreated");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", savedInvestment.getId());
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return "Investment created successfully";
    }

    public Boolean canInvest(Long propertyId, Double amount) {
        if (!propertyExists(propertyId)) {
            return false; //"Property does not exist";
        }

        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            return false; //"Property not found";
        }

        BigDecimal totalInvested = investmentRepository.findTotalInvestedByPropertyID(propertyId);
        if (totalInvested == null) {
            totalInvested = BigDecimal.ZERO;
        }

        if (totalInvested.add(BigDecimal.valueOf(amount)).compareTo(BigDecimal.valueOf(property.getPrice().longValue())) > 0) {
            return false; //"Investment exceeds property price";
        }

        return true; //"Investment is possible";
    }

    // User
    public Boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public String createUser(User user) {
        if (userExists(user.getId())) {
            return "User already exists";
        } else if (!user.getRole().equals("Investor")) {
            return "Role must be either Investor";
        } else {
            userRepository.save(user);
            return "User created successfully";
        }
    }



    // Property
    public Boolean propertyExists(Long propertyId) {
        return propertyRepository.existsById(propertyId);
    }
    public String createProperty(Property property) {
        if (propertyExists(property.getId())) {
            return "Property already exists";
        } else {
            propertyRepository.save(property);

            return "Property created successfully";
        }
    }

    // Payment
    public String createPayment(Payment payment) {
        if (paymentRepository.existsById(payment.getId())) {
            return "Payment already exists";
        } else {
            paymentRepository.save(payment);

            return "Payment created successfully";
        }
    }

    // Certificat
    public String createCertificat(Certificat certificat) {
        if (certificatRepository.existsById(certificat.getId())) {
            return "Certificat already exists";
        } else {
            certificatRepository.save(certificat);

            return "Certificat created successfully";
        }
    }

    // public String createUser(User user) {
    //     if (userRepository.existsById(user.getId())) {
    //         return "User already exists";
    //     } else if (!user.getRole().equals("Investor") && !user.getRole().equals("Agent")) {
    //         return "Role must be either Investor or Agent";
    //     } else {
    //         userRepository.save(user);
    //         //Json Object
    //         ObjectNode objectNode = new ObjectMapper().createObjectNode();

    //         kafkaProducer.sendMessage("user-topic", "User created: " + user.getId());
    //         return "User created successfully";
    //     }
    // }

    // public String loginUser(User user) {
    //     if (userRepository.existsById(user.getId())) {
    //         User userFromDb = userRepository.findById(user.getId()).get();
    //         if (userFromDb.getPassword().equals(user.getPassword())) {
    //             return "Login successful" + userFromDb.getId();
    //         } else {
    //             return "Invalid password";
    //         }
    //     } else {
    //         return "User does not exist";
    //     }
    // }
}