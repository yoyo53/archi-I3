package certificate.service.certificateservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import certificate.service.certificateservice.dto.CertificateDTO;
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
    public ResponseEntity<Iterable<Certificate>> getCertificates() {
        try {
            return ResponseEntity.ok(certificateService.getCertificates());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Iterable<Certificate>> getCertificatesByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(certificateService.getCertificatesByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Iterable<Certificate>> getCertificatesByPropertyId(@PathVariable Long propertyId) {
        try {
            return ResponseEntity.ok(certificateService.getCertificatesByPropertyId(propertyId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/investment/{investmentId}")
    public ResponseEntity<Certificate> getCertificateByInvestmentId(@PathVariable Long investmentId) {
        try {
            Certificate certificate = certificateService.getCertificateByInvestmentId(investmentId);
            if (certificate != null) {
                return ResponseEntity.ok(certificate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createCertificate(@RequestBody CertificateDTO certificateDTO) {
        try {
            Certificate createdCertificate = certificateService.createCertificate(certificateDTO);
            URI location = URI.create("/api/certificates/" + createdCertificate.getId());
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable Long id) {
        try {
            Certificate certificate = certificateService.getCertificate(id);
            if (certificate != null) {
                return ResponseEntity.ok(certificate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        try {
            Long deletedId = certificateService.deleteCertificate(id);
            if (deletedId != null) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}