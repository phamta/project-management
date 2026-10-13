package com.tanvan.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GalleryController {

    @GetMapping("/gallery.html")
    public String gallery() {
        return "gallery";
    }
}
