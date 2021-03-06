package com.adyang.blogger.comment;

import com.adyang.blogger.article.Article;
import com.adyang.blogger.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ocpsoft.prettytime.PrettyTime;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
public class Comment extends BaseEntity {
    private String authorName;
    @Lob
    private String body;
    @ManyToOne
    private Article article;

    public Comment(String authorName, String body) {
        this.authorName = authorName;
        this.body = body;
    }

    public String getCommentTimeSinceArticleCreation() {
        PrettyTime p = new PrettyTime(toUtilDate(article.getCreatedDate()));
        String durationStr = p.formatDuration(toUtilDate(getCreatedDate()));
        return durationStr.isEmpty() ? "moments" : durationStr;
    }

    private Date toUtilDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
