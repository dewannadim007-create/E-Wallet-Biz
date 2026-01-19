package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * FirstController - Landing page
 */
@Controller
public class FirstController {

    @org.springframework.web.bind.annotation.ResponseBody
    @GetMapping("/ping")
    public String ping() {
        return "Server is running!";
    }

    /**
     * Show landing/first page
     */
    @GetMapping("/")
    public String root() {
        System.out.println("DEBUG: Root URL / accessed. Redirecting to /first");
        return "redirect:/first";
    }

    /**
     * Show landing/first page
     */
    @GetMapping("/first")
    public String showFirstPage(Model model) {
        System.out.println("DEBUG: /first accessed. Returning 'first' view");
        return "first";
    }

    /**
     * Redirect to user login
     */
    @GetMapping("/first/user")
    public String user() {
        return "redirect:/login";
    }

    /**
     * Redirect to FAQ
     */
    @GetMapping("/first/faq")
    public String changeToFaq() {
        return "redirect:/faq";
    }

    /**
     * Redirect to branch list
     */
    @GetMapping("/first/branch")
    public String branch() {
        return "redirect:/branch";
    }
}
