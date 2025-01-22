package investment.service.investmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class InvestmentserviceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(InvestmentserviceApplication.class, args);
    }

}
