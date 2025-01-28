package time.service.timeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import time.service.timeservice.model.time;
import time.service.timeservice.service.timeService;
import time.service.timeservice.dto.TimeDTO;

@RestController
@RequestMapping("/api/times")
public class timeController {

    private final timeService timeService;

    @Autowired
    public timeController(timeService timeService) {
        this.timeService = timeService;
    }

    @PostMapping
    public ResponseEntity<String> pingtimeforeveryservice(@RequestBody TimeDTO TimeDTO, @RequestHeader("Authorization") Long userID) {
        return ResponseEntity.ok("time service is up");
    }
}