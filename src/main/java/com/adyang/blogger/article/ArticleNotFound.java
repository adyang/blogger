package com.adyang.blogger.article;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Article no longer available")
public class ArticleNotFound extends RuntimeException {
}
