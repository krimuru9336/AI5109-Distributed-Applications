package com.hsfulda.distributedsystems.routing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    // Root file
    public static final String root = "index";

    // Use to automatically redirect on startup when working on a specific exercise
    public static final String startRoute = root;

    @GetMapping("/")
    public String rootRoute(Model model) {
        if (startRoute.equals(root)) {
            return root;
        }
        return "redirect:/" + startRoute;
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "index";
    }
}