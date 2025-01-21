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

    @PostMapping("/create")
    public ResponseEntity<String> createCertificate(@RequestBody Certificate certificate) {
        String result = certificateService.createCertificate(certificate);
        if (result.equals("Certificate created successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable Long id) {
        Certificate certificate = certificateService.getCertificate(id);
        if (certificate != null) {
            return ResponseEntity.ok(certificate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}