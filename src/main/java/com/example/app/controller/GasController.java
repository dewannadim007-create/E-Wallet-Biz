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
public class GasController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/gas")
    public String showGasPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        double availableBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);

        model.addAttribute("availableBalance", availableBalance);
        model.addAttribute("user", loggedUser);

        return "gas";
    }

    @GetMapping("/gas/set-500")
    public String add500(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 500);
        return showGasPage(session, model);
    }

    @GetMapping("/gas/set-1000")
    public String add1000(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 1000);
        return showGasPage(session, model);
    }

    @GetMapping("/gas/set-1200")
    public String add1200(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 1200);
        return showGasPage(session, model);
    }

    @GetMapping("/gas/set-1500")
    public String add1500(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 1500);
        return showGasPage(session, model);
    }

    @PostMapping("/api/gas/proceed")
    public String proceed(@RequestParam("account") String account,
            @RequestParam("amount") double givenAmount,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        double balance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);

        if (givenAmount < balance &&
                UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate) &&
                transactionService.utilityAccountCheck("titas", account)) {

            transactionService.utilityBillPay(account, "titas", "gas", givenAmount);
            transactionService.senderBalanceUpdateOnline(loggedUser.getMobile(), givenAmount);

            transactionService.transactionHistory(
                    new Transaction(account, LocalDate.now().toString(), givenAmount,
                            "gas bill", loggedUser.getAccount(),
                            transactionService.generateRefID()));

            redirectAttributes.addFlashAttribute("successMessage", "Payment Completed Successfully");
            return "redirect:/gas";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Insert Valid Data");
            return "redirect:/gas";
        }
    }

    @GetMapping("/gas/to-utility")
    public String changeToUtility() {
        return "redirect:/utility";
    }
}
