package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/api/auth/login")
    public String login(@RequestParam("mobile") String mobile,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (mobile == null || mobile.isEmpty() || password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter mobile and password");
            return "redirect:/login";
        }

        User admin = userService.adminLogin(mobile, password);
        if (admin != null) {

            session.setAttribute("loggedUser", admin);
            return "redirect:/admin/home";
        }

        User user = userService.login(mobile, password);
        if (user != null) {

            session.setAttribute("loggedUser", user);
            return "redirect:/home";
        } else {

            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Mobile or Password");
            return "redirect:/login";
        }
    }

    @GetMapping("/login/to-registration")
    public String changeToRegistration() {
        return "redirect:/register";
    }

    @GetMapping("/login/back")
    public String back() {
        return "redirect:/first";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/first";
    }
}
