package time.service.timeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

// import time.service.timeservice.model.time;
import time.service.timeservice.service.timeService;
import time.service.timeservice.dto.TimeDTO;

@RestController
@RequestMapping("/api/time")
public class timeController {

    private final timeService timeService;

    @Autowired
    public timeController(timeService timeService) {
        this.timeService = timeService;
    }

    @PostMapping
    public ResponseEntity<String> pingtimeforeveryservice(@RequestBody TimeDTO TimeDTO) {
        try {
            ResponseEntity<ObjectNode> response = timeService.sendTime(TimeDTO);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("time service is up");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("time service is down");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}