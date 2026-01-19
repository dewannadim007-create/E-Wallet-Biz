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

import java.time.LocalDate;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        return "registration";
    }

    @PostMapping("/register")
    public String register(@RequestParam("name") String name,
            @RequestParam("nid") String nid,
            @RequestParam("dob") String dobString,
            @RequestParam("account") String account,
            @RequestParam("mobile") String mobile,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        boolean check = true;

        if (name == null || name.isEmpty()) {
            check = false;
        }

        if (nid == null || nid.isEmpty()) {
            check = false;
        }

        try {
            LocalDate dob = LocalDate.parse(dobString);
            if (dob.isAfter(LocalDate.now()) || dob.equals(LocalDate.now())) {
                check = false;
            }
        } catch (Exception e) {
            check = false;
        }

        if (account == null || account.isEmpty()) {
            check = false;
        }

        if (mobile == null || mobile.isEmpty()) {
            check = false;
        }

        if (email == null || email.isEmpty() ||
                !email.matches(".*@(gmail\\.com|yahoo\\.com|outlook\\.com|hotmail\\.com)$")) {
            check = false;
        }

        if (password != null && !password.isEmpty()) {
            int length = password.length();
            if (length > 8 || length == 7 || length == 6 || length == 5 ||
                    length == 4 || length == 3 || length == 2 || length == 1) {
                check = false;
            }
        }

        if (check) {
            User existingUser = userService.getUserByAccount(account);

            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bank Account Not Found. Please contact branch.");
                return "redirect:/register";
            }

            boolean identityMatched = true;

            if (!existingUser.getMobile().equals(mobile))
                identityMatched = false;
            if (!existingUser.getNid().equals(nid))
                identityMatched = false;
            if (!existingUser.getName().equalsIgnoreCase(name))
                identityMatched = false;
            if (existingUser.getDOB() != null && !existingUser.getDOB().equals(dobString))
                identityMatched = false;

            if (!identityMatched) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Verification Failed: Details do not match our bank records.");
                return "redirect:/register";
            }

            User userUpdate = new User();
            userUpdate.setAccount(account);
            userUpdate.setName(existingUser.getName());
            userUpdate.setMobile(existingUser.getMobile());
            userUpdate.setNid(existingUser.getNid());
            userUpdate.setDOB(existingUser.getDOB());
            userUpdate.setEmail(email);
            userUpdate.setPassword(password);

            boolean isRegistered = userService.registration(userUpdate);

            if (isRegistered) {
                UserService.createOnlineBankingAccount(account, mobile,
                        existingUser.getWalletBalance() != null ? existingUser.getWalletBalance() : 0.0, mongoTemplate);

                redirectAttributes.addFlashAttribute("successMessage", "Registration Successful! You can now login.");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "System Error during registration.");
                return "redirect:/register";
            }

        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill all fields correctly.");
            return "redirect:/register";
        }
    }

    @GetMapping("/register/to-login")
    public String changeToLogin() {
        return "redirect:/login";
    }
}
