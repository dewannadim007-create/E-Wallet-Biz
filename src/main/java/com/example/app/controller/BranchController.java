package com.example.app.controller;

import com.example.app.model.Branch;
import com.example.app.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping("/branch")
    public String showBranchList(Model model) {

        List<Branch> branchList = branchService.getBranchList();

        model.addAttribute("branches", branchList);
        return "branch";
    }

    @GetMapping("/branch/search")
    public String startSearch(@RequestParam("search") String search, Model model) {

        String searchLower = search.toLowerCase();
        List<Branch> branchList = branchService.getBranchList();

        List<Branch> branchFilteredList = branchList.stream()
                .filter(branch -> branch.getName().toLowerCase().contains(searchLower) ||
                        branch.getType().toLowerCase().contains(searchLower) ||
                        branch.getLocation().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());

        model.addAttribute("branches", branchFilteredList);
        model.addAttribute("searchTerm", search);
        return "branch";
    }

    @GetMapping("/branch/to-first")
    public String changeToFirst() {
        return "redirect:/first";
    }
}
