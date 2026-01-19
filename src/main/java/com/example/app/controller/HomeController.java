package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.model.Transaction;
import com.example.app.services.TransactionService;
import com.example.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;


@Controller
public class HomeController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TransactionService transactionService;

   
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        String[] info = UserService.userInfo(loggedUser.getMobile(), mongoTemplate);

        if (info != null) {
            model.addAttribute("name", info[0].toUpperCase());
        }

        int hour = LocalTime.now().getHour();
        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning!";
        } else if (hour >= 12 && hour < 19) {
            greeting = "Good Afternoon!";
        } else if (hour >= 19 && hour < 20) {
            greeting = "Good Evening!";
        } else {
            greeting = "Good Night!";
        }

        model.addAttribute("greetings", greeting);
        model.addAttribute("user", loggedUser);

        List<Transaction> allTransactions = transactionService.getTransactionList(loggedUser.getAccount(),
                loggedUser.getMobile());


        List<Transaction> recentTransactions = allTransactions.stream()
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("recentTransactions", recentTransactions);

        return "home";
    }


    @GetMapping("/api/home/check-wallet")
    public String checkWallet(HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        double checkBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        redirectAttributes.addFlashAttribute("balance", checkBalance);

        return "redirect:/home";
    }


    @GetMapping("/home/to-menu")
    public String changeToMenu() {
        return "redirect:/menu";
    }

    @GetMapping("/home/to-utility")
    public String changeToUtility() {
        return "redirect:/utility";
    }


    @GetMapping("/home/to-send-money")
    public String changeToSendMoney() {
        return "redirect:/send-money";
    }

    @GetMapping("/home/to-ebanking")
    public String changeToEBanking() {
        return "redirect:/ebanking";
    }

    @GetMapping("/home/to-checkbook")
    public String changeToCheckBook() {
        return "redirect:/checkbook";
    }

    @GetMapping("/home/to-statement")
    public String changeToStatement() {
        return "redirect:/statement";
    }

    @GetMapping("/home/to-history")
    public String changeToHistory() {
        return "redirect:/history";
    }
}
