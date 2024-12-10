package com.master.voting.controller;

import com.master.voting.model.User;
import com.master.voting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Повертає шаблон реєстрації
    }


    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        // Перевірка, чи існує користувач з таким ім'ям
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists."); // Додати повідомлення про помилку
            return "register";
        }


        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match."); // Додати повідомлення про помилку
            return "register";
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        return "redirect:/login"; //
    }
}
