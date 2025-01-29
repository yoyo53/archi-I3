package investment.service.investmentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.service.InvestmentService;
import investment.service.investmentservice.dto.InvestmentDTO;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Autowired
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping
    public ResponseEntity<Object> getInvestments() {
        try {
            Iterable<Investment> investments = investmentService.getInvestments();
            return ResponseEntity.ok(investments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createInvestment(@RequestBody InvestmentDTO investmentDTO, @RequestHeader("Authorization") Long userID) {
        try {
            Investment result = investmentService.createInvestment(investmentDTO, userID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getInvestment(@PathVariable Long id) {
        try {
            Investment investment = investmentService.getInvestment(id);
            if (investment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(investment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getInvestmentsByUser(@RequestHeader("Authorization") Long userID) {
        try {

            Iterable<Investment> investments = investmentService.getInvestmentsByUser(userID);
            return ResponseEntity.ok(investments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}