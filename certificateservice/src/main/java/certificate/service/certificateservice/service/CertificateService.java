package certificate.service.certificateservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import certificate.service.certificateservice.kafka.KafkaProducer;
import certificate.service.certificateservice.model.Investment;
import certificate.service.certificateservice.repository.InvestmentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import certificate.service.certificateservice.model.Certificate;
import certificate.service.certificateservice.repository.CertificateRepository;

@Service
public class CertificateService {

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.application.timezone}")
    private String timeZone;

    private final InvestmentRepository investmentRepository;
    private final CertificateRepository certificateRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    private LocalDate systemDate;

    @Autowired
    public CertificateService(
            InvestmentRepository investmentRepository,
            CertificateRepository certificateRepository,
            KafkaProducer kafkaProducer,
            ObjectMapper objectMapper) {
        this.investmentRepository = investmentRepository;
        this.certificateRepository = certificateRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.systemDate = null;
    }

    public Investment createInvestment(@NotNull @Valid Investment investment) {
        Investment savedInvestment = investmentRepository.save(investment);
        return savedInvestment;
    }

    public Investment updateInvestmentStatus(@NotNull @Valid Long investmentId, String status) {
        Investment investment = investmentRepository.findById(investmentId).orElseThrow();
        investment.setStatus(status);
        Investment savedInvestment = investmentRepository.save(investment);
        if (savedInvestment.getStatus().equals(Investment.InvestmentStatus.COMPLETED.getDescription())) {
            createCertificate(investmentId);
        }
        return savedInvestment;
    }

    public Investment getInvestment(@NotNull @Valid Long id) {
        return investmentRepository.findById(id).orElse(null);
    }

    public Iterable<Certificate> getCertificates() {
        return certificateRepository.findAll();
    }

    public Iterable<Certificate> getCertificatesByUserId(@NotNull @Valid Long userId) {
        return certificateRepository.findByInvestment_UserId(userId);
    }

    public Iterable<Certificate> getCertificatesByPropertyId(@NotNull @Valid Long propertyId) {
        return certificateRepository.findByInvestment_PropertyId(propertyId);
    }

    public Certificate getCertificateByInvestmentId(@NotNull @Valid Long investmentId) {
        return certificateRepository.findByInvestmentId(investmentId);
    }

    public Certificate getCertificate(@NotNull @Valid Long id) {
        return certificateRepository.findById(id).orElse(null);
    }

    public void setDefaultDate(String defaultDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(defaultDate, formatter);
        this.systemDate = date;
    }

    public void changeDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = LocalDate.parse(date, formatter);
        this.systemDate = newDate;

        Iterable<Certificate> certificates = certificateRepository.findByEmissionDate(date);
        for (Certificate certificate : certificates) {
            ObjectNode payload = objectMapper.convertValue(certificate, ObjectNode.class);
            ObjectNode event = objectMapper.createObjectNode()
                    .put(EVENT_TYPE, "CertificateDelivery")
                    .set(PAYLOAD, payload);
            kafkaProducer.sendMessage(topic, event);
        }
    }

    public Certificate createCertificate(Long investmentID) {
        String deliveryDate = systemDate.plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Investment investmentFund = getInvestment(investmentID);
        Certificate certificate = new Certificate(investmentFund, deliveryDate);
        certificateRepository.save(certificate);

        ObjectNode payload = objectMapper.convertValue(certificate, ObjectNode.class);
        ObjectNode event = objectMapper.createObjectNode()
                .put(EVENT_TYPE, "CertificateCreated")
                .set(PAYLOAD, payload);
        kafkaProducer.sendMessage(topic, event);

        return certificate;
    }
}