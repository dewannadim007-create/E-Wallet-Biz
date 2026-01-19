package com.example.app.controller;

import com.example.app.model.Transaction;
import com.example.app.model.User;
import com.example.app.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TransactionProfileController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transaction-profile")
    public String showTransactionProfile(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        
        List<Transaction> transactionList = transactionService.getTransactionList(loggedUser.getAccount(),
                loggedUser.getMobile());

        model.addAttribute("transactionList", transactionList);

        
        model.addAttribute("filterOptions", new String[] {
                "utility", "gas bill", "electricity bill", "bank to bank", "bank to wallet",
                "wallet to wallet", "wallet to bank", "add to wallet", "recharge"
        });
        model.addAttribute("user", loggedUser);

        return "transactionProfile";
    }

    
    @GetMapping("/transaction-profile/filter")
    public String filter(@RequestParam("filterType") String filterBy,
            HttpSession session,
            Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        String filterLower = filterBy.toLowerCase();
        List<Transaction> transactionList = transactionService.getTransactionList(loggedUser.getAccount(),
                loggedUser.getMobile());

        List<Transaction> filteredList = transactionList.stream()
                .filter(t -> t.getType().contains(filterLower))
                .collect(Collectors.toList());

        model.addAttribute("transactionList", filteredList);
        model.addAttribute("filterType", filterBy);
        model.addAttribute("user", loggedUser);

        model.addAttribute("filterOptions", new String[] {
                "utility", "gas bill", "electricity bill", "bank to bank", "bank to wallet",
                "wallet to wallet", "wallet to bank", "add to wallet", "recharge"
        });

        return "transactionProfile";
    }

    
    @GetMapping("/transaction-profile/reset")
    public String reset(HttpSession session, Model model) {
        return showTransactionProfile(session, model);
    }

    @GetMapping("/transaction-profile/to-menu")
    public String changeToMenu() {
        return "redirect:/menu";
    }
}
