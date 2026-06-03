package edu.wgu.jkellogg.ToDoList.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController {

    @RequestMapping(value = {"/", "/home", "/contact", "/app", "/app/**"})
    public String forward(HttpServletRequest request) {
        return "forward:/index.html";
    }
}
