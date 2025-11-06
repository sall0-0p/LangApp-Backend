package com.lordbucket.langlearn.controller;

import com.lordbucket.langlearn.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class WebVerificationController {
    private final UserService userService;

    public WebVerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token, Model model) {
        try {
            userService.verifyUser(token);
            // This string "verification-success" tells Spring to
            // render the 'verification-success.html' file.
            return "verification-success";

        } catch (Exception e) {
            // You should create a 'verification-failure.html' page
            // to show the error message.
            model.addAttribute("error", e.getMessage());
            return "verification-failure"; // (Create this HTML page)
        }
    }
}
