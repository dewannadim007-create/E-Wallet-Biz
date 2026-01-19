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

/**
 * WBController - Wallet to Bank transfer
 */
@Controller
public class WBController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/wb-transfer")
    public String showWBTransferPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);

        // Add available balance (Wallet)
        double availableBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        model.addAttribute("availableBalance", availableBalance);

        return "wB";
    }

    @PostMapping("/api/wb-transfer")
    public String transfer(@RequestParam("amount") double amount,
            @RequestParam("receiverAccount") String receiverAccount,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean haveAccount = userService.checkAccount(receiverAccount);
        double senderBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        double remainingBalance = senderBalance - amount;
        boolean pinVerification = UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate);

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
            return "redirect:/wb-transfer";
        }
        if (monthlyCount >= 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Monthly Transaction Count Limit (500) Exceeded.");
            return "redirect:/wb-transfer";
        }
        if (dailyExpense + amount > 25000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Daily Limit (25000) Exceeded for Digital Wallet.");
            return "redirect:/wb-transfer";
        }

        if (haveAccount && remainingBalance >= amount && pinVerification) {
            // Transfer successful - EXACT SAME LOGIC
            transactionService.balanceTransfer(receiverAccount, amount);
            transactionService.senderBalanceUpdateOnline(loggedUser.getMobile(), amount);
            transactionService.transactionHistory(
                    new Transaction(receiverAccount, LocalDate.now().toString(), amount,
                            "wallet to bank", loggedUser.getAccount(),
                            transactionService.generateRefID()));

            redirectAttributes.addFlashAttribute("successMessage", "Transfer Successful");
            return "redirect:/wb-transfer";
        } else {
            // Transfer failed - EXACT SAME LOGIC
            redirectAttributes.addFlashAttribute("errorMessage", "Please Provide Valid Information");
            return "redirect:/wb-transfer";
        }
    }

    @GetMapping("/wb-transfer/to-send-money")
    public String changeToHome() {
        return "redirect:/send-money";
    }
}
