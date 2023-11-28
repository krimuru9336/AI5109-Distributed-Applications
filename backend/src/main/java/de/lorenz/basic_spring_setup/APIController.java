package de.lorenz.basic_spring_setup;

import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author: Lorenz Hohmann (ID: 1259904)
 * @Date: 01.11.2023
 */
@Controller
@RequestMapping("/api")
public class APIController {

  private String apiURL = "https://api.thecatapi.com/v1/images/search";

  /**
   * @Author: Lorenz Hohmann (ID: 1259904)
   * @Date: 01.11.2023
   */
  @GetMapping
  public void makeRestCall(HttpServletResponse res) throws IOException {
    String imageURL = "";

    // Call url and print response header, status code and json response
    try {
      ObjectMapper mapper = new ObjectMapper();

      WebClient client = WebClient.create(apiURL);
      ResponseEntity<String> response = client.method(HttpMethod.GET).retrieve().toEntity(String.class).block();
      JsonNode root = mapper.readTree(response.getBody());

      System.out.println("Statuscode: " + response.getStatusCode());
      System.out.println("=====================================");
      System.out.println("Headers: " + response.getHeaders());
      System.out.println("=====================================");
      System.out.println("Response: " + root);

      imageURL = root.get(0).get("url").asText();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    try {
      PrintWriter out = res.getWriter();
      res.setHeader("Content-Type", "text/html");
      out.println("<img src=\"" + imageURL + "\">");
      out.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
