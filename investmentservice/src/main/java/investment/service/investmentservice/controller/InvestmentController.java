package investment.service.investmentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import investment.service.investmentservice.model.Investment;
import investment.service.investmentservice.service.InvestmentService;


@RestController
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentService investmentservice;

    @Autowired
    public InvestmentController(InvestmentService investmentservice) {
        this.investmentservice = investmentservice;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    // @PostMapping("/test")
    // public ResponseEntity<String> zizicacaboudin(@RequestBody Investment investment) {
    //     return ResponseEntity.ok("zizicacaboudin");
        // String result = investmentservice.createUser(user);
        // if (result.equals("User created successfully")) {
        //     return ResponseEntity.ok(result);
        // } else {
        //     return ResponseEntity.badRequest().body(result);
        // }
    // }

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