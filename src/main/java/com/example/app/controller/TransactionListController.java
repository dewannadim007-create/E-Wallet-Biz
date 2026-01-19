package com.example.app.controller;

import com.example.app.model.Transaction;
import com.example.app.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Controller
public class TransactionListController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/admin/transactions")
    public String showTransactionList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String days,
            HttpSession session, Model model) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        List<Transaction> allTransactions = transactionService.getAllTransactionList();

        List<Transaction> filteredList = allTransactions.stream()
                .filter(t -> {
                    if (search != null && !search.trim().isEmpty()) {
                        String s = search.toLowerCase();
                        boolean match = (t.getRef() != null && t.getRef().toLowerCase().contains(s)) ||
                                (t.getSender() != null && t.getSender().toLowerCase().contains(s)) ||
                                (t.getReceiver() != null && t.getReceiver().toLowerCase().contains(s)) ||
                                (t.getType() != null && t.getType().toLowerCase().contains(s));
                        if (!match)
                            return false;
                    }

                    if (type != null && !type.isEmpty() && !type.equalsIgnoreCase("All")) {
                        if (!t.getType().equalsIgnoreCase(type))
                            return false;
                    }

                    if (days != null && !days.isEmpty() && !days.equalsIgnoreCase("All")) {
                        try {
                            long d = Long.parseLong(days);
                            if (t.getTransactionDate() != null) {
                                LocalDateTime cutoff = LocalDateTime.now().minusDays(d);
                                if (t.getTransactionDate().isBefore(cutoff))
                                    return false;
                            }
                        } catch (Exception e) {
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        model.addAttribute("transactions", filteredList);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedDays", days);
        return "transactionList";
    }

    @GetMapping("/admin/transactions/to-home")
    public String changeToHome() {
        return "redirect:/admin/home";
    }
}
