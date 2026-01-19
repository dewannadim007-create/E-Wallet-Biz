package com.example.app.controller;

import com.example.app.model.User;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChequeBookStatusController {

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/admin/cheque-status")
    public String showChequeBookStatus(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getUserRole())) {
            return "redirect:/login";
        }

        List<User> chequeList = UserService.getChequeList(mongoTemplate);

        List<User> filteredList = chequeList.stream()
                .filter(c -> {
                    boolean match = true;
                    if (search != null && !search.isEmpty()) {
                        String s = search.toLowerCase();
                        match = (c.getName() != null && c.getName().toLowerCase().contains(s)) ||
                                (c.getId() != null && c.getId().contains(s)) ||
                                (c.getNid() != null && c.getNid().contains(s));
                    }
                    if (!match)
                        return false;

                    if (status != null && !status.isEmpty() && !status.equals("All")) {
                        if (c.getAccountType() == null || !c.getAccountType().equalsIgnoreCase(status)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        model.addAttribute("cheques", filteredList);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("user", loggedUser);
        return "chequeBookStatus";
    }

    @PostMapping("/admin/api/cheque-status/update")
    public String updateStatus(@RequestParam("accountId") String accountId,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes) {

        if ("delete".equals(action)) {
            userService.updateChequeStatus(accountId, "Deleted");
            redirectAttributes.addFlashAttribute("successMessage", "Application Marked as Deleted (Archived)");
        } else if ("approve".equals(action)) {
            userService.updateChequeStatus(accountId, "Success");
            redirectAttributes.addFlashAttribute("successMessage", "Application Approved");
        } else if ("reject".equals(action)) {
            userService.updateChequeStatus(accountId, "Declined");
            redirectAttributes.addFlashAttribute("successMessage", "Application Declined");
        }

        return "redirect:/admin/cheque-status";
    }

    @GetMapping("/admin/cheque-status/to-home")
    public String changeToAdminHome() {
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/cheque-status/home")
    public String changeToHome() {
        return "redirect:/admin/home";
    }
}
