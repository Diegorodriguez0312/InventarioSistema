package com.inventario.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Component
public class RequestContextConfig {

    @ModelAttribute("path")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
