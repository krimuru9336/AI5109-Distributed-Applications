// Sahan Wijesinghe - 09.11.2023 - 1453575

package com.example.bmi200;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeowFactsApiResponse {
	public MeowFactsApiResponse() {}
	
    @JsonProperty("data")
    private String[] data;

    public String[] getData() {
        return data;
    }
}

