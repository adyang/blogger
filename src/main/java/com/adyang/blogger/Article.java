package com.adyang.blogger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
public class Article extends BaseEntity {
    private String title;
    @Lob
    private String body;
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    List<Comment> comments = new ArrayList<>();

    protected Article() {
    }

    public Article(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setArticle(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setArticle(null);
    }
}
