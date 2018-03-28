package com.adyang.blogger.article;

import lombok.Data;

@Data
public class ArticleForm {
    private Long id;
    private String title;
    private String body;
    private String tagList;
    private Long imageId;

    protected ArticleForm() {
    }

    public ArticleForm(Long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }
}
