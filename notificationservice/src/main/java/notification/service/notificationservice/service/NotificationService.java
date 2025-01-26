package notification.service.notificationservice.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import notification.service.notificationservice.dto.NotificationDTO;
import notification.service.notificationservice.kafka.KafkaProducer;
import notification.service.notificationservice.model.Certificate;
import notification.service.notificationservice.model.Investment;
import notification.service.notificationservice.repository.CertificateRepository;
import notification.service.notificationservice.repository.InvestmentRepository;

@Service
public class NotificationService {

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

    @Autowired
    public NotificationService(
            InvestmentRepository investmentRepository,
            CertificateRepository certificateRepository,
            KafkaProducer kafkaProducer,
            ObjectMapper objectMapper) {
        this.investmentRepository = investmentRepository;
        this.certificateRepository = certificateRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    public Investment createInvestment(@NotNull @Valid Investment investment) {
        Investment savedInvestment = investmentRepository.save(investment);
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

    public Certificate createCertificate(@NotNull @Valid NotificationDTO notificationDTO) {
        Certificate certificate = createCertificateFromDTO(notificationDTO);
        Certificate savedCertificate = certificateRepository.save(certificate);

        ObjectNode payload = objectMapper.createObjectNode()
                .put("id", savedCertificate.getId());
        ObjectNode event = objectMapper.createObjectNode()
                .put(EVENT_TYPE, "CertificateCreated")
                .set(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return savedCertificate;
    }

    public Certificate getCertificate(@NotNull @Valid Long id) {
        return certificateRepository.findById(id).orElse(null);
    }

    public Long deleteCertificate(@NotNull @Valid Long id) {
        if (certificateRepository.existsById(id)) {
            certificateRepository.deleteById(id);

            ObjectNode payload = objectMapper.createObjectNode()
                    .put("id", id);
            ObjectNode event = objectMapper.createObjectNode()
                    .put(EVENT_TYPE, "CertificateDeleted")
                    .set(PAYLOAD, payload);

            kafkaProducer.sendMessage(topic, event);
            return id;
        } else {
            return null;
        }
    }

    private Certificate createCertificateFromDTO(@NotNull @Valid NotificationDTO notificationDTO) {
        Investment investment = investmentRepository.findById(notificationDTO.getInvestmentId()).orElseThrow();
        return new Certificate(investment, getISOdate());
    }

    private String getISOdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate date = LocalDate.now(zoneId);
        return date.format(formatter);
    }
}