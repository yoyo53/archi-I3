package investment.service.investmentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.service.InvestmentService;


@RestController
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Autowired
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    // @GetMapping("/test")
    // public ResponseEntity<String> test() {
    //     return ResponseEntity.ok("test");
    // }

    @PostMapping("/create")
    public ResponseEntity<String> zizicacaboudin(@RequestBody Investment investment) {
        System.out.println("Investment Details: ");
        System.out.println("UserID: " + investment.getUserID());
        System.out.println("PropertyID: " + investment.getPropertyID());
        System.out.println("AmountInvested: " + investment.getAmountInvested());
        if (investment.getPropertyID() == null || investment.getUserID() == null || investment.getAmountInvested() == null) {
            return ResponseEntity.badRequest().body("UserID, Properties, Amount cannot be null");
        }
        if (investment.getAmountInvested() <= 0) {
            return ResponseEntity.badRequest().body("Amount must be greater than 0");
        }

        //Check if user exists
        if (!investmentService.userExists(investment.getUserID())) {
            return ResponseEntity.badRequest().body("User does not exist");
        }

        //Check if property exists
        if (!investmentService.propertyExists(investment.getPropertyID())) {
            return ResponseEntity.badRequest().body("Property does not exist");
        }
        


        String result = investmentService.createInvestment(investment);
        if (result.equals("User created successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // @PostMapping("/login")
    // public ResponseEntity<String> loginUser(@RequestBody User user) {
    //     String result = investmentservice.loginUser(user);
    //     if (result.startsWith("Login successful")) {
    //         return ResponseEntity.ok(result);
    //     } else {
    //         return ResponseEntity.badRequest().body(result);
    //     }
    // }
}