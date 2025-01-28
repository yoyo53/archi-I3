package investment.service.investmentservice.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import investment.service.investmentservice.kafka.KafkaProducer;

import investment.service.investmentservice.dto.InvestmentDTO;

// Investment
import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.model.Investment.InvestmentStatus;
import investment.service.investmentservice.repository.InvestmentRepository;

// User
import investment.service.investmentservice.model.User;
import investment.service.investmentservice.model.Payment.PaymentStatus;
import investment.service.investmentservice.model.User.UserRole;
import investment.service.investmentservice.model.Property.PropertyStatus;
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

    private LocalDate systemDate;

    @Value("${spring.application.timezone}")
    private String timeZone;

    @Autowired
    public InvestmentService(InvestmentRepository investmentRepository, UserRepository userRepository, PropertyRepository propertyRepository, KafkaProducer kafkaProducer, PaymentRepository paymentRepository, CertificatRepository certificatRepository) {
        this.investmentRepository = investmentRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.paymentRepository = paymentRepository;
        this.certificatRepository = certificatRepository;
        this.kafkaProducer = kafkaProducer;
        this.systemDate = null;
    }

    // Investment
    public Investment createInvestment(@NotNull @Valid InvestmentDTO investmentDTO, @NotNull Long userID) {
        if (investmentDTO.getAmount() < 500) {
            throw new IllegalArgumentException("Investment amount must be at least 500");
        }

        User user = userRepository.findById(userID).orElseThrow();
        Property property = propertyRepository.findById(investmentDTO.getPropertyId()).orElseThrow();
        canInvest(property, investmentDTO.getAmount(), user);

        Investment investment = new Investment(property, user, getISOdate(), investmentDTO.getAmount());
        
        Investment savedInvestment = investmentRepository.save(investment);

        //Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "InvestmentCreated");
        ObjectNode payload = new ObjectMapper().convertValue(savedInvestment, ObjectNode.class);
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

    public void canInvest(@NotNull @Valid Property property, Double amount, User user) {

        BigDecimal totalInvested = investmentRepository.findTotalInvestedByInvestment_PropertyId(property.getId());
        if (totalInvested == null) {
            totalInvested = BigDecimal.ZERO;
        }

        if (totalInvested.add(BigDecimal.valueOf(amount)).compareTo(BigDecimal.valueOf(property.getPrice().longValue())) > 0) {
            throw new IllegalArgumentException("Investment exceeds property price");
        }

        if(!property.getStatus().equals(PropertyStatus.OPENED.getDescription())){
            throw new IllegalArgumentException("Property is not opened for investment");
        }

        if(!user.getRole().equals(UserRole.INVESTOR.getDescription())){
            throw new IllegalArgumentException("User is not an investor");
        }
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

        if (property.getStatus().equals(PropertyStatus.FUNDED.getDescription())) {
            Iterable<Investment> investments = investmentRepository.findByProperty_id(property.getId()).orElseThrow();
            for (Investment investment : investments) {
                investment.setStatus(InvestmentStatus.COMPLETED.getDescription());
                investmentRepository.save(investment);

                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "InvestmentCompleted");
                ObjectNode payload = new ObjectMapper().convertValue(investment, ObjectNode.class);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
        }
        return updatedProperty;
    }

    // Payment
    public Payment createPayment(Payment payment, Long investmentID) {
        Investment investment = investmentRepository.findById(investmentID).orElseThrow();
        Payment savedPayment = paymentRepository.save(payment);
        investment.setPayment(savedPayment);
        investmentRepository.save(investment);
        return savedPayment;
    }

    // Certificat
    public Certificat createCertificat(Certificat certificat) {
        Certificat savedCertificat = certificatRepository.save(certificat);
        return savedCertificat;
    }

    public Payment updatePaymentStatus(Payment payment) {
        Payment updatedPayment = paymentRepository.save(payment);
        
        Investment investment = investmentRepository.findByPayment_id(payment.getId()).orElseThrow();

        ObjectNode event = new ObjectMapper().createObjectNode();
        if(payment.getStatus().equals(PaymentStatus.SUCCESS.getDescription())){
            investment.setStatus(InvestmentStatus.SUCCESS.getDescription());
            event.put(EVENT_TYPE, "InvestmentSuccessful");

        }else{
            investment.setStatus(InvestmentStatus.FAILED.getDescription());
            event.put(EVENT_TYPE, "InvestmentFailed");
        }

        Investment updatedInvestment = investmentRepository.save(investment);
        ObjectNode payload = new ObjectMapper().convertValue(updatedInvestment, ObjectNode.class);
        event.set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);

        return updatedPayment;
    }

    private String getISOdate() {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
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
    }
}