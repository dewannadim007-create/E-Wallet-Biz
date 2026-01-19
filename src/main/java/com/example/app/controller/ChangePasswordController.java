package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChangePasswordController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/change-password")
    public String showChangePasswordPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);
        return "changePassword";
    }

    @PostMapping("/api/change-password")
    public String submit(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean check = true;

        if (currentPassword != null && !currentPassword.isEmpty()) {
            int length = currentPassword.length();
            if (length > 8 || length == 7 || length == 6 || length == 5 ||
                    length == 4 || length == 3 || length == 2 || length == 1) {
                check = false;
            }
        } else {
            if (currentPassword == null)
                check = false;
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            int length = newPassword.length();
            if (length > 8 || length == 7 || length == 6 || length == 5 ||
                    length == 4 || length == 3 || length == 2 || length == 1) {
                check = false;
            }
        } else {
            check = false;
        }

        if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
            check = false;
            redirectAttributes.addFlashAttribute("errorMessage",
                    "New Password And Confirm Password Cannot Be Different");
            return "redirect:/change-password";
        }

        if (check) {
            UserService.changePassword(loggedUser.getMobile(), newPassword, mongoTemplate);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Password Changed Successfully");
            return "redirect:/change-password";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid password format");
            return "redirect:/change-password";
        }
    }

    @GetMapping("/change-password/to-menu")
    public String changeToMenu() {
        return "redirect:/menu";
    }
}
