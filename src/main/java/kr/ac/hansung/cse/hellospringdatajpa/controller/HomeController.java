package kr.ac.hansung.cse.hellospringdatajpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Authentication auth, Model model) {
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
        return "home";
    }
}
