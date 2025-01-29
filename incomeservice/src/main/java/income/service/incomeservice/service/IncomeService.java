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

    public Income getIncome(Long userId) {
        return incomeRepository.findById(userId).orElse(null);
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
        return certificateRepository.save(certificate);

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
        String dateString = systemDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (systemDate.getDayOfMonth() == 1) {
            Iterable<Investment> investments =  investmentRepository.findByCertificate_EmissionDateBefore(dateString);
            for (Investment investment : investments) {
                Income income = new Income(investment, dateString, investment.getAmountInvested() * investment.getProperty().getAnnualRentalIncomeRate() / 12);

                if (systemDate.getMonthValue() == 1) {
                    income.setAmount(income.getAmount() + investment.getAmountInvested() * investment.getProperty().getAppreciationRate());
                }
                ObjectNode event = new ObjectMapper().createObjectNode();
                event.put(EVENT_TYPE, "IncomeCreated");
                ObjectNode payload = new ObjectMapper().convertValue(income, ObjectNode.class);
                event.set(PAYLOAD, payload);

                kafkaProducer.sendMessage(topic, event);
            }
        }
    }
}