package com.adyang.blogger.article;

import com.adyang.blogger.comment.Comment;
import com.adyang.blogger.entity.BaseEntity;
import com.adyang.blogger.tag.Tag;
import com.adyang.blogger.tag.Tagging;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true, exclude = "taggings")
@NoArgsConstructor
@Entity
public class Article extends BaseEntity {
    private String title;
    @Lob
    private String body;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tagging> taggings = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;

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

    public void addTag(Tag tag) {
        Tagging tagging = new Tagging(this, tag);
        taggings.add(tagging);
        tag.getTaggings().add(tagging);
    }

    public void removeTag(Tag tag) {
        List<Tagging> toRemoveTaggings = taggings.stream()
                .filter(tagging -> tagging.getTag().equals(tag))
                .collect(Collectors.toList());
        taggings.removeAll(toRemoveTaggings);
        tag.getTaggings().removeAll(toRemoveTaggings);
    }

    public List<Tag> getTags() {
        return taggings.stream()
                .map(Tagging::getTag)
                .collect(Collectors.toList());
    }
}
