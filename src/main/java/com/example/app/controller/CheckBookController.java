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
import java.time.LocalDate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.bson.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckBookController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/chequebook")
    public String showCheckBookPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("name", loggedUser.getName());
        model.addAttribute("account", loggedUser.getAccount());
        model.addAttribute("mobile", loggedUser.getMobile());
        model.addAttribute("user", loggedUser);

        model.addAttribute("pageOptions", new Integer[] { 10, 20, 50 });

        model.addAttribute("checkNumberOptions", new Integer[] { 1, 2 });

        try {
            Query query = new Query(Criteria.where("account").is(loggedUser.getAccount()));
            List<Document> historyDocs = mongoTemplate.find(query, Document.class, "cheque");

            List<Map<String, Object>> history = new ArrayList<>();
            for (Document doc : historyDocs) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", doc.getString("applied"));
                item.put("pages", doc.get("page"));
                item.put("quantity", doc.get("chequeBook"));
                String status = doc.getString("status");
                item.put("status", status != null ? status : "Pending");
                history.add(item);
            }
            model.addAttribute("history", history);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "chequeBook";
    }

    @PostMapping("/api/chequebook/apply")
    public String apply(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "checkNumber", required = false) Integer checkNumber,
            @RequestParam(value = "password", required = false) String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        if (page == null || checkNumber == null || password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill all fields");
            return "redirect:/chequebook";
        }

        String applyDate = String.valueOf(LocalDate.now());
        String account = loggedUser.getAccount();

        if (UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate)) {
            if (!UserService.lastApplied(account, mongoTemplate)) {
                UserService.chequeApply(account, applyDate, page, checkNumber,
                        loggedUser.getName(), mongoTemplate);
                redirectAttributes.addFlashAttribute("successMessage", "Success");
                return "redirect:/chequebook";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Already Applied");
                return "redirect:/chequebook";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Password");
            return "redirect:/chequebook";
        }
    }

    @GetMapping("/chequebook/to-home")
    public String changeToHome() {
        return "redirect:/home";
    }
}
