package edu.ucsb.cs56.mapache_search.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ucsb.cs56.mapache_search.entities.Tag;
import edu.ucsb.cs56.mapache_search.repositories.TagRepository;

@Controller
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping("/tags")
    public String getTags(Model model, OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        Iterable<Tag> tags = tagRepository.findAll();
        model.addAttribute("tag_template", new Tag());
        model.addAttribute("tags", tags);
        return "tags";
    }

    @PostMapping("/tags/create")
    public String saveKey(@ModelAttribute Tag tag, Model model, OAuth2AuthenticationToken token) {
        if (tagRepository.findByName(tag.getName()).isEmpty()) {
            Tag t = new Tag();
            t.setName(tag.getName());
            tagRepository.save(t);
        }

        Iterable<Tag> tags = tagRepository.findAll();
        model.addAttribute("tag_template", new Tag());
        model.addAttribute("tags", tags);
        return "tags";
    }

    @PostMapping("/tags/search")
    public String tagSearch(@ModelAttribute Tag tag, Model model, OAuth2AuthenticationToken token) {

        List<Tag> foundTags = tagRepository.findByName(tag.getName());
        
        int size = foundTags.size();
        model.addAttribute("foundTagsSize",size);

        Iterable<Tag> tags = tagRepository.findAll();
        model.addAttribute("tag_template", new Tag());
        model.addAttribute("foundTags", foundTags);
        model.addAttribute("tags", tags);

        return "tags";
    }

    @GetMapping("/tags/search")
    public String tagSort(@ModelAttribute Tag tag, Model model, OAuth2AuthenticationToken token, String filter) {
        List<Tag> allTags = tagRepository.findAll();
        if (filter == "alphabetical") {
            Collections.sort(allTags, (t1, t2)->{
                return t1.getName().compareTo(t2.getName());
            });
            model.addAttribute("alphabeticalTags", allTags);
        }
    }

}