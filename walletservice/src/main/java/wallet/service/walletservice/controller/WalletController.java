package wallet.service.walletservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wallet.service.walletservice.model.Wallet;
import wallet.service.walletservice.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit/{amount}")
    public ResponseEntity<String> deposit(@PathVariable Double amount ,@RequestHeader("Authorization") Long userID) {
        try {
            walletService.deposit(userID, amount);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/withdraw/{amount}")
    public ResponseEntity<String> withdraw(@PathVariable Double amount ,@RequestHeader("Authorization") Long userID) {
        try {
            walletService.withdraw(userID, amount);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/me")
    public ResponseEntity<Object> getWallet(@RequestHeader("Authorization") Long userID) {
        try {
            Wallet wallet = walletService.getWallet(userID);
            if(wallet == null){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(wallet);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}