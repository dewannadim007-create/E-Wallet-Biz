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
public class AddUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/admin/add-user")
    public String showAddUserPage(Model model) {
        return "addUser";
    }

    @PostMapping("/admin/api/add-user")
    public String add(@RequestParam("name") String name,
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
            User user = new User(name, mobile, email, password, dobString, account, nid);

            boolean haveAccount = userService.checkAccount(account);

            if (haveAccount) {
                boolean exist = UserService.existingAccount(mobile, account, mongoTemplate);

                if (!exist) {
                    boolean isRegistered = userService.registration(user);

                    if (isRegistered) {
                        UserService.createOnlineBankingAccount(account, mobile, 0, mongoTemplate);
                        redirectAttributes.addFlashAttribute("successMessage", "Adding Complete. Proceeding To Home");
                        return "redirect:/admin/home";
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "Error");
                        return "redirect:/admin/add-user";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Account Already Exist");
                    return "redirect:/admin/add-user";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No Account In The Bank");
                return "redirect:/admin/add-user";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill all fields correctly");
            return "redirect:/admin/add-user";
        }
    }

    @GetMapping("/admin/add-user/to-home")
    public String changeToAdminHome() {
        return "redirect:/admin/home";
    }
}
