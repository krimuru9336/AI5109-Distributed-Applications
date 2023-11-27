package bmidemo.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import org.thymeleaf.templateresolver.StringTemplateResolver;

import com.google.gson.Gson;

import ch.qos.logback.core.joran.sanity.Pair;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;


// Name : Shyam Joshi
// Date : 7/11/2023
// Matriculation number 1482098

@Controller
public class bmiController {
    
    @Autowired
    bmiRepository repository;
    @Autowired
    RestService service;

    String FrontendServerUrl = "http://172.205.240.26/resources/templates/";
    String BackendServerUrl = "http://172.201.217.3/";

    @GetMapping("/index")
    public void index(Model Model, HttpServletResponse  resp ) throws IOException{
        Model.addAttribute("bmiData",new BmiData());
        Model.addAttribute("submit_url",BackendServerUrl+"submit");
        resp.setContentType("text/html");
        try(PrintWriter writer = resp.getWriter()){
            writer.println(generateThymeleafTemplate("index",Model));
        }
    }

    @PostMapping(value="/submit")
    public void submit(@ModelAttribute BmiData data, Model model ,HttpServletResponse resp) throws IOException {
        BmiData d = new BmiData();
        d.name = data.name;
        d.weight = data.weight;
        d.height = data.height;
        d.bmi = d.weight / (d.height * d.height);
        repository.save(d);
        model.addAttribute("bmiData", new BmiData());
        model.addAttribute("bmi", "Your bmi is : "+d.bmi);
        model.addAttribute("submit_url",BackendServerUrl+"submit");
        resp.setContentType("text/html");
        try(PrintWriter writer = resp.getWriter()){
            writer.println(generateThymeleafTemplate("index",model));
        }
    }

    @RequestMapping("/showAll")
    public void showAll(Model model, HttpServletResponse resp ) throws IOException {
        Iterable<BmiData> res = repository.findAll();
        model.addAttribute("bmiData", res);
        resp.setContentType("text/html");
        try(PrintWriter writer = resp.getWriter()){
            writer.println(generateThymeleafTemplate("showAll",model));
        }
    }

    String restUrl = "https://restcountries.com/v3.1/name/";

    @GetMapping("/welcome")
    public void welcom(Model model, HttpServletResponse resp) throws IOException{
        resp.setContentType("text/html");
        try(PrintWriter writer = resp.getWriter()){
            writer.println(generateThymeleafTemplate("welcome",model));
        }
    }

    @GetMapping(path = {"/find", "/find/{name}"})
    public void findCountry(@PathVariable(required = false,name = "name") String country,Model model, HttpServletResponse resp) throws IOException{
        if(StringUtils.isEmpty(country)){
            resp.setContentType("text/html");
            try(PrintWriter writer = resp.getWriter()){
                writer.println(generateThymeleafTemplate("welcome",model));
            }
        }
        
        String url = restUrl+country;
        
        
        String res = service.Get(url, new ArrayList<Pair<String,String>>());

        Gson gson = new Gson();
        countryData clicks[] = gson.fromJson(res, countryData[].class);    

        model.addAttribute("country", clicks[0]);

        resp.setContentType("text/html");
        try(PrintWriter writer = resp.getWriter()){
            writer.println(generateThymeleafTemplate("CountryDetails",model));
        }
    }

    private String generateThymeleafTemplate(String name,Model model) {
        
        String res = service.Get(FrontendServerUrl+"templates/"+name+".html",new ArrayList<>());

        // Create a StringTemplateResolver
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode("HTML");

        // Create a TemplateEngine
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        // Process the template using Thymeleaf's Context
        Context context = new Context();

        context.setLocale(Locale.ENGLISH);
        model.asMap().forEach(context::setVariable);
    
        return templateEngine.process(res, context);
    }

    // Name : Shyam Joshi
    // Date : 12/11/2023
    // Matriculation number 1482098
    
    
}
