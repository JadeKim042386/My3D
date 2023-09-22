package joo.project.my3d.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/guide")
@RequiredArgsConstructor
public class PrintingGuideController {

    @GetMapping("/materials")
    public String materials() {

        return "guide/materials";
    }

    @GetMapping("/printing_process")
    public String printing_process() {

        return "guide/printing_process";
    }
}
