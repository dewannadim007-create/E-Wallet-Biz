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
public class BBController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/bb-transfer")
    public String showBBTransferPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);

        double availableBalance = UserService.getBalanceAccount(loggedUser.getAccount(), mongoTemplate);
        model.addAttribute("availableBalance", availableBalance);

        return "bb";
    }

    @PostMapping("/api/bb-transfer")
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
        double senderBalance = UserService.getBalanceAccount(loggedUser.getAccount(), mongoTemplate);
        double remainingBalance = senderBalance - amount - 1000;
        boolean pinVerification = UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate);
        boolean accountCheck = loggedUser.getAccount().equals(receiverAccount);

        String currentMonth = LocalDate.now().getMonth().toString();
        String[] monthlyStats = transactionService.getMonthlyAccountExpense(loggedUser, currentMonth);
        String[] dailyStats = transactionService.getDailyAccountExpense(loggedUser);

        double monthlyExpense = Double.parseDouble(monthlyStats[0]);
        int monthlyCount = Integer.parseInt(monthlyStats[1]);
        double dailyExpense = Double.parseDouble(dailyStats[0]);

        if (monthlyExpense + amount > 500000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Monthly Limit (500,000) Exceeded for Linked Bank.");
            return "redirect:/bb-transfer";
        }
        if (monthlyCount >= 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Monthly Transaction Count Limit (500) Exceeded.");
            return "redirect:/bb-transfer";
        }
        if (dailyExpense + amount > 50000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Daily Limit (50000) Exceeded for Linked Bank.");
            return "redirect:/bb-transfer";
        }

        if (haveAccount && remainingBalance > amount && pinVerification && !accountCheck) {
            transactionService.balanceTransfer(receiverAccount, amount);
            transactionService.senderBalanceUpdate(loggedUser.getAccount(), amount);
            transactionService.transactionHistory(
                    new Transaction(receiverAccount, LocalDate.now().toString(), amount,
                            "bank to bank", loggedUser.getAccount(),
                            transactionService.generateRefID()));

            redirectAttributes.addFlashAttribute("successMessage", "Transfer Successful");
            return "redirect:/bb-transfer";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Please Provide Valid Information");
            return "redirect:/bb-transfer";
        }
    }

    @GetMapping("/bb-transfer/to-send-money")
    public String changeToHome() {
        return "redirect:/send-money";
    }
}
