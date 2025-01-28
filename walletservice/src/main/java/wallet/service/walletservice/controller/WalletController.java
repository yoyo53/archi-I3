package wallet.service.walletservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import wallet.service.walletservice.service.WalletService;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody ObjectNode request) {
        Long userId;
        Double amount;
        try {
            userId = request.get("userId").asLong();
            amount = request.get("amount").asDouble();
        }
        catch (ClassCastException e) {
            return ResponseEntity.badRequest().body("Wallet deposit : Invalid input types");
        }

        try {
            walletService.deposit(userId, amount);
            
            URI location = URI.create(String.format("/wallets/deposit/%d", userId));
            return ResponseEntity.created(location).body("Wallet deposit : Success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Wallet deposit : " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody ObjectNode request) {
        Long userId;
        Double amount;
        try {
            userId = request.get("userId").asLong();
            amount = request.get("amount").asDouble();
        }
        catch (ClassCastException e) {
            return ResponseEntity.badRequest().body("Wallet deposit : Invalid input types");
        }

        try {
            walletService.withdraw(userId, amount);
            
            URI location = URI.create(String.format("/wallets/withdraw/%d", userId));
            return ResponseEntity.created(location).body("Wallet withdraw : Success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Wallet withdraw : " + e.getMessage());
        }


    }
}