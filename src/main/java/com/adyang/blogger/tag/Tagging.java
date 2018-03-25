package com.adyang.blogger.tag;

import com.adyang.blogger.article.Article;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = { "article", "tag" })
@ToString(callSuper = true, exclude = {"article", "tag"})
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Tagging {
    @Data
    @Embeddable
    public static class Id implements Serializable {
        private Long articleId;
        private Long tagId;

        protected Id() {
        }

        public Id(Long articleId, Long tagId) {
            this.articleId = articleId;
            this.tagId = tagId;
        }
    }

    @EmbeddedId
    private Id id;

    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @MapsId("articleId")
    private Article article;

    @ManyToOne
    @MapsId("tagId")
    private Tag tag;

    public Tagging(Article article, Tag tag) {
        this.article = article;
        this.tag = tag;
        this.id = new Id(article.getId(), tag.getId());
    }
}
