package payment.service.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PaymentserviceApplication {

	public static void main(String[] args) {

		// Charger le fichier .env
        Dotenv dotenv = Dotenv.configure()
                              .directory("./") // Indique l'emplacement du .env
                              .ignoreIfMissing()
                              .load();

        // Charger les variables d'environnement dans le système
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
		
		SpringApplication.run(PaymentserviceApplication.class, args);
	}

}
