package com.mssmfactory.covidrescuersbackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class CommonController {

    @GetMapping("/")
    public void index(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("/login");
    }
}