package investment.service.investmentservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import investment.service.investmentservice.kafka.KafkaProducer;

import investment.service.investmentservice.dto.InvestmentDTO;

// Investment
import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.repository.InvestmentRepository;

// User
import investment.service.investmentservice.model.User;
import investment.service.investmentservice.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    private static final int INVESTMENT_LIMIT_PER_YEAR = 100000;

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
    public Investment createInvestment(@NotNull @Valid InvestmentDTO investmentDTO, @NotNull Long userID) {
        if (investmentDTO.getAmount() <= 500) {
            throw new IllegalArgumentException("Investment amount must be greater than zero");
        }
        
        if (investmentRepository.findInvestmentTotalByInvestment_UserId(userID).compareTo(BigDecimal.valueOf(INVESTMENT_LIMIT_PER_YEAR)) > 0) {
            throw new IllegalArgumentException("Investment amount exceeds maximum limit for this year");
        }

        Property property = propertyRepository.findById(investmentDTO.getPropertyId()).orElseThrow();
        if (!canInvest(property, investmentDTO.getAmount())) {
            throw new IllegalArgumentException("Investment exceeds property price");
        }
        User user = userRepository.findById(userID).orElseThrow();
        

        Investment investment = new Investment(property, user, investmentDTO.getAmount());
        
        Investment savedInvestment = investmentRepository.save(investment);

        //Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "InvestmentCreated");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", savedInvestment.getId());
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return savedInvestment;
    }

    public Iterable<Investment> getInvestments() {
        return investmentRepository.findAll();
    }

    public Investment getInvestment(Long id) {
        return investmentRepository.findById(id).orElse(null);
    }

    public Iterable<Investment> getInvestmentsByUser(Long userID) {
        return investmentRepository.findByUser_id(userID);
    }

    public Boolean canInvest(@NotNull @Valid Property property, Double amount) {

        BigDecimal totalInvested = investmentRepository.findTotalInvestedByInvestment_PropertyId(property.getId());
        if (totalInvested == null) {
            totalInvested = BigDecimal.ZERO;
        }

        if (totalInvested.add(BigDecimal.valueOf(amount)).compareTo(BigDecimal.valueOf(property.getPrice().longValue())) > 0) {
            return false; //"Investment exceeds property price";
        }

        return true; //"Investment is possible";
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