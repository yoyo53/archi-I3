package property.service.propertyservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.service.propertyservice.model.Property;
import property.service.propertyservice.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id, @RequestHeader("Authorization") String userID) {
        try {
            propertyService.deleteProperty(id, userID);
            return ResponseEntity.ok("Property deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting property");
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Property>> getProperties() {
        try{
            return ResponseEntity.ok().body(propertyService.getProperties());
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok().body(propertyService.getPropertyById(id));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProperty(@PathVariable Long id, @RequestBody Property property, @RequestHeader("Authorization") String userID) {
        try {
            propertyService.updateProperty(id, property, userID);
            return ResponseEntity.ok("Property updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating property");
        }
    }


    
}
