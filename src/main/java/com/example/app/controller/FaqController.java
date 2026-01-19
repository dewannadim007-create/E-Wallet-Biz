package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 */
@Controller
public class FaqController {

    /**
     * Show FAQ page
     */
    @GetMapping("/faq")
    public String showFaqPage(Model model) {
        return "faq";
    }

    /**
     * Redirect to first page
     */
    @GetMapping("/faq/to-first")
    public String changeToFirst() {
        return "redirect:/first";
    }
}
