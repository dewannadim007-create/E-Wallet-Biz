package com.example.app.controller;

import com.example.app.model.Transaction;
import com.example.app.model.User;
import com.example.app.services.TransactionService;
import com.example.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserListController - Admin view users with search and delete
 */
@Controller
public class UserListController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/admin/users")
    public String showUserList(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        List<User> userList = userService.getUserList();
        // Filter out ADMIN users from the list
        userList = userList.stream()
                .filter(u -> !"ADMIN".equals(u.getUserRole()))
                .collect(Collectors.toList());

        model.addAttribute("users", userList);
        model.addAttribute("searchTerm", ""); // Initialize to avoid null
        return "userList";
    }

    @GetMapping("/admin/users/search")
    public String startSearch(@RequestParam("search") String search,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        String searchLower = search.toLowerCase();
        List<User> userList = UserService.getUserList(mongoTemplate);

        // EXACT SAME FILTER LOGIC
        List<User> filteredList = userList.stream()
                .filter(user -> !"ADMIN".equals(user.getUserRole())) // Exclude Admins
                .filter(user -> user.getAccount().toLowerCase().contains(searchLower) ||
                        user.getName().toLowerCase().contains(searchLower) ||
                        user.getNid().contains(searchLower) ||
                        user.getMobile().contains(searchLower))
                .collect(Collectors.toList());

        model.addAttribute("users", filteredList);
        model.addAttribute("searchTerm", search);
        return "userList";
    }

    // ENHANCED DETAIL VIEW (was getUserBalance)
    @GetMapping("/admin/users/balance")
    public String getUserBalance(@RequestParam("mobile") String mobile,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        // Fetch User Object
        Query query = new Query(Criteria.where("mobile").is(mobile));
        User selectedUser = mongoTemplate.findOne(query, User.class);

        if (selectedUser != null) {
            // Get Balances
            double walletBalance = UserService.getBalanceOnline(mobile, mongoTemplate);
            double accountBalance = UserService.getBalanceAccount(selectedUser.getAccount(), mongoTemplate); // Use
                                                                                                             // account
                                                                                                             // for bank
                                                                                                             // balance

            // Note: Update user object with fetched fresh balances for display
            selectedUser.setWalletBalance(walletBalance);
            selectedUser.setBalance(accountBalance);

            // Get Recent Transactions (Last 5)
            List<Transaction> transactions = transactionService.getTransactionList(selectedUser.getAccount());
            // Sort by Date Descending and Limit 5
            transactions.sort(Comparator.comparing(Transaction::getTransactionDate,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            if (transactions.size() > 5) {
                transactions = transactions.subList(0, 5);
            }

            model.addAttribute("selectedUser", selectedUser);
            model.addAttribute("selectedMobile", mobile); // Keep for compatibility if view uses it
            model.addAttribute("walletBalance", walletBalance);
            model.addAttribute("accountBalance", accountBalance);
            model.addAttribute("recentTransactions", transactions);
        }

        // Keep the list populated
        List<User> userList = userService.getUserList();
        userList = userList.stream()
                .filter(u -> !"ADMIN".equals(u.getUserRole()))
                .collect(Collectors.toList());
        model.addAttribute("users", userList);
        model.addAttribute("searchTerm", "");

        return "userList";
    }

    // EDIT USER ENDPOINTS
    @GetMapping("/admin/user/edit")
    public String showEditUser(@RequestParam("mobile") String mobile, HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        Query query = new Query(Criteria.where("mobile").is(mobile));
        User userToEdit = mongoTemplate.findOne(query, User.class);

        if (userToEdit == null) {
            return "redirect:/admin/users";
        }

        model.addAttribute("editUser", userToEdit);
        return "editUser";
    }

    @PostMapping("/admin/user/update")
    public String updateUser(User user, RedirectAttributes redirectAttributes) {
        try {
            Query query = new Query(Criteria.where("mobile").is(user.getMobile()));
            Update update = new Update()
                    // ONLY allow updating these specific fields
                    // Name, NID, DOB are restricted
                    .set("email", user.getEmail())
                    .set("address", user.getAddress());
            // Mobile is the key, so it can't be changed here easily without breaking the
            // key reference
            // Account, Balance, Password are NOT included here, so they remain unchanged.

            mongoTemplate.updateFirst(query, update, User.class);

            redirectAttributes.addFlashAttribute("successMessage", "User details updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/api/users/delete")
    public String delete(@RequestParam("mobile") String mobile,
            RedirectAttributes redirectAttributes) {

        userService.deleteUser(mobile);

        redirectAttributes.addFlashAttribute("warningMessage", "Deleted");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/to-home")
    public String changeToHome() {
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/users/to-admin-home")
    public String changeToAdminHome() {
        return "redirect:/admin/home";
    }
}
