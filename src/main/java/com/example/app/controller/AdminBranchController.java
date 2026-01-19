package com.example.app.controller;

import com.example.app.model.Branch;
import com.example.app.model.User;
import com.example.app.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/branches")
public class AdminBranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public String showBranchList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            HttpSession session, Model model) {
        if (!isAdmin(session))
            return "redirect:/login";

        List<Branch> branches = branchService.getBranchList();

        if ((search != null && !search.trim().isEmpty()) || (type != null && !type.equals("All") && !type.isEmpty())) {
            final String s = (search != null) ? search.toLowerCase() : "";

            branches = branches.stream()
                    .filter(b -> {
                        boolean matchesSearch = true;
                        if (!s.isEmpty()) {
                            matchesSearch = (b.getName() != null && b.getName().toLowerCase().contains(s)) ||
                                    (b.getLocation() != null && b.getLocation().toLowerCase().contains(s));
                        }

                        boolean matchesType = true;
                        if (type != null && !type.equals("All") && !type.isEmpty()) {
                            matchesType = b.getType() != null && b.getType().equalsIgnoreCase(type);
                        }

                        return matchesSearch && matchesType;
                    })
                    .collect(Collectors.toList());

            model.addAttribute("searchTerm", search);
            model.addAttribute("selectedType", type);
        }

        model.addAttribute("branches", branches);
        return "admin-branches";
    }

    @GetMapping("/add")
    public String showAddBranchForm(HttpSession session, Model model) {
        if (!isAdmin(session))
            return "redirect:/login";

        model.addAttribute("branch", new Branch());
        model.addAttribute("pageTitle", "Add New Location");
        return "admin-branch-form";
    }

    @PostMapping("/add")
    public String addBranch(@ModelAttribute Branch branch, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session))
            return "redirect:/login";

        try {
            branchService.saveBranch(branch);
            redirectAttributes.addFlashAttribute("success", "Location added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding location: " + e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    @GetMapping("/edit/{id}")
    public String showEditBranchForm(@PathVariable("id") String id, HttpSession session, Model model) {
        if (!isAdmin(session))
            return "redirect:/login";

        Branch branch = branchService.getBranchById(id);
        if (branch == null) {
            return "redirect:/admin/branches";
        }
        model.addAttribute("branch", branch);
        model.addAttribute("pageTitle", "Edit Location");
        return "admin-branch-form";
    }

    @PostMapping("/update")
    public String updateBranch(@ModelAttribute Branch branch, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session))
            return "redirect:/login";

        try {
            branchService.saveBranch(branch);
            redirectAttributes.addFlashAttribute("success", "Location updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating location: " + e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    @GetMapping("/delete/{id}")
    public String deleteBranch(@PathVariable("id") String id, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session))
            return "redirect:/login";

        try {
            branchService.deleteBranch(id);
            redirectAttributes.addFlashAttribute("success", "Location deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting location: " + e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    private boolean isAdmin(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && "ADMIN".equals(loggedUser.getUserRole());
    }
}
