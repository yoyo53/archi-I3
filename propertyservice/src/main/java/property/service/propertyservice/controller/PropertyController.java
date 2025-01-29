package property.service.propertyservice.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import property.service.propertyservice.dto.CreatePropertyDTO;
import property.service.propertyservice.dto.UpdatePropertyDTO;
import property.service.propertyservice.model.Property;
import property.service.propertyservice.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final Logger logger = LoggerFactory.getLogger(PropertyController.class);

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProperty(@PathVariable Long id, @RequestHeader("Authorization") Long userID) {
        try {
            Long deletedID = propertyService.deleteProperty(id, userID);
            if (deletedID == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

    @GetMapping("/open")
    public ResponseEntity<Iterable<Property>> getOpenProperties() {
        try{
            return ResponseEntity.ok().body(propertyService.getOpenProperties());
        }catch(Exception e){
            logger.error("Error getting open properties", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProperty(@PathVariable Long id, @RequestBody UpdatePropertyDTO propertyDTO, @RequestHeader("Authorization") Long userID) {
        try {
            Property newProperty = propertyService.updateProperty(id, propertyDTO, userID);
            return ResponseEntity.ok().body(newProperty);
        } catch (Exception e) {
            logger.error("Error getting open properties", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    
}
