package time.service.timeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import time.service.timeservice.service.timeService;

@RestController
@RequestMapping("/api/time")
public class timeController {

    private final timeService timeService;

    @Autowired
    public timeController(timeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    public ResponseEntity<Object> getTime() {
        try {
            return ResponseEntity.ok().body(timeService.getTime());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add/{nbDays}")
    public ResponseEntity<Object> addDays(@PathVariable Long nbDays) {
        try {
            timeService.addDays(nbDays);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/set/{date}")
    public ResponseEntity<Object> setTime(@PathVariable String date) {
        try {
            timeService.moveToDate(date);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}