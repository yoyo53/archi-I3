package property.service.propertyservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import property.service.propertyservice.model.Property;
import property.service.propertyservice.service.PropertyService;

@RestController
@RequestMapping("/api/payments")
public class PropertyController {

    private final PropertyService propertyService;


    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    

    @PostMapping("/create")
    public ResponseEntity<String> createProperty(@RequestBody Property property, @RequestHeader("Authorization") String userID) {
        try{
            propertyService.createProperty(property, userID);
            URI resourceLocation = new URI("/api/payments/" + property.getId());
            return ResponseEntity.created(resourceLocation).body("Property created");
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error creating property");
        }
    }


    
}
