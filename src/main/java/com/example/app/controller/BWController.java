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
public class BWController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/bw-transfer")
    public String showBWTransferPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);

        double availableBalance = UserService.getBalanceAccount(loggedUser.getAccount(), mongoTemplate);
        model.addAttribute("availableBalance", availableBalance);

        return "bW";
    }

    @PostMapping("/api/bw-transfer")
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
        double senderBalance = UserService.getBalanceAccount(loggedUser.getAccount(), mongoTemplate);
        double remainingBalance = senderBalance - amount - 1000;
        boolean pinVerification = UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate);
        boolean accountCheck = loggedUser.getMobile().equals(receiverWallet);

        String currentMonth = LocalDate.now().getMonth().toString();
        String[] monthlyStats = transactionService.getMonthlyAccountExpense(loggedUser, currentMonth);
        String[] dailyStats = transactionService.getDailyAccountExpense(loggedUser);

        double monthlyExpense = Double.parseDouble(monthlyStats[0]);
        int monthlyCount = Integer.parseInt(monthlyStats[1]);
        double dailyExpense = Double.parseDouble(dailyStats[0]);

        if (monthlyExpense + amount > 500000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Monthly Limit (500,000) Exceeded for Linked Bank.");
            return "redirect:/bw-transfer";
        }
        if (monthlyCount >= 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Monthly Transaction Count Limit (500) Exceeded.");
            return "redirect:/bw-transfer";
        }
        if (dailyExpense + amount > 50000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Daily Limit (50000) Exceeded for Linked Bank.");
            return "redirect:/bw-transfer";
        }

        if (haveAccount && remainingBalance > amount && pinVerification && !accountCheck) {
            transactionService.balanceTransferOnline(receiverWallet, amount);
            transactionService.senderBalanceUpdate(loggedUser.getAccount(), amount);
            transactionService.transactionHistory(
                    new Transaction(receiverWallet, LocalDate.now().toString(), amount,
                            "bank to wallet", loggedUser.getAccount(),
                            transactionService.generateRefID()));

            redirectAttributes.addFlashAttribute("successMessage", "Transfer Successful");
            return "redirect:/bw-transfer";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Please Provide Valid Information");
            return "redirect:/bw-transfer";
        }
    }

    @GetMapping("/bw-transfer/to-send-money")
    public String changeToHome() {
        return "redirect:/send-money";
    }
}
