package com.adyang.blogger.tag;

import com.adyang.blogger.article.Article;
import com.adyang.blogger.entity.BaseEntity;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(of = "name", callSuper = false)
@ToString(callSuper = true, exclude = "taggings")
@NoArgsConstructor
@Entity
public class Tag extends BaseEntity {
    private String name;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "tag", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
    private Set<Tagging> taggings = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }

    public List<Article> getArticles() {
        return taggings.stream()
                .map(Tagging::getArticle)
                .collect(Collectors.toList());
    }
}
