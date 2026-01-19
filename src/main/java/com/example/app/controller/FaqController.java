package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaqController {

    @GetMapping("/faq")
    public String showFaqPage(Model model) {
        return "faq";
    }

    @GetMapping("/faq/to-first")
    public String changeToFirst() {
        return "redirect:/first";
    }
}
