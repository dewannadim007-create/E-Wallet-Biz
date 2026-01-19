package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class SendMoneyController {

    @GetMapping("/send-money")
    public String showSendMoneyPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "sendMoney";
    }

    @GetMapping("/send-money/to-bb")
    public String changeToBB() {
        return "redirect:/bb-transfer";
    }

    @GetMapping("/send-money/to-bw")
    public String changeToBW() {
        return "redirect:/bw-transfer";
    }

    @GetMapping("/send-money/to-wb")
    public String changeToWB() {
        return "redirect:/wb-transfer";
    }

    @GetMapping("/send-money/to-ww")
    public String changeToWW() {
        return "redirect:/ww-transfer";
    }

    @GetMapping("/send-money/to-home")
    public String changeToHome() {
        return "redirect:/home";
    }

    @GetMapping("/send-money/to-login")
    public String changeToLogin(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
