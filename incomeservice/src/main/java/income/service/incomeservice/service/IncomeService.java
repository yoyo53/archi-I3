package income.service.incomeservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import income.service.incomeservice.kafka.KafkaProducer;
import income.service.incomeservice.repository.CertificateRepository;
import income.service.incomeservice.repository.IncomeRepository;
import income.service.incomeservice.repository.InvestmentRepository;
import income.service.incomeservice.repository.PropertyRepository;
import income.service.incomeservice.model.Certificate;
import income.service.incomeservice.model.Income;
import income.service.incomeservice.model.Investment;
import income.service.incomeservice.model.Property;

@Service
public class IncomeService {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final IncomeRepository incomeRepository;
    private final PropertyRepository propertyRepository;
    private final InvestmentRepository investmentRepository;
    private final CertificateRepository certificateRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    private LocalDate systemDate;

    private final String EVENT_TYPE = "EventType";
    private final String PAYLOAD = "Payload";

    @Autowired
    public IncomeService(IncomeRepository incomeRepository, KafkaProducer kafkaProducer, ObjectMapper objectMapper, PropertyRepository propertyRepository, InvestmentRepository investmentRepository, CertificateRepository certificateRepository) {
        this.incomeRepository = incomeRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.propertyRepository = propertyRepository;
        this.investmentRepository = investmentRepository;
        this.certificateRepository = certificateRepository;
    }

    public Iterable<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }

    public Income getIncomeById(Long id) {
        return incomeRepository.findById(id).orElse(null);
    }

    public Iterable<Income> getIncomesByUserId(Long userId) {
        return incomeRepository.findByInvestment_UserId(userId);
    }

    public Property createProperty (Property property) {
        return propertyRepository.save(property);
    }

    public Investment createInvestment(Long propertyId, Long userId, Double amountInvested, Double sharesOwned, Long id) {
        Property property = propertyRepository.findById(propertyId).orElseThrow();
        Investment investment = new Investment(property, userId, amountInvested, sharesOwned, id);
        return investmentRepository.save(investment);
    }

    public Certificate createCertificate(Certificate certificate, Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId).orElseThrow();
        certificate.setInvestment(investment);
        investment.setCertificate(certificate);
        return certificateRepository.save(certificate);

    }

    public void changeDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = LocalDate.parse(date, formatter);
        this.systemDate = newDate;

        if (systemDate.getDayOfMonth() == 1) {
            Iterable<Investment> investments =  investmentRepository.findByCertificate_EmissionDateBefore(date);
            for (Investment investment : investments) {
                Income income = new Income(investment, date, investment.getAmountInvested() * investment.getProperty().getAnnualRentalIncomeRate() / 12);

                if (systemDate.getMonthValue() == 1) {
                    income.setAmount(income.getAmount() + investment.getAmountInvested() * investment.getProperty().getAppreciationRate());
                }

                incomeRepository.save(income);
                ObjectNode event = objectMapper.createObjectNode();
                event.put(EVENT_TYPE, "IncomeCreated");
                ObjectNode payload = objectMapper.convertValue(income, ObjectNode.class);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
        }
    }

    public Investment linkInvestmentToCertificate(Long investmentId, Long certificateId) {
        Investment investment = investmentRepository.findById(investmentId).orElseThrow();
        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow();
        investment.setCertificate(certificate);
        return investmentRepository.save(investment);
    }

}