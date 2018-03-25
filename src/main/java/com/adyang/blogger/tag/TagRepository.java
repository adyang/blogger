package com.adyang.blogger.tag;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    @EntityGraph(attributePaths = {"taggings"})
    Optional<Tag> findById(Long id);

    Optional<Tag> findOneByName(String name);
}
