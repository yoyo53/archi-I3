package time.service.timeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import time.service.timeservice.model.time;
import time.service.timeservice.service.timeService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import time.service.timeservice.dto.timeDTO;

@RestController
@RequestMapping("/api/times")
public class timeController {

    private final timeService timeService;

    @Autowired
    public timeController(timeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    public ResponseEntity<String> pingtimeforeveryservice() {
        return ResponseEntity.ok("time service is up");
    }
}