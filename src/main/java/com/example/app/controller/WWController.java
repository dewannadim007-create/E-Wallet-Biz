package com.example.app.controller;

import com.example.app.model.Transaction;
import com.example.app.model.User;
import com.example.app.services.TransactionService;
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
import java.time.LocalDate;

@Controller
public class WWController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/ww-transfer")
    public String showWWTransferPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);

       
        double availableBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        model.addAttribute("availableBalance", availableBalance);

        return "wW";
    }

    @PostMapping("/api/ww-transfer")
    public String transfer(@RequestParam("amount") double amount,
            @RequestParam("receiverWallet") String receiverWallet,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean haveAccount = userService.checkAccountOnline(receiverWallet);
        double senderBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        double remainingBalance = senderBalance - amount;
        boolean pinVerification = UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate);
        boolean accountCheck = loggedUser.getMobile().equals(receiverWallet);

        // Check Limits (Digital Wallet)
        String currentMonth = LocalDate.now().getMonth().toString();
        String[] monthlyStats = transactionService.getMonthlyWalletExpense(loggedUser, currentMonth);
        String[] dailyStats = transactionService.getDailyWalletExpense(loggedUser);

        double monthlyExpense = Double.parseDouble(monthlyStats[0]);
        int monthlyCount = Integer.parseInt(monthlyStats[1]);
        double dailyExpense = Double.parseDouble(dailyStats[0]);

        if (monthlyExpense + amount > 100000) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Monthly Limit (100,000) Exceeded for Digital Wallet.");
            return "redirect:/ww-transfer";
        }
        if (monthlyCount >= 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Monthly Transaction Count Limit (500) Exceeded.");
            return "redirect:/ww-transfer";
        }
        if (dailyExpense + amount > 25000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Daily Limit (25000) Exceeded for Digital Wallet.");
            return "redirect:/ww-transfer";
        }

        if (haveAccount && remainingBalance >= amount && pinVerification && !accountCheck) {
            // Transfer successful - EXACT SAME LOGIC
            transactionService.balanceTransferOnline(receiverWallet, amount);
            transactionService.senderBalanceUpdateOnline(loggedUser.getMobile(), amount);
            transactionService.transactionHistory(
                    new Transaction(receiverWallet, LocalDate.now().toString(), amount,
                            "wallet to wallet", loggedUser.getAccount(),
                            transactionService.generateRefID()));

            redirectAttributes.addFlashAttribute("successMessage", "Transfer Successful");
            return "redirect:/ww-transfer";
        } else {
            // Transfer failed - EXACT SAME LOGIC
            redirectAttributes.addFlashAttribute("errorMessage", "Please Provide Valid Information");
            return "redirect:/ww-transfer";
        }
    }

    @GetMapping("/ww-transfer/to-send-money")
    public String changeToHome() {
        return "redirect:/send-money";
    }
}
