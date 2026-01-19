package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class UtilityController {

    @GetMapping("/utility")
    public String showUtilityPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "utility";
    }

    @GetMapping("/utility/to-home")
    public String changeToHome() {
        return "redirect:/home";
    }

    @GetMapping("/utility/to-gas")
    public String changeToGas() {
        return "redirect:/gas";
    }

    @GetMapping("/utility/to-payment")
    public String changeToPayment() {
        return "redirect:/payment";
    }

    @GetMapping("/utility/to-recharge")
    public String changeToRecharge() {
        return "redirect:/recharge";
    }
}
