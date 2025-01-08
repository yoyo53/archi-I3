package database.service.dbservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class kafkaController {

    private final kafkaProducer producerService;

    public kafkaController(kafkaProducer producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/publish")
    public String publish(@RequestParam("message") String message) {
        producerService.sendMessage(message);
        return "Message sent to Kafka: " + message;
    }
}