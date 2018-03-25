package com.adyang.blogger.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/tags")
public class TagsController {
    private static final String TAGS_TEMPLATE_DIR = "tags/";
    private static final String FLASH_NOTICE = "flashNotice";
    private TagRepository tagRepository;

    @Autowired
    public TagsController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public String index(Model model) {
        Iterable<Tag> tags = tagRepository.findAll();
        model.addAttribute("tags", tags);
        return TAGS_TEMPLATE_DIR + "index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Tag tag = findTagBy(id);
        model.addAttribute("tag", tag);
        return TAGS_TEMPLATE_DIR + "show";
    }

    private Tag findTagBy(Long id) {
        Optional<Tag> foundTag = tagRepository.findById(id);
        return foundTag.orElseThrow(TagNotFound::new);
    }

    @DeleteMapping("/{id}")
    public String destroy(@PathVariable Long id, RedirectAttributes model) {
        Tag tag = findTagBy(id);
        tagRepository.deleteById(id);
        model.addFlashAttribute(FLASH_NOTICE, String.format("Tag '%s' Deleted!", tag.getName()));
        return "redirect:/tags";
    }
}
