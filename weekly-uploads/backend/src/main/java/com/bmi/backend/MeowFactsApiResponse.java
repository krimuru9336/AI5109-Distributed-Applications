// Sahan Wijesinghe - 05.12.2023 - Mtr Nr 1453575

package com.bmi.backend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeowFactsApiResponse {
	public MeowFactsApiResponse() {}
	
    @JsonProperty("data")
    private String[] data;

    public String[] getData() {
        return data;
    }
}

