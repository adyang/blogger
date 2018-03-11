package com.adyang.blogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Controller
public class ArticlesController {
    private ArticleRepository articleRepository;

    @Autowired
    public ArticlesController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/articles")
    public String index(Model model) {
        Iterable<Article> articles = articleRepository.findAll();
        model.addAttribute("articles", articles);
        return "index";
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model) {
        Optional<Article> article = articleRepository.findById(id);
        model.addAttribute("article", article.orElseThrow(ArticleNotFound::new));
        return "show";
    }

    @Bean
    CommandLineRunner setUp() {
        return args -> {
            articleRepository.save(new Article("titleOne", "body text one."));
            articleRepository.save(new Article("titleTwo", "body text two."));
        };
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Article no longer available")
    private class ArticleNotFound extends RuntimeException {
    }
}
