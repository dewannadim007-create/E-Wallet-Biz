package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String showMenuPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "menu";
    }

    @GetMapping("/menu/to-home")
    public String changeToHome() {
        return "redirect:/home";
    }

    @GetMapping("/menu/to-login")
    public String changeToLogin(HttpSession session) {
        session.invalidate();
        return "redirect:/first";
    }

    @GetMapping("/menu/to-transaction-profile")
    public String changeToTransactionProfile() {
        return "redirect:/transaction-profile";
    }

    @GetMapping("/menu/to-password")
    public String changeToPassword() {
        return "redirect:/change-password";
    }

    @GetMapping("/menu/to-user-profile")
    public String changeToUserProfile() {
        return "redirect:/user-profile";
    }
}
