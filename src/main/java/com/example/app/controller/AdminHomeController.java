package com.example.app.controller;

import com.example.app.model.Transaction;
import com.example.app.model.User;
import com.example.app.services.TransactionService;
import com.example.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import java.util.List;

/**
 */
@Controller
public class AdminHomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Show admin home page with greetings
     */
    @GetMapping("/admin/home")
    public String showAdminHome(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getUserRole())) {
            return "redirect:/login";
        }

        
        model.addAttribute("name", "Admin".toUpperCase());

        // Greetings logic - EXACT SAME
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

        // Add Statistics
        try {
            // Total Users
            List<User> userList = userService.getUserList(); // Or use mongoTemplate.count if cleaner, but this is safe
            long totalUsers = userList.size();
            model.addAttribute("totalUsers", totalUsers);

            // Transaction Stats
            List<Transaction> transactionList = transactionService.getAllTransactionList();
            long totalTransactions = transactionList.size();

            // Daily Stats
            String todayStr = java.time.LocalDate.now().toString();
            long dailyTransactions = 0;
            double dailyVolume = 0;

            for (Transaction t : transactionList) {
                // Check if date matches today (String match is safer given the constructor
                // logic)
                if (t.getDate() != null && t.getDate().startsWith(todayStr)) {
                    dailyTransactions++;
                    dailyVolume += t.getAmount();
                }
            }

            model.addAttribute("totalTransactions", totalTransactions);
            model.addAttribute("dailyTransactions", dailyTransactions);
            model.addAttribute("dailyVolume", dailyVolume);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to 0
            model.addAttribute("totalUsers", 0);
            model.addAttribute("totalTransactions", 0);
            model.addAttribute("dailyTransactions", 0);
            model.addAttribute("dailyVolume", 0.0);
        }

        return "adminHome";
    }

    /**
     * Redirect to user list
     */
    @GetMapping("/admin/home/to-users")
    public String changeToUsers() {
        return "redirect:/admin/users";
    }

    /**
     * Redirect to cheque book status
     */
    @GetMapping("/admin/home/to-cheque-status")
    public String changeToChequeBookStatus() {
        return "redirect:/admin/cheque-status";
    }

    /**
     * Redirect to add user
     */
    @GetMapping("/admin/home/to-add-user")
    public String changeToAddUser() {
        return "redirect:/admin/add-user";
    }

    /**
     * Redirect to transaction list
     */
    @GetMapping("/admin/home/to-transactions")
    public String changeToTransactionList() {
        return "redirect:/admin/transactions";
    }

    /**
     * Financial Analytics Page
     * New feature for aggregating transaction data
     */
    @GetMapping("/admin/analytics")
    public String showAnalytics(
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "ALL") String type,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate,
            HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getUserRole())) {
            return "redirect:/login";
        }

        List<Transaction> allTransactions = transactionService.getAllTransactionList();

        // Filter Logic
        List<Transaction> filtered = allTransactions.stream().filter(t -> {
            boolean matchType = "ALL".equalsIgnoreCase(type)
                    || (t.getType() != null && t.getType().equalsIgnoreCase(type));

            boolean matchDate = true;
            if (t.getDate() != null) {
                String txDate = t.getDate().substring(0, 10); // Extract YYYY-MM-DD
                if (startDate != null && !startDate.isEmpty()) {
                    if (txDate.compareTo(startDate) < 0)
                        matchDate = false;
                }
                if (endDate != null && !endDate.isEmpty()) {
                    if (txDate.compareTo(endDate) > 0)
                        matchDate = false;
                }
            }
            return matchType && matchDate;
        }).collect(java.util.stream.Collectors.toList());

        // Calculations
        double totalVolume = filtered.stream().mapToDouble(Transaction::getAmount).sum();
        long count = filtered.size();
        double avg = count > 0 ? totalVolume / count : 0.0;

        // Formatting
        model.addAttribute("totalVolume", String.format("%.2f", totalVolume));
        model.addAttribute("transactionCount", count);
        model.addAttribute("avgValue", String.format("%.2f", avg));

        // Form State
        model.addAttribute("selectedType", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin-analytics";
    }

    /**
     * Logout and redirect to first page
     */
    @GetMapping("/admin/logout")
    public String changeToFirst(HttpSession session) {
        session.invalidate();
        return "redirect:/first";
    }
}
