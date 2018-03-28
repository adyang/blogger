package com.adyang.blogger.article;

import com.adyang.blogger.comment.Comment;
import com.adyang.blogger.comment.CommentForm;
import com.adyang.blogger.tag.Tag;
import com.adyang.blogger.tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/articles")
public class ArticlesController {
    private static final String ARTICLE_FORM = "articleForm";
    private static final String FLASH_NOTICE = "flashNotice";
    private static final String ARTICLES_TEMPLATE_DIR = "articles/";

    private ArticleRepository articleRepository;
    private TagRepository tagRepository;

    @Autowired
    public ArticlesController(ArticleRepository articleRepository, TagRepository tagRepository) {
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public String index(Model model) {
        Iterable<Article> articles = articleRepository.findAll();
        model.addAttribute("articles", articles);
        return ARTICLES_TEMPLATE_DIR + "index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Optional<Article> foundArticle = articleRepository.findById(id);
        Article article = foundArticle.orElseThrow(ArticleNotFound::new);
        model.addAttribute("article", article);
        model.addAttribute("commentForm", new CommentForm(article.getId()));
        return ARTICLES_TEMPLATE_DIR + "show";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute(ARTICLE_FORM, new ArticleForm());
        return ARTICLES_TEMPLATE_DIR + "new";
    }

    @Transactional
    @PostMapping
    public String create(ArticleForm articleForm, @RequestParam("image") MultipartFile file, RedirectAttributes model) throws IOException {
        Article newArticle = mapNewArticle(articleForm);
        overwriteImage(newArticle, file);
        Article savedArticle = articleRepository.save(newArticle);
        model.addFlashAttribute(FLASH_NOTICE, String.format("Article '%s' Created!", savedArticle.getTitle()));
        return "redirect:/articles/" + savedArticle.getId();
    }

    private void overwriteImage(Article article, MultipartFile file) throws IOException {
        Image image = createImage(file);
        article.setImage(image);
    }

    private Image createImage(MultipartFile file) throws IOException {
        if (file.isEmpty())
            return null;
        String fileName = Instant.now().getEpochSecond() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(ImagesController.IMAGE_DIR, fileName));
        return new Image(fileName, file.getContentType(), file.getSize());
    }

    private Article mapNewArticle(ArticleForm articleForm) {
        Article newArticle = new Article(articleForm.getTitle(), articleForm.getBody());
        List<Tag> tags = parseTagList(articleForm.getTagList());
        for (Tag tag : tags)
            newArticle.addTag(tag);
        return newArticle;
    }

    private List<Tag> parseTagList(String tagList) {
        return Stream.of(tagList.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .map(this::findOrCreateTag)
                .collect(Collectors.toList());
    }

    private Tag findOrCreateTag(String name) {
        return tagRepository.findOneByName(name)
                .orElseGet(() -> new Tag(name));
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
                .map(this::toArticleForm)
                .orElseThrow(ArticleNotFound::new);
        model.addAttribute(ARTICLE_FORM, articleForm);
        return ARTICLES_TEMPLATE_DIR + "edit";
    }

    private ArticleForm toArticleForm(Article a) {
        ArticleForm articleForm = new ArticleForm(a.getId(), a.getTitle(), a.getBody());
        articleForm.setTagList(convertToString(a.getTags()));
        Optional.ofNullable(a.getImage())
                .map(Image::getId)
                .ifPresent(articleForm::setImageId);
        return articleForm;
    }

    private String convertToString(List<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }

    @Transactional
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, ArticleForm articleForm, @RequestParam("image") MultipartFile file, RedirectAttributes model) throws IOException {
        Article article = findArticleBy(id);
        mapArticle(articleForm, article);
        overwriteImage(article, file);
        updateArticleTags(article, articleForm.getTagList());
        articleRepository.save(article);

        model.addFlashAttribute(FLASH_NOTICE, String.format("Article '%s' Updated!", article.getTitle()));

        return "redirect:/articles/" + article.getId();
    }

    private void mapArticle(ArticleForm articleForm, Article article) {
        article.setTitle(articleForm.getTitle());
        article.setBody(articleForm.getBody());
    }

    private void updateArticleTags(Article article, String tagList) {
        List<Tag> articleFormTags = parseTagList(tagList);
        removeMissingTags(article, articleFormTags);
        List<Tag> newTags = filterNewTags(article, articleFormTags);
        tagRepository.saveAll(newTags);
        newTags.forEach(article::addTag);
    }

    private List<Tag> filterNewTags(Article article, List<Tag> articleFormTags) {
        Predicate<Tag> tagWhichIsNew = formTag -> !article.getTags().contains(formTag);
        return articleFormTags.stream()
                .filter(tagWhichIsNew)
                .collect(Collectors.toList());
    }

    private void removeMissingTags(Article article, List<Tag> articleFormTags) {
        article.getTags().stream()
                .filter(tag -> !articleFormTags.contains(tag))
                .forEach(article::removeTag);
    }

    @Bean
    CommandLineRunner setUp() {
        return args -> {
            Article articleOne = new Article("titleOne", "body text one.");
            articleOne.addTag(new Tag("tagone"));
            articleRepository.save(articleOne);

            Article titleTwo = new Article("titleTwo", "body text two.");
            Comment commentTwo = new Comment();
            commentTwo.setAuthorName("Chewbacca");
            commentTwo.setBody("RAWR!");
            titleTwo.addComment(commentTwo);
            articleRepository.save(titleTwo);


            Optional<Article> article = articleRepository.findById(articleOne.getId());
            System.out.println(article.map(Article::getTags).orElseThrow(ArticleNotFound::new));
        };
    }
}
