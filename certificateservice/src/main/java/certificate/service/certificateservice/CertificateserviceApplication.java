package certificate.service.certificateservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CertificateserviceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(CertificateserviceApplication.class, args);
    }

}
