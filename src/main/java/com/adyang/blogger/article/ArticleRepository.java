package com.adyang.blogger.article;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    @EntityGraph(attributePaths = {"comments", "taggings"})
    Optional<Article> findById(Long id);
}
