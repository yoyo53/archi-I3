package income.service.incomeservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import income.service.incomeservice.model.Income;
import income.service.incomeservice.service.IncomeService;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllIncomes() {
        try {
            Iterable<Income> incomes = incomeService.getAllIncomes();
            return ResponseEntity.ok().body(incomes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getIncome(@RequestHeader("Authorization") Long userID) {
        try {
            Income income = incomeService.getIncomeById(userID);
            if(income == null){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(income);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getIncomeByUser(@RequestHeader("Authorization") Long userID) {
        try {
            Iterable<Income> incomes = incomeService.getIncomesByUserId(userID);
            return ResponseEntity.ok().body(incomes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}