package property.service.propertyservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import property.service.propertyservice.dto.CreatePropertyDTO;
import property.service.propertyservice.dto.UpdatePropertyDTO;
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


    @PostMapping
    public ResponseEntity<Object> createProperty(@RequestBody CreatePropertyDTO propertyDTO, @RequestHeader("Authorization") Long userID) {
        try{
            Property propertySaved = propertyService.createProperty(propertyDTO, userID);
            URI resourceLocation = new URI("/api/properties/" + propertySaved.getId());
            return ResponseEntity.created(resourceLocation).body(propertySaved);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getProperties() {
        try{
            return ResponseEntity.ok().body(propertyService.getProperties());
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPropertyById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok().body(propertyService.getPropertyById(id));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/open")
    public ResponseEntity<Object> getOpenProperties() {
        try{
            return ResponseEntity.ok().body(propertyService.getOpenProperties());
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProperty(@PathVariable Long id, @RequestBody UpdatePropertyDTO propertyDTO, @RequestHeader("Authorization") Long userID) {
        try {
            Property newProperty = propertyService.updateProperty(id, propertyDTO, userID);
            return ResponseEntity.ok().body(newProperty);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    
}
