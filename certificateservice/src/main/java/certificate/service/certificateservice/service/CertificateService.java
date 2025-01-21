package certificate.service.certificateservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import certificate.service.certificateservice.kafka.KafkaProducer;
import certificate.service.certificateservice.model.Certificate;
import certificate.service.certificateservice.repository.CertificateRepository;

@Service
public class CertificateService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final CertificateRepository certificateRepository;
    private final KafkaProducer kafkaProducer;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, KafkaProducer kafkaProducer) {
        this.certificateRepository = certificateRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public String createCertificate(Certificate certificate) {
        Certificate savedCertificate = certificateRepository.save(certificate);

        // Json Object
        ObjectNode event = new ObjectMapper().createObjectNode();
        event.put(EVENT_TYPE, "CertificateCreated");
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("id", savedCertificate.getId());
        event.put(PAYLOAD, payload);

        kafkaProducer.sendMessage(topic, event);
        return "Certificate created";
    }

    public Certificate getCertificate(Long id) {
        return certificateRepository.findById(id).orElse(null);
    }
}