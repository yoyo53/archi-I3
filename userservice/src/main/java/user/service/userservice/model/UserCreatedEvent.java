package user.service.userservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignore les champs nuls
public class UserCreatedEvent {

    @JsonProperty("EventType")
    private String eventType;

    @JsonProperty("Payload")
    private Payload payload;

    public UserCreatedEvent(String eventType, Payload payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    // Classe imbriquée pour le Payload
    public static class Payload {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("email")
        private String email;

        @JsonProperty("role")
        private String role;

        public Payload(Long id, String email, String role) {
            this.id = id;
            this.email = email;
            this.role = role;
        }
    }
}

