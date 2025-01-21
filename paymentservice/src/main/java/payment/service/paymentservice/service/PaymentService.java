package payment.service.paymentservice.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import payment.service.paymentservice.model.Payment;
import payment.service.paymentservice.repository.PaymentRepository;

@Service
public class PaymentService {
    private PaymentRepository paymentRepository;

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Value("${spring.application.timezone}")
    private String timeZone;
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        payment.setDate(getISOdate());
        return paymentRepository.save(payment);
    }

    private String getISOdate() {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        logger.warn(timeZone);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(new Date());

    }
    
}
