package joo.project.my3d.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final Environment env;

    @Value("${aws.s3.url}")
    private String S3Url;

    @GetMapping
    public String main(Model model) {
        model.addAttribute("modelPath", S3Url);

        return "index";
    }

    @ResponseBody
    @GetMapping("/profile")
    public String getProfile() {

        return Arrays.stream(env.getActiveProfiles())
                .findFirst()
                .orElse("");
    }
}
