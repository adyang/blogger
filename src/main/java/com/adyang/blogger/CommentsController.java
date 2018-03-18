package com.adyang.blogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private ArticleRepository articleRepository;
    private CommentRepository commentRepository;

    @Autowired
    public CommentsController(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping
    public String create(CommentForm commentForm) {
        Optional<Article> foundArticle = articleRepository.findById(commentForm.getArticleId());
        Article article = foundArticle.orElseThrow(ArticleNotFound::new);
        Comment comment = new Comment(commentForm.getAuthorName(), commentForm.getBody());
        comment.setArticle(article);
        commentRepository.save(comment);
        return "redirect:/articles/" + article.getId();
    }
}
