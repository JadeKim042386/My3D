package joo.project.my3d.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Value("${aws.s3.url}")
    private String S3Url;

    @GetMapping
    public String main(Model model) {
        model.addAttribute("modelPath", S3Url);

        return "index";
    }
}
