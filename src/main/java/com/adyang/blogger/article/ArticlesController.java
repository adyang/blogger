package com.adyang.blogger.article;

import com.adyang.blogger.comment.Comment;
import com.adyang.blogger.comment.CommentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/articles")
public class ArticlesController {
    private static final String ARTICLE_FORM = "articleForm";
    private static final String FLASH_NOTICE = "flashNotice";

    private ArticleRepository articleRepository;

    @Autowired
    public ArticlesController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public String index(Model model) {
        Iterable<Article> articles = articleRepository.findAll();
        model.addAttribute("articles", articles);
        return "index";
    }

    @GetMapping("/{id}")
    public String  show(@PathVariable Long id, Model model) {
        Optional<Article> foundArticle = articleRepository.findById(id);
        Article article = foundArticle.orElseThrow(ArticleNotFound::new);
        model.addAttribute("article", article);
        model.addAttribute("commentForm", new CommentForm(article.getId()));
        return "show";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute(ARTICLE_FORM, new ArticleForm());
        return "new";
    }

    @PostMapping
    public String create(ArticleForm articleForm, RedirectAttributes model) {
        Article newArticle = new Article(articleForm.getTitle(), articleForm.getBody());
        Article savedArticle = articleRepository.save(newArticle);

        model.addFlashAttribute(FLASH_NOTICE, String.format("Article '%s' Created!", savedArticle.getTitle()));

        return "redirect:/articles/" + savedArticle.getId();
    }

    @DeleteMapping("/{id}")
    public String destroy(@PathVariable Long id, RedirectAttributes model) {
        Article article = findArticleBy(id);
        articleRepository.deleteById(id);
        model.addFlashAttribute(FLASH_NOTICE, String.format("Article '%s' Deleted!", article.getTitle()));
        return "redirect:/articles";
    }

    private Article findArticleBy(Long id) {
        Optional<Article> foundArticle = articleRepository.findById(id);
        return foundArticle.orElseThrow(ArticleNotFound::new);
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Optional<Article> article = articleRepository.findById(id);
        ArticleForm articleForm = article
                .map(a -> new ArticleForm(a.getId(), a.getTitle(), a.getBody()))
                .orElseThrow(ArticleNotFound::new);
        model.addAttribute(ARTICLE_FORM, articleForm);
        return "edit";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, ArticleForm articleForm, RedirectAttributes model) {
        Article article = findArticleBy(id);
        mapArticle(articleForm, article);
        articleRepository.save(article);

        model.addFlashAttribute(FLASH_NOTICE, String.format("Article '%s' Updated!", article.getTitle()));

        return "redirect:/articles/" + article.getId();
    }

    private void mapArticle(ArticleForm articleForm, Article article) {
        article.setTitle(articleForm.getTitle());
        article.setBody(articleForm.getBody());
    }

    @Bean
    CommandLineRunner setUp() {
        return args -> {
            Article articleOne = new Article("titleOne", "body text one.");
            articleRepository.save(articleOne);
            Article titleTwo = new Article("titleTwo", "body text two.");
            Comment commentTwo = new Comment();
            commentTwo.setAuthorName("Chewbacca");
            commentTwo.setBody("RAWR!");
            titleTwo.addComment(commentTwo);
            articleRepository.save(titleTwo);
        };
    }
}
