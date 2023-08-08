package joo.project.my3d;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("model_articles")
public class ModelArticlesController {
    @GetMapping
    public String articles() {
        return "model_articles/index";
    }

    @GetMapping("/{article_id}")
    public String article(@PathVariable Long article_id) {
        return "model_articles/detail";
    }
}
