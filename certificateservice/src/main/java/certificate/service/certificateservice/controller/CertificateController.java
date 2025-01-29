package certificate.service.certificateservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import certificate.service.certificateservice.model.Certificate;
import certificate.service.certificateservice.service.CertificateService;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public ResponseEntity<Object> getCertificates() {
        try {
            return ResponseEntity.ok(certificateService.getCertificates());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCertificatesByUserId(@RequestHeader("Authorization") Long userId) {
        try {
            return ResponseEntity.ok(certificateService.getCertificatesByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Object> getCertificatesByPropertyId(@PathVariable Long propertyId) {
        try {
            return ResponseEntity.ok(certificateService.getCertificatesByPropertyId(propertyId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/investment/{investmentId}")
    public ResponseEntity<Object> getCertificateByInvestmentId(@PathVariable Long investmentId) {
        try {
            Certificate certificate = certificateService.getCertificateByInvestmentId(investmentId);
            if (certificate != null) {
                return ResponseEntity.ok(certificate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCertificate(@PathVariable Long id) {
        try {
            Certificate certificate = certificateService.getCertificate(id);
            if (certificate != null) {
                return ResponseEntity.ok(certificate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}