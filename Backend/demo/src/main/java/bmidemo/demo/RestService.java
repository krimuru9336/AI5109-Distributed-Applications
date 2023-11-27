package bmidemo.demo;

import java.util.ArrayList;

import javax.swing.text.Style;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import ch.qos.logback.core.joran.sanity.Pair;
import reactor.core.publisher.Mono;

@Service
public class RestService {
    
    WebClient webClient;

    public String Get(String Url, ArrayList<Pair<String,String>> args)
    {
        if(args.size() > 0){
            Url += "?";
            int i = 0;
            for (Pair<String,String> pair : args) {
                Url += pair.first+"="+pair.second;
                if(i != args.size() - 1){
                    Url += "&";
                }
            }
            
        }
        webClient = WebClient.create(Url);
        System.out.println(Url);
        String result;
        try {
            Mono<ClientResponse> spec = webClient.get().exchangeToMono(res -> Mono.just(res));
            ClientResponse resp = spec.block();
            System.out.println("Status code : "+resp.statusCode().value());
            System.out.println("content type : "+resp.headers().asHttpHeaders().getContentType());
            result = webClient.get().retrieve().bodyToMono(String.class).block();
            System.out.println(result);
        
            System.out.println("Response Body: " + result);
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }
        return result;
    }

    // Name : Shyam Joshi
    // Date : 12/11/2023
    // Matriculation number 1482098

    public String Post(String Url, ArrayList<Pair<String,String>> args){
        webClient = WebClient.create(Url);
        String result = webClient.post().bodyValue(args).retrieve().bodyToMono(String.class).block();
        return result;
    }

}
