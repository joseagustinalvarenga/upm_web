package com.upm.institutional.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", "Ha ocurrido un error inesperado.");
        model.addAttribute("message", e.getMessage());
        return "error"; // We should create a simple error.html
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        model.addAttribute("error", "Error en la operación.");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    // 404 handling usually requires config in properties too:
    // spring.mvc.throw-exception-if-no-handler-found=true
}
