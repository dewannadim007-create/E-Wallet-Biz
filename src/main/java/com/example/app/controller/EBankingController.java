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
public class EBankingController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/ebanking")
    public String showEBankingPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        double walletBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);

        double bankBalance = UserService.getBalanceAccount(loggedUser.getAccount(), mongoTemplate);

        model.addAttribute("walletBalance", walletBalance);
        model.addAttribute("bankBalance", bankBalance);
        model.addAttribute("user", loggedUser);

        return "eBanking";
    }

    @GetMapping("/api/ebanking/check-wallet")
    public String checkWallet(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        double walletBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        model.addAttribute("walletBalance", walletBalance);

        return "redirect:/ebanking";
    }

    @GetMapping("/api/ebanking/check-bank")
    public String checkBank(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        double bankBalance = UserService.getBalanceAccount(loggedUser.getAccount(), mongoTemplate);
        model.addAttribute("bankBalance", bankBalance);

        return "redirect:/ebanking";
    }

    @GetMapping("/ebanking/add-money")
    public String addMoney() {
        return "redirect:/add-money";
    }

    @GetMapping("/ebanking/to-home")
    public String changeToHome() {
        return "redirect:/home";
    }
}
