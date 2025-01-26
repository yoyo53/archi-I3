package payment.service.paymentservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import payment.service.paymentservice.model.Payment;
import payment.service.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody Payment payment) {
        try{
            paymentService.createPayment(payment);
            URI resourceLocation = new URI("/api/payments/" + payment.getId());
            return ResponseEntity.created(resourceLocation).body("Payment created");
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error creating payment");
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Payment>> getPayments() {
        try{
            return ResponseEntity.ok().body(paymentService.getPayments());
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok().body(paymentService.getPaymentById(id));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    
}
