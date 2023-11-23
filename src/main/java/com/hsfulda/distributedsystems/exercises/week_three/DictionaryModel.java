package com.hsfulda.distributedsystems.exercises.week_three;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/*
 * Author : Nick Stolbov, Matrikel Nr.: 1269907
 * Created: 10.11.2023
 */
public class DictionaryModel {

    private HttpStatusCode statusCode;

    private HttpHeaders headers;

    private String data;

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
