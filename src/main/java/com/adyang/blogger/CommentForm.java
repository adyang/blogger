package com.adyang.blogger;

import lombok.Data;

@Data
public class CommentForm {
    private String authorName;
    private String body;
    private Long articleId;

    protected CommentForm() {
    }

    public CommentForm(Long articleId) {
        this.articleId = articleId;
    }
}
