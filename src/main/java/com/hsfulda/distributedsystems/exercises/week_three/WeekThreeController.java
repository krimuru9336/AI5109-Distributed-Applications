package com.hsfulda.distributedsystems.exercises.week_three;

import com.hsfulda.distributedsystems.routing.Routes;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.Collections;

@Controller
public class WeekThreeController {
    private static final String defaultRoute = Routes.weekThree;

    private DictionaryService dictionaryService;

    WeekThreeController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    // Returns correct html with inserted entries from DB, BE: 2
    @GetMapping(defaultRoute)
    public String getPage(Model model) {
        return defaultRoute;
    }

    /*
     * Author : Nick Stolbov, Matrikel Nr.: 1269907
     * Created: 10.11.2023
     */
    // Sends REST request to public api and saves result in variable
    @PostMapping(value = defaultRoute + "/find", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String findWordInDictionary(Model model, @ModelAttribute WordModel wordModel) {
        ClientResponse response = this.dictionaryService.getDictionaryResponse(wordModel.getWord());

        DictionaryModel dictionaryModel = new DictionaryModel();
        dictionaryModel.setStatusCode(response.statusCode());
        dictionaryModel.setHeaders(response.headers().asHttpHeaders());
        dictionaryModel.setData(response.bodyToMono(String.class).block());

        model.addAttribute("word", wordModel.getWord());
        model.addAttribute("dictionaryModel", dictionaryModel);

        return defaultRoute;
    }
}
