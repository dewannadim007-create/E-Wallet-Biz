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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StatementController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/statement")
    public String showStatementPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        String[] dailyWalletStatement = transactionService.getDailyWalletExpense(loggedUser);
        model.addAttribute("dailyWalletExpense", dailyWalletStatement[0]);
        model.addAttribute("dailyWalletTr", dailyWalletStatement[1]);

        String[] dailyAccountStatement = transactionService.getDailyAccountExpense(loggedUser);
        model.addAttribute("dailyAccountExpense", dailyAccountStatement[0]);
        model.addAttribute("dailyAccountTr", dailyAccountStatement[1]);

        String currentMonth = LocalDate.now().getMonth().toString();
        String[] monthlyWalletStatement = transactionService.getMonthlyWalletExpense(loggedUser, currentMonth);
        model.addAttribute("monthlyWalletExpense", monthlyWalletStatement[0]);
        model.addAttribute("monthlyWalletTr", monthlyWalletStatement[1]);

        String[] monthlyAccountStatement = transactionService.getMonthlyAccountExpense(loggedUser, currentMonth);
        model.addAttribute("monthlyAccountExpense", monthlyAccountStatement[0]);
        model.addAttribute("monthlyAccountTr", monthlyAccountStatement[1]);

        model.addAttribute("user", loggedUser);

        return "statement";
    }

    @GetMapping("/history")
    public String showHistoryPage(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "days", required = false) String days,
            HttpSession session,
            Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        List<Transaction> allTransactions = transactionService.getTransactionList(loggedUser.getAccount(),
                loggedUser.getMobile());

        List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(t -> {
                    if (search != null && !search.trim().isEmpty()) {
                        String lowerSearch = search.toLowerCase();
                        String otherParty = t.getSender().equals(loggedUser.getAccount()) ? t.getReceiver()
                                : t.getSender();

                        boolean matchRef = t.getRef() != null && t.getRef().toLowerCase().contains(lowerSearch);
                        boolean matchType = t.getType().toLowerCase().contains(lowerSearch);
                        boolean matchParty = otherParty.toLowerCase().contains(lowerSearch);

                        if (!matchRef && !matchType && !matchParty) {
                            return false;
                        }
                    }

                    if (type != null && !type.isEmpty() && !type.equalsIgnoreCase("All")) {
                        if (!t.getType().equalsIgnoreCase(type)) {
                            return false;
                        }
                    }

                    if (days != null && !days.isEmpty() && !days.equalsIgnoreCase("All")) {
                        try {
                            long d = Long.parseLong(days);
                            LocalDateTime cutoff = LocalDateTime.now().minusDays(d);
                            if (t.getTransactionDate() != null && t.getTransactionDate().isBefore(cutoff)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        model.addAttribute("transactions", filteredTransactions);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedDays", days);
        model.addAttribute("user", loggedUser);

        return "history";
    }

}
