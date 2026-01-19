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
public class RechargeController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/recharge")
    public String showRechargePage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null)
            return "redirect:/login";

        double availableBalance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        model.addAttribute("availableBalance", availableBalance);

        model.addAttribute("operators", new String[] {
                "Grameenphone", "Airtel", "Banglalink", "Teletalk", "Robi"
        });
        model.addAttribute("types", new String[] { "Prepaid", "Postpaid" });
        model.addAttribute("user", loggedUser);

        return "recharge";
    }

    @PostMapping("/api/recharge/proceed")
    public String proceed(@RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "amount", required = false) String amountStr,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "operator", required = false) String operator,
            @RequestParam(value = "type", required = false) String type,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null)
            return "redirect:/login";

        if (amountStr == null || amountStr.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Insert Valid Data");
            return "redirect:/recharge";
        }

        double givenAmount = Double.parseDouble(amountStr);
        double balance = UserService.getBalanceOnline(loggedUser.getMobile(), mongoTemplate);
        String providerLower = operator.toLowerCase();

        if (givenAmount < balance &&
                UserService.verifyPin(password, loggedUser.getMobile(), mongoTemplate) &&
                givenAmount >= 20 &&
                account.length() == 11) {

            transactionService.utilityBillPay(account, providerLower, type, givenAmount);
            transactionService.senderBalanceUpdateOnline(loggedUser.getMobile(), givenAmount);

            transactionService.transactionHistory(
                    new Transaction(account, LocalDate.now().toString(), givenAmount,
                            "recharge", loggedUser.getAccount(),
                            transactionService.generateRefID()));

            redirectAttributes.addFlashAttribute("successMessage", "Payment Completed Successfully");
            return "redirect:/recharge";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Insert Valid Data");
            return "redirect:/recharge";
        }
    }

    @GetMapping("/recharge/set-20")
    public String add20(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 20);
        return showRechargePage(session, model);
    }

    @GetMapping("/recharge/set-50")
    public String add50(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 50);
        return showRechargePage(session, model);
    }

    @GetMapping("/recharge/set-100")
    public String add100(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 100);
        return showRechargePage(session, model);
    }

    @GetMapping("/recharge/set-500")
    public String add500(Model model, HttpSession session) {
        model.addAttribute("presetAmount", 500);
        return showRechargePage(session, model);
    }

    @GetMapping("/recharge/to-utility")
    public String changeToUtility() {
        return "redirect:/utility";
    }
}
