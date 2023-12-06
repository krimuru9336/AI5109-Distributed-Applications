package DogApi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Author: Thomas Niestroj (MatrikelNr: 142396)
 * Created: 08.11.2023
 * */
public class DogApiResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}

