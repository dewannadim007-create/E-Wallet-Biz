package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class UserProfileController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/user-profile")
    public String showUserProfile(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        
        String[] info = UserService.userInfo(loggedUser.getMobile(), mongoTemplate);

        if (info != null) {
            
            model.addAttribute("name", info[0]); 
            model.addAttribute("mobile", info[1]); 
            model.addAttribute("account", info[2]);
            model.addAttribute("email", info[3]);
            model.addAttribute("dob", info[4]); 
            model.addAttribute("nid", info[5]);
        }

        model.addAttribute("user", loggedUser);

        return "userProfile";
    }

    @GetMapping("/user-profile/to-menu")
    public String changeToMenu() {
        return "redirect:/menu";
    }

    @GetMapping("/user-profile/to-first")
    public String changeToFirst(HttpSession session) {
        session.invalidate();
        return "redirect:/first";
    }
}
