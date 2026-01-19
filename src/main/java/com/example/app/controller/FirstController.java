package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FirstController {

    @org.springframework.web.bind.annotation.ResponseBody
    @GetMapping("/ping")
    public String ping() {
        return "Server is running!";
    }

    @GetMapping("/")
    public String root() {
        System.out.println("DEBUG: Root URL / accessed. Redirecting to /first");
        return "redirect:/first";
    }

    @GetMapping("/first")
    public String showFirstPage(Model model) {
        System.out.println("DEBUG: /first accessed. Returning 'first' view");
        return "first";
    }

    @GetMapping("/first/user")
    public String user() {
        return "redirect:/login";
    }

    @GetMapping("/first/faq")
    public String changeToFaq() {
        return "redirect:/faq";
    }

    @GetMapping("/first/branch")
    public String branch() {
        return "redirect:/branch";
    }
}
