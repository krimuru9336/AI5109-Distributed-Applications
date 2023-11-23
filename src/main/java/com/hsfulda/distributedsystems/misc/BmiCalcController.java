package com.hsfulda.distributedsystems.misc;

import com.hsfulda.distributedsystems.routing.Routes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BmiCalcController {

    private static final String defaultRoute = Routes.bmiCalc;

    // Dependency injection analog to @Autowired
    private final BmiRepository bmiRepository;

    public double calculatedBmi;

    public BmiCalcController(BmiRepository bmiRepository) {
        this.bmiRepository = bmiRepository;
    }

    @GetMapping(defaultRoute)
    public String getPage(Model model) {
        model.addAttribute("calculatedBmi", calculatedBmi);
        return defaultRoute;
    }

    @PostMapping(defaultRoute)
    public String calcBmi(@ModelAttribute BmiModel model) {
        calculatedBmi = model.getHeight() / 2;
        saveBmiToDB(model, calculatedBmi);
        return "redirect:/" + defaultRoute;
    }

    private void saveBmiToDB(BmiModel model, double bmi) {
        BmiDBEntity entity = new BmiDBEntity();

        entity.setName(model.getName());
        entity.setWeight(model.getWeight());
        entity.setHeight(model.getHeight());
        entity.setBmi(bmi);

        bmiRepository.save(entity);
    }
}
